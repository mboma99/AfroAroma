package com.example.afroaroma.controller

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.navigation.Navigation
import com.example.afroaroma.R
import com.example.afroaroma.model.DrinkAdapterOrders
import com.example.afroaroma.model.FirestoreModel
import com.example.afroaroma.databinding.FragmentArchivedOrderViewBinding
import com.example.afroaroma.model.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ArchivedOrderViewFragment : Fragment() {

    private lateinit var binding: FragmentArchivedOrderViewBinding
    private lateinit var archivedOrderListView: ListView
    private lateinit var firestoreModel: FirestoreModel
    private var selectedOrder: Order? = null
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArchivedOrderViewBinding.inflate(inflater, container, false)
        val view = binding.root

        archivedOrderListView = view.findViewById(R.id.archivedOrderListView)
        arguments?.let {
            selectedOrder = it.getParcelable("selectedOrder")
            if (selectedOrder == null) {
                Log.e("ArchivedOrderViewFragment", "Selected Order is null.")
            } else {
                Log.d("ArchivedOrderViewFragment", "Selected Order ID: ${selectedOrder!!.orderId}")
            }
        }

        firestoreModel = FirestoreModel()


        val orderItemsAdapter = selectedOrder?.let { DrinkAdapterOrders(requireContext(), it.drinksList) }
        archivedOrderListView.adapter = orderItemsAdapter

        val orderIdTextView: TextView = view.findViewById(R.id.orderIdTextView)
        val orderDateTextView: TextView = view.findViewById(R.id.orderDateTextView)

        if (selectedOrder != null) {
            binding.orderIdTextView.text = "${selectedOrder!!.orderId}"
        }
        if (selectedOrder != null) {
            orderDateTextView.text = "${formatDateToCustomFormat(selectedOrder!!.orderDate)}"
        }

        //act ass back button
        binding.btnBack.setOnClickListener {
            if (getFragmentManager() != null) {
                getFragmentManager()?.popBackStack();
            }else {
                Navigation.findNavController(view).navigate(R.id.action_archivedOrderViewFragment_to_archiveOrdersFragment)
            }
        }
        val userEmailTextView: TextView = view.findViewById(R.id.userEmailTextView)
        val addTitleTextView: TextView = view.findViewById(R.id.addTitleTextView)
        val userNameTextView: TextView = view.findViewById(R.id.userNameTextView)
        val totalCostTextView: TextView = view.findViewById(R.id.totalCostTextView)

        selectedOrder?.let {
            firestoreModel.getUser(it.userId,
                onSuccess = { user ->
                    if (user != null) {
                        addTitleTextView.text = "View ${user.firstName?.capitalizeFirstLetter()}'s Order"
                        userNameTextView.text = "${user.firstName?.capitalizeFirstLetter()} ${user.lastName?.capitalizeFirstLetter()}"
                        userEmailTextView.text = "${user.email}"
                        val totalCost = selectedOrder?.calculateTotalCost()
                        totalCostTextView.text = "Total Cost: £${"%.2f".format(totalCost)}"
                    } else {
                        // Handle the case where the user is null
                        Log.e(TAG, "User is null for order with userId: ${it.userId}")
                        // You can add additional error handling logic here if needed
                    }
                },
                onFailure = {
                    // Log an error when the user retrieval fails
                    Log.e(TAG, "Failed to retrieve user for order with userId: ${it.userId}")
                    // You can add additional error handling logic here if needed
                }
            )
        }



        return view
    }

    fun formatDateToCustomFormat(date: Date): String {
        val desiredFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
        return desiredFormat.format(date)
    }

    fun String.capitalizeFirstLetter(): String {
        return if (isNotEmpty()) {
            this[0].toUpperCase() + substring(1)
        } else {
            this
        }
    }
}
