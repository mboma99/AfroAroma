package com.example.afroaroma.controller

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.afroaroma.R
import com.example.afroaroma.databinding.FragmentFeedbackViewBinding
import com.example.afroaroma.model.AuthModel
import com.example.afroaroma.model.Feedback
import com.example.afroaroma.model.FirestoreModel
import com.example.afroaroma.model.Order
import java.text.SimpleDateFormat
import java.util.Locale

// ... (your imports)

// ... (your imports)

class FeedbackViewFragment : Fragment() {
    private lateinit var binding: FragmentFeedbackViewBinding
    private lateinit var authModel: AuthModel
    private lateinit var firestoreModel: FirestoreModel
    private var selectedOrder: Order? = null
    private var selectedFeedback: Feedback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authModel = AuthModel()
        firestoreModel = FirestoreModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedbackViewBinding.inflate(inflater, container, false)
        val view = binding.root
        selectedFeedback = arguments?.getParcelable<Feedback>("selectedFeedback")!!

        if (selectedFeedback?.readStatus == false) {
            binding.btnUpdateRead.visibility = View.VISIBLE
            binding.btnUpdateRead.setOnClickListener {
                firestoreModel.updateReadStatus(
                    selectedFeedback!!.feedbackId,
                    onSuccess = {
                        binding.btnUpdateRead.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), "Feed Back Updated", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = {}
                    )
            }
        }


        if (selectedFeedback != null) {
            binding.feedbackHeadlineInput.text = selectedFeedback!!.feedbackHeadline
            binding.feedbackInput.text = selectedFeedback!!.feedback
            binding.feedbackScoreInput.text = "${selectedFeedback!!.rating} / 5"

            firestoreModel.getOrderById(
                selectedFeedback!!.orderId,
                onSuccess = { order ->
                    if (order != null) {
                        selectedOrder = order
                        val formattedDate = SimpleDateFormat("dd / MMMM / yyyy", Locale.getDefault()).format(order.orderDate)
                        binding.orderDateInput.text = formattedDate ?: "N/A"

                        firestoreModel.getUser(order.userId,
                            onSuccess = { user ->
                                if (user != null) {
                                    val formattedFullName = formatUserFullName(user.firstName, user.lastName)
                                    binding.customerNameInput.text = "${formattedFullName.takeIf { it.isNotBlank() } ?: "N/A"}."
                                } else {
                                    // Handle the case where the user is null
                                    // Log or display an error message
                                }
                            },
                            onFailure = {
                                // Log or display an error message
                            }
                        )
                    } else {
                        // Handle the case where the order is null
                        // Log or display an error message
                    }
                },
                onFailure = {
                    // Log or display an error message
                }
            )
        }
        binding.btnBack.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_feedbackViewFragment_to_feedbackFragment)
        }

        binding.btnViewOrder.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable("selectedOrder", selectedOrder)
            }
            if (selectedOrder!=null) {
                Navigation.findNavController(view)
                    .navigate(R.id.action_feedbackViewFragment_to_archivedOrderViewFragment, bundle)
            } else {
                Toast.makeText(requireContext(), "Order Couldn't be parsed", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun formatUserFullName(firstName: String?, lastName: String?): String {
        val capitalizedFirstName = firstName?.capitalize() ?: ""
        val firstCharLastName = lastName?.capitalize()?.take(1) ?: ""

        return "$capitalizedFirstName $firstCharLastName"
    }
}

