package com.example.r7mobiiliprojekti

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

// Luodaan ViewModel
class IngredientViewModel : ViewModel() {
    private val recipeIngredients = mutableStateMapOf<String, Ingredient>()
    private val groceryListIngredients = mutableStateMapOf<String, Ingredient>()

    val recipeIngredientsList = MutableStateFlow(recipeIngredients.values.toList())
    val groceryListIngredientsList = MutableStateFlow(groceryListIngredients.values.toList())
    fun addToRecipe(ingredient: Ingredient) {
        updateIngredientList(recipeIngredients, recipeIngredientsList, ingredient) { it.quantityForRecipe++ }
    }
    fun removeFromRecipe(ingredient: Ingredient) {
        updateIngredientList(recipeIngredients, recipeIngredientsList, ingredient) {
            if (it.quantityForRecipe > 1) {
                it.quantityForRecipe--
            } else {
                recipeIngredients.remove(ingredient.name)
            }
        }
    }

    fun deleteFromRecipe(ingredient: Ingredient) {
        updateIngredientList(recipeIngredients, recipeIngredientsList, ingredient) {
            recipeIngredients.remove(ingredient.name)
        }
    }
    fun deleteFromList(ingredient: Ingredient) {
        updateIngredientList(groceryListIngredients, groceryListIngredientsList, ingredient) {
            groceryListIngredients.remove(ingredient.name)
        }
    }
    fun saveGroceryList(context: Context, groceryList: List<Ingredient>, listName: String) {
        GroceryPreferences.saveGroceryList(context, groceryList, listName)
    }
    fun addToList(ingredient: Ingredient) {
        updateIngredientList(groceryListIngredients, groceryListIngredientsList, ingredient) { it.quantityForList++ }
    }
    fun removeFromList(ingredient: Ingredient) {
        updateIngredientList(groceryListIngredients, groceryListIngredientsList, ingredient) {
            if (it.quantityForList > 1) {
                it.quantityForList--
            } else {
                groceryListIngredients.remove(ingredient.name)
            }
        }
    }
    private fun updateIngredientList(
        ingredientsMap: MutableMap<String, Ingredient>,
        ingredientsList: MutableStateFlow<List<Ingredient>>,
        ingredient: Ingredient,
        updateQuantity: (Ingredient) -> Unit
    ) {
        val existingIngredient = ingredientsMap[ingredient.name]
        if (existingIngredient != null) {
            updateQuantity(existingIngredient)
        } else {
            val newIngredient = ingredient.copy()
            ingredientsMap[ingredient.name] = newIngredient
            updateQuantity(newIngredient)
        }
        ingredientsList.value = ingredientsMap.values.toList()
    }
}
data class Ingredient(
    val name: String,
    val imageUrl: String,
    var quantityForList: Int = 0,
    var quantityForRecipe: Int = 0
)
// Hakunäkymä
@Composable
fun SearchView(viewModel: IngredientViewModel, appId: String, appKey: String) {
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Ingredient>>(emptyList()) }

    val focusRequester = remember { FocusRequester() }
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tekstikenttä
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp)
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (searchText.isNotEmpty()) {
                        searchIngredients(searchText, appId, appKey) { results ->
                            searchResults = results.filter { it.imageUrl.isNotEmpty() }
                        }
                        // Sulkee näppäimistön
                        coroutineScope.launch {
                            softwareKeyboardController?.hide()
                        }
                    }
                }
            )
        )
        // Avaa näppäimistön automaattisesti käyttäjälle kun näkymä avataan
        DisposableEffect(Unit) {
            focusRequester.requestFocus()
            onDispose { }
        }
        // Ainesosien listanäkymä
        LazyColumn {
            items(searchResults) { ingredient ->
                IngredientItem(
                    ingredient = ingredient,
                    onAddToRecipeClick = { viewModel.addToRecipe(ingredient) },
                    onAddToListClick = { viewModel.addToList(ingredient) },
                    onRemoveFromRecipeClick = { viewModel.removeFromRecipe(ingredient) },
                    onRemoveFromListClick = { viewModel.removeFromList(ingredient) }
                )
            }
        }
        // Haku-painike
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (searchText.isNotEmpty()) {
                    searchIngredients(searchText, appId, appKey) { results ->
                        searchResults = results.filter { it.imageUrl.isNotEmpty() }
                    }
                }
                // Sulkee näppäimistön
                coroutineScope.launch {
                    softwareKeyboardController?.hide()
                }
            }
        ) {
            Text("Search")
        }
    }
}

// Ainesosan yksittäinen näkymä
@Composable
fun IngredientItem(
    ingredient: Ingredient,
    onAddToRecipeClick: () -> Unit,
    onAddToListClick: () -> Unit,
    onRemoveFromRecipeClick: () -> Unit,
    onRemoveFromListClick: () -> Unit
) {
    val quantityForRecipeState = remember { mutableStateOf(ingredient.quantityForRecipe) }
    val quantityForListState = remember { mutableStateOf(ingredient.quantityForList) }

    LaunchedEffect(ingredient.quantityForRecipe, ingredient.quantityForList) {
        quantityForRecipeState.value = ingredient.quantityForRecipe
        quantityForListState.value = ingredient.quantityForList
    }
    LaunchedEffect(ingredient) {
        quantityForRecipeState.value = ingredient.quantityForRecipe
        quantityForListState.value = ingredient.quantityForList
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .size(90.dp),
        elevation = 0.dp,
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ainesosan kuva
            if (ingredient.imageUrl.isNotEmpty()) {
                Image(
                    painter = rememberImagePainter(ingredient.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(Color.Gray)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = ingredient.name,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row {

                    // Ainesosan määrä (Listalle lisääminen)
                    if (quantityForListState.value > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(
                                modifier = Modifier.size(40.dp),
                                onClick = {
                                    if (quantityForListState.value > 0) {
                                        quantityForListState.value--
                                        onRemoveFromListClick()
                                    }
                                }
                            ) {
                                Text(text = "-")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = quantityForListState.value.toString())
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                modifier = Modifier.size(40.dp),
                                onClick = {
                                    quantityForListState.value++
                                    onAddToListClick()
                                },
                            ) {
                                Text(text = "+")
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                onAddToListClick()
                                quantityForListState.value++
                            },
                            modifier = Modifier
                                .width(100.dp)
                                .height(36.dp),
                            elevation = ButtonDefaults.elevation(0.dp)
                        ) {
                            Text(
                                text = "Add to List",
                                fontSize = 8.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // Ainesosan reseptiin lisääminen
                    var buttonClicked by remember { mutableStateOf(false) }
                    LaunchedEffect(ingredient) {
                        buttonClicked = false
                    }
                    OutlinedButton(
                        onClick = {
                            if (buttonClicked) {
                                onRemoveFromRecipeClick()
                            } else {
                                onAddToRecipeClick()
                            }
                            buttonClicked = !buttonClicked
                        },
                        modifier = Modifier
                            .widthIn(max = 200.dp)
                            .height(36.dp),
                        elevation = ButtonDefaults.elevation(0.dp)
                    ) {
                        Text(
                            text = if (buttonClicked) "Added!" else "Add to Recipes",
                            fontSize = 8.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

fun searchIngredients(query: String, appId: String, appKey: String, onResult: (List<Ingredient>) -> Unit) {
    val apiUrl = "https://api.edamam.com/api/food-database/v2/parser?app_id=$appId&app_key=$appKey&ingr=$query"
    val url = URL(apiUrl)
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val connection = url.openConnection() as HttpsURLConnection
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val ingredients = parseIngredientsFromResponse(response)
            withContext(Dispatchers.Main) {
                onResult(ingredients)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

private fun parseIngredientsFromResponse(response: String): List<Ingredient> {
    val ingredients = mutableListOf<Ingredient>()
    try {
        val jsonObject = JSONObject(response)
        val hintsArray = jsonObject.getJSONArray("hints")
        for (i in 0 until hintsArray.length()) {
            val hint = hintsArray.getJSONObject(i)
            val food = hint.getJSONObject("food")
            val name = food.optString("label", "Unknown Ingredient")
            val image = food.optString("image", "")
            ingredients.add(Ingredient(name, image))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ingredients
}