package com.example.afroaroma.controller

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.afroaroma.R
import com.example.afroaroma.model.Drink

class DrinkAdapter(private val context: Context, private val drinks: List<Drink>) : BaseAdapter() {

    override fun getCount(): Int {
        return drinks.size
    }

    override fun getItem(position: Int): Any {
        return drinks[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View

        if (convertView == null) {
            // Inflate a new view if it doesn't exist yet
            view = inflater.inflate(R.layout.item_drink, null)
        } else {
            // Reuse the existing view
            view = convertView
        }

        // Get the TextView from the item_drink.xml layout
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val priceTextView: TextView = view.findViewById(R.id.priceTextView)
        //val quantityTextView: TextView = view.findViewById(R.id.quantityTextView)

        // Set the name of the drink in the TextView
        val drink = getItem(position) as Drink
        nameTextView.text = drink.name
        priceTextView.text = "£${drink.price}"
        //quantityTextView.text = "Stock: ${drink.quantity}"

        return view
    }
}

