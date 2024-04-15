package com.example.r7mobiiliprojekti



import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.database
import com.google.firebase.firestore.auth.User

class SignInActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 9001
    }
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_login)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser

        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val signInButton = findViewById<Button>(R.id.signInButton)
        signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        Log.d(TAG, "signIn: called")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        Log.d(TAG, "firebaseAuthWithGoogle: called with idToken $idToken")

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userEmail = user?.email

                    // Save the user's data to Firebase
                    user?.let {
                        saveUserDataToDatabase(it.uid, userEmail ?: "")
                        Log.d(TAG, "saves the email $userEmail")
                        UserAccountManager.googleAccountId = it.uid
                    }

                    Toast.makeText(this, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserDataToDatabase(userId: String, email: String, premium: Boolean? = null) {
        val database =
            FirebaseDatabase.getInstance("https://r7-mobiiliprojekti-default-rtdb.europe-west1.firebasedatabase.app").reference
        val usersRef = database.child("users").child(userId)

        // gets current premium status from the database
        usersRef.child("premium").get().addOnSuccessListener { dataSnapshot ->
            val currentPremiumStatus = dataSnapshot.getValue(Boolean::class.java)

            val finalPremiumStatus = premium ?: currentPremiumStatus ?: false

            val userData = mapOf(
                "userId" to userId,
                "email" to email,
                "premium" to finalPremiumStatus
            )
            usersRef.setValue(userData)
            Log.d(TAG, "Data saved: $userData")
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Error getting premium status: $exception")
        }
    }}

