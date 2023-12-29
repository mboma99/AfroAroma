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
import com.bumptech.glide.Glide


// EditDrinkActivity.kt
class EditDrinkActivity : AppCompatActivity() {
    private lateinit var firestoreController: FirestoreController
    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var titleTextView: TextView
    private lateinit var btnBack: ImageView
    private lateinit var btnImageUpload: ImageView
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
        setContentView(R.layout.activity_edit_drink)

        firestoreController = FirestoreController()
        // Initialize UI components
        btnBack = findViewById(R.id.btnBack)
        nameEditText = findViewById(R.id.editNameEditText)
        descriptionEditText = findViewById(R.id.editDescriptionEditText)
        priceEditText = findViewById(R.id.editPriceEditText)
        quantityEditText = findViewById(R.id.editQuantityEditText)
        titleTextView = findViewById(R.id.editTitleTextView)
        btnImageUpload = findViewById(R.id.btnImageUpload)
        progressBar = findViewById(R.id.progressBar)

        val selectedDrink = intent.getParcelableExtra<Drink>("selectedDrink")
        //load image
        if (selectedDrink?.imageUrl?.isNotEmpty() == true) {
            loadAndDisplayImage(selectedDrink?.imageUrl)
        }

        if (selectedDrink != null) {
            populateUI(selectedDrink)
        } else {
            Toast.makeText(this, "Error: Selected drink not found", Toast.LENGTH_SHORT).show()
        }

        val saveButton: Button = findViewById(R.id.createButton)
        saveButton.setOnClickListener {
            showEditConfirmationDialog(selectedDrink)
        }

        btnBack.setOnClickListener { redirectToAdminMenu() }

        btnImageUpload.setOnClickListener{ chooseImage() }
    }

    private fun loadAndDisplayImage(imageUrl: String?) {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.photo_placeholder)
            .error(R.drawable.google)
            .into(btnImageUpload)
    }


    private fun chooseImage() {
        // Use the image picker to select an image
        imageActivityResult.launch("image/*")
    }


    private fun populateUI(selectedDrink: Drink?) {
        // Populate UI components with the details of the selected Drink
        if (selectedDrink != null) {
            titleTextView.text = "Edit ${selectedDrink.name}"
            nameEditText.setText("${selectedDrink.name}")
            descriptionEditText.setText("${selectedDrink.drinkDescription}")
            priceEditText.setText("${selectedDrink.price}")
            quantityEditText.setText("${selectedDrink.quantity}")
        } else {
            titleTextView.text = "null"
        }

    }
    private fun showEditConfirmationDialog(selectedDrink: Drink?) {

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Confirm edit of ${selectedDrink?.name}")

        val alertDialog = dialogBuilder.create()

        val btnConfirmEdit: Button = dialogView.findViewById(R.id.btnConfirm)
        val btnCancelEdit: Button = dialogView.findViewById(R.id.btnCancel)
        btnConfirmEdit.setOnClickListener {
            alertDialog.dismiss()
            saveEditedData(selectedDrink)
        }
        btnCancelEdit.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun saveEditedDataWithoutImage(selectedDrink: Drink?) {
        if (selectedDrink != null) {
            selectedDrink.name = nameEditText.text.toString()
            selectedDrink.drinkDescription = descriptionEditText.text.toString()
            selectedDrink.price = priceEditText.text.toString().toDouble()
            selectedDrink.quantity = quantityEditText.text.toString().toLong()


            firestoreController.updateDrink(
                selectedDrink,
                onSuccess = {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Drink updated successfully", Toast.LENGTH_SHORT).show()
                    redirectToAdminMenu()
                },
                onFailure = { errorMessage ->
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun saveEditedData (selectedDrink: Drink?) {
        progressBar.visibility = View.VISIBLE
        if (selectedImageUri != null) {
            if (selectedDrink != null) {
                firestoreController.updateDrinkWithImage(
                    selectedDrink,
                    selectedImageUri!!,
                    onSuccess = {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, "Drink and image updated successfully", Toast.LENGTH_SHORT).show()
                        redirectToAdminMenu()
                    },
                    onFailure = { errorMessage ->
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        } else {
            // No image selected, only update the drink
            saveEditedDataWithoutImage(selectedDrink)
        }
    }

    //Navigation

    private fun redirectToAdminMenu() {
        val intent = Intent(this, MenuAdminActivity::class.java)
        startActivity(intent)
        finish()
    }


}
