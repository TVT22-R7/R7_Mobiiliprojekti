package com.example.r7mobiiliprojekti

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.Surface
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
@Composable
fun ProfileScreen() {
    val mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser
    val viewModel: IngredientViewModel = viewModel()
    Surface(
        color = if (DarkmodeON.darkModeEnabled) Color.DarkGray else Color.White
    ) {
        val textColor = DarkModeTextHelper.getTextColor(DarkmodeON.darkModeEnabled)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Welcome, ${currentUser?.displayName}",
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(text = "Stored recipes")
            RecipesSection(viewModel = viewModel)
            Text(text = "Stored lists")
            GroceryListSection(viewModel = viewModel)
        }
    }
}
data class DialogState(
    val content: @Composable () -> Unit,
    val onDismiss: () -> Unit = {}
)

@Composable
fun RecipesSection(viewModel: IngredientViewModel) {
    val context = LocalContext.current
    // Use state to hold the list of recipes
    val (storedRecipes, setStoredRecipes) = remember {
        mutableStateOf(
            RecipePreferences.getRecipes(
                context
            )
        )
    }
    val showFullRecipeMap = remember { mutableStateMapOf<Int, Boolean>() }
    var recipeIsOpen by remember { mutableStateOf(false) }

    // Check if there are stored recipes
    if (storedRecipes.isNotEmpty()) {
        LazyColumn {
            itemsIndexed(storedRecipes) { index, recipe ->
                val limitedRecipe = if (recipe.length > 20) {
                    recipe.substring(0, 20) + "..."
                } else {
                    recipe
                }
                val showFullRecipe = showFullRecipeMap[index] ?: false
                Column(
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
                    AlertDialog(
                        onDismissRequest = {
                            showFullRecipeMap[index] = false
                        },
                        title = { Text(text = "Recipe") },
                        text = { Text(text = recipe) },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showFullRecipeMap[index] = false
                                }
                            ) {
                                Text("Close")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GroceryListSection(viewModel: IngredientViewModel) {
    val context = LocalContext.current
    var listIsOpen by remember { mutableStateOf(false) }
    val dialogState = remember { mutableStateOf<DialogState?>(null) }
    val (storedLists, setStoredLists) = remember {
        mutableStateOf(
            GroceryPreferences.getGroceryList(context)
        )
    }
    val showFullListMap = remember { mutableStateMapOf<Int, Boolean>() }
    if (storedLists.isNotEmpty()) {
        LazyColumn {
            itemsIndexed(storedLists) { index, (listName, listItems) ->
                val limitedList = if (listName.length > 20) {
                    listName.substring(0, 20) + "..."
                } else {
                    listName
                }
                val showFullList = showFullListMap[index] ?: false

                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            showFullListMap[index] = !showFullList
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = limitedList.trim(),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                GroceryPreferences.removeGroceryList(context, listName)
                                setStoredLists(GroceryPreferences.getGroceryList(context))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete List"
                            )
                        }
                        IconButton(
                            onClick = { shareLists(context, listItems.map { it.name }) }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Share list"
                            )
                        }
                    }
                    if (showFullList) {
                        val selectedListItems = remember { mutableStateOf(listItems.toMutableList()) }
                        GroceryListDialog(
                            listName = listName,
                            listItems = selectedListItems.value,
                            onDismiss = {
                                showFullListMap[index] = false
                                listIsOpen = false
                            }
                        )
                    }
                }
            }
        }
    }
    dialogState.value?.let { dialog ->
        dialog.content()
    }
}
@Composable
fun GroceryListDialog(
    listName: String,
    listItems: List<Ingredient>,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = listName) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                listItems.forEach { listItem ->
                    val painter = rememberImagePainter(listItem.imageUrl)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(shape = RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${listItem.name}: ${listItem.quantityForList}",
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Close")
            }
        }
    )
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
private fun shareLists(context: Context, lists: List<String>) {
    val list = lists.joinToString(separator = "\n")
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, list)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}