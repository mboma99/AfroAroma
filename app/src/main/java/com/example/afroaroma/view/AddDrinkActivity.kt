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
            val name = nameEditText.text.toString()
            val drinkDescription = descriptionEditText.text.toString()
            val price = priceEditText.text.toString().toDouble()
            val quantity = quantityEditText.text.toString().toLong()
            val newDrink = Drink("", name, drinkDescription, price, quantity )

            showCreatConfirmDialog(newDrink)
            //

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
        progressBar.visibility = View.VISIBLE
        if (selectedImageUri != null) {
           if (drink != null) {
               firestoreController.addDrinkWithPhoto(
                   drink,
                   selectedImageUri!!,
                   onSuccess = {
                       progressBar.visibility = View.GONE
                       Toast.makeText(this, "Drink added successfully", Toast.LENGTH_SHORT).show()
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
        firestoreController.addDrink(
            drink,
            onSuccess = {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Drink added successfully", Toast.LENGTH_SHORT).show()
                redirectToAdminMenu()
            },
            onFailure = { errorMessage ->
                progressBar.visibility = View.GONE
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