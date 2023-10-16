package net.softglobe.groceryplanner.model.network

data class  User(
    val email: String,
    val name: String,
    val password : String,
    val auth_token: String = ""
)