package com.example.afroaroma

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.afroaroma.controller.AuthController
import com.example.afroaroma.controller.FirestoreController
import com.example.afroaroma.databinding.FragmentAdminHomeBinding
import com.example.afroaroma.databinding.FragmentLoginBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class AdminHomeFragment : Fragment() {

    private lateinit var binding: FragmentAdminHomeBinding
    private lateinit var authController: AuthController
    private lateinit var firestoreController: FirestoreController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        authController = AuthController()
        firestoreController = FirestoreController()

        binding = FragmentAdminHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.textViewLogout.setOnClickListener {
            signOut()
        }

        val currentUser = authController.getCurrentUserId()
        if (currentUser != null) {
            firestoreController.getUserFirstName(currentUser,
                onSuccess = { firstName ->
                    binding.txtWelcome.text = "Welcome back, ${firstName}"
                },
                onFailure = {
                    // Handle failure, e.g., document doesn't exist
                }
            )

        }
        binding.viewMenu.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_adminHomeFragment_to_adminMenuFragment)
        }

        binding.viewOrders.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_adminHomeFragment_to_liveOrdersFragment)
        }

        binding.viewArchive.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_adminHomeFragment_to_archiveOrdersFragment)
        }

        return view

    }


    private fun signOut() {
        Firebase.auth.signOut()
        view?.let { Navigation.findNavController(it).navigate(R.id.action_adminHomeFragment_to_mainFragment) }
    }

}