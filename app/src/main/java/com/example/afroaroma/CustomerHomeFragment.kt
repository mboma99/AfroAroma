package com.example.afroaroma

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.afroaroma.controller.AuthController
import com.example.afroaroma.controller.FirestoreController
import com.example.afroaroma.databinding.FragmentAdminMenuBinding
import com.example.afroaroma.databinding.FragmentCustomerHomeBinding


class CustomerHomeFragment : Fragment() {

    private lateinit var binding: FragmentCustomerHomeBinding
    private lateinit var authController: AuthController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authController = AuthController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCustomerHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.textViewLogout.setOnClickListener { signOut() }

        return view
    }

    private fun signOut() {
        authController.signOut()
        view?.let { Navigation.findNavController(it).navigate(R.id.action_customerAccountFragment_to_mainFragment) }
    }
}
