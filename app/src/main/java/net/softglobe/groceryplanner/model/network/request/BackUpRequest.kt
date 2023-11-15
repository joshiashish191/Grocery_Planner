package net.softglobe.groceryplanner.model.network.request

import net.softglobe.groceryplanner.model.Grocery
import net.softglobe.groceryplanner.model.Modification

data class BackUpRequest(
    val groceryList: List<Grocery>,
    val modificationsList: List<Modification>,
    val email : String,
    val error: Boolean = false,
    val message: String = "",
    val authToken : String
)
