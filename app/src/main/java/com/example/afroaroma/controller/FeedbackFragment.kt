package com.example.afroaroma.controller

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.afroaroma.R
import com.example.afroaroma.databinding.FragmentFeedbackBinding
import com.example.afroaroma.model.AuthModel
import com.example.afroaroma.model.Feedback
import com.example.afroaroma.model.FeedbackAdapter
import com.example.afroaroma.model.FirestoreModel

class FeedbackFragment : Fragment() {

    private lateinit var binding: FragmentFeedbackBinding
    private lateinit var authModel: AuthModel
    private lateinit var firestoreModel: FirestoreModel
    private lateinit var feedbackListView: ListView
    private lateinit var feedbackAdapter: FeedbackAdapter
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
        binding = FragmentFeedbackBinding.inflate(inflater, container, false)
        val view = binding.root

        feedbackListView = binding.feedbackListView

        // Initialize your firestoreController or any other necessary components.
        val currentUser = authModel.getCurrentUserId()
        if (currentUser != null) {
            firestoreModel.getFeedbackList(
                onSuccess = { feedbackList ->
                    feedbackAdapter = FeedbackAdapter(requireContext(), feedbackList)

                    // Set the FeedbackAdapter on the ListView
                    feedbackListView.adapter = feedbackAdapter
                    feedbackListView.choiceMode = ListView.CHOICE_MODE_SINGLE
                    feedbackListView.selector = resources.getDrawable(R.drawable.item_drink_selector)
                },
                onFailure = {
                    // Handle the failure case
                    println("Failed to get feedback.")
                }
            )
        }


        feedbackListView.setOnItemClickListener { _, _, position, _ ->
            selectedFeedback = feedbackAdapter.getItem(position) as Feedback
        }

        binding.btnBack.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_feedbackFragment_to_adminHomeFragment)
        }

        binding.btnViewFeedback.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable("selectedFeedback", selectedFeedback)
            }
            if (selectedFeedback!=null) {
                Navigation.findNavController(view)
                    .navigate(R.id.action_feedbackFragment_to_feedbackViewFragment, bundle)
            } else {
                Toast.makeText(requireContext(), "No drink selected", Toast.LENGTH_SHORT).show()
            }
        }


        return view
    }
}
