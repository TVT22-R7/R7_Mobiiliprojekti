package com.example.r7mobiiliprojekti

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

class Website : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContent()
        }
    }
}

@Composable
fun MainContent() {
    var searchValue by remember { mutableStateOf(TextFieldValue()) }
    var products by remember { mutableStateOf(listOf("Juice", "Bread", "Fruits", "Rice")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        FoodList(products)

        Spacer(modifier = Modifier.height(16.dp))

        SearchField(
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { searchValue = it },
            value = searchValue,
            onSearch = { newValue ->
                products = products + newValue.text
                searchValue = TextFieldValue()
            }
        )
    }
}

@Composable
fun FoodList(products: List<String>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        products.forEach { product ->
            ProductItem(product = product)
        }
    }
}

@Composable
fun ProductItem(product: String) {
    Box(
        modifier = Modifier
            .background(Color.LightGray)
            .padding(10.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = product)
    }
}

@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    onValueChange: (TextFieldValue) -> Unit,
    value: TextFieldValue,
    onSearch: (TextFieldValue) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = {"Search for products"},
        onImeActionPerformed = { action: ImeAction, controller: TextFieldValue ->
            if (action == ImeAction.Search) {
                onSearch(controller)
            }
        }
    )
}

fun OutlinedTextField(modifier: Modifier, value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit, label: () -> String, onImeActionPerformed: (ImeAction, TextFieldValue) -> Unit) {

}
