package com.example.afroaroma.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.afroaroma.R

class FeedbackAdapter(private val context: Context, private var feedbackList: List<Feedback>) : BaseAdapter() {

    override fun getCount(): Int {
        return feedbackList.size
    }

    override fun getItem(position: Int): Any {
        return feedbackList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View

        if (convertView == null) {
            view = inflater.inflate(R.layout.item_feedback, null) // replace with your feedback item layout
        } else {
            view = convertView
        }

        val feedbackHeadlineTextView: TextView = view.findViewById(R.id.feedbackHeadlineTextView)
        val ratingTextView: TextView = view.findViewById(R.id.ratingTextView)
        val statusReadTextView: TextView = view.findViewById(R.id.statusReadTextView)

        // Set the feedback details in the TextViews
        val feedback = getItem(position) as Feedback
        feedbackHeadlineTextView.text = feedback.feedbackHeadline
        //feedbackTextView.text = feedback.feedback
        ratingTextView.text = "${feedback.rating} / 5"

        statusReadTextView.text = if (feedback.readStatus) {
            "Read"
        } else {
            "Not Read"
        }

        return view
    }

    fun updateData(newFeedbackList: List<Feedback>) {
        feedbackList = newFeedbackList
        notifyDataSetChanged()
    }
}
