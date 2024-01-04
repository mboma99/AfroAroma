package com.example.afroaroma.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.afroaroma.databinding.FragmentCustomerMenuBinding
import com.example.afroaroma.model.AuthModel
import com.example.afroaroma.R
import androidx.fragment.app.Fragment


class CustomerMenuFragment : Fragment(R.layout.fragment_customer_home) {

    private lateinit var binding: FragmentCustomerMenuBinding
    private lateinit var authModel: AuthModel

        override fun onCreate(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            binding = FragmentCustomerMenuBinding.inflate(inflater)
            return binding.root
            val button = findViewById<Button>(R.id.buttonKilmanajaroRoast)


    }






    }