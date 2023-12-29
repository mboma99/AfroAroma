package com.example.afroaroma.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.afroaroma.R
import com.example.afroaroma.controller.AuthController
import com.example.afroaroma.controller.FirestoreController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var authController: AuthController
    private lateinit var firestoreController: FirestoreController
    private lateinit var textViewUserEmail: TextView
    private lateinit var textViewLogout: TextView
    private lateinit var btnMenu: Button
    private lateinit var btnOrders: Button
    private lateinit var btnPromotions: Button
    private lateinit var btnFeedback: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MenuActivity", "onCreate started")
        setContentView(R.layout.activity_admin_home)



        authController = AuthController()
        firestoreController = FirestoreController()
        textViewUserEmail = findViewById(R.id.txtWelcome)
        textViewLogout = findViewById(R.id.textViewLogout)

        btnMenu = findViewById(R.id.viewMenu)
        btnOrders = findViewById(R.id.viewOrders)
        btnPromotions = findViewById(R.id.viewPromotions)
        btnFeedback = findViewById(R.id.viewFeedback)



        val currentUser = authController.getCurrentUserId()
        if (currentUser != null) {
            firestoreController.getUserFirstName(currentUser,
                    onSuccess = { firstName ->
                        textViewUserEmail.text = "Welcome back, ${firstName}"
                    },
                    onFailure = {
                        // Handle failure, e.g., document doesn't exist
                    }
                )

        }
       //listeners
        textViewLogout.setOnClickListener { signOut() }

        btnMenu.setOnClickListener { redirectToMenuAdmin() }
        btnOrders.setOnClickListener{redirectToOrders()}

    }


    //navigation

    private fun redirectToMenuAdmin() {
        val intent = Intent(this, MenuAdminActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun redirectToOrders() {
        val intent = Intent(this, ViewOrderActivity::class.java)
        startActivity(intent)
        finish()
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