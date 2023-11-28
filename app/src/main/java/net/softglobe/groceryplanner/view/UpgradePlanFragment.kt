package net.softglobe.groceryplanner.view

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.launch
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.databinding.FragmentUpgradePlanBinding
import net.softglobe.groceryplanner.model.Preferences
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import net.softglobe.groceryplanner.viewmodel.MainViewModelFactory


class UpgradePlanFragment : Fragment() {
    lateinit var binding: FragmentUpgradePlanBinding
    private lateinit var preferences: Preferences
    private val viewModel by viewModels<MainViewModel> { MainViewModelFactory(activity?.baseContext!!) }

    private lateinit var paymentSheet: PaymentSheet
    lateinit var customerConfig: PaymentSheet.CustomerConfiguration
    lateinit var paymentIntentClientSecret: String
    lateinit var planType: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_upgrade_plan, container, false
        )
        initView()
        return binding.root
    }

    private fun initView() {
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        binding.originalPricePerMonth.paintFlags =
            binding.originalPricePerMonth.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        binding.originalPricePerYear.paintFlags =
            binding.originalPricePerMonth.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        preferences = Preferences(activity?.applicationContext!!)
        binding.originalPricePerMonth.text =
            "@₹${preferences.getMonthlyOriginalPriceForPaidVersion()}"
        binding.discountedPricePerMonth.text =
            " Only ₹${preferences.getMonthlyDiscountedPriceForPaidVersion()} / Month"
        binding.originalPricePerYear.text =
            "@₹${preferences.getAnnualOriginalPriceForPaidVersion()}"
        binding.discountedPricePerYear.text =
            " Only ₹${preferences.getAnnualDiscountedPriceForPaidVersion()} / Year"

        binding.btnBuyMonthlyPlan.text =
            "Buy 1 month plan for Only ₹${preferences.getMonthlyDiscountedPriceForPaidVersion()}"
        binding.btnBuyAnnualPlan.text =
            "Buy 1 year plan for Only ₹${preferences.getAnnualDiscountedPriceForPaidVersion()}\n (Recommended)"

        binding.btnBuyMonthlyPlan.setOnClickListener {
            lifecycleScope.launch {
                planType = "monthly"
                fetchApi()
                presentPaymentSheet()
            }
        }
        binding.btnBuyAnnualPlan.setOnClickListener {
            planType = "annual"
            lifecycleScope.launch {
                fetchApi()
                presentPaymentSheet()
            }
        }
    }

    private suspend fun fetchApi() {
        try {
            val response = viewModel.callPaymentFetchApi(planType)
            if (response.isSuccessful && response.body() != null) {
                if (!response.body()!!.result.error) {
                    paymentIntentClientSecret = response.body()!!.paymentIntent
                    customerConfig = PaymentSheet.CustomerConfiguration(
                        response.body()!!.customer,
                        response.body()!!.ephemeralKey
                    )

                    val publishableKey = response.body()!!.publishableKey
                    PaymentConfiguration.init(activity?.applicationContext!!, publishableKey)
                } else {
                    Toast.makeText(
                        activity,
                        response.body()!!.result.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    activity,
                    "Something went wrong. Please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } catch (e: Exception) {
            Log.d("TAG", "fetchApi: $e")
            Toast.makeText(
                activity,
                "Something went wrong. Please try again",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun presentPaymentSheet() {
        paymentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret,
            PaymentSheet.Configuration(
                merchantDisplayName = "Softglobe Technologies",
                customer = customerConfig,
            )
        )
    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when(paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                print("Canceled")
            }
            is PaymentSheetResult.Failed -> {
                print("Error: ${paymentSheetResult.error}")
            }
            is PaymentSheetResult.Completed -> {
                print("Completed")
                lifecycleScope.launch {
                    val response = viewModel.updatePaymentDetails(planType)
                    if (response.isSuccessful && response.body() != null) {
                        if (!response.body()!!.error) {
                            findNavController().navigate(R.id.action_upgradePlanFragment_to_paymentSuccessFragment)
                            Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(activity, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}