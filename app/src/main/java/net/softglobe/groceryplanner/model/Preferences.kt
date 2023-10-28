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

    fun getGroceryRecordsLimit() : Int {
        return preferences.getInt(Constants.GROCERY_RECORDS_LIMIT_FOR_FREE, 100)
    }

    fun getModificationsRecordsLimit() : Int {
        return preferences.getInt(Constants.MODIFICATION_RECORDS_LIMIT_FOR_FREE, 3)
    }

    fun getAuthToken() : String {
        return preferences.getString(Constants.AUTH_TOKEN, "")!!
    }

    fun getUserEmail() : String {
        return preferences.getString(Constants.EMAIL, "")!!
    }

    fun clearAllPreferences() {
        editor.clear()
        editor.apply()
    }
}