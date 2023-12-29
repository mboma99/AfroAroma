package com.example.afroaroma.controller
import com.example.afroaroma.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlin.reflect.KFunction1

class AuthController {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var lastAuthException: FirebaseAuthException? = null

    fun getLastAuthException(): FirebaseAuthException? {
        return lastAuthException
    }

    fun signInWithEmailPassword(email: String, password: String, onComplete: KFunction1<User?, Unit>) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    val firbaseUser = auth.currentUser
                    val user = firbaseUser?.let { User(it.uid) }
                    onComplete(user)
                } else {
                    lastAuthException = task.exception as? FirebaseAuthException
                    onComplete(null)
                }
            }
    }

    //create a customer account
    fun signUpWithEmailPassword(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        onComplete: (User?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userModel = User(user!!.uid,false ,firstName, lastName, email)
                    onComplete(userModel)
                } else {
                    onComplete(null)
                }
            }
    }


    fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser
        return if (firebaseUser != null) {
            User(firebaseUser.uid, null, "","" ,firebaseUser.email ?:"")
        } else {
            null
        }
    }
    fun getCurrentUserId():String? {
        return auth.currentUser?.uid
    }
    fun getFirebaseUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun signOut() {
        auth.signOut()
    }
}