package com.example.afroaroma.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.afroaroma.R
import com.example.afroaroma.controller.AuthController
import com.example.afroaroma.controller.DrinkAdapter
import com.example.afroaroma.controller.FirestoreController
import com.example.afroaroma.controller.OrderAdapter
import com.example.afroaroma.model.Drink
import com.example.afroaroma.model.Order

class ViewOrderActivity : AppCompatActivity() {
    private lateinit var authController: AuthController
    private lateinit var firestoreController: FirestoreController
    private lateinit var activeOrdersListView: ListView
    private lateinit var archiveOrdersListView: ListView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var btnBack: ImageView
    private lateinit var btnUpdate: Button
    private lateinit var btnClose:Button
    private var selectedOrder: Order? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ViewOrderActivity", "onCreate started tetetetetetete")
        setContentView(R.layout.activity_view_order)

        btnBack = findViewById(R.id.btnBack)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnClose = findViewById(R.id.btnComplete)
        activeOrdersListView = findViewById(R.id.activeOrdersListView)
        archiveOrdersListView = findViewById(R.id.archiveOrdersListView)


        authController = AuthController()
        firestoreController = FirestoreController()

        val currentUser = authController.getCurrentUserId()
        if (currentUser != null) {
            val orderList = mutableListOf<Order>()
            firestoreController.getOrdersList(
                onSuccess = { fetchedOrders ->
                    Log.d("ViewOrderActivity", "Data retrieved successfully. Number of orders: ${fetchedOrders.size}")

                    val orderList = mutableListOf<Order>()
                    orderList.addAll(fetchedOrders)

                    val readyOrdersList = orderList.filter { it.orderStatus == "Ready" || it.orderStatus == "Preparing" }
                    val collectedOrdersList = orderList.filter { it.orderStatus == "Collected" }

                    val readyOrdersAdapter = OrderAdapter(this, readyOrdersList)
                    val collectedOrdersAdapter = OrderAdapter(this, collectedOrdersList)

                    activeOrdersListView.adapter = readyOrdersAdapter
                    archiveOrdersListView.adapter = collectedOrdersAdapter

                    activeOrdersListView.setOnItemClickListener { _, _, position, _ ->
                        selectedOrder = readyOrdersAdapter.getItem(position) as Order
                        Toast.makeText(this, "Clicked on order: ${selectedOrder!!.orderId}", Toast.LENGTH_SHORT).show()
                    }

                    archiveOrdersListView.setOnItemClickListener { _, _, position, _ ->
                        // Handle item click for collected orders
                        selectedOrder = collectedOrdersAdapter.getItem(position) as Order
                        Toast.makeText(this, "Clicked on archived order: ${selectedOrder!!.orderId}", Toast.LENGTH_SHORT).show()
                    }

                    activeOrdersListView.choiceMode = ListView.CHOICE_MODE_SINGLE
                    activeOrdersListView.selector = resources.getDrawable(R.drawable.item_drink_selector)

                },
                onFailure = { exception ->
                    Log.d("ViewOrderActivity", "yeah nah this didn't work:${exception.message} ")
                    // Handle failure, e.g., show an error message
                    Toast.makeText(this, "Failed to fetch orders: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }

        setListeners()

    }
    private fun updateOrderDialog(order: Order) {

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Confirm Order is Ready")

        val alertDialog = dialogBuilder.create()

        val btnConfirm: Button = dialogView.findViewById(R.id.btnConfirm)
        val btnCancel: Button = dialogView.findViewById(R.id.btnCancel)
        btnConfirm.setOnClickListener {
            orderReady()
            alertDialog.dismiss()
        }
        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun completeOrderDialog(order: Order) {

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Confirm Order is Completed")

        val alertDialog = dialogBuilder.create()

        val btnConfirm: Button = dialogView.findViewById(R.id.btnConfirm)
        val btnCancel: Button = dialogView.findViewById(R.id.btnCancel)
        btnConfirm.setOnClickListener {
            orderCompleted()
            alertDialog.dismiss()
        }
        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun setListeners() {
        btnBack.setOnClickListener { redirectToAdminHome() }
        btnUpdate.setOnClickListener {
            selectedOrder?.let { it1 -> updateOrderDialog(it1) }
        }
        btnClose.setOnClickListener {
            selectedOrder?.let { it1 -> completeOrderDialog(it1) }
        }

    }

    private fun updateOrderStatus(orderId: String, newStatus: String) {
        firestoreController.updateOrderStatus(
            orderId,
            newStatus,
            onSuccess = {
                // Handle success, e.g., show a message
                Toast.makeText(this, "Order status updated successfully", Toast.LENGTH_SHORT).show()
                // Optionally, refresh the order list or UI
            },
            onFailure = { exception ->
                // Handle failure, e.g., show an error message
                Toast.makeText(this, "Failed to update order status: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun orderReady(){
        if (selectedOrder != null) {
            // Call the updateOrderStatus function
            updateOrderStatus(selectedOrder!!.orderId, "Ready")
            redirectToViewOrders()

        } else {
            Toast.makeText(this, "No order selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun orderCompleted(){
        if (selectedOrder != null) {
            // Call the updateOrderStatus function
            updateOrderStatus(selectedOrder!!.orderId, "Collected")
            redirectToViewOrders()
        } else {
            Toast.makeText(this, "No order selected", Toast.LENGTH_SHORT).show()
        }
    }

    // Navigation
    private fun redirectToViewOrders() {
        val intent = Intent(this, ViewOrderActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun redirectToAdminHome() {
        val intent = Intent(this, AdminHomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
