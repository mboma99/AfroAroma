package com.example.afroaroma

import android.content.ContentValues
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.afroaroma.controller.AuthController
import com.example.afroaroma.controller.FirestoreController
import com.example.afroaroma.databinding.FragmentLoginBinding
import com.example.afroaroma.databinding.FragmentMainBinding
import com.example.afroaroma.model.User


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var authController: AuthController
    private lateinit var firestoreController: FirestoreController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authController = AuthController()
        firestoreController = FirestoreController()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root


        // Use the safe call operator ?. to avoid NullPointerException
        val errorMessageText = binding.textError
        val textViewNoAccount = binding.txtNoAccount

        val originalText: String = textViewNoAccount.text.toString()
        val spannableString = SpannableString(originalText)
        spannableString.setSpan(UnderlineSpan(), 0, originalText.length, 0)
        textViewNoAccount.text = spannableString

        binding.txtNoAccount.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.btnLoginPage.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signInWithEmailPassword(email, password)
            } else {
                errorMessageText.text = "Fill in all required field(s)"
            }
        }
        val userId = authController.getFirebaseUser()?.uid
        if (userId != null) {
            navUserRoles(userId)
        }

        return view
    }

    public override fun onStart() {
        super.onStart()
    }

    private fun signInWithEmailPassword(email: String, password: String) {
        authController.signInWithEmailPassword(email, password, ::onSignInComplete)
    }

    private fun onSignInComplete(user: User?) {
        val errorMessageText = binding.textError // Use the same error message text view

        if (user != null) {
            firestoreController.checkUserRole(user, ::onUserRoleChecked)
        } else {
            val exception = authController.getLastAuthException()
            val errorMessage = when (exception?.errorCode) {
                "ERROR_WRONG_PASSWORD" -> "Incorrect password"
                "ERROR_USER_NOT_FOUND" -> "Email not recognized"
                else -> "Authentication failed. Please try again."
            }
            errorMessageText.text = errorMessage ?: "Authentication failed. Please try again."
        }
    }



    private fun onUserRoleChecked(isAdmin: Boolean) {
        val navController = view?.let { Navigation.findNavController(it) }

        if (isAdmin) {
            navController?.navigate(R.id.action_loginFragment_to_adminHomeFragment)
        } else {
            navController?.navigate(R.id.action_loginFragment_to_customerAccountFragment)
        }
    }

    private fun navUserRoles(userId: String) {
        val navController = view?.let { Navigation.findNavController(it) }
        firestoreController.checkUserRoles(userId,
            onSuccess = { isAdmin ->
                if (isAdmin) {
                    navController?.navigate(R.id.action_loginFragment_to_adminHomeFragment)
                } else {
                    navController?.navigate(R.id.action_loginFragment_to_customerAccountFragment)
                }
            },
            onFailure = {
                //helps log if document doesn't exist
                Log.d(ContentValues.TAG, "No such document")
            }
        )
    }
}

