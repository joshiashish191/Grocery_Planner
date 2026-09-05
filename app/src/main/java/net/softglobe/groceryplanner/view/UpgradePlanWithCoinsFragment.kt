package net.softglobe.groceryplanner.view

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.model.LoadingInstance
import net.softglobe.groceryplanner.model.Preferences
import net.softglobe.groceryplanner.ui.theme.GroceryPlannerTheme
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import net.softglobe.groceryplanner.viewmodel.MainViewModelFactory

class UpgradePlanWithCoinsFragment : Fragment() {

    private val viewModel by viewModels<MainViewModel> { MainViewModelFactory(activity?.baseContext!!) }
    private lateinit var preferences: Preferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        initView()
        return ComposeView(requireContext()).apply {
            setContent {
                GroceryPlannerTheme {
                    UpgradePlanWithCoinsView()
                }
            }
        }
    }

    @Preview
    @Composable
    private fun UpgradePlanWithCoinsView() {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.premium_member_icon),
                        contentDescription = "Premium Member Icon",
                        modifier = Modifier.size(60.dp)
                    )

                    Text(
                        text = "Prime Membership",
                        modifier = Modifier.padding(top = 8.dp),
                        style = TextStyle(
                            fontSize = 30.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = stringResource(R.string.prime_earn_coins),
                        modifier = Modifier.padding(top = 10.dp),
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = Color.Black
                        ),
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = {
                            AlertDialog.Builder(context)
                                .setTitle("Gain Coins")
                                .setMessage(
                                    "Gain more coins and unlock the premium plan! Watching the " +
                                            "video Ad earn coins, which you can use to redeem the " +
                                            "premium plan."
                                )
                                .setIcon(R.drawable.icon_gold_coin)
                                .setPositiveButton("Gain Coin") { dialog, which ->
                                    showRewardedAd(requireActivity(), viewModel)
                                }
                                .show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text(
                            text = "Watch Ad and Gain Coins",
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = Color.White
                            )
                        )
                    }

                    Text(
                        text = "OR",
                        modifier = Modifier.padding(top = 10.dp),
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = Color.Black,
                        )
                    )

                    Button(
                        onClick = {
                            findNavController().navigate(R.id.action_upgradePlanWithCoinsFragment_to_upgradePlanFragment)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text(
                            text = "Buy Premium Plan",
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = Color.White
                            )
                        )
                    }

                    Text(
                        text = "The Prime Membership comes with lot of benefits as follows:",
                        modifier = Modifier.padding(top = 10.dp),
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    )

                    val featuresList = listOf(
                        "Unlimited Grocery Records",
                        "Unlimited Modifications Records",
                        "Server Backups",
                        "Technical Support",
                        "No Ads"
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        featuresList.forEach { feature ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.bullet),
                                    contentDescription = null
                                )
                                Text(
                                    text = feature,
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        color = Color.Black
                                    ),
                                )
                            }
                        }
                    }

                    Text(
                        text = "... And much more. Unlock Prime plan using coins by clicking below",
                        modifier = Modifier.padding(top = 10.dp),
                        style = TextStyle(
                            fontSize = 25.sp,
                            color = Color.Black
                        ),
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = {
                            buyWithCoins("monthly")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text(
                            text = "Redeem Prime plan for 1 month with ${preferences.getCoinsForMonthlyPlan()} coins",
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = Color.White
                            ),
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        onClick = {
                            buyWithCoins("annual")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text(
                            text = "Redeem Prime plan for 1 year with ${preferences.getCoinsForAnnualPlan()} coins",
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = Color.White
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    private fun initView() {
        preferences = Preferences(activity?.applicationContext!!)
    }

    private fun buyWithCoins(planType : String) {
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                LoadingInstance.showLoading(requireActivity())
            }
            try {
                val response = viewModel.buyWithCoins(planType)
                if (response.isSuccessful && response.body() != null) {
                    if (!response.body()!!.error) {
                        preferences.setUserPaidStatus(true)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                activity,
                                response.body()!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            LoadingInstance.hideLoading()
                            findNavController().navigate(R.id.action_upgradePlanWithCoinsFragment_to_paymentSuccessFragment)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                activity,
                                response.body()!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } catch (e : Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        activity,
                        "Something went wrong. Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    LoadingInstance.hideLoading()
                }
            }
        }
    }

    private fun showRewardedAd(activity: Activity, viewModel: MainViewModel) {
        AdManager.getRewardedAd()?.let { ad ->
            ad.show(activity) { rewardItem ->
                // Handle the reward.
                val rewardAmount = rewardItem.amount
                val rewardType = rewardItem.type
                lifecycleScope.launch(Dispatchers.IO) {
                    withContext(Dispatchers.Main) {
                        LoadingInstance.showLoading(requireActivity())
                    }
                    try {
                        val response = viewModel.grantReward()
                        if (response.isSuccessful && response.body() != null) {
                            if (!response.body()!!.result.error) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        activity,
                                        response.body()!!.result.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        activity,
                                        response.body()!!.result.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    activity,
                                    "Something went wrong. Please try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e : Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                activity,
                                "Something went wrong. Please try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } finally {
                        withContext(Dispatchers.Main) {
                            LoadingInstance.hideLoading()
                        }
                    }
                }
            }
        } ?: run {
        }
        AdManager.loadRewardedAd(activity)
    }
}