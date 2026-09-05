package net.softglobe.groceryplanner.view

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.launch
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.model.LoadingInstance
import net.softglobe.groceryplanner.model.Preferences
import net.softglobe.groceryplanner.ui.theme.GroceryPlannerTheme
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import net.softglobe.groceryplanner.viewmodel.MainViewModelFactory


class UpgradePlanFragment : Fragment() {
    private lateinit var preferences: Preferences
    private val viewModel by viewModels<MainViewModel> { MainViewModelFactory(activity?.baseContext!!) }

    private lateinit var paymentSheet: PaymentSheet
    var customerConfig: PaymentSheet.CustomerConfiguration? = null
    var paymentIntentClientSecret: String = ""
    var planType: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        initView()
        return ComposeView(requireContext()).apply {
            setContent {
                GroceryPlannerTheme {
                    PlanView()
                }
            }
        }
    }

    @Preview
    @Composable
    private fun PlanView() {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
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
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.premium_member_icon),
                        contentDescription = "Premium Member Icon",
                        modifier = Modifier.size(60.dp)
                    )

                    Text(
                        text = "Prime Membership",
                        modifier = Modifier.padding(top = 10.dp),
                        style = TextStyle(
                            fontSize = 30.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    val planMonthlyPrice = buildAnnotatedString {
                        append("@")
                        withStyle(
                            style = SpanStyle(
                                textDecoration = TextDecoration.LineThrough
                            )
                        ) {
                            append("${preferences.getMonthlyOriginalPriceForPaidVersion()} ")
                        }
                        append("Only ₹${preferences.getMonthlyDiscountedPriceForPaidVersion()} / Month")
                    }

                    Text(
                        text = planMonthlyPrice,
                        modifier = Modifier.padding(top = 15.dp),
                        style = TextStyle(
                            fontSize = 25.sp,
                            color = Color.Black
                        )
                    )

                    val planAnnualPrice = buildAnnotatedString {
                        append("@")
                        withStyle(
                            style = SpanStyle(
                                textDecoration = TextDecoration.LineThrough
                            )
                        ) {
                            append("${preferences.getAnnualOriginalPriceForPaidVersion()} ")
                        }
                        append("Only ₹${preferences.getAnnualDiscountedPriceForPaidVersion()} / Year")
                    }

                    Text(
                        text = planAnnualPrice,
                        modifier = Modifier.padding(top = 15.dp),
                        style = TextStyle(
                            fontSize = 25.sp,
                            color = Color.Black
                        )
                    )

                    Text(
                        text = "The Prime Membership comes with lot of benefits as follows:",
                        modifier = Modifier.padding(top = 10.dp),
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    )

                    val featuresList = mutableListOf(
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
                                    modifier = Modifier.padding(top = 5.dp),
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        color = Color.Black
                                    ),
                                )
                            }
                        }
                    }

                    Text(
                        text = "... And much more. Save more by purchasing an Annual Plan!",
                        modifier = Modifier.padding(top = 8.dp),
                        style = TextStyle(
                            fontSize = 25.sp,
                            color = Color.Black
                        ),
                    )

                    Button(
                        onClick = {
                            showPaymentIntegrationInProgressPopup()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Buy 1 month plan for Only ₹${preferences.getMonthlyDiscountedPriceForPaidVersion()}",
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = Color.White
                            )
                        )
                    }

                    Button(
                        onClick = {
                            showPaymentIntegrationInProgressPopup()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Buy 1 year plan for Only ₹${preferences.getAnnualDiscountedPriceForPaidVersion()} (Recommended)",
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
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        preferences = Preferences(activity?.applicationContext!!)
    }

    private fun showPaymentIntegrationInProgressPopup() {
        AlertDialog.Builder(context)
            .setTitle("Feature In Progress")
            .setMessage(
                "This feature is not available yet. The payment gateway integration is in progress and will be available soon." +
                        "We appreciate your patience. Till then you can watch Ads to gain coins and unlock the prime plan."
            )
            .setIcon(R.drawable.warning_icon)
            .setPositiveButton("OK") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private suspend fun fetchApi() {
        try {
            LoadingInstance.showLoading(requireActivity())
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
        } finally {
            LoadingInstance.hideLoading()
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
                    LoadingInstance.showLoading(requireActivity())
                    try {
                        val response = viewModel.updatePaymentDetails(planType)
                        if (response.isSuccessful && response.body() != null) {
                            if (!response.body()!!.error) {
                                preferences.setUserPaidStatus(true)
                                findNavController().navigate(R.id.action_upgradePlanFragment_to_paymentSuccessFragment)
                                Toast.makeText(
                                    activity,
                                    response.body()!!.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    activity,
                                    response.body()!!.message,
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
                    } catch (e : Exception) {
                        Toast.makeText(activity, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show()
                    } finally {
                        LoadingInstance.hideLoading()
                    }
                }
            }
        }
    }
}