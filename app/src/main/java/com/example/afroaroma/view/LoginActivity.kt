package com.example.afroaroma.view

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.afroaroma.R
import com.example.afroaroma.admin.controller.AuthController
import com.example.afroaroma.admin.controller.FirestoreController
import com.example.afroaroma.model.User

class LoginActivity : AppCompatActivity() {

    private lateinit var authController: AuthController
    private lateinit var firestoreController: FirestoreController
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var textViewNoAccount: TextView
    private lateinit var errorMessageText:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authController = AuthController()
        firestoreController = FirestoreController()
        // Initialize Firebase Auth should be able to remove

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
        val userId = authController.getFirebaseUser()?.uid
        if (userId != null) {
            navUserRoles(userId)
        }
    }
    private fun redirectToSignupActivity() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun signInWithEmailPassword(email: String, password: String) {
        authController.signInWithEmailPassword(email, password, ::onSignInComplete)
    }

    private fun onSignInComplete(user: User?) {
        if (user != null) {
            // Authentication successful
            firestoreController.checkUserRole(user, ::onUserRoleChecked)
        } else {
            // Authentication failed
            errorMessageText.text = "Incorrect password"
            Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onUserRoleChecked(isAdmin: Boolean) {
        if (isAdmin) {
            redirectToAdminHome()
        } else {
            redirectToCustomerHome()
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
                Log.d(TAG, "No such document")
            }
        )
    }


    //navigation
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