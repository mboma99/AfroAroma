package com.example.afroaroma.controller

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import com.example.afroaroma.model.Drink
import com.example.afroaroma.model.Order
import com.example.afroaroma.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

class FirestoreController {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
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
                Log.e(TAG, "Error getting document", e)
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
                Log.e(TAG, "Error getting document", e)
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
        val storageRef: StorageReference = storage.reference.child("drink_images/${UUID.randomUUID()}")
        drinkRef.update(
            "name", drink.name,
            "description", drink.drinkDescription,
            "price", drink.price,
            "quantity", drink.quantity,
        )
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Error updating drink")
            }
    }

    fun updateDrinkWithImage(
        drink: Drink,
        imageUri: Uri,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val drinkRef = db.collection("Menu").document(drink.drinkId)

        // Upload image to Firebase Storage
        val storageRef: StorageReference = storage.reference.child("drink_images/${UUID.randomUUID()}")
        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()

                    // Update the drink with the new image URL
                    drinkRef.update("imageUrl", imageUrl)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onFailure(e.message ?: "Error updating drink with image URL")
                        }
                }
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Error uploading image")
            }
    }



    fun addDrink(drink: Drink, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val drinksCollection = db.collection("Menu")
        val data = hashMapOf(
            "name" to drink.name,
            "description" to drink.drinkDescription,
            "price" to drink.price,
            "quantity" to drink.quantity,
            "imageUrl" to drink.imageUrl
        )
        drinksCollection.add(data)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Error adding drink")
            }
    }

    fun addDrinkWithPhoto(drink: Drink, imageUri: Uri, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val drinksCollection = db.collection("Menu")
        val storageRef: StorageReference = storage.reference.child("drink_images/${UUID.randomUUID()}")

        // Upload image to Firebase Storage
        storageRef.putFile(imageUri)
            .addOnSuccessListener { storageTaskSnapshot ->
                // Get the download URL for the uploaded image
                storageTaskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                    // Create a new drink with the image URL
                    val data = hashMapOf(
                        "name" to drink.name,
                        "description" to drink.drinkDescription,
                        "price" to drink.price,
                        "quantity" to drink.quantity,
                        "imageUrl" to downloadUri.toString() // Use the download URL
                    )

                    // Add drink data to Firestore
                    drinksCollection.add(data)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onFailure(e.message ?: "Error adding drink")
                        }

                }?.addOnFailureListener { e ->
                    onFailure(e.message ?: "Error getting image download URL")
                }
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Error uploading image")
            }
    }


    fun deleteDrink(drink: Drink, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val drinkRef = db.collection("Menu").document(drink.drinkId)
        val storageRef = drink.imageUrl?.let { storage.getReferenceFromUrl(it) }
        // Get the image URL from Firestore before deleting the document
        drinkRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val imageUrl = documentSnapshot.getString("imageUrl")

                // Delete the image from Firebase Storage
                if (!imageUrl.isNullOrEmpty()) {

                    if (storageRef != null) {
                        storageRef.delete()
                            .addOnSuccessListener {

                                drinkRef.delete()
                                    .addOnSuccessListener {
                                        onSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        onFailure(e.message ?: "Error deleting drink")
                                    }
                            }
                            .addOnFailureListener { e ->
                                onFailure(e.message ?: "Error deleting image")
                            }
                    }
                } else {
                    // No image URL found, just delete the drink data
                    drinkRef.delete()
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onFailure(e.message ?: "Error deleting drink")
                        }
                }
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Error getting drink data")
            }
    }

    fun getDrinkById(drinkId: String, onSuccess: (Drink?) -> Unit, onFailure: () -> Unit) {
        val drinksRef = db.collection("Menu").document(drinkId)

        drinksRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val name = documentSnapshot.getString("name") ?: ""
                    val drinkDescription = documentSnapshot.getString("description") ?: ""

                    val price: Double = try {
                        documentSnapshot.getDouble("price") ?: 0.00
                    } catch (e: Exception) {
                        0.0
                    }

                    val quantity: Long = try {
                        documentSnapshot.getLong("quantity") ?: 0L
                    } catch (e: Exception) {
                        0L
                    }

                    val imageUrl = documentSnapshot.getString("imageUrl") ?: ""

                    val drink = Drink(drinkId, name, drinkDescription, price, quantity, imageUrl)
                    onSuccess(drink)
                } else {
                    onSuccess(null) // Document with the specified ID not found
                }
            }
            .addOnFailureListener {
                // Handle the case where getting the drink fails
                onFailure()
            }
    }

    //orders
    fun getOrdersList(
        onSuccess: (List<Order>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        Log.d("ViewOrderActivity", "getOrdersList: Start fetching orders")
        db.collection("Orders")
            .get()
            .addOnSuccessListener { querySnapshot ->
                Log.d("ViewOrderActivity", "getOrdersList: Successfully fetched orders")
                val ordersList = mutableListOf<Order>()

                for (document in querySnapshot) {
                    Log.d("ViewOrderActivity", "getOrdersList: Processing order document")

                    val orderId = document.id
                    val userId = document.getString("userId")
                    val orderDate = document.getDate("orderDate")
                    val orderStatus = document.getString("orderFullfilled")
                    val drinksList = mutableListOf<Drink>()

                    orderId?.let { uid ->
                        if (userId != null) {
                            getItemsForUser(uid,
                                onSuccess = { items ->
                                    Log.d("ViewOrderItemsCount", "getOrdersList: Successfully fetched items ${items.size}")

                                    val drinksFetchCount = AtomicInteger(items.size)

                                    for (item in items) {
                                        val itemId = item["drinkId"] as String
                                        val quantity = item["quantity"] as Long

                                        getDrinksForItemId(itemId, quantity,
                                            onSuccess = { drinks ->
                                                Log.d("ViewOrderActivityDrinks", "getOrdersList: Successfully fetched drinks for ${drinks}")

                                                drinksList.addAll(drinks)

                                                if (drinksFetchCount.decrementAndGet() == 0) {
                                                    // All drinks for this order have been fetched
                                                    val order = orderDate?.let {
                                                        Order(orderId, userId, drinksList,
                                                            it, orderStatus)
                                                    }

                                                    if (order != null) {
                                                        ordersList.add(order)
                                                    }

                                                    if (ordersList.size == querySnapshot.size()) {
                                                        // All orders have been processed
                                                        Log.d("ViewOrderActivity", "getOrdersList: All orders processed, invoking onSuccess")
                                                        onSuccess(ordersList)
                                                    }
                                                }
                                            },
                                            onFailure = { exception ->
                                                // Handle failure
                                                Log.e("ViewOrderActivity", "getOrdersList: Error fetching drinks: ${exception.message}")
                                            }
                                        )
                                    }
                                },
                                onFailure = { exception ->
                                    // Handle failure
                                    Log.e("ViewOrderActivity", "getOrdersList: Error fetching items for user: ${exception.message}")
                                }
                            )
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ViewOrderActivity", "getOrdersList: Failed to fetch orders: ${exception.message}")
                onFailure(exception)
            }
    }


    fun updateOrderStatus(orderId: String, newStatus: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val orderRef = db.collection("Orders").document(orderId)

        // Create a map with the new order status
        val updateData = mapOf(
            "orderFullfilled" to newStatus
        )

        orderRef
            .update(updateData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


    private fun getItemsForUser(orderId: String, onSuccess: (List<Map<String, Any>>) -> Unit, onFailure: (Exception) -> Unit) {
        val itemsArray = mutableListOf<Map<String, Any>>()

        db.collection("Orders")
            .document(orderId)
            .collection("items")
            .get()
            .addOnSuccessListener { result ->
                itemsArray.addAll(result.documents.map { itemDocument ->
                    val itemId = itemDocument.getString("itemId")
                    val drinkId = itemDocument.getString("drinkId")
                    val quantity = itemDocument.getLong("quantity") ?: 0

                    mapOf(
                        "itemId" to (itemId ?: ""),
                        "drinkId" to (drinkId ?: ""),
                        "quantity" to quantity
                    )
                })

                onSuccess(itemsArray)
            }
            .addOnFailureListener { exception ->
                // Handle exceptions, e.g., log the error
                onFailure(exception)
            }
    }


    private fun getDrinksForItemId(itemId: String, quantity: Long, onSuccess: (List<Drink>) -> Unit, onFailure: (Exception) -> Unit) {
        val drinksList = mutableListOf<Drink>()

        db.document("Menu/$itemId")
            .get()
            .addOnSuccessListener { drinkDocument ->
                val drinkName = drinkDocument.getString("name")
                val drinkDescription = drinkDocument.getString("description")
                val drinkPrice = drinkDocument.getDouble("price")
                val imageUrl = drinkDocument.getString("imageUrl")

                // Create a Drink object and add it to the list
                val drink = drinkName?.let {
                    Drink(itemId, it, drinkDescription, drinkPrice, quantity, imageUrl)
                }
                if (drink != null) {
                    drinksList.add(drink)
                }

                onSuccess(drinksList)
            }
            .addOnFailureListener { exception ->
                // Handle failure
                onFailure(exception)
            }
    }
}


