package com.example.r7mobiiliprojekti

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.r7mobiiliprojekti.ui.theme.R7MobiiliprojektiTheme
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.Text

import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.clickable
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            R7MobiiliprojektiTheme {
                // A surface container using the 'background' color from the theme
                Surface {
                    MainContent()
                }
            }
        }
    }

    // funktio joka navigoi profiili sivulle
    fun navigateToProfile(navController: NavHostController) {
        navController.navigate(BottomNavigationScreens.Profile.route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    fun navigateToSettings(navController: NavHostController) {
        navController.navigate(BottomNavigationScreens.Settings.route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    fun navigateToGrocery(navController: NavHostController) {
        navController.navigate(BottomNavigationScreens.GroceryList.route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    // funktio joka navigoi reseptit sivulle
    fun navigateToRecipes(navController: NavHostController) {
        navController.navigate(BottomNavigationScreens.Recipes.route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    fun navigateToSearch(navController: NavHostController) {
        navController.navigate("search")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent() {
    val navController = rememberNavController()
    val activity = (LocalContext.current as? MainActivity) // Get MainActivity instance
    val viewModel: IngredientViewModel = viewModel()

    // setupataa navigaatio
    val items = listOf(
        BottomNavigationScreens.Profile,
        BottomNavigationScreens.GroceryList,
        BottomNavigationScreens.Recipes,
        BottomNavigationScreens.Settings

    )

    //navController.currentBackStackEntryAsState() tarkistaaa onko nykyinen painike valittu ja päivittää näytön näkymän sen mukaisesti
    val backStackEntry by navController.currentBackStackEntryAsState()


    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.clickable {
                    activity?.navigateToSearch(navController)
                }
            ) {
                TopAppBar(
                    title = { Text(text = "Search...") },
                    navigationIcon = {
                        IconButton(onClick = {
                            activity?.navigateToSearch(navController)
                        }) {
                            Icon(Icons.Filled.Search, contentDescription = "Search")
                        }
                    }
                )
            }
        },
        bottomBar = {
            BottomNavigation(
                backgroundColor = Color.Gray
            ) {
                items.forEach { screen ->
                    BottomNavigationItem(
                        selected = backStackEntry?.destination?.route == screen.route,
                        onClick = {
                            //
                            when (screen) {
                                is BottomNavigationScreens.Profile -> activity?.navigateToProfile(navController)
                                is BottomNavigationScreens.GroceryList -> activity?.navigateToGrocery(navController)
                                is BottomNavigationScreens.Recipes -> activity?.navigateToRecipes(navController)
                                is BottomNavigationScreens.Settings -> activity?.navigateToSettings(navController)
                                // lisätään tarvittaessa lisää ruutuja
                            }
                        },

                        label = { Text(stringResource(screen.resourceId)) },
                        icon = {
                            when (screen) {
                                //iconien asettelut, joista mennään näkymiin
                                is BottomNavigationScreens.Profile -> Icon(Icons.Filled.Person, contentDescription = null)
                                is BottomNavigationScreens.GroceryList -> Icon(Icons.Filled.ShoppingCart, contentDescription = null)
                                is BottomNavigationScreens.Recipes -> Icon(Icons.Filled.Menu, contentDescription = null)
                                is BottomNavigationScreens.Settings -> Icon(Icons.Filled.Settings, contentDescription = null)// Handle other screens if needed

                                else -> {

                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        //reititykset funktioihin tässätiedostossa
        NavHost(navController, startDestination = BottomNavigationScreens.Profile.route, Modifier.padding(innerPadding)) {
            composable(BottomNavigationScreens.Profile.route) {
                Profile()
            }
            composable(BottomNavigationScreens.GroceryList.route) {
                GroceryList()
            }
            composable(BottomNavigationScreens.Recipes.route) {
                RecipesPage(viewModel = viewModel)
            }
            composable(BottomNavigationScreens.Settings.route) {
                Settings()
            }
            composable("search") {
                SearchView(
                    viewModel = viewModel,
                    appId = BuildConfig.APP_ID,
                    appKey = BuildConfig.INGREDIENT_API_KEY
                )
            }

        }
    }
}

@Composable
fun GroceryList() {
    //ostoslista tiedoston composable
}

@Composable
fun RecipesPageContent() {
    val viewModel: IngredientViewModel = viewModel() // Obtain an instance of IngredientViewModel
    RecipesPage(viewModel = viewModel)
}

@Composable
fun Profile() {
    ProfileScreen()
}

@Composable
fun Settings() {
    SettingsScreen()
}
// Alareunan navigointikohteiden luokka
sealed class BottomNavigationScreens(
    val route: String,
    val resourceId: Int
) {
    object Profile : BottomNavigationScreens("profile", R.string.profile)
    object GroceryList : BottomNavigationScreens("Grocery", R.string.GroceryList)
    object Recipes : BottomNavigationScreens("recipes", R.string.recipes)
    object Settings : BottomNavigationScreens("settings", R.string.settings)

}
