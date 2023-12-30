package com.example.afroaroma

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import com.example.afroaroma.controller.AuthController
import com.example.afroaroma.controller.FirestoreController
import com.example.afroaroma.controller.OrderAdapter
import com.example.afroaroma.model.Order

class LiveOrdersFragment : Fragment() {
    private lateinit var authController: AuthController
    private lateinit var firestoreController: FirestoreController
    private lateinit var activeOrdersListView: ListView
    private lateinit var btnBack: ImageView
    private lateinit var btnUpdate: Button
    private lateinit var btnClose: Button
    private lateinit var orderAdapter: OrderAdapter
    private var selectedOrder: Order? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authController = AuthController()
        firestoreController = FirestoreController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_live_orders, container, false)
        initializeViews(view)
        setListeners()
        loadOrders()
        return view
    }

    private fun initializeViews(view: View) {
        activeOrdersListView = view.findViewById(R.id.activeOrdersListView)
        btnBack = view.findViewById(R.id.btnBack)
        btnUpdate = view.findViewById(R.id.btnUpdate)
        btnClose = view.findViewById(R.id.btnComplete)
    }

    private fun setListeners() {
        btnBack.setOnClickListener { navigateToAdminHome() }
        btnUpdate.setOnClickListener { selectedOrder?.let { updateOrderDialog(it) } }
        btnClose.setOnClickListener { selectedOrder?.let { completeOrderDialog(it) } }
    }

    private fun loadOrders() {
        val currentUser = authController.getCurrentUserId()
        if (currentUser != null) {
            firestoreController.getOrdersList(
                onSuccess = { fetchedOrders ->
                    try {
                        val orderList = fetchedOrders.filter { it.orderStatus == "Ready" || it.orderStatus == "Preparing" }
                        orderAdapter = OrderAdapter(requireContext(), orderList)
                        activeOrdersListView.adapter = orderAdapter
                        setListViewListeners()
                    } catch (e: Exception) {
                        Log.e("LiveOrdersFragment", "Error loading orders: ${e.message}", e)
                    }
                },
                onFailure = { exception ->
                    Log.e("LiveOrdersFragment", "Failed to fetch orders: ${exception.message}", exception)
                }
            )
        }
    }


    private fun setListViewListeners() {
        activeOrdersListView.setOnItemClickListener { _, _, position, _ ->
            selectedOrder = orderAdapter.getItem(position) as Order
            //showToast("Clicked on order: ${selectedOrder!!.orderId}")
        }
        activeOrdersListView.choiceMode = ListView.CHOICE_MODE_SINGLE
        activeOrdersListView.selector = resources.getDrawable(R.drawable.item_drink_selector)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun updateOrderDialog(order: Order) {
        showConfirmationDialog(order, "Confirm Order is Ready") {
            orderReady()
        }
    }

    private fun completeOrderDialog(order: Order) {
        showConfirmationDialog(order, "Confirm Order is Completed") {
            orderCompleted()
        }
    }

    private fun showConfirmationDialog(order: Order, title: String, confirmAction: () -> Unit) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle(title)

        val alertDialog = dialogBuilder.create()

        val btnConfirm: Button = dialogView.findViewById(R.id.btnConfirm)
        val btnCancel: Button = dialogView.findViewById(R.id.btnCancel)

        btnConfirm.setOnClickListener {
            confirmAction()
            alertDialog.dismiss()
        }

        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun orderReady() {
        selectedOrder?.let {
            updateOrderStatus(it.orderId, "Ready")
            refreshOrderAdapter()
        } ?: showToast("No order selected")
    }

    private fun orderCompleted() {
        selectedOrder?.let {
            updateOrderStatus(it.orderId, "Collected")
            refreshOrderAdapter()
        } ?: showToast("No order selected")
    }

    private fun refreshOrderAdapter() {
        loadOrders()
        //showToast("Order status updated successfully")
    }

    private fun updateOrderStatus(orderId: String, newStatus: String) {
        firestoreController.updateOrderStatus(
            orderId,
            newStatus,
            onSuccess = {
                //showToast("Order status updated successfully")
            },
            onFailure = { exception ->
                showToast("Failed to update order status: ${exception.message}")
            }
        )
    }

    private fun navigateToAdminHome() {
        view?.let { Navigation.findNavController(it).navigate(R.id.action_liveOrdersFragment_to_adminHomeFragment) }
    }
}

