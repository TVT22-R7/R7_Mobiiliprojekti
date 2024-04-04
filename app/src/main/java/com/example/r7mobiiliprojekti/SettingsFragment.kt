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

    val darkModeEnabled = isDarkMode

    Surface(color = if (darkModeEnabled) Color.DarkGray else Color.White) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Settings",color = if(darkModeEnabled) Color.White else Color.Black,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp),

            )
            ChangeColor()
            RescaleButton()
            Spacer(modifier = Modifier.height(20.dp))
            Spacer(modifier = Modifier.height(300.dp))

            LogoutButton(context = context)
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
fun ChangeColor() {
    Button(
        onClick = { toggleDarkMode(); isDarkMode },
        modifier = Modifier.height((32 * scale).dp)
            .width((150 * scale).dp)
    ) {
        Text(text = if (isDarkMode) "Light mode" else "Dark mode")
    }
}
@Composable
fun RescaleButton() {
    Column {
        Button(
            onClick = { rescaleUI() },
            modifier = Modifier.padding(8.dp)
                .height((32 * scale).dp)
                .width((150 * scale).dp)
        ) {
            Text(text = "Increase Size")
        }

        Button(
            onClick = { resetScale() },
            modifier = Modifier.padding(8.dp)
                .height((32 * scale).dp)
                .width((150 * scale).dp)
        ) {
            Text(text = "Reset Size")
        }
    }
}


private fun signOutAndStartSignInActivity(context: Context) {
    val mAuth = FirebaseAuth.getInstance()
    mAuth.signOut()

    val intent = Intent(context, SignInActivity::class.java)
    context.startActivity(intent)

    (context as? Activity)?.finish()
}