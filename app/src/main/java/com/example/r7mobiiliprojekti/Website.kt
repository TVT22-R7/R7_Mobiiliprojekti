package com.example.r7mobiiliprojekti

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

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
fun GroceriesView(viewModel: IngredientViewModel) {
    var searchValue by remember { mutableStateOf(TextFieldValue()) }
    var products by remember { mutableStateOf(listOf("")) }
    val shoppingListIngredientsList = viewModel.groceryListIngredientsList.collectAsState().value
    val context = LocalContext.current
    var ingredientList by remember {
        mutableStateOf(emptyList<String>())
    }

    for (ingredient in shoppingListIngredientsList) {
        ingredientList = ingredientList + ingredient.name
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        FoodList(products)

        Spacer(modifier = Modifier.height(16.dp))
        shoppingListIngredientsList.forEach { ingredient ->
            IngredientListRow(
                ingredient = ingredient,
                onIngredientRemove = { viewModel.deleteFromList(ingredient) }
            )
        }
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
fun IngredientListRow(ingredient: Ingredient, onIngredientRemove: (Ingredient) -> Unit) {
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
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(text = ingredient.name)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${ingredient.quantityForList}")
        }

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
}
@Composable
fun FoodList(products: List<String>) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        // Placeholder
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(shape = RoundedCornerShape(8.dp))
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            products.forEach { product ->
                ProductItem(product = product)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
    /*
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            products.forEach { product ->
                ProductItem(product = product)
            }
        }*/
}

@Composable
fun ProductItem(product: String) {
    Text(
        text = product,
        modifier = Modifier.padding(start = 8.dp)
    )
    /*
    Box(
        modifier = Modifier
            .background(Color.LightGray)
            .padding(10.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = product)
    }*/
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