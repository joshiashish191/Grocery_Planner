package net.softglobe.groceryplanner.model.network.response

import net.softglobe.groceryplanner.model.network.Result

data class PaymentFetchResponse(
    val customer: String,
    val ephemeralKey: String,
    val paymentIntent: String,
    val publishableKey: String,
    val result: Result
)