// FirestoreController.kt
package com.example.afroaroma.controller

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import com.example.afroaroma.model.Drink
import com.example.afroaroma.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot

class FirestoreController {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun checkUserRole(user: User , onComplete: (Boolean) -> Unit) {
        val userRef: DocumentReference = db.document("Users/${user.uid}")

        userRef.get()
            .addOnSuccessListener { documentSnapshot: DocumentSnapshot? ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val isAdmin: Boolean = documentSnapshot.getBoolean("isAdmin") ?: false
                    onComplete(isAdmin)
                } else {
                    onComplete(false)
                }
            }
            .addOnFailureListener { e: Exception ->
                // Handle failure
                onComplete(false)
            }
    }


    fun addUserToDB(user: User, onComplete: () -> Unit) {
        val userRef = user.uid?.let { db.collection("Users").document(it) }
        val userData = hashMapOf(
            "FirstName" to user.firstName,
            "LastName" to user.lastName,
            "EmailAddress" to user.email,
            "isAdmin" to user.isAdmin
        )

        if (userRef != null) {
            userRef.set(userData)
                .addOnSuccessListener {
                    onComplete()
                }
                .addOnFailureListener { e ->
                    // Handle failure
                }
        }
    }


    //checks user roles and if user is admin return true
    fun checkUserRoles(userId: String?, onSuccess: (Boolean) -> Unit, onFailure: () -> Unit) {
        val userRef = userId?.let { db.collection("Users").document(it) }
        userRef?.get()?.addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val isAdmin = documentSnapshot.getBoolean("isAdmin")
                onSuccess(isAdmin == true)
            } else {
                onFailure()
            }
        }?.addOnFailureListener { e ->
            onFailure()
            Log.e(TAG, "Error getting document", e)
        }
    }

    fun getUserFirstName(userId: String, onSuccess: (String) -> Unit, onFailure: () -> Unit){
        val userRef = db.document("Users/$userId")
        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    //get custom type
                    val firstName = documentSnapshot.getString("FirstName")
                    if (firstName!= null) {
                        onSuccess(firstName)
                    } else {
                        onFailure()
                    }
                } else {
                    onFailure()
                }
            }
            .addOnFailureListener { e ->
                onFailure()
                Log.e(ContentValues.TAG, "Error getting document", e)
            }
    }

    //needs a rework

    fun getUser(userId: String, onSuccess: (User) -> Unit, onFailure: () -> Unit) {
        val userRef = db.document("Users/$userId")
        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    //get custom type
                    val user = documentSnapshot.toObject(User::class.java)
                    if (user != null) {
                        onSuccess(user)
                    } else {
                        onFailure()
                    }
                } else {
                    onFailure()
                }
            }
            .addOnFailureListener { e ->
                onFailure()
                Log.e(ContentValues.TAG, "Error getting document", e)
            }
    }


    fun getMenu(onSuccess: (List<Drink>) -> Unit, onFailure: () -> Unit) {
        val drinksRef = db.collection("Menu")

        drinksRef.get()
            .addOnSuccessListener { documents ->
                val menu = mutableListOf<Drink>()

                for (document in documents) {
                    val drinkId = document.id
                    val name = document.getString("name") ?: ""
                    val drinkDescription = document.getString("description") ?: ""

                    val price: Double = try {
                        document.getDouble("price") ?: 0.00
                    } catch (e: Exception) {
                        0.0
                    }

                    val quantity: Long = try {
                        document.getLong("quantity") ?: 0L
                    } catch (e: Exception) {
                        0L
                    }

                    val imageUrl = document.getString("imageUrl") ?: ""

                    val drink = Drink(drinkId, name, drinkDescription, price, quantity, imageUrl)
                    menu.add(drink)
                }

                onSuccess(menu)
            }
            .addOnFailureListener {
                // Handle the case where getting the menu fails
                onFailure()
            }
    }



    fun updateDrink(drink: Drink, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val drinkRef = db.collection("Menu").document(drink.drinkId)

        drinkRef.update(
            "name", drink.name,
            "description", drink.drinkDescription,
            "price", drink.price,
            "quantity", drink.quantity
        )
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Error updating drink")
            }
    }

    fun addDrink(drink: Drink, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val drinksCollection = db.collection("Menu")
        val data = hashMapOf(
            "name" to drink.name,
            "description" to drink.drinkDescription,
            "price" to drink.price,
            "quantity" to drink.quantity
        )
        drinksCollection.add(data)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Error adding drink")
            }
    }

    fun deleteDrink(drink: Drink, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val drinkRef = db.collection("Menu").document(drink.drinkId)

        drinkRef.delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Error deleting drink")
            }
    }




}
