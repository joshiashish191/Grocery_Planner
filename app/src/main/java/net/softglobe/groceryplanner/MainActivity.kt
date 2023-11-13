package net.softglobe.groceryplanner

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import net.softglobe.groceryplanner.model.Preferences
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import net.softglobe.groceryplanner.viewmodel.MainViewModelFactory

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>{ MainViewModelFactory(this) }
    private lateinit var preferences : Preferences
    private var keepOnSplashScreen = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                lifecycleScope.launch {
                    try {
                        preferences = Preferences(this@MainActivity)
                        val response = viewModel.getMetadata()
                        val userMetadataResponse = viewModel.getUserMetadata(preferences.getUserEmail())
                        if (response.isSuccessful && response.body() != null) {
                            response.body()?.apply {
                                preferences.setGroceryRecordsLimitForFree(groceryRecordsLimitForFree)
                                preferences.setModificationRecordsLimitForFree(modificationRecordsLimitForFree)
                                preferences.setMonthlyOriginalPriceForPaidVersion(monthlyOriginalPriceForPaidVersion)
                                preferences.setAnnualOriginalPriceForPaidVersion(annualOriginalPriceForPaidVersion)
                                preferences.setMonthlyDiscountedPriceForPaidVersion(monthlyDiscountedPriceForPaidVersion)
                                preferences.setAnnualDiscountedPriceForPaidVersion(annualDiscountedPriceForPaidVersion)
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
                    } catch (e : Exception) {
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
        setContentView(R.layout.activity_main)
    }
}