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
import com.example.afroaroma.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
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
