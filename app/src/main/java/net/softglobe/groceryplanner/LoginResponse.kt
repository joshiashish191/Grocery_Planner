package net.softglobe.groceryplanner

import net.softglobe.groceryplanner.model.Result
import net.softglobe.groceryplanner.model.User

data class LoginResponse(
    val user : User,
    val result: Result
)
