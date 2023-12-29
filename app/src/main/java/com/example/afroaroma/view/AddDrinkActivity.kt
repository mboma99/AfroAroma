package com.example.afroaroma.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
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
    private lateinit var btnImageUpload: ImageView
    private lateinit var btnCreate: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var errorTextView: TextView
    private var selectedImageUri: Uri? = null

    private val imageActivityResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                btnImageUpload.setImageURI(it)
                selectedImageUri = it
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_drink)

        firestoreController = FirestoreController()
        // Initialize UI components
        errorTextView = findViewById(R.id.errorTextView) // Replace R.id.errorTextView with the actual ID of your TextView
        btnBack = findViewById(R.id.btnBack)
        nameEditText = findViewById(R.id.editNameEditText)
        descriptionEditText = findViewById(R.id.editDescriptionEditText)
        priceEditText = findViewById(R.id.editPriceEditText)
        quantityEditText = findViewById(R.id.editQuantityEditText)
        titleTextView = findViewById(R.id.addTitleTextView)
        btnCreate= findViewById(R.id.createButton)
        btnImageUpload = findViewById(R.id.btnImageUpload)
        progressBar = findViewById(R.id.progressBar)

        btnCreate.setOnClickListener {
            val name = sanitizeInput(nameEditText.text.toString())
            val drinkDescription = sanitizeInput(descriptionEditText.text.toString())
            val priceString = priceEditText.text.toString()
            val quantityString = quantityEditText.text.toString()

            val errorMessages = validateFields(name, drinkDescription, priceString, quantityString)

            if (errorMessages.isEmpty()) {
                val price = priceString.toDouble()
                val quantity = quantityString.toLong()
                val newDrink = Drink("", name, drinkDescription, price, quantity)
                showCreatConfirmDialog(newDrink)
            } else {
                showError(errorMessages)
            }
        }
        btnImageUpload.setOnClickListener{ chooseImage() }

        btnBack.setOnClickListener { redirectToAdminMenu() }
    }


    private fun showCreatConfirmDialog(selectedDrink: Drink?) {

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Confirm you want to add ${selectedDrink?.name}")

        val alertDialog = dialogBuilder.create()

        val btnConfirmEdit: Button = dialogView.findViewById(R.id.btnConfirm)
        val btnCancelEdit: Button = dialogView.findViewById(R.id.btnCancel)
        btnConfirmEdit.setOnClickListener {
            alertDialog.dismiss()
            if (selectedDrink != null) {
                createDrink(selectedDrink)
            }
        }
        btnCancelEdit.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun chooseImage() {
        // Use the image picker to select an image
        imageActivityResult.launch("image/*")
    }

    private fun createDrink(drink: Drink){
        if (selectedImageUri != null) {
            progressBar.visibility = View.VISIBLE
           if (drink != null) {
               firestoreController.addDrinkWithPhoto(
                   drink,
                   selectedImageUri!!,
                   onSuccess = {
                       progressBar.visibility = View.GONE
                       Toast.makeText(this, "Drink with photo added successfully", Toast.LENGTH_SHORT).show()
                       redirectToAdminMenu()
                   },
                   onFailure = { errorMessage ->
                       progressBar.visibility = View.GONE
                       Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                   }
               )
           }
        } else {
            createDrinkWithOutImage(drink)
        }
    }
    private fun createDrinkWithOutImage (drink: Drink){
        if (drink != null) {
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
    }

    private fun validateFields(name: String, description: String, priceString: String, quantityString: String): String {
        val missingFields = mutableListOf<String>()

        if (name.isBlank()) {
            missingFields.add("Name")
        }

        if (description.isBlank()) {
            missingFields.add("Description")
        }

        if (priceString.isBlank()) {
            missingFields.add("Price")
        } else if (!isValidPrice(priceString)) {
            return "Invalid price format. Please use 0.00 format."
        }

        if (quantityString.isBlank()) {
            missingFields.add("Quantity")
        } else if (!isValidQuantity(quantityString)) {
            return "Invalid quantity format. Please enter a valid number."
        }

        return if (missingFields.isEmpty()) {
            ""
        } else {
            "Missing field(s): ${missingFields.joinToString(", ")}"
        }
    }

    private fun sanitizeInput(input: String): String {
        return input.replace(Regex("[.#\$\\\\[\\\\]/]"), "")
    }

    private fun isValidPrice(price: String): Boolean {
        val regex = Regex("^\\d+(\\.\\d{1,2})?$")
        return regex.matches(price)
    }

    private fun isValidQuantity(quantity: String): Boolean {
        return quantity.matches("\\d+".toRegex())
    }

    private fun showError(message: String) {
        errorTextView.text = message
        errorTextView.visibility = View.VISIBLE
    }



    private fun redirectToAdminMenu() {
        val intent = Intent(this, MenuAdminActivity::class.java)
        startActivity(intent)
        finish()
    }


}