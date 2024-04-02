package com.example.r7mobiiliprojekti

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.r7mobiiliprojekti.R
import com.example.r7mobiiliprojekti.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        mAuth = Firebase.auth
        val currentUser = mAuth.currentUser

        // Find views
        val welcomeMessageTextView = view.findViewById<TextView>(R.id.welcome_message)
        val logoutButton = view.findViewById<Button>(R.id.logout_button)

        // Display welcome message with username
        currentUser?.let {
            val userName = it.displayName
            welcomeMessageTextView.text = "Welcome, $userName"
        }

        // Handle logout button click
        logoutButton.setOnClickListener {
            signOutAndStartSignInActivity()
        }

        return view
    }

    private fun signOutAndStartSignInActivity() {
        mAuth.signOut()

        val intent = Intent(requireContext(), SignInActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}
