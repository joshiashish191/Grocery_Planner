package net.softglobe.groceryplanner.model.network

data class  User(
    val email: String,
    val name: String,
    val password : String,
    val authToken: String = "",
    val isPaidUser : Int = 0,
    val planExpirationDate : String? = null
)