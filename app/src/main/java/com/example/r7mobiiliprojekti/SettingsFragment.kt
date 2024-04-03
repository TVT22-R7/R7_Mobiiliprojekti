package com.example.r7mobiiliprojekti

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
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth


@Composable
fun SettingsScreen() {
    val mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser
    val context = LocalContext.current

    Surface(color = Color.White) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp)) // Spacer for top padding
            Text(
                text = "Settings",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
            ChangeColor()

            Spacer(modifier = Modifier.height(400.dp))// Spacer for separation
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
    ) {
        Text(text = "Logout")
    }
}

@Composable
fun ChangeColor() {
    Button(
        onClick = { ChangeColorForApp() },

    ) {
        Text(text = "Dark mode")
    }
}
    fun ChangeColorForApp() {

    }

private fun signOutAndStartSignInActivity(context: Context) {
    val mAuth = FirebaseAuth.getInstance()
    mAuth.signOut()

    val intent = Intent(context, SignInActivity::class.java)
    context.startActivity(intent)

    (context as? Activity)?.finish()
}