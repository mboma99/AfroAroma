package com.example.afroaroma.view

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.example.afroaroma.R
import com.example.afroaroma.controller.AuthController
import com.example.afroaroma.controller.FirestoreController
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var authController: AuthController
    private lateinit var firestoreController: FirestoreController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authController = AuthController()
        firestoreController = FirestoreController()

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
