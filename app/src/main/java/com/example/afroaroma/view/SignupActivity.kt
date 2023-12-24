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
import com.example.afroaroma.controller.AuthController
import com.example.afroaroma.controller.FirestoreController
import com.example.afroaroma.model.User
import com.google.firebase.auth.FirebaseAuth


class SignupActivity : AppCompatActivity() {

    private lateinit var authController: AuthController
    private lateinit var firestoreController: FirestoreController

    private lateinit var textViewHaveAcc: TextView

    private lateinit var auth: FirebaseAuth //deletable

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var passwordConfirmEditText: EditText
    private lateinit var errorMessageText:TextView
    private lateinit var signupButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        authController = AuthController()
        firestoreController = FirestoreController()


        firstNameEditText = findViewById(R.id.firstName)
        lastNameEditText = findViewById(R.id.lastName)
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        passwordConfirmEditText = findViewById(R.id.passwordConfirm)
        signupButton = findViewById(R.id.btnSignUp)
        errorMessageText = findViewById(R.id.textError)


        signupButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val passwordConfirm = passwordConfirmEditText.text.toString()
            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val specialChars = charArrayOf('@', '#', '$', '%', '&')

            var message = ""

            if (email.isNotEmpty() && password.isNotEmpty() && passwordConfirm.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()) {
                    if (matchPassword(password, passwordConfirm)) {
                        if (password.length >= 6) {
                            if (specialChars.any { char -> password.contains(char) }) {
                                try {
                                    signUpWithEmailPassword(email, password, firstName, lastName)
                                    // Add any code to handle successful sign-up, if needed.
                                } catch (e: Exception) {
                                    // Handle exceptions thrown by signUpWithEmailPassword
                                    message = "Sign-up failed. Please try again."
                                }
                            } else {
                                message = "The password does not contain any special character."
                            }
                        } else {
                            message = "Password is too short. It must be at least 6 characters."
                        }
                    } else {
                        message = "Passwords don't match."
                    }
            } else {
                message = "Not all fields are filled."
            }

            errorMessageText.text = message
        }


        textViewHaveAcc = findViewById(R.id.txtHaveAccount)
        textViewHaveAcc.setOnClickListener {
            redirectToLoginActivity()
        }
    }

    private fun signUpWithEmailPassword(email: String, password: String, firstName: String, lastName: String) {
        authController.signUpWithEmailPassword(email, password, firstName, lastName, ::onSignUpComplete)
    }

    private fun onSignUpComplete(user: User?) {
        if (user != null) {
            // Sign-up successful, proceed with Firestore logic
            firestoreController.addUserToDB(user, ::onUserAddedToDB)
        } else {
            // Sign-up failed
            errorMessageText.text = "Authentication failed."
            Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onUserAddedToDB() {
        // Handle any additional logic after the user is added to the Firestore database
        // For example, redirect to the home screen
        checkUserRoles()
    }

    private fun checkUserRoles() {

        val userId = authController.getFirebaseUser()?.uid
        firestoreController.checkUserRoles(userId,
            onSuccess = { isAdmin ->
                if (isAdmin) {
                    redirectToAdminHome()
                } else {
                    redirectToCustomerHome()
                }
            },
            onFailure = {
                // Handle failure, e.g., document doesn't exist
                Log.d(TAG, "No such document")
            }
        )
    }

    private fun matchPassword(password: String, passwordConfirm: String): Boolean {
        return password == passwordConfirm
    }

    private fun redirectToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
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