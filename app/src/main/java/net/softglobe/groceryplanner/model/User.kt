package net.softglobe.groceryplanner.model

data class User(
    val auth_token: String,
    val email: String,
    val name: String
)