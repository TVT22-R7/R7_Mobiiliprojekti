package com.example.r7mobiiliprojekti

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.layout.Row
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem

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
    var licensesDialogShown by remember { mutableStateOf(false) }

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
            Spacer(modifier = Modifier.height(300.dp))
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
            // Text content for the licenses dialog
            Text(text = "This is where you can display the licenses information.")
        },
        confirmButton = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = {
                        // Open license information in a new tab or browser window
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
    // Replace this URL with the actual URL of your license information
    val licenseUrl = "https://example.com/licenses"

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
fun LicensesDropdown() {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Button(
            onClick = { expanded = !expanded }, // Toggle the expanded state
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Licenses")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            // Add items for licenses here if needed
            // Example:
            DropdownMenuItem(onClick = {
                // Handle license item click
                expanded = false
            }) {
                Text(text = "License ")
            }

            DropdownMenuItem(onClick = {
                // Handle license item click
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

private fun navigateToGoogleAccount(context: Context) {
    val mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser
    currentUser?.let {
        // Get the Google account URI
        val googleAccountUri = Uri.parse("https://myaccount.google.com")

        // Create an intent to view the Google account URI
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
