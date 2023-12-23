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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore


class SignupActivity : AppCompatActivity() {

    private lateinit var textViewHaveAcc: TextView
    private lateinit var auth: FirebaseAuth
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
    private fun signUpWithEmailPassword(email: String, password: String, firstName: String,lastName: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    auth.currentUser
                    val user: FirebaseUser? = auth.currentUser
                    addUserToDB(firstName, lastName, email)
                    Toast.makeText(
                        baseContext,
                        "Authentication passed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun addUserToDB(firstName: String, lastName: String, email: String) {
        // Access a Cloud Firestore instance from your Activity
        val db = Firebase.firestore
        val userId = (auth.currentUser)?.uid // Get the UID of the current user

        if (userId != null) {
            val userRef = db.collection("Users").document(userId)
            val user = hashMapOf<String, Any?>()
            user.put("FirstName", firstName)
            user.put("LastName", lastName)
            user.put("EmailAddress", email)
            user.put("isAdmin", 0)

            userRef.set(user)
                .addOnSuccessListener {
                    loginDirect()
                    Log.d(TAG, "DocumentSnapshot added with ID: $userId")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        } else {
            Log.e(TAG, "User ID is null.")
        }
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
}