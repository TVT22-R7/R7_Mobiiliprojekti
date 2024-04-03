package com.example.r7mobiiliprojekti

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.client.OpenAI
import kotlin.time.Duration.Companion.seconds

class OpenAIPage : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        val env = System.getenv("OPENAI_API_KEY")
        print(env)
    }

    val openai = OpenAI(
        token = "your-api-key",
        timeout = Timeout(socket = 60.seconds),
        // additional configurations...
    )
}

@Composable
fun RecipesPage() {
    val itemList = listOf("Banana", "Tomato", "EGGS", "Milk", "Pepsi","bread")

    Surface(modifier = Modifier.fillMaxSize()) {
        ItemList(itemList = itemList)
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
