package com.example.r7mobiiliprojekti

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Surface
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

@Composable
fun GroceriesView(viewModel: IngredientViewModel) {
    var searchValue by remember { mutableStateOf(TextFieldValue()) }

    val shoppingListIngredientsList = viewModel.groceryListIngredientsList.collectAsState().value
    var ingredientList by remember {
        mutableStateOf(emptyList<String>())
    }

    for (ingredient in shoppingListIngredientsList) {
        ingredientList = ingredientList + ingredient.name
    }
    Surface(color = if (DarkmodeON.darkModeEnabled) Color.DarkGray else Color.White){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(16.dp))
        shoppingListIngredientsList.forEach { ingredient ->
            IngredientRowWithCount(
                ingredient = ingredient,
                onIngredientDown = { viewModel.removeFromList(ingredient) },
                onIngredientUp = { viewModel.addToList(ingredient) }
            )
        }
        SearchField(
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { searchValue = it },
            value = searchValue,
            onSearch = { product -> viewModel.addToList(Ingredient(name = product.text, imageUrl = ""))
            }
        )
    }
}}

@Composable
fun IngredientRowWithCount(ingredient: Ingredient, onIngredientDown: (Ingredient) -> Unit, onIngredientUp: (Ingredient) -> Unit) {
    Surface(color = if (DarkmodeON.darkModeEnabled) Color.DarkGray else Color.White){
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
                onIngredientDown(ingredient)
            }
        ) {
            Text(text = "-")
        }

        // Tuotteen määrä
        Text(
            text = "${ingredient.quantityForList}",
            modifier = Modifier.padding(all = 8.dp)
        )

        // Lisää tuotteen määrää
        FloatingActionButton(
            modifier = Modifier
                .size(36.dp),
            onClick = {
                onIngredientUp(ingredient)
            }
        ) {
            Text(text = "+")
        }

    }
}}

@Composable
fun IngredientListRow(ingredient: Ingredient, onIngredientRemove: (Ingredient) -> Unit) {
    Surface(
        color = if (DarkmodeON.darkModeEnabled) Color.DarkGray else Color.White
    ) {
        val textColor = DarkModeTextHelper.getTextColor(DarkmodeON.darkModeEnabled)

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
                    .size(width = 72.dp, height = 36.dp),
                onClick = {
                    onIngredientRemove(ingredient)
                }
            ) {
                Text(text = "Remove")

            }
        }
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