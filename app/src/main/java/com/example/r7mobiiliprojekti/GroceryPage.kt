package com.example.r7mobiiliprojekti
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberImagePainter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

@Composable
fun GroceriesView(viewModel: IngredientViewModel) {
    var searchValue by remember { mutableStateOf(TextFieldValue()) }
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }
    val openDialog = { showDialog.value = true }
    val listNameToSave = remember { mutableStateOf("") }
    val groceryListIngredients = viewModel.groceryListIngredientsList.collectAsState().value
    var ingredientList by remember {
        mutableStateOf(emptyList<String>())
    }

    for (ingredient in groceryListIngredients) {
        ingredientList = ingredientList + ingredient.name
    }

    var dialogOpen by remember {
        mutableStateOf(false)
    }
    Surface(color = if (DarkmodeON.darkModeEnabled) Color.DarkGray else Color.White) {
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column (
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(weight = 1f, fill = false)
            ) {
                groceryListIngredients.forEach { ingredient ->
                    IngredientRowWithCount(
                        ingredient = ingredient,
                        onIngredientDown = { viewModel.removeFromList(ingredient) },
                        onIngredientUp = { viewModel.addToList(ingredient) }
                    )
                }
            }
        }

        if (dialogOpen) {
            Dialog(
                onAddItem = {
                    product -> viewModel.addToList(Ingredient(name = product.text, imageUrl = ""))
                    searchValue = TextFieldValue("")
                },
                onCloseDialog = {
                    dialogOpen = false
                    searchValue = TextFieldValue("")
                },
                itemName = searchValue,
                onItemNameChange = {searchValue = it}
            )
            DisposableEffect(listNameToSave.value) {
                if (listNameToSave.value.isNotEmpty()) {
                    val groceryList = groceryListIngredients.map { it.copy() }
                    viewModel.saveGroceryList(context, groceryList, listNameToSave.value)
                }
                onDispose { }
            }
        }

        DialogButton {
            dialogOpen = true
        }

        SaveListBtn(showDialog = openDialog)
        if (showDialog.value) {
            DialogToSaveList(
                onDismissRequest = { showDialog.value = false }
            ) { listName ->
                listNameToSave.value = listName
                showDialog.value = false
            }
        }
    }
}

@Composable
fun Dialog(
    onAddItem: (TextFieldValue) -> Unit,
    onCloseDialog: () -> Unit,
    itemName: TextFieldValue,
    onItemNameChange: (TextFieldValue) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    AlertDialog(
        onDismissRequest = onCloseDialog,
        title = { Text(text = "Add item") },
        text = {
            OutlinedTextField(
                modifier = Modifier
                    .focusRequester(focusRequester),
                value = itemName,
                onValueChange = onItemNameChange,
                label = { Text("Search") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onAddItem(itemName)
                    onCloseDialog()
                }
            ) {
                Text(text = "Add")
            }
        },
        dismissButton = {
            Button(
                onClick = onCloseDialog
            ) {
                Text(text = "Cancel")
            }
        }
    )
}

@Composable
fun DialogButton(
    onDialogOpen: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        OutlinedButton(
            onClick = onDialogOpen,
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text("+")
        }
    }
}

@Composable
fun addButton(
    onAddItem: (String) -> Unit,
    onCloseDialog: () -> Unit,
    itemName: String,
    onItemNameChange: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onCloseDialog,
        title = { Text(text = "Add Item") },
        text = {
            OutlinedTextField(
                value = itemName,
                onValueChange = onItemNameChange,
                label = { Text("Item Name") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onAddItem(itemName)
                    onCloseDialog()
                }
            ) {
                Text(text = "Add")
            }
        },
        dismissButton = {
            Button(
                onClick = onCloseDialog
            ) {
                Text(text = "Cancel")
            }
        }
    )
}

@Composable
fun IngredientRowWithCount(
    ingredient: Ingredient,
    onIngredientDown: (Ingredient) -> Unit,
    onIngredientUp: (Ingredient) -> Unit
) {
    Surface(color = if (DarkmodeON.darkModeEnabled) Color.DarkGray else Color.White) {
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
    }
}

@Composable
fun IngredientListRow(
    ingredient: Ingredient,
    onIngredientRemove: (Ingredient) -> Unit
) {
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
fun SearchField(
    modifier: Modifier = Modifier,
    onValueChange: (TextFieldValue) -> Unit,
    value: TextFieldValue,
    onSearch: (TextFieldValue) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch(value) })
    )
}

@Composable
fun AddItemDialog(
    onAddItem: (String) -> Unit,
    onCloseDialog: () -> Unit
) {
    var itemName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCloseDialog,
        title = { Text(text = "Add Item") },
        text = {
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Item Name") }
            )
        },
        confirmButton = {
            Button(onClick = {
                onAddItem(itemName)
                onCloseDialog()
            }) {
                Text(text = "Add")
            }
        },
        dismissButton = {
            Button(onClick = onCloseDialog) {
                Text("Cancel")
            }
        }
    )
}
@Composable
fun SaveListBtn(
    showDialog: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(
            onClick = { showDialog() },
            modifier = Modifier.padding(8.dp),
        ) {
            Text("Save List")
        }
    }
}
@Composable
fun DialogToSaveList(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Name your list"
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    modifier = Modifier.width(200.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    onConfirmation(textFieldValue.text.trim())
                }) {
                    Text("Confirm")
                }
            }
        }
    }
}
object GroceryPreferences {
    private const val PREFS_NAME = "GroceryPreferences"
    fun saveGroceryList(context: Context, groceryList: List<Ingredient>, listName: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(groceryList)
        editor.putString(listName, json)
        editor.apply()
    }
    fun removeGroceryList(context: Context, listName: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(listName)
        editor.apply()
    }
    fun getGroceryList(context: Context): List<Pair<String, List<Ingredient>>> {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.all.mapNotNull { entry ->
            val listName = entry.key
            val json = entry.value as? String
            if (json != null) {
                val type = object : TypeToken<List<Ingredient>>() {}.type
                val items = Gson().fromJson<List<Ingredient>>(json, type)
                listName to items
            } else {
                null
            }
        }
    }
}
