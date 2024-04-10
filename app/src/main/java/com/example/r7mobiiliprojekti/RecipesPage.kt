package com.example.r7mobiiliprojekti

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import coil.compose.rememberImagePainter
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.chatCompletionRequest
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.r7mobiiliprojekti.ui.theme.R7MobiiliprojektiTheme
import kotlinx.coroutines.launch

@Composable
fun RecipesPage(viewModel: IngredientViewModel) {
    val recipeIngredientsList by viewModel.recipeIngredientsList

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
    val createRecipeOnClick: () -> Unit = {
        coroutineScope.launch {
            val ingredients = ingredientList.joinToString(separator = ", ")
            recipeText = createMessage(ingredients)
            recipeVisible = true
            Log.d("chat message", ingredients)
        }
    }

    fun onItemRemove(ingredient: Ingredient){
        viewModel.removeFromRecipe(ingredient)
    }

    Column(modifier = Modifier) {
        LazyColumn {
            items(recipeIngredientsList) { ingredient ->
                IngredientRow(ingredient = ingredient, onIngredientRemove = {onItemRemove(ingredient)})
            }
        }

        // A button that generates a recipe using OpenAI, and shows the recipe to user
        RecipeButton(onClick = createRecipeOnClick)
    }

    if (recipeVisible){
        ResponseCard(text = recipeText, onClick = {recipeVisible = false})
    }
}


@Composable
fun IngredientRow(ingredient: Ingredient, onIngredientRemove: () -> Unit) {
    var ingredientQuantity by remember {
        mutableStateOf(ingredient.quantity)
    }

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

        // Vähennä tuotteen määrää
        FloatingActionButton(
            modifier = Modifier
                .size(36.dp),
            onClick = {
                if (ingredientQuantity > 1) {
                    ingredientQuantity--
                } else {
                    onIngredientRemove()
                }
            }
        ) {
            Text(text = "-")
        }

        // Tuotteen määrä
        Text(
            text = "${ingredientQuantity}",
            modifier = Modifier.padding(all = 8.dp)
        )

        // Lisää tuotteen määrää
        FloatingActionButton(
            modifier = Modifier
                .size(36.dp),
            onClick = {ingredientQuantity++}
        ) {
            Text(text = "+")
        }

    }
}

// Creates openai bot, sends a request and returns the answer
private suspend fun createMessage(request: String) : String{
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
    return message
}

@Composable
fun RecipeButton(onClick: () -> Unit) {

    Button(onClick = onClick) {
        Text("Create Recipe")
    }
}

@Composable
fun ResponseCard (text: String, onClick: () -> Unit) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
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
    IngredientRow(ingredient = Ingredient(name = "banana", imageUrl = "", 100), {})
}
