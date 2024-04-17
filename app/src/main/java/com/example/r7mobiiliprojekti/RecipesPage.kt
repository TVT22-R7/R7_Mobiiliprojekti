package com.example.r7mobiiliprojekti

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.chatCompletionRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.r7mobiiliprojekti.DarkmodeON.darkModeEnabled
import com.example.r7mobiiliprojekti.UserAccountManager.googleAccountId
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun RecipesPage(viewModel: IngredientViewModel) {
    Surface(color = if (darkModeEnabled) Color.DarkGray else Color.White) {
        val recipeIngredientsList = viewModel.recipeIngredientsList.collectAsState().value
        val context = LocalContext.current
        var ingredientList by remember {
            mutableStateOf(emptyList<String>())
        }

        for (ingredient in recipeIngredientsList) {
            ingredientList = ingredientList + ingredient.name
        }
        val coroutineScope = rememberCoroutineScope()
        // saves recipe received from openai
        var recipeText by remember {
            mutableStateOf("")
        }
        var recipeVisible by remember {
            mutableStateOf(false)
        }

        // Launches a coroutine to get openai response
        val createRecipeOnClick: (context: Context) -> Unit = { context ->
            coroutineScope.launch {
                // Check users premium status if he can generate recipe
                val isPremium = checkUserPremiumStatus(googleAccountId) // Pass the users id here
                if (isPremium) {
                    val ingredients = ingredientList.joinToString(separator = ", ")
                    recipeText = createMessage(ingredients, context)
                    recipeVisible = true
                    Log.d("chat message", ingredients)
                } else {
                    // message for non premium pleb
                    showNonPremiumMessage(context)
                }
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            recipeIngredientsList.forEach { ingredient ->
                IngredientRow(ingredient = ingredient, onIngredientRemove = { viewModel.deleteFromRecipe(ingredient) })
            }
            // A button that generates a recipe using OpenAI, and shows the recipe to user
            RecipeButton(onClick = { createRecipeOnClick(context) })
        }

        if (recipeVisible) {
            ResponseCard(text = recipeText, onClick = { recipeVisible = false })
        }
    }
}

suspend fun checkUserPremiumStatus(userId: String?): Boolean {
    return suspendCoroutine { continuation ->
        if (userId == null) {
            continuation.resume(false)
            return@suspendCoroutine
        }

        val database = FirebaseDatabase.getInstance("https://r7-mobiiliprojekti-default-rtdb.europe-west1.firebasedatabase.app").reference
        val usersRef = database.child("users").child(userId)

        // Get current premium status from the database
        usersRef.child("premium").get().addOnSuccessListener { dataSnapshot ->
            val currentPremiumStatus = dataSnapshot.getValue(Boolean::class.java)
            val isPremium = currentPremiumStatus ?: false
            continuation.resume(isPremium)
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Error getting premium status: $exception")
            // error
            continuation.resume(false)
        }
    }
}


fun showNonPremiumMessage(context: Context) {
//message for non premium user
    Toast.makeText(context, "Upgrade to premium to access this feature.", Toast.LENGTH_SHORT).show()
}



@Composable
fun IngredientRow(ingredient: Ingredient, onIngredientRemove: (Ingredient) -> Unit) {
    Surface(color = if (darkModeEnabled) Color.DarkGray else Color.White){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        // Tuotteen kuva
        Image(
            painter = rememberImagePainter(ingredient.imageUrl),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(shape = RoundedCornerShape(8.dp))
        )
        // Tuotteen nimi
        Text(
            text = ingredient.name,
            modifier = Modifier.padding(start = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Poista tuote
        FloatingActionButton(
            modifier = Modifier
                .size(width = 72.dp,height = 36.dp),
            onClick = {
                onIngredientRemove(ingredient)
            }
        ) {
            Text(text = "Remove")
        }
    }
}}

// Creates openai bot, sends a request and returns the answer
private suspend fun createMessage(request: String, context: Context) : String{
    val openAI = OpenAI(
        token = BuildConfig.OPENAI_API_KEY
    )

    val modelId = ModelId("gpt-3.5-turbo")

    val chatMessages = mutableListOf(
        ChatMessage(
            role = ChatRole.System,
            content = "You help users to come up with a recipe using ingredients they already have. You don't have to use every ingredient. You can add items to purchase. List only name, ingredients and instructions."
        ),
        ChatMessage(
            role = ChatRole.User,
            content = request
        )
    )

    val completionRequest = chatCompletionRequest {
        model = modelId
        messages = chatMessages
    }

    val response = openAI.chatCompletion(completionRequest)
    val message = response.choices.first().message.content ?: "message not found"
    Log.d("chat message", message)
    RecipePreferences.saveRecipe(context, message)
    return message
}

@Composable
fun RecipeButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 25.dp)
            .height((32 * UiScale.scale).dp)
            .width((150 * UiScale.scale).dp)
    ) {
        Text("Create Recipe")
    }
}


@Composable
fun ResponseCard (text: String, onClick: () -> Unit) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Surface(color = if (darkModeEnabled) Color.DarkGray else Color.White){
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            modifier = Modifier
                .width(screenWidth - 50.dp)
                .height(screenHeight - 100.dp)
                .align(Alignment.Center)
                .padding(all = 16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 50.dp
            )
        ) {
            Column {
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 48.dp)
                        .verticalScroll(rememberScrollState()),
                    text = text,
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 16.sp,
                )

                Spacer(modifier = Modifier.weight(1f))

                Row (
                    verticalAlignment = Alignment.Bottom,
                ) {

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        modifier = Modifier
                            .padding(end = 36.dp, bottom = 16.dp),
                        onClick = onClick
                    ) {
                        Text(
                            text = "Close"
                        )
                    }
                }
            }


        }

    }
}}



@Composable
fun ItemList(itemList: List<String>, modifier: Modifier = Modifier) {
    Surface(color = if (darkModeEnabled) Color.DarkGray else Color.White){
    LazyColumn(modifier = modifier) {
        items(itemList) { item ->
            ItemCard(
                item = item,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}}

@Composable
fun ItemCard(item: String, modifier: Modifier = Modifier) {
    Surface(color = if (darkModeEnabled) Color.DarkGray else Color.White){
    Card(modifier = modifier) {
        Text(
            text = item,
            modifier = Modifier
                .height(100.dp)
                .width(100.dp),
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}}

@Preview(showSystemUi = true)
@Composable
private fun ItemCardPreview() {
    // IngredientRow(ingredient = Ingredient(name = "banana", imageUrl = "", 100), {}, beef{})
}
object RecipePreferences {
    private const val PREFS_NAME = "RecipePreferences"
    private const val KEY_RECIPES = "recipes"

    fun saveRecipe(context: Context, recipe: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val recipes = getRecipes(context).toMutableList()
        recipes.add(recipe)
        val editor = sharedPreferences.edit()
        editor.putStringSet(KEY_RECIPES, recipes.toSet())
        editor.apply()
    }

    fun getRecipes(context: Context): List<String> {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getStringSet(KEY_RECIPES, emptySet())?.toList() ?: emptyList()
    }

    fun removeRecipe(context: Context, recipe: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val recipes = getRecipes(context).toMutableList()
        recipes.remove(recipe)
        val editor = sharedPreferences.edit()
        editor.putStringSet(KEY_RECIPES, recipes.toSet())
        editor.apply()
    }
}
