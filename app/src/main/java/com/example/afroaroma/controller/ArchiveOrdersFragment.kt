package com.example.afroaroma.controller

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.afroaroma.R
import com.example.afroaroma.model.AuthModel
import com.example.afroaroma.model.FirestoreModel
import com.example.afroaroma.databinding.FragmentArchiveOrdersBinding
import com.example.afroaroma.model.Order
import com.example.afroaroma.model.OrderAdapter

class ArchiveOrdersFragment: Fragment() {

    private lateinit var binding: FragmentArchiveOrdersBinding
    private lateinit var authModel: AuthModel
    private lateinit var firestoreModel: FirestoreModel
    private lateinit var archiveOrdersListView: ListView
    private var selectedOrder: Order? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authModel = AuthModel()
        firestoreModel = FirestoreModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArchiveOrdersBinding.inflate(inflater, container, false)
        val view = binding.root


        archiveOrdersListView = view.findViewById(R.id.archiveOrdersListView)

        authModel = AuthModel()
        firestoreModel = FirestoreModel()

        val currentUser = authModel.getCurrentUserId()
        if (currentUser != null) {
            val orderList = mutableListOf<Order>()
            firestoreModel.getOrdersList(
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
                        //Toast.makeText(requireContext(), "Clicked on archived order: ${selectedOrder!!.orderId}", Toast.LENGTH_SHORT).show()
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
