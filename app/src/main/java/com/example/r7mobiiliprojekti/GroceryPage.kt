package com.example.r7mobiiliprojekti

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

/*
class Website : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContent()
        }
    }
}
 */

@Composable
fun GroceriesView() {
    var searchValue by remember { mutableStateOf(TextFieldValue()) }
    var products by remember { mutableStateOf(listOf<String>()) }

    fun onIngredientRemove(product: String){
        products = products - product
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        FoodList(products, onIngredientRemove = {product -> onIngredientRemove(product)})

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
fun FoodList(products: List<String>, onIngredientRemove: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        products.forEach { product ->
            IngredientRow(ingredient = Ingredient(name = product, imageUrl = ""), onIngredientRemove = {onIngredientRemove(product)})
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
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), keyboardActions = KeyboardActions(onSearch = {onSearch(value)})
    )
}

