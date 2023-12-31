package com.example.afroaroma

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.afroaroma.controller.AuthController
import com.example.afroaroma.controller.FirestoreController
import com.example.afroaroma.controller.OrderAdapter
import com.example.afroaroma.databinding.FragmentAdminMenuBinding
import com.example.afroaroma.databinding.FragmentArchiveOrdersBinding
import com.example.afroaroma.model.Order

class archiveOrdersFragment: Fragment() {

    private lateinit var binding: FragmentArchiveOrdersBinding
    private lateinit var authController: AuthController
    private lateinit var firestoreController: FirestoreController
    private lateinit var archiveOrdersListView: ListView
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
        binding = FragmentArchiveOrdersBinding.inflate(inflater, container, false)
        val view = binding.root


        archiveOrdersListView = view.findViewById(R.id.archiveOrdersListView)

        authController = AuthController()
        firestoreController = FirestoreController()

        val currentUser = authController.getCurrentUserId()
        if (currentUser != null) {
            val orderList = mutableListOf<Order>()
            firestoreController.getOrdersList(
                onSuccess = { fetchedOrders ->
                    Log.d("LiveOrdersFragment", "Data retrieved successfully. Number of orders: ${fetchedOrders.size}")

                    val orderList = mutableListOf<Order>()
                    orderList.addAll(fetchedOrders)

                    val collectedOrdersList = orderList.filter { it.orderStatus == "Collected" }

                    val collectedOrdersAdapter = OrderAdapter(requireContext(), collectedOrdersList)

                    archiveOrdersListView.adapter = collectedOrdersAdapter

                    archiveOrdersListView.setOnItemClickListener { _, _, position, _ ->
                        // Handle item click for collected orders
                        selectedOrder = collectedOrdersAdapter.getItem(position) as Order
                        Toast.makeText(requireContext(), "Clicked on archived order: ${selectedOrder!!.orderId}", Toast.LENGTH_SHORT).show()
                    }

                    archiveOrdersListView.choiceMode = ListView.CHOICE_MODE_SINGLE
                    archiveOrdersListView.selector = resources.getDrawable(R.drawable.item_drink_selector)

                },
                onFailure = { exception ->
                    Log.d("LiveOrdersFragment", "yeah nah this didn't work:${exception.message} ")
                    // Handle failure, e.g., show an error message
                    Toast.makeText(requireContext(), "Failed to fetch orders: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }





        binding.btnBack.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_archiveOrdersFragment_to_adminHomeFragment)
        }

        binding.btnViewOrder.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable("selectedOrder", selectedOrder)
            }
            Navigation.findNavController(view).navigate(R.id.action_archiveOrdersFragment_to_archivedOrderViewFragment, bundle)
        }

        return view
    }
}
