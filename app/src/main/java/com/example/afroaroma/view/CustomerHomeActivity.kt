package com.example.afroaroma.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.afroaroma.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CustomerHomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var textViewLogout: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_home)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        textViewLogout = findViewById(R.id.textViewLogout)

        textViewLogout.setOnClickListener {
            signOut()
        }

    }

    private fun signOut() {
        Firebase.auth.signOut()
        redirectToMainActivity()
    }

    private fun redirectToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}