package com.example.afroaroma.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.afroaroma.R

class DrinkAdapter(private val context: Context, private var drinks: List<Drink>) : BaseAdapter() {

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

            view = inflater.inflate(R.layout.item_drink, null)
        } else {

            view = convertView
        }

        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val priceTextView: TextView = view.findViewById(R.id.priceTextView)
        //val quantityTextView: TextView = view.findViewById(R.id.quantityTextView)

        // Set the name of the drink in the TextView
        val drink = getItem(position) as Drink
        nameTextView.text = drink.name
        priceTextView.text = "£${"%.2f".format(drink.price)}"

        //quantityTextView.text = "Stock: ${drink.quantity}"

        return view
    }

    fun updateData(newDrinks: List<Drink>) {
        drinks = newDrinks
        notifyDataSetChanged()
    }
}
