package com.example.afroaroma.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.afroaroma.R
import com.example.afroaroma.controller.AuthController
import com.example.afroaroma.controller.DrinkAdapter
import com.example.afroaroma.controller.FirestoreController
import com.example.afroaroma.model.Drink

class MenuAdminActivity : AppCompatActivity() {


    private lateinit var authController: AuthController
    private lateinit var firestoreController: FirestoreController
    private lateinit var menuListView: ListView
    private lateinit var drinkAdapter: DrinkAdapter
    private lateinit var btnBack: ImageView
    private lateinit var btnEdit: Button
    private lateinit var btnAdd: Button
    private lateinit var btnDelete:Button
    private var selectedDrink: Drink? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_admin)

        btnBack = findViewById(R.id.btnBack)
        btnEdit = findViewById(R.id.btnEdit)
        btnAdd = findViewById(R.id.btnAdd)
        btnDelete = findViewById(R.id.btnDelete)
        authController = AuthController()
        firestoreController = FirestoreController()
        menuListView = findViewById(R.id.menuListView)





        val currentUser = authController.getCurrentUserId()
        if (currentUser != null) {
            firestoreController.getMenu(
                onSuccess = { menu ->
                    // Create a DrinkAdapter to display the menu in the ListView
                    drinkAdapter = DrinkAdapter(this, menu)

                    // Set the DrinkAdapter on the ListView
                    menuListView.adapter = drinkAdapter
                    menuListView.choiceMode = ListView.CHOICE_MODE_SINGLE
                    menuListView.selector = resources.getDrawable(R.drawable.item_drink_selector)
                },
                onFailure = {
                    // Handle the failure case
                    println("Failed to get the menu.")
                }
            )
        }


        btnEdit.setOnClickListener {
            // Check if an item is selected
            if (selectedDrink != null) {
                redirectToEditMenu()
            } else {
                Toast.makeText(this, "Please select an item to edit", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle item click events if needed
        menuListView.setOnItemClickListener { _, _, position, _ ->
            selectedDrink = drinkAdapter.getItem(position) as Drink
            //Toast.makeText(this, "Selected: ${selectedDrink!!.name}", Toast.LENGTH_SHORT).show()
        }


        btnDelete.setOnClickListener { showDeleteConfirmationDialog(selectedDrink) }
        btnAdd.setOnClickListener { redirectToAddDrink() }
        btnBack.setOnClickListener { redirectToAdminHome() }


    }

    private fun showDeleteConfirmationDialog(selectedDrink: Drink?) {

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Confirm Deletion of ${selectedDrink?.name}")

        val alertDialog = dialogBuilder.create()

        val btnConfirmDelete: Button = dialogView.findViewById(R.id.btnConfirm)
        val btnCancelDelete: Button = dialogView.findViewById(R.id.btnCancel)
        btnConfirmDelete.setOnClickListener {
            deleteDrink(selectedDrink)
            alertDialog.dismiss()
        }
        btnCancelDelete.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun deleteDrink(selectedDrink: Drink?) {
        if (selectedDrink != null) {

            firestoreController.deleteDrink(
                selectedDrink,
                onSuccess = {
                    Toast.makeText(this, "Drink deleted successfully", Toast.LENGTH_SHORT).show()
                    redirectToAdminMenu()
                },
                onFailure = { errorMessage ->
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    //navigation
    private fun redirectToAdminHome() {
        val intent = Intent(this, AdminHomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun redirectToAdminMenu() {
        val intent = Intent(this, MenuAdminActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun redirectToAddDrink() {
        val intent = Intent(this, AddDrinkActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun redirectToEditMenu() {
        val intent = Intent(this, EditDrinkActivity::class.java).also {
            it.putExtra("selectedDrink",selectedDrink)
        }
        startActivity(intent)
        finish()
    }


}
