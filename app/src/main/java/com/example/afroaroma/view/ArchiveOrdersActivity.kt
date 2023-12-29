package com.example.afroaroma.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import com.example.afroaroma.R
import com.example.afroaroma.controller.AuthController
import com.example.afroaroma.controller.FirestoreController
import com.example.afroaroma.controller.OrderAdapter
import com.example.afroaroma.model.Order

class ArchiveOrdersActivity : AppCompatActivity() {

    private lateinit var authController: AuthController
    private lateinit var firestoreController: FirestoreController
    private lateinit var archiveOrdersListView: ListView
    private lateinit var btnBack: ImageView
    private lateinit var btnArchiveView: Button
    private var selectedOrder: Order? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive_orders)

        btnBack = findViewById(R.id.btnBack)
        btnArchiveView = findViewById(R.id.btnViewOrder)

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

                    val collectedOrdersList = orderList.filter { it.orderStatus == "Collected" }

                    val collectedOrdersAdapter = OrderAdapter(this, collectedOrdersList)

                    archiveOrdersListView.adapter = collectedOrdersAdapter



                    archiveOrdersListView.setOnItemClickListener { _, _, position, _ ->
                        // Handle item click for collected orders
                        selectedOrder = collectedOrdersAdapter.getItem(position) as Order
                        Toast.makeText(this, "Clicked on archived order: ${selectedOrder!!.orderId}", Toast.LENGTH_SHORT).show()
                    }

                    archiveOrdersListView.choiceMode = ListView.CHOICE_MODE_SINGLE
                    archiveOrdersListView.selector = resources.getDrawable(R.drawable.item_drink_selector)

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

    private fun setListeners() {
        btnBack.setOnClickListener { redirectToAdminHome() }
    }

    private fun redirectToAdminHome() {
        val intent = Intent(this, AdminHomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}