package com.example.afroaroma.controller

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import com.example.afroaroma.R
import com.example.afroaroma.model.FirestoreModel
import com.example.afroaroma.model.Drink

class AddDrinkFragment : Fragment() {

    private lateinit var firestoreModel: FirestoreModel
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_drink, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestoreModel = FirestoreModel()

        errorTextView = view.findViewById(R.id.errorTextView)
        btnBack = view.findViewById(R.id.btnBack)
        nameEditText = view.findViewById(R.id.editNameEditText)
        descriptionEditText = view.findViewById(R.id.editDescriptionEditText)
        priceEditText = view.findViewById(R.id.editPriceEditText)
        quantityEditText = view.findViewById(R.id.editQuantityEditText)
        titleTextView = view.findViewById(R.id.addTitleTextView)
        btnCreate = view.findViewById(R.id.createButton)
        btnImageUpload = view.findViewById(R.id.btnImageUpload)
        progressBar = view.findViewById(R.id.progressBar)

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
                showCreateConfirmDialog(newDrink)
            } else {
                showError(errorMessages)
            }
        }

        btnImageUpload.setOnClickListener { chooseImage() }

        btnBack.setOnClickListener {
            requireView().let {
                if (it != null && it.isAttachedToWindow) {
                    Navigation.findNavController(it).navigate(R.id.action_addDrinkFragment_to_adminMenuFragment)
                }
            }
        }
    }

    fun showCreateConfirmDialog(selectedDrink: Drink?) {
        selectedDrink?.let {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm, null)
            val dialogBuilder = AlertDialog.Builder(requireActivity())
                .setView(dialogView)
                .setTitle("Confirm you want to add ${it.name}")

            val alertDialog = dialogBuilder.create()

            val btnConfirmEdit: Button = dialogView.findViewById(R.id.btnConfirm)
            val btnCancelEdit: Button = dialogView.findViewById(R.id.btnCancel)
            btnConfirmEdit.setOnClickListener {
                alertDialog.dismiss()
                createDrink(selectedDrink)
            }
            btnCancelEdit.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
        }
    }

    private fun chooseImage() {
        imageActivityResult.launch("image/*")
    }

    private fun createDrink(drink: Drink){
        if (selectedImageUri != null) {
            progressBar.visibility = View.VISIBLE
            if (drink != null) {
                firestoreModel.addDrinkWithPhoto(
                    drink,
                    selectedImageUri!!,
                    onSuccess = {
                        progressBar.visibility = View.GONE
                        //Toast.makeText(this, "Drink with photo added successfully", Toast.LENGTH_SHORT).show()
                        view?.let { Navigation.findNavController(it).navigate(R.id.action_addDrinkFragment_to_adminMenuFragment) }
                    },
                    onFailure = { errorMessage ->
                        progressBar.visibility = View.GONE
                        //Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        } else {
            createDrinkWithOutImage(drink)
        }
    }
    private fun createDrinkWithOutImage (drink: Drink){
        if (drink != null) {
            firestoreModel.addDrink(
                drink,
                onSuccess = {
                    //Toast.makeText(this, "Drink added successfully", Toast.LENGTH_SHORT).show()
                    view?.let { Navigation.findNavController(it).navigate(R.id.action_addDrinkFragment_to_adminMenuFragment) }
                },
                onFailure = { errorMessage ->
                    //Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
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
}
