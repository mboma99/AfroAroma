package com.example.afroaroma.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.afroaroma.R
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter(private val context: Context, private val orders: List<Order>) : BaseAdapter() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun getCount(): Int {
        return orders.size
    }

    override fun getItem(position: Int): Any {
        return orders[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            // Inflate a new view if it doesn't exist yet
            view = inflater.inflate(R.layout.item_order, null)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            // Reuse the existing view
            view = convertView
            holder = view.tag as ViewHolder
        }

        // Set the data of the order in the TextViews
        val order = getItem(position) as Order
        val drinkNames = order.drinksList.joinToString("\n") { drink ->
            "${drink.name ?: ""}   x${drink.quantity}"
        }
        val totalCost = order.calculateTotalCost()

        holder.drinkNameTextView.text = drinkNames
        holder.orderDateTextView.text = context.getString(R.string.order_date_format, dateFormat.format(order.orderDate))
        holder.priceTextView.text = context.getString(R.string.total_cost_format, totalCost)
        // Inside your adapter's getView method
        if (order.orderStatus == "Ready") {
            holder.orderStatusTextView.text = context.getString(R.string.order_status_ready)
        } else if (order.orderStatus == "Preparing") {
            holder.orderStatusTextView.text = context.getString(R.string.order_status_preparing)
            holder.orderStatusTextView.setTextColor(ContextCompat.getColor(context, R.color.colorPreparing))
        } else if (order.orderStatus == "Collected") {
            holder.orderStatusTextView.text = context.getString(R.string.order_status_collected)
            holder.orderStatusTextView.setTextColor(ContextCompat.getColor(context, R.color.colorCollected))
        }


        return view
    }

    private class ViewHolder(view: View) {
        val priceTextView: TextView = view.findViewById(R.id.priceTextView)
        val drinkNameTextView: TextView = view.findViewById(R.id.drinkNameTextView)
        val orderDateTextView: TextView = view.findViewById(R.id.orderDateTextView)
        val orderStatusTextView: TextView = view.findViewById(R.id.orderStatusTextView)
    }
}


