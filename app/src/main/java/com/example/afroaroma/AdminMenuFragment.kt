package com.example.afroaroma

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import com.example.afroaroma.controller.AuthController
import com.example.afroaroma.controller.DrinkAdapter
import com.example.afroaroma.controller.FirestoreController
import com.example.afroaroma.databinding.FragmentAdminMenuBinding
import com.example.afroaroma.model.Drink


class AdminMenuFragment : Fragment() {

    private lateinit var binding: FragmentAdminMenuBinding
    private lateinit var authController: AuthController
    private lateinit var firestoreController: FirestoreController
    private lateinit var menuListView: ListView
    private lateinit var drinkAdapter: DrinkAdapter
    private var selectedDrink: Drink? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authController = AuthController()
        firestoreController = FirestoreController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminMenuBinding.inflate(inflater, container, false)
        val view = binding.root


        // Initialize the ListView from the layout
        menuListView = binding.menuListView

        // Initialize your authController, firestoreController, or any other necessary components.

        val currentUser = authController.getCurrentUserId()
        if (currentUser != null) {
            firestoreController.getMenu(
                onSuccess = { menu ->
                    // Create a DrinkAdapter to display the menu in the ListView
                    drinkAdapter = DrinkAdapter(requireContext(), menu)

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

        menuListView.setOnItemClickListener { _, _, position, _ ->
            selectedDrink = drinkAdapter.getItem(position) as Drink
            // Toast.makeText(requireContext(), "Selected: ${selectedDrink!!.name}", Toast.LENGTH_SHORT).show()
        }

        binding.btnBack.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_adminMenuFragment_to_adminHomeFragment)
        }

        binding.btnEdit.setOnClickListener {
                val bundle = Bundle().apply {
                    putParcelable("selectedDrink", selectedDrink)
                }
                Navigation.findNavController(view).navigate(R.id.action_adminMenuFragment_to_editIDrinkFragment, bundle)
        }

        binding.btnAdd.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_adminMenuFragment_to_addDrinkFragment)
        }

        binding.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog(selectedDrink)
        }

        return view
    }

    private fun showDeleteConfirmationDialog(selectedDrink: Drink?) {

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
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
                    //Toast.makeText(this, "Drink deleted successfully", Toast.LENGTH_SHORT).show()
                    refreshContents()
                },
                onFailure = { errorMessage ->
                    //Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun refreshContents() {
        val currentUser = authController.getCurrentUserId()
        if (currentUser != null) {
            firestoreController.getMenu(
                onSuccess = { menu ->
                    // Update the data in the DrinkAdapter and notify the ListView of the changes
                    drinkAdapter.updateData(menu)
                },
                onFailure = {
                    // Handle the failure case
                    println("Failed to get the menu.")
                }
            )
        }
    }

}