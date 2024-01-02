package net.softglobe.groceryplanner.model

import android.content.Context
import android.content.SharedPreferences

class Preferences(context : Context) {
    private val preferences : SharedPreferences = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE)
    private val editor = preferences.edit()

    fun isUserLoggedIn() : Boolean {
        return preferences.getBoolean(Constants.IS_LOGGED_IN, false)
    }

    fun isPaidUser() : Boolean {
        return preferences.getBoolean(Constants.IS_PAID_USER, false)
    }

    fun setUserLoginStatus(loginStatus : Boolean) {
        editor.putBoolean(Constants.IS_LOGGED_IN, loginStatus)
        editor.apply()
    }

    fun setUserPaidStatus(paymentStatus : Boolean) {
        editor.putBoolean(Constants.IS_PAID_USER, paymentStatus)
        editor.apply()
    }

    fun setAuthToken(token : String) {
        editor.putString(Constants.AUTH_TOKEN, token)
        editor.apply()
    }

    fun setUserEmail(email : String) {
        editor.putString(Constants.EMAIL, email)
        editor.apply()
    }

    fun setGroceryRecordsLimitForFree(limit : String) {
        editor.putString(Constants.GROCERY_RECORDS_LIMIT_FOR_FREE, limit)
        editor.apply()
    }

    fun setModificationRecordsLimitForFree(limit : String) {
        editor.putString(Constants.MODIFICATION_RECORDS_LIMIT_FOR_FREE, limit)
        editor.apply()
    }

    fun setMonthlyOriginalPriceForPaidVersion(price : String) {
        editor.putString(Constants.MONTHLY_ORIGINAL_PRICE_FOR_PAID_VERSION, price)
        editor.apply()
    }

    fun setAnnualOriginalPriceForPaidVersion(price : String) {
        editor.putString(Constants.ANNUAL_ORIGINAL_PRICE_FOR_PAID_VERSION, price)
        editor.apply()
    }

    fun setMonthlyDiscountedPriceForPaidVersion(price : String) {
        editor.putString(Constants.MONTHLY_DISCOUNTED_PRICE_FOR_PAID_VERSION, price)
        editor.apply()
    }

    fun setAnnualDiscountedPriceForPaidVersion(price : String) {
        editor.putString(Constants.ANNUAL_DISCOUNTED_PRICE_FOR_PAID_VERSION, price)
        editor.apply()
    }

    fun setPrivacyPolicyUrl(url : String) {
        editor.putString(Constants.PRIVACY_POLICY_URL, url)
        editor.apply()
    }

    fun getAuthToken() : String {
        return preferences.getString(Constants.AUTH_TOKEN, "")!!
    }

    fun getUserEmail() : String {
        return preferences.getString(Constants.EMAIL, "")!!
    }

    fun getGroceryRecordsLimitForFree() : String {
        return preferences.getString(Constants.GROCERY_RECORDS_LIMIT_FOR_FREE, "100")!!
    }

    fun getModificationRecordsLimitForFree() : String {
        return preferences.getString(Constants.MODIFICATION_RECORDS_LIMIT_FOR_FREE, "10")!!
    }

    fun getMonthlyOriginalPriceForPaidVersion() : String {
        return preferences.getString(Constants.MONTHLY_ORIGINAL_PRICE_FOR_PAID_VERSION, "")!!
    }

    fun getAnnualOriginalPriceForPaidVersion() : String {
        return preferences.getString(Constants.ANNUAL_ORIGINAL_PRICE_FOR_PAID_VERSION, "")!!
    }

    fun getMonthlyDiscountedPriceForPaidVersion() : String {
        return preferences.getString(Constants.MONTHLY_DISCOUNTED_PRICE_FOR_PAID_VERSION, "")!!
    }

    fun getAnnualDiscountedPriceForPaidVersion() : String {
        return preferences.getString(Constants.ANNUAL_DISCOUNTED_PRICE_FOR_PAID_VERSION, "")!!
    }

    fun getPrivacyPolicyUrl() : String {
        return preferences.getString(Constants.PRIVACY_POLICY_URL, "")!!
    }

    fun clearAllPreferences() {
        editor.clear()
        editor.apply()
    }
}