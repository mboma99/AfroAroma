package com.example.afroaroma

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInAccount

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
    }

    public override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            loginDirect()
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
                Log.d(ContentValues.TAG, "No such document")
            }
        }?.addOnFailureListener { e ->
            // Handle exceptions or failures
            Log.e(ContentValues.TAG, "Error getting document", e)
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
    fun redirectToSignUp(view: View) {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Go to login page function
    fun redirectToLoginPage(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
