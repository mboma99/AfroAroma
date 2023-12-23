package com.example.afroaroma.model

import com.google.firebase.database.PropertyName

data class User(@get:PropertyName("uid")
                @set:PropertyName("uid")
                var uid: String = "", val isAdmin: Boolean? = null, val firstName: String? = null, val lastName: String? = null, val email: String? = null)


