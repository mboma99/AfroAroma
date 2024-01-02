package com.example.afroaroma.controller

import android.content.ContentValues
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.afroaroma.R
import com.example.afroaroma.model.AuthModel
import com.example.afroaroma.model.FirestoreModel
import com.example.afroaroma.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding
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

        binding = FragmentSplashBinding.inflate(inflater, container, false)
        val view = binding.root

        Handler().postDelayed({
            val userId = authModel.getFirebaseUser()?.uid
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
        firestoreModel.checkUserRoles(userId,
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