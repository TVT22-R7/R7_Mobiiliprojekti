package com.example.r7mobiiliprojekti

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalUriHandler
import com.example.r7mobiiliprojekti.DarkmodeON.darkModeEnabled
import com.example.r7mobiiliprojekti.DarkmodeON.isDarkMode
import com.example.r7mobiiliprojekti.DarkmodeON.toggleDarkMode
import com.example.r7mobiiliprojekti.UiScale.rescaleUI
import com.example.r7mobiiliprojekti.UiScale.resetScale
import com.example.r7mobiiliprojekti.UiScale.scale
import com.google.firebase.database.FirebaseDatabase


@Composable
fun SettingsScreen() {
    val mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser
    val context = LocalContext.current
    val buttonColors = ButtonDefaults.buttonColors(
        backgroundColor = if (darkModeEnabled) Color.White else Color.LightGray,
        contentColor = if (darkModeEnabled) Color.Black else Color.Black
    )
    var licensesDialogShown by remember { mutableStateOf(false) } // Define licensesDialogShown variable

    LaunchedEffect(darkModeEnabled) {
        // This block will execute whenever darkModeEnabled changes
        Log.d("DarkModeEnabled", "Value changed to: $darkModeEnabled")
    }

    // Call LicensesDialog and HUDSettingsDropdown outside the definition of buttonColors

    HUDSettingsDropdown(buttonColors = buttonColors)

    Surface(color = if (darkModeEnabled) Color.DarkGray else Color.White) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Settings",
                color = if(darkModeEnabled) Color.White else Color.Black,
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                modifier = Modifier.padding(16.dp)
            )



            LicensesDropdown(buttonColors)

            Spacer(modifier = Modifier.height(20.dp))
            HUDSettingsDropdown(buttonColors)
            Spacer(modifier = Modifier.height(20.dp))
            PremiumButton(buttonColors = buttonColors)
            Spacer(modifier = Modifier.height(20.dp))
            LogoutButton(context = context, buttonColors = buttonColors)
            Spacer(modifier = Modifier.height(18.dp))
            GoogleAccountButton(buttonColors = buttonColors)
        }
    }
}



@Composable
fun HUDSettingsDropdown(buttonColors: ButtonColors) {
    var expanded by remember { mutableStateOf(false) }

    Surface(color = if (darkModeEnabled) Color.DarkGray else Color.White) {
        Column {
            Button(
                onClick = { expanded = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 45.dp)
                    .height((32 * scale).dp)
                    .width((150 * scale).dp),
                colors = buttonColors
            ) {
                Text(text = "HUD settings")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                DropdownMenuItem(onClick = {
                    toggleDarkMode()
                    expanded = false
                }) {
                    Text(text = if (isDarkMode) "Light mode" else "Dark mode")
                }

                DropdownMenuItem(onClick = {
                    rescaleUI()
                    expanded = false
                }) {
                    Text(text = "Increase UI")
                }

                DropdownMenuItem(onClick = {
                    resetScale()
                    expanded = false
                }) {
                    Text(text = "Reset UI")
                }
            }
        }
    }
}
@Composable
fun LicensesDropdown(buttonColors: ButtonColors) {
    // Define your LicensesDropdown composable function here
    // Example implementation:
    var expanded by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    Column {
        Button(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 45.dp)
                .height((32 * scale).dp)
                .width((150 * scale).dp),
                    colors = buttonColors
        ) {
            Text(text = "Licenses")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            DropdownMenuItem(onClick = {
                uriHandler.openUri("https://github.com/google/secrets-gradle-plugin")
                expanded = false
            }) {
                Text(text = "Secrets Gradle Plugin")
            }

            DropdownMenuItem(onClick = {
                uriHandler.openUri("https://www.edamam.com/")
                expanded = false
            }) {
                Text(text = "Edamam food database API")
            }
        }
    }
}



@Composable
fun PremiumButton(buttonColors: ButtonColors) {
    Button(
        onClick = { GiveUserPremium(premium = true) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 45.dp)
            .height((32 * scale).dp)
            .width((150 * scale).dp),
        colors = buttonColors
    ) {
        Text(text = "Premium")
    }
}

@Composable
fun GoogleAccountButton(buttonColors: ButtonColors) {
    val context = LocalContext.current

    Button(
        onClick = { navigateToGoogleAccount(context) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 45.dp)
            .height((32 * scale).dp)
            .width((150 * scale).dp),
        colors = buttonColors
    ) {
        Text(text = "Google Account")
    }
}

@Composable
fun LogoutButton(context: Context, buttonColors: ButtonColors) {
    Button(
        onClick = { signOutAndStartSignInActivity(context) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 45.dp)
            .height((32 * scale).dp)
            .width((150 * scale).dp),
        colors = buttonColors
    ) {
        Text(text = "Logout")
    }
}

private fun GiveUserPremium(premium:Boolean){
    val database = FirebaseDatabase.getInstance("https://r7-mobiiliprojekti-default-rtdb.europe-west1.firebasedatabase.app").reference
    val usersRef = database.child("users")

    val userId = UserAccountManager.googleAccountId
    Log.d("chat message", UserAccountManager.googleAccountId ?: "Google account ID is null")

    userId?.let {
        usersRef.child(it).child("premium").setValue(premium)
    }
}


private fun navigateToGoogleAccount(context: Context) {
    val mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser
    Log.d(TAG, "current user =  $currentUser")
    currentUser?.let {
        // Get the Google account URI
        val googleAccountUri = Uri.parse("https://myaccount.google.com")


        val intent = Intent(Intent.ACTION_VIEW, googleAccountUri)

        // Start the activity to view the Google account
        context.startActivity(intent)
    }
}


private fun signOutAndStartSignInActivity(context: Context) {
    val mAuth = FirebaseAuth.getInstance()
    mAuth.signOut()

    val intent = Intent(context, SignInActivity::class.java)
    context.startActivity(intent)

    (context as? Activity)?.finish()
}