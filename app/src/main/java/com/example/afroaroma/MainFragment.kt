package com.example.afroaroma

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.afroaroma.controller.AuthController
import com.example.afroaroma.controller.FirestoreController
import com.example.afroaroma.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
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
        binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnSignUpPage.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_signUpFragment)
        }
        binding.btnLoginPage.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_loginFragment)
        }

        return view
    }
}
