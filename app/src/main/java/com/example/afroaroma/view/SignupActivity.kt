package com.example.afroaroma.view

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
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
    private lateinit var errorMessageText: TextView
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
            val email = emailEditText.text.toString().toLowerCase()
            val password = passwordEditText.text.toString()
            val passwordConfirm = passwordConfirmEditText.text.toString()
            val firstName = firstNameEditText.text.toString().toLowerCase()
            val lastName = lastNameEditText.text.toString().toLowerCase()

            val validationMessage = validateSignUpFields(email, password, passwordConfirm, firstName, lastName)

            if (validationMessage.isBlank()) {
                if (isValidPassword(password)) {
                    try {
                        signUpWithEmailPassword(email, password, firstName, lastName)
                        // Add any code to handle successful sign-up, if needed.
                    } catch (e: Exception) {
                        // Handle exceptions thrown by signUpWithEmailPassword
                        errorMessageText.text = "Sign-up failed. Please try again."
                    }
                } else {
                    errorMessageText.text = "Invalid password format"
                }
            } else {
                errorMessageText.text = validationMessage
            }
        }

        textViewHaveAcc = findViewById(R.id.txtHaveAccount)
        val originalText: String = textViewHaveAcc.text.toString()
        val spannableString = SpannableString(originalText)
        spannableString.setSpan(UnderlineSpan(), 0, originalText.length, 0)
        textViewHaveAcc.text = spannableString


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

    private fun isValidPassword(password: String): Boolean {
        val digitRegex = Regex(".*\\d.*")
        val uppercaseRegex = Regex(".*[A-Z].*")
        val lowercaseRegex = Regex(".*[a-z].*")
        val specialCharRegex = Regex(".*[!@#\$%^&*()_+\\-=\\[\\]{};':\",.<>?/\\\\|].*")

        return password.length >= 6 &&
                digitRegex.matches(password) &&
                uppercaseRegex.matches(password) &&
                lowercaseRegex.matches(password) &&
                specialCharRegex.matches(password)
    }

    private fun validateSignUpFields(email: String, password: String, passwordConfirm: String, firstName: String, lastName: String): String {
        val missingRequirements = mutableListOf<String>()

        when {
            firstName.isBlank() -> missingRequirements.add("First Name")
            lastName.isBlank() -> missingRequirements.add("Last Name")
            email.isBlank() -> missingRequirements.add("Email")
            password.isBlank() -> missingRequirements.add("Password")
            passwordConfirm.isBlank() -> missingRequirements.add("Confirm Password")
            !matchPassword(password, passwordConfirm) -> missingRequirements.add("Passwords must match")

            else -> {
                val requirementsMessage = passwordRequirements(password)
                if (requirementsMessage != null) {
                    missingRequirements.add(requirementsMessage)
                }
            }
        }

        return if (missingRequirements.isEmpty()) {
            ""
        } else {
            "Missing field(s): ${missingRequirements.joinToString(", ")}"
        }
    }


    private fun matchPassword(password: String, passwordConfirm: String): Boolean {
        return password == passwordConfirm
    }

    private fun passwordRequirements(password: String): String? {
        val uppercaseRegex = Regex("[A-Z]")
        val lowercaseRegex = Regex("[a-z]")
        val digitRegex = Regex("\\d")

        val missingRequirements = mutableListOf<String>()

        if (!uppercaseRegex.containsMatchIn(password)) {
            missingRequirements.add("Uppercase letter")
        }

        if (!lowercaseRegex.containsMatchIn(password)) {
            missingRequirements.add("Lowercase letter")
        }

        if (!digitRegex.containsMatchIn(password)) {
            missingRequirements.add("Digit")
        }

        return if (missingRequirements.isEmpty()) {
            null
        } else {
            "Password is missing: ${missingRequirements.joinToString(", ")}"
        }
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
