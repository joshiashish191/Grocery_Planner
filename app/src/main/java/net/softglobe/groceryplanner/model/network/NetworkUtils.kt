package net.softglobe.groceryplanner.model.network

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import net.softglobe.groceryplanner.R

object NetworkUtils {

    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        if (cm != null) {
            // Check for network connectivity on Android 10 (API level 29) and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
                return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
            } else {
                // Check for network connectivity on Android versions below 10
                val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
                return activeNetwork != null && activeNetwork.isConnectedOrConnecting
            }
        }

        return false
    }

    fun handleNoInternet(context: Context) {
        // Your code to handle the no internet case (e.g., show a dialog, display a message)
        AlertDialog.Builder(context)
            .setTitle("No Internet")
            .setMessage("Please connect to the internet to load account details")
            .setIcon(R.drawable.warning_icon)
            .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->

            })
            .show()
    }
}