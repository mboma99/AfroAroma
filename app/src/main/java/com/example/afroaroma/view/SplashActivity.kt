package com.example.afroaroma.view

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.example.afroaroma.R
import com.example.afroaroma.controller.AuthController
import com.example.afroaroma.controller.FirestoreController

class SplashActivity : AppCompatActivity() {

    private lateinit var authController: AuthController
    private lateinit var firestoreController: FirestoreController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        authController = AuthController()
        firestoreController = FirestoreController()

        Handler().postDelayed({
            val userId = authController.getFirebaseUser()?.uid
            if (userId != null) {
                navUserRoles(userId)
            } else {
                redirectToMain()
            }
        }, 5000)
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
    private fun redirectToMain(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
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
}