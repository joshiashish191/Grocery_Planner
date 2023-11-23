package net.softglobe.groceryplanner.model.network.response

data class PaymentFetchResponse(
    val customer: String,
    val ephemeralKey: String,
    val paymentIntent: String,
    val publishableKey: String
)