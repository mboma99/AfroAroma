package com.example.afroaroma

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
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

        val db = Firebase.firestore
        val userId = (auth.currentUser)?.uid // Get the UID of the current user

        val userRef = userId?.let { db.document("Users/${userId}") }

        // Get the current user
        val currentUser = auth.currentUser

        // Initialize TextView
        textViewUserEmail = findViewById(R.id.txtWelcome)

        // Check if the user is signed in
        if (currentUser != null) {
            userRef?.get()?.addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val firstName = documentSnapshot.getString("FirstName")
                    textViewUserEmail.text = "Welcome back, $firstName"
                } else {
                    // Document doesn't exist, handle accordingly
                    Log.d(ContentValues.TAG, "No such document")
                }
            }?.addOnFailureListener { e ->
                // Handle exceptions or failures
                Log.e(ContentValues.TAG, "Error getting document", e)
            }
        }

        textViewLogout = findViewById(R.id.textViewLogout)

        textViewLogout.setOnClickListener {
            signOut()

        }

    }



    private fun redirectToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun signOut() {
        Firebase.auth.signOut()
        redirectToMainActivity()
    }

}