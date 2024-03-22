package net.softglobe.groceryplanner

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import net.softglobe.groceryplanner.view.AdManager
import net.softglobe.groceryplanner.model.Preferences
import net.softglobe.groceryplanner.model.network.NetworkUtils
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import net.softglobe.groceryplanner.viewmodel.MainViewModelFactory

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>{ MainViewModelFactory(this) }
    private lateinit var preferences : Preferences
    private var keepOnSplashScreen = true
    private var updateDialogShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            if (NetworkUtils.isNetworkConnected(this@MainActivity)) {
                setKeepOnScreenCondition {
                    lifecycleScope.launch {
                        try {
                            preferences = Preferences(this@MainActivity)
                            val response = viewModel.getMetadata()
                            val userMetadataResponse =
                                viewModel.getUserMetadata(preferences.getUserEmail())
                            if (response.isSuccessful && response.body() != null) {
                                response.body()?.apply {
                                    preferences.setGroceryRecordsLimitForFree(
                                        groceryRecordsLimitForFree
                                    )
                                    preferences.setModificationRecordsLimitForFree(
                                        modificationRecordsLimitForFree
                                    )
                                    preferences.setMonthlyOriginalPriceForPaidVersion(
                                        monthlyOriginalPriceForPaidVersion
                                    )
                                    preferences.setAnnualOriginalPriceForPaidVersion(
                                        annualOriginalPriceForPaidVersion
                                    )
                                    preferences.setMonthlyDiscountedPriceForPaidVersion(
                                        monthlyDiscountedPriceForPaidVersion
                                    )
                                    preferences.setAnnualDiscountedPriceForPaidVersion(
                                        annualDiscountedPriceForPaidVersion
                                    )
                                    preferences.setPrivacyPolicyUrl(
                                        privacyPolicyUrl
                                    )

                                    preferences.setCoinsForMonthlyPlan(coinsForMonthlyPlan)
                                    preferences.setCoinsForAnnualPlan(coinsForAnnualPlan)
                                }
                                if (preferences.isUserLoggedIn()) {
                                    if (userMetadataResponse.isSuccessful && userMetadataResponse.body() != null) {
                                        userMetadataResponse.body()?.apply {
                                            if (isPaidUser == 0)
                                                preferences.setUserPaidStatus(false)
                                            else
                                                preferences.setUserPaidStatus(true)
                                            keepOnSplashScreen = false
                                        }
                                    }
                                } else {
                                    keepOnSplashScreen = false
                                }
                            } else {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Something went wrong. Please try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@MainActivity,
                                "Something went wrong. Please try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    keepOnSplashScreen
                }
            }
        }
        setContentView(R.layout.activity_main)
        AdManager.initializeAd(this)
        AdManager.getAdRequest()
        AdManager.loadRewardedAd(this)
    }

    override fun onResume() {
        super.onResume()

        if (NetworkUtils.isNetworkConnected(this)) {
            lifecycleScope.launch {
                try {
                    preferences = Preferences(this@MainActivity)
                    val response = viewModel.getMetadata()
                    if (response.isSuccessful && response.body() != null) {
                        response.body()?.apply {
                                showUpdateDialog(
                                    liveVersionCode,
                                    isMandatoryUpdate.equals("yes", ignoreCase = true)
                                )
                                updateDialogShown = true
                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Something went wrong. Please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@MainActivity,
                        "Something went wrong. Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showUpdateDialog(liveVersionCode: String, isMandatoryUpdate: Boolean) {
        if (Integer.parseInt(liveVersionCode) > BuildConfig.VERSION_CODE) {
            AlertDialog.Builder(this@MainActivity)
                .setTitle("Update Available")
                .setMessage(
                    if (isMandatoryUpdate)
                    resources.getString(R.string.update_available_mandatory_msg) else
                    resources.getString(R.string.update_available_optional_msg)
                )
                .setPositiveButton("Ok") { dialog, which ->
                    try {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=$packageName")
                            )
                        )
                    } catch (e: ActivityNotFoundException) {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                            )
                        )
                    }
                }
                .setIcon(R.drawable.app_icon)
                .setCancelable(!isMandatoryUpdate)
                .show()
        }
    }
}