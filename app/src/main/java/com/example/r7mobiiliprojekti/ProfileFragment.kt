package com.example.r7mobiiliprojekti

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
@Composable
fun ProfileScreen() {
    val mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser

    val context = LocalContext.current
    // Use state to hold the list of recipes
    val (storedRecipes, setStoredRecipes) = remember { mutableStateOf(RecipePreferences.getRecipes(context)) }
    val showFullRecipeMap = remember { mutableStateMapOf<Int, Boolean>() }
    var recipeIsOpen by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Welcome, ${currentUser?.displayName}",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Check if there are stored recipes
        if (storedRecipes.isNotEmpty()) {
            // Display recipe titles
            storedRecipes.forEachIndexed { index, recipe ->
                val limitedRecipe = if (recipe.length > 20) {
                    recipe.substring(0, 20) + "..."
                } else {
                    recipe
                }

                val showFullRecipe = showFullRecipeMap[index] ?: false

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        // Clickable text to show full recipe
                        ClickableText(
                            text = AnnotatedString(limitedRecipe.trim()),
                            onClick = {
                                // Only one recipe can be open at a time
                                if (!recipeIsOpen) {
                                    showFullRecipeMap[index] = !showFullRecipe
                                    recipeIsOpen = true
                                } else {
                                    showFullRecipeMap.forEach { (key, _) ->
                                        showFullRecipeMap[key] = false
                                    }
                                    showFullRecipeMap[index] = true
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )

                        // Button to remove the recipe
                        IconButton(
                            onClick = {
                                RecipePreferences.removeRecipe(context, recipe)
                                // Update the stored recipes state after removing a recipe
                                setStoredRecipes(RecipePreferences.getRecipes(context))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete Recipe"
                            )
                        }

                        // Button to share recipes
                        IconButton(
                            onClick = { shareRecipes(context, listOf(recipe)) }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Share Recipe"
                            )
                        }
                    }
                }

                if (showFullRecipe) {
                    ResponseCard(text = recipe) {
                        showFullRecipeMap[index] = false
                        recipeIsOpen = false
                    }
                }
            }
        } else {
            Text(
                text = "No recipes stored",
                style = MaterialTheme.typography.body1
            )
        }
    }
}

private fun shareRecipes(context: Context, recipes: List<String>) {
    val recipesText = recipes.joinToString(separator = "\n")

    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, recipesText)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}
