package com.example.afroaroma

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.afroaroma.databinding.FragmentLoginBinding
import com.example.afroaroma.databinding.FragmentSignUpBinding

class signUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
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

        return view
    }


}