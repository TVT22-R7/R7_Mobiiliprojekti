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
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import com.google.firebase.database.FirebaseDatabase

var scale by mutableStateOf(1.0f)
var isDarkMode by mutableStateOf(false)

fun toggleDarkMode() {
    isDarkMode = !isDarkMode
}
fun rescaleUI() {
    scale = 1.4f

}
fun resetScale(){
    scale =1.0f


}
@Composable
fun SettingsScreen() {
    val mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser
    val context = LocalContext.current
    val configuration = context.resources.configuration
    var licensesDialogShown by remember { mutableStateOf(false) }
    when (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_NO -> {} // Night mode is not active, we're using the light theme.
        Configuration.UI_MODE_NIGHT_YES -> {} // Night mode is active, we're using dark theme.
    }

    val darkModeEnabled = isDarkMode

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
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )

            if (licensesDialogShown) {
                LicensesDialog(onDismissRequest = { licensesDialogShown = false  })
            }


            LicensesDropdown()


            HUDSettingsDropdown()

            Spacer(modifier = Modifier.height(20.dp))
            ToggleDarkModeButton()
            Spacer(modifier = Modifier.height(250.dp))
            PremiumButton()
            Spacer(modifier = Modifier.height(20.dp))
            LogoutButton(context = context)
            Spacer(modifier = Modifier.height(18.dp))
            GoogleAccountButton()
        }

    }
}
@Composable
fun LicensesDialog(onDismissRequest: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Licenses") },
        text = {

            Text(text = "This is where you can display the licenses information.")
        },
        confirmButton = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = {

                        openLicenseInNewTab(context)
                        onDismissRequest()
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
                ) {
                    Text(text = "Open in New Tab")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onDismissRequest,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
                ) {
                    Text(text = "Close")
                }
            }
        }
    )
}

private fun openLicenseInNewTab(context: Context) {
    val licenseUrl = ""

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(licenseUrl))
    context.startActivity(intent)
}
@Composable
fun HUDSettingsDropdown() {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Button(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
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
@Composable
fun ToggleDarkModeButton() {
    val context = LocalContext.current
    val isNightMode = isSystemInDarkTheme()

    Button(
        onClick = {
            if (isNightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            (context as? Activity)?.recreate()
        },
        modifier = Modifier
            .height(32.dp)
            .width(150.dp)
    ) {
        Text(text = if (isNightMode) "Light Mode" else "Dark Mode", fontSize = 14.sp)
    }
}
@Composable
fun LicensesDropdown() {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Button(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Licenses")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {

            DropdownMenuItem(onClick = {

                expanded = false
            }) {
                Text(text = "License ")
            }

            DropdownMenuItem(onClick = {

                expanded = false
            }) {
                Text(text = "License ")
            }
        }
    }
}


@Composable
fun LogoutButton(context: Context) {

    Button(
        onClick = { signOutAndStartSignInActivity(context) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 45.dp)
            .height((32 * scale).dp)
            .width((150 * scale).dp)
    ) {
        Text(text = "Logout")
    }
}
@Composable
fun PremiumButton(){

    Button(
        onClick = { GiveUserPremium(premium = true) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 45.dp)
            .height((32 * scale).dp)
            .width((150 * scale).dp)
    ) {
        Text(text = "Premium")
    }
}

@Composable
fun GoogleAccountButton() {
    val context = LocalContext.current

    Button(
        onClick = { navigateToGoogleAccount(context) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 45.dp)
            .height((32 * scale).dp)
            .width((150 * scale).dp)
    ) {
        Text(text = "Google Account")
    }
}
private fun GiveUserPremium(premium:Boolean){
    val database = FirebaseDatabase.getInstance("https://r7-mobiiliprojekti-default-rtdb.europe-west1.firebasedatabase.app").reference
    val usersRef = database.child("users")

    val userId = UserAccountManager.googleAccountId

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
