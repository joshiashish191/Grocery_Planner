package net.softglobe.groceryplanner.model.network.response

import net.softglobe.groceryplanner.model.network.Result

data class UserMetaDataResponse(
    val result: Result,
    val isPaidUser : Int
)
