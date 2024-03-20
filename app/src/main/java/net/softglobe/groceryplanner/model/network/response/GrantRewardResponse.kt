package net.softglobe.groceryplanner.model.network.response

import net.softglobe.groceryplanner.model.network.Result

data class GrantRewardResponse(
    val result: Result,
    val totalCoins : Int = 0
)
