package com.example.afroaroma.view

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.afroaroma.R
import com.example.afroaroma.admin.controller.AuthController
import com.example.afroaroma.admin.controller.FirestoreController
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {

    private lateinit var googleSignInClient : GoogleSignInAccount
    private lateinit var authController: AuthController
    private lateinit var firestoreController: FirestoreController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authController = AuthController()
        firestoreController = FirestoreController()

    }

    public override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val userId = authController.getFirebaseUser()?.uid
        if (userId != null) {
            navUserRoles(userId)
        }
    }

    private fun navUserRoles(userId: String) {
        firestoreController.checkUserRoles(userId,
            onSuccess = { isAdmin ->
                if (isAdmin) {
                    redirectToAdminHome()
                } else {
                    redirectToCustomerHome()
                }
            },
            onFailure = {
                //helps log if document doesn't exist
                Log.d(ContentValues.TAG, "No such document")
            }
        )
    }

    //Navigation
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
