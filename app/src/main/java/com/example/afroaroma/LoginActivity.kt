package com.example.afroaroma

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class LoginActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var textViewNoAccount: TextView
    private lateinit var errorMessageText:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.btnLoginPage)
        errorMessageText = findViewById(R.id.textError)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            var message = ""
            if (email.isNotEmpty() && password.isNotEmpty()) {
                signInWithEmailPassword(email, password)
            } else {
                message = "Enter Password"
                errorMessageText.text = message
            }
        }
        textViewNoAccount= findViewById(R.id.txtNoAccount)
        textViewNoAccount.setOnClickListener {
            redirectToSignupActivity()
        }
    }

    public override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            redirectToAdminHome()
        }
    }
    private fun redirectToSignupActivity() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun signInWithEmailPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Toast.makeText(
                        baseContext,
                        "Authentication passed!!",
                        Toast.LENGTH_SHORT,
                    ).show()
                    loginDirect()
                } else {
                    errorMessageText.text = "Incorrect password"
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun loginDirect() {
        val db = Firebase.firestore
        val userId = (auth.currentUser)?.uid // Get the UID of the current user

        val userRef = userId?.let { db.collection("Users").document(it) }
        userRef?.get()?.addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val isAdmin = documentSnapshot.getLong("isAdmin")

                if (isAdmin != null && isAdmin == 1L) {
                    // The user is an admin, perform the appropriate action
                    // For example, redirect to the admin dashboard
                    redirectToAdminHome()
                } else {
                    // The user is not an admin, perform the appropriate action
                    // For example, redirect to the regular user dashboard
                    redirectToCustomerHome()
                }
            } else {
                // Document doesn't exist, handle accordingly
                Log.d(TAG, "No such document")
            }
        }?.addOnFailureListener { e ->
            // Handle exceptions or failures
            Log.e(TAG, "Error getting document", e)
        }
    }


    private fun redirectToAdminHome() {
        val intent = Intent(this, AdminHomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun redirectToCustomerHome() {
        val intent = Intent(this, CustomerHomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun redirectToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


}