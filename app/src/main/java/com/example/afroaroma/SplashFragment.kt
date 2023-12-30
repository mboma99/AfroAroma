package com.example.afroaroma

import android.content.ContentValues
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.afroaroma.controller.AuthController
import com.example.afroaroma.controller.FirestoreController
import com.example.afroaroma.databinding.FragmentLoginBinding
import com.example.afroaroma.databinding.FragmentSignUpBinding
import com.example.afroaroma.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding
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

        binding = FragmentSplashBinding.inflate(inflater, container, false)
        val view = binding.root

        Handler().postDelayed({
            val userId = authController.getFirebaseUser()?.uid
            if (userId != null) {
                navUserRoles(userId)
            } else {
                Navigation.findNavController(view).navigate(R.id.action_splashActivity_to_mainFragment)
            }
        }, 5000)



        return view
    }

    private fun onBackPressed() {

    }


    private fun navUserRoles(userId: String) {
        firestoreController.checkUserRoles(userId,
            onSuccess = { isAdmin ->
                if (isAdmin) {
                    view?.let { Navigation.findNavController(it).navigate(R.id.action_splashActivity_to_adminHomeFragment) }
                } else {
                    view?.let { Navigation.findNavController(it).navigate(R.id.action_splashActivity_to_customerAccountFragment) }
                }
            },
            onFailure = {
                //helps log if document doesn't exist
                Log.d(ContentValues.TAG, "No such document")
            }
        )
    }

}