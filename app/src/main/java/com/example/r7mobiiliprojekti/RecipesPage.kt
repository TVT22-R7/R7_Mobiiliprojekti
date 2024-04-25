package com.example.r7mobiiliprojekti

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Surface
import androidx.compose.material.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import coil.compose.rememberImagePainter
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
    val buttonColors = ButtonDefaults.buttonColors(
        backgroundColor = if (darkModeEnabled) Color.White else Color.LightGray,
        contentColor = if (darkModeEnabled) Color.Black else Color.Black
    )
        val recipeIngredientsList = viewModel.recipeIngredientsList.collectAsState().value
        val context = LocalContext.current


        // saves recipe received from openai
        var recipeText by remember {
            mutableStateOf("")
        }

        var recipeVisible by remember {
            mutableStateOf(false)
        }

        var recipeIsLoading by remember {
            mutableStateOf(false)
        }

        var canRegenerateRecipe by remember {
            mutableStateOf(true)
        }

    val coroutineScope = rememberCoroutineScope()

    // Launches a coroutine to get openai response
    fun createRecipeOnClick(context: Context) {

        // Loading icon on button
        recipeIsLoading = true

        coroutineScope.launch {
            Log.d("chat message", googleAccountId ?: "Google account ID is null")
            // Check user's premium status to determine if they can generate a recipe
            val isPremium = checkUserPremiumStatus(googleAccountId) // Assuming googleAccountId is accessible here
            if (isPremium) {
                if (recipeIngredientsList.isEmpty()) {
                    recipeVisible = true
                    recipeText = "Please add ingredients"
                    recipeIsLoading = false
                    return@launch
                }

                val ingredients = recipeIngredientsList.joinToString(separator = ", ") { it.name }
                recipeText = createMessage(ingredients, context)
                recipeVisible = true
                recipeIsLoading = false
                Log.d("chat message", ingredients)
            } else {
                // Show message for non-premium user
                recipeIsLoading = false
                showNonPremiumMessage(context)
            }
        }
    }

    Surface(color = if (darkModeEnabled) Color.DarkGray else Color.White){
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column (
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(weight = 1f, fill = false)
            ) {
                recipeIngredientsList.forEach { ingredient ->
                    IngredientRow(
                        ingredient = ingredient,
                        onIngredientRemove = { viewModel.deleteFromRecipe(ingredient) }
                    )
                }
            }

            // A button that generates a recipe using OpenAI, and shows the recipe to user
            RecipeButton(
                onClick = { createRecipeOnClick(context) },

                modifier = Modifier
                    .padding(12.dp)
                    .height(52.dp)
                    .width(150.dp),
                isLoading = recipeIsLoading,
                buttonColors = buttonColors
            )
        }

        if (recipeVisible){
            ResponseCard(
                text = recipeText,
                onClose = {recipeVisible = false},
                canRegenerate = canRegenerateRecipe,
                onRegenerateRecipe = {createRecipeOnClick(context)}
            )
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
        Log.e(TAG, "WE GOT ID =  : $userId")

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
private suspend fun createMessage(request: String, context: Context) : String {
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
        )    )

    Log.d("Request", chatMessages.toString())

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
fun RecipeButton(
    onClick: () -> Unit,
    modifier: Modifier,
    isLoading: Boolean,
    buttonColors: androidx.compose.material.ButtonColors // Assuming ButtonColors is the correct type
) {

    if (isLoading){
        Button(
            onClick = { /*Do nothing*/ },
            modifier = Modifier
                .height((40 * UiScale.scale).dp)
                .width((150 * UiScale.scale).dp)
                .offset(y = 8.dp),
            colors = buttonColors // Applying buttonColors here
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    } else {
        Button(
            onClick = onClick,
            modifier = Modifier
                .height((40 * UiScale.scale).dp)
                .width((150 * UiScale.scale).dp)
                .offset(y = 8.dp),
            colors = buttonColors // Applying buttonColors here
        ) {
            Text(text = "Create Recipe")
        }
    }
}


@Composable
fun ResponseCard (text: String, onClose: () -> Unit, canRegenerate: Boolean = false, onRegenerateRecipe: () -> Unit = {}) {
    // Dialog for full screen card
    Dialog(
        onDismissRequest = { onClose() },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false, usePlatformDefaultWidth = false)
    ) {
        ElevatedCard(
            modifier = Modifier
                .padding(all = 16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 50.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(weight = 1f, fill = false)
                        .padding(all = 12.dp),
                    text = text,
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 16.sp,
                )

                Row(

                ) {
                    if (canRegenerate) {
                        Button(
                            modifier = Modifier
                                .padding(start = 16.dp, bottom = 16.dp),
                            onClick = onRegenerateRecipe
                        ) {
                            Text(text = "Generate new")
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        modifier = Modifier
                            .padding(end = 16.dp, bottom = 16.dp),
                        onClick = onClose
                    ) {
                        Text(
                            text = "Close",
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun ItemList(itemList: List<String>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(itemList) { item ->
            ItemCard(
                item = item,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun ItemCard(item: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Text(
            text = item,
            modifier = Modifier
                .height(100.dp)
                .width(100.dp),
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ItemCardPreview() {
    ResponseCard(text = "Lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum ", onClose = {})
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
