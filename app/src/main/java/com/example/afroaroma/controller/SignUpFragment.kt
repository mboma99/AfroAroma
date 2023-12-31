package com.example.afroaroma.controller

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.afroaroma.R
import com.example.afroaroma.model.AuthModel
import com.example.afroaroma.model.FirestoreModel
import com.example.afroaroma.databinding.FragmentSignUpBinding
import com.example.afroaroma.model.User

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var authModel: AuthModel
    private lateinit var firestoreModel: FirestoreModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authModel = AuthModel()
        firestoreModel = FirestoreModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.txtHaveAccount.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        binding.txtWelcome.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_mainFragment)
        }

        binding.btnSignUp.setOnClickListener {
            // Handle sign-up logic here
            val email = binding.email.text.toString().toLowerCase()
            val password = binding.password.text.toString()
            val passwordConfirm = binding.passwordConfirm.text.toString()
            val firstName = binding.firstName.text.toString().toLowerCase()
            val lastName = binding.lastName.text.toString().toLowerCase()

            val validationMessage = validateSignUpFields(email, password, passwordConfirm, firstName, lastName)

            if (validationMessage.isBlank()) {
                if (isValidPassword(password)) {
                    try {
                        signUpWithEmailPassword(email, password, firstName, lastName)
                        // Add any code to handle successful sign-up, if needed.
                    } catch (e: Exception) {
                        // Handle exceptions thrown by signUpWithEmailPassword
                        // Show an error message
                        binding.textError.text = "Sign-up failed. Please try again."
                    }
                } else {
                    binding.textError.text = "Invalid password format"
                }
            } else {
                binding.textError.text = validationMessage
            }
        }

        return view
    }

    private fun signUpWithEmailPassword(email: String, password: String, firstName: String, lastName: String) {
        authModel.signUpWithEmailPassword(email, password, firstName, lastName, ::onSignUpComplete)
    }

    private fun onSignUpComplete(user: User?) {
        val errorMessageText = binding.textError

        if (user != null) {
            firestoreModel.addUserToDB(user, ::onUserAddedToDB)
        } else {
            val exception = authModel.getLastAuthException()
            val errorMessage = when (exception?.errorCode) {
                "ERROR_WRONG_PASSWORD" -> "Incorrect password"
                "ERROR_USER_NOT_FOUND" -> "Email not recognized"
                else -> "Authentication failed. Please try again."
            }
            errorMessageText.text = errorMessage?: "Authentication failed. Please try again."
            //Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onUserAddedToDB() {
        checkUserRoles()
    }

    private fun checkUserRoles() {
        val userId = authModel.getFirebaseUser()?.uid
        firestoreModel.checkUserRoles(userId,
            onSuccess = { isAdmin ->
                if (isAdmin) {
                    redirectToAdmin()
                } else {
                    redirectToCustomer()
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

    fun redirectToCustomer() {
        view?.let { Navigation.findNavController(it).navigate(R.id.action_signUpFragment_to_customerAccountFragment) }
    }

    fun redirectToAdmin() {
        view?.let { Navigation.findNavController(it).navigate(R.id.action_signUpFragment_to_adminHomeFragment) }
    }
}
