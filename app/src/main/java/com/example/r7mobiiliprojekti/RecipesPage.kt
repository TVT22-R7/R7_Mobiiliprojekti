package com.example.r7mobiiliprojekti

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.chatCompletionRequest
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.r7mobiiliprojekti.ui.theme.R7MobiiliprojektiTheme
import kotlinx.coroutines.launch

@Composable
fun RecipesPage() {
    // val itemList = listOf("Banana", "Tomato", "EGGS", "Milk", "Pepsi","bread")
    // ItemList(itemList = itemList)

    RecipeButton()

}

private suspend fun createMessage(request: String) : String{
    val openAI = OpenAI(
        token = BuildConfig.OPENAI_API_KEY
    )
    val modelId = ModelId("gpt-3.5-turbo")

    val chatMessages = mutableListOf(
        ChatMessage(
            role = ChatRole.User,
            content = request
        )
    )

    val request = chatCompletionRequest {
        model = modelId
        messages = chatMessages
    }

    val response = openAI.chatCompletion(request)
    val message = response.choices.first().message.content ?: "message not found"
    Log.d("chat message", message)
    return message
}

@Composable
fun RecipeButton() {
    // Returns a scope that's cancelled when RecipeButton is removed from composition
    val coroutineScope = rememberCoroutineScope()
    var response: String = ""

    val createRecipeOnClick: () -> Unit = {
        coroutineScope.launch {
            response= createMessage("Hey")
            Log.d("chat message", response)
        }
    }

    Button(onClick = createRecipeOnClick) {
        Text("Create Recipe")
    }
}

@Composable
fun ResponseCard (text: String) {
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
                .align(Alignment.Center),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 50.dp
            )
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineMedium,
            )
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

@Preview(showBackground = true)
@Composable
private fun ItemCardPreview() {
    ItemList(listOf("Banana", "Tomato", "EGGS"))
}
