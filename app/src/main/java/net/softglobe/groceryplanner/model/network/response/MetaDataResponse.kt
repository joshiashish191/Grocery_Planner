package net.softglobe.groceryplanner.model.network.response

data class MetaDataResponse(
    val groceryRecordsLimitForFree : String,
    val modificationRecordsLimitForFree : String,
    val monthlyOriginalPriceForPaidVersion : String,
    val annualOriginalPriceForPaidVersion : String,
    val monthlyDiscountedPriceForPaidVersion : String,
    val annualDiscountedPriceForPaidVersion : String,
    val isPlanActive : Boolean,
    val privacyPolicyUrl : String,
    val coinsForMonthlyPlan : String,
    val coinsForAnnualPlan : String
)
