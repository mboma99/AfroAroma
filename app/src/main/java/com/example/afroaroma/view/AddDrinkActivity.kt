package com.example.afroaroma.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.afroaroma.R
import com.example.afroaroma.controller.FirestoreController
import com.example.afroaroma.model.Drink

class AddDrinkActivity : AppCompatActivity() {

    private lateinit var firestoreController: FirestoreController
    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var titleTextView: TextView
    private lateinit var btnBack: ImageView
    private lateinit var btnCreate: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_drink)

        firestoreController = FirestoreController()
        // Initialize UI components
        btnBack = findViewById(R.id.btnBack)
        nameEditText = findViewById(R.id.editNameEditText)
        descriptionEditText = findViewById(R.id.editDescriptionEditText)
        priceEditText = findViewById(R.id.editPriceEditText)
        quantityEditText = findViewById(R.id.editQuantityEditText)
        titleTextView = findViewById(R.id.editTitleTextView)
        btnCreate= findViewById(R.id.createButton)

        btnCreate.setOnClickListener {
            val name = nameEditText.text.toString()
            val drinkDescription = descriptionEditText.text.toString()
            val price = priceEditText.text.toString().toDouble()
            val quantity = quantityEditText.text.toString().toLong()
            val newDrink = Drink("", name, drinkDescription, price, quantity )
            createDrink(newDrink)
            //

        }

        btnBack.setOnClickListener { redirectToAdminMenu() }
    }

    private fun createDrink (drink: Drink){
        firestoreController.addDrink(
            drink,
            onSuccess = {
                Toast.makeText(this, "Drink added successfully", Toast.LENGTH_SHORT).show()
                redirectToAdminMenu()
            },
            onFailure = { errorMessage ->
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun redirectToAdminMenu() {
        val intent = Intent(this, MenuAdminActivity::class.java)
        startActivity(intent)
        finish()
    }


}