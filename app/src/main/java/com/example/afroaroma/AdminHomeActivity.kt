package com.example.afroaroma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var textViewUserEmail: TextView
    private lateinit var textViewLogout: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Get the current user
        val currentUser = auth.currentUser

        // Initialize TextView
        textViewUserEmail = findViewById(R.id.txtWelcome)

        // Check if the user is signed in
        if (currentUser != null) {
            // Get the user's email
            val userEmail = currentUser.email

            // Set the user's email on the TextView
            textViewUserEmail.text = "Welcomeback, $userEmail"
        }

        textViewLogout = findViewById(R.id.textViewLogout)

        textViewLogout.setOnClickListener {
            signOut()
            redirectToMainActivity()
        }

    }



    private fun redirectToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun signOut() {
        Firebase.auth.signOut()
    }
}