package net.softglobe.groceryplanner.model.network.response

import net.softglobe.groceryplanner.model.network.Result
import net.softglobe.groceryplanner.model.network.User

data class LoginResponse(
    val user : User,
    val result: Result
)
