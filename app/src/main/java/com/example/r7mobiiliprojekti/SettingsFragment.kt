package com.example.r7mobiiliprojekti

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth


@Composable
fun SettingsScreen() {
    val mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Settings",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
        Button(
            onClick = { signOutAndStartSignInActivity(context) }
        ) {
            Text(text = "Logout")
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