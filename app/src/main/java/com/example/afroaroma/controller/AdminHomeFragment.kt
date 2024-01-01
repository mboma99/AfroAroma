package com.example.afroaroma.controller

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.afroaroma.R
import com.example.afroaroma.model.AuthModel
import com.example.afroaroma.model.FirestoreModel
import com.example.afroaroma.databinding.FragmentAdminHomeBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class AdminHomeFragment : Fragment() {

    private lateinit var binding: FragmentAdminHomeBinding
    private lateinit var authModel: AuthModel
    private lateinit var firestoreModel: FirestoreModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        authModel = AuthModel()
        firestoreModel = FirestoreModel()

        binding = FragmentAdminHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.textViewLogout.setOnClickListener {
            signOut()
        }

        val currentUser = authModel.getCurrentUserId()
        if (currentUser != null) {
            firestoreModel.getUserFirstName(currentUser,
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
        binding.viewFeedback.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_adminHomeFragment_to_feedbackFragment)
        }

        return view

    }


    private fun signOut() {
        Firebase.auth.signOut()
        view?.let { Navigation.findNavController(it).navigate(R.id.action_adminHomeFragment_to_mainFragment) }
    }

}