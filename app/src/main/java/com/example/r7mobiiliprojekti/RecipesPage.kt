package com.example.r7mobiiliprojekti

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
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Row
import coil.compose.rememberImagePainter
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip

@Composable
fun RecipesPage(viewModel: IngredientViewModel) {
    val recipeIngredientsList by viewModel.recipeIngredientsList

    LazyColumn {
        items(recipeIngredientsList) { ingredient ->
            IngredientRow(ingredient = ingredient)
        }
    }
}
@Composable
fun IngredientRow(ingredient: Ingredient) {
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

        // Tuotteen määrä
        Text(
            text = "${ingredient.quantity}",
            modifier = Modifier.padding(start = 8.dp)
        )
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
