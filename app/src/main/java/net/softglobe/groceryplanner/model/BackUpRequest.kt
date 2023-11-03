package net.softglobe.groceryplanner.model

data class BackUpRequest(
    val groceryList: List<Grocery>,
    val modificationsList: List<Modification>,
    val email : String,
    val error: Boolean = false,
    val message: String = ""
)
