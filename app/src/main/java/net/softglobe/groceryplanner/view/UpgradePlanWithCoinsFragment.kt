package net.softglobe.groceryplanner.view

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.databinding.FragmentUpgradePlanWithCoinsBinding
import net.softglobe.groceryplanner.model.LoadingInstance
import net.softglobe.groceryplanner.model.Preferences
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import net.softglobe.groceryplanner.viewmodel.MainViewModelFactory

class UpgradePlanWithCoinsFragment : Fragment() {

    lateinit var binding: FragmentUpgradePlanWithCoinsBinding
    private val viewModel by viewModels<MainViewModel> { MainViewModelFactory(activity?.baseContext!!) }
    private lateinit var preferences: Preferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_upgrade_plan_with_coins, container, false
        )
        initView()
        return binding.root
    }

    private fun initView() {
        preferences = Preferences(activity?.applicationContext!!)
        binding.btnShowAd.setOnClickListener {
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
        }

        binding.btnBuyMonthlyPlan.text = "Redeem Prime plan for 1 month with ${preferences.getCoinsForMonthlyPlan()} coins"
        binding.btnBuyAnnualPlan.text = "Redeem Prime plan for 1 year with ${preferences.getCoinsForAnnualPlan()} coins"

        binding.btnBuyMonthlyPlan.setOnClickListener {
            buyWithCoins("monthly")
        }

        binding.btnBuyAnnualPlan.setOnClickListener {
            buyWithCoins("annual")
        }

        binding.btnShowPricing.setOnClickListener {
            findNavController().navigate(R.id.action_upgradePlanWithCoinsFragment_to_upgradePlanFragment)
        }
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