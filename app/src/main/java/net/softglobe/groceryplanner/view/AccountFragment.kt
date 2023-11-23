package net.softglobe.groceryplanner.view

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import net.softglobe.groceryplanner.databinding.FragmentAccountBinding
import net.softglobe.groceryplanner.model.Preferences
import net.softglobe.groceryplanner.model.network.request.BackUpRequest
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import net.softglobe.groceryplanner.viewmodel.MainViewModelFactory


class AccountFragment : Fragment() {

    lateinit var binding : FragmentAccountBinding
    private lateinit var preferences : Preferences
    private val viewModel by viewModels<MainViewModel>{ MainViewModelFactory(activity?.baseContext!!) }

    private lateinit var paymentSheet: PaymentSheet
    lateinit var customerConfig: PaymentSheet.CustomerConfiguration
    lateinit var paymentIntentClientSecret: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater,
            R.layout.fragment_account, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        preferences = Preferences(activity?.applicationContext!!)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        fetchApi()
        binding.payBtn.setOnClickListener {
            presentPaymentSheet()
        }

        if (preferences.isPaidUser()) {
            binding.txtPlanName.apply {
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.premium_member_icon,0,0,0)
                text = "  Premium Member"
            }
            binding.txtUpgradePlan.text = resources.getText(R.string.already_premium_plan_description)
        } else {
            binding.txtPlanName.apply {
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_icon,0,0,0)
                text = "  Sparkle Plan"
            }
            binding.txtUpgradePlan.text = resources.getText(R.string.upgrade_plan_description)
        }

        lifecycleScope.launch {
            try {
                val response = viewModel.getUserDetails(preferences.getUserEmail())
                if (response.isSuccessful && response.body() != null) {
                    if (!response.body()!!.result.error) {
                        binding.txtName.text = response.body()!!.user.name
                        binding.txtEmail.text = response.body()!!.user.email
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
            } catch (e : Exception) {
                Toast.makeText(
                    activity,
                    "Something went wrong. Please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.txtChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_changePasswordFragment)
        }

        binding.txtLogout.setOnClickListener {
            preferences.clearAllPreferences()
            findNavController().navigate(R.id.action_accountFragment_to_loginFragment)
        }

        binding.clImportBackup.setOnClickListener {
            if (preferences.isPaidUser()) {
                lifecycleScope.launch {
                    try {
                        val response = viewModel.importBackupFromServer(preferences.getUserEmail())
                        if (response.isSuccessful && response.body() != null) {
                            if (!response.body()!!.error) {
                                viewModel.clearAllGroceryData()
                                viewModel.clearAllModifications()
                                val groceryList = response.body()!!.groceryList
                                val modificationsList = response.body()!!.modificationsList
                                groceryList.forEach {grocery ->
                                    viewModel.insertGroceryItemOnly(grocery)
                                }
                                modificationsList.forEach {modification ->
                                    viewModel.insertModification(modification)
                                }
                                Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(activity, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e : Exception) {
                        Toast.makeText(activity, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(activity, "This is a paid feature!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.clExportBackup.setOnClickListener {
            if (preferences.isPaidUser()) {
                lifecycleScope.launch {
                    val groceryList = viewModel.getGroceryListWithoutObserver()
                    val modificationsList =  viewModel.getAllModificationsListWithoutObserver()
                    val backUpOperationsRequest = BackUpRequest(
                        groceryList, modificationsList, preferences.getUserEmail(), authToken = preferences.getAuthToken()
                    )
                    try {
                        val response = viewModel.backupToServer(backUpOperationsRequest)

                        if (response.isSuccessful && response.body() != null) {
                            if (!response.body()!!.error) {
                                Toast.makeText(
                                    activity, response.body()!!.message, Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    activity, response.body()!!.message, Toast.LENGTH_SHORT
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
                        Toast.makeText(
                            activity,
                            "Something went wrong. Please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(activity, "This is a paid feature!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.txtName.setOnClickListener {
            val view = LayoutInflater.from(activity).inflate(R.layout.dialog_change_name, null)
            val newName = view.findViewById<EditText>(R.id.et_new_name)
            AlertDialog.Builder(requireActivity())
                .setTitle("Change Name")
                .setPositiveButton("Change"){ dialog, position ->
                    val newNameString = newName.text.toString()
                    if (!TextUtils.isEmpty(newNameString)) {
                        updateName(newNameString)
                    } else {
                        Toast.makeText(activity, "Please enter new name", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                .setView(view)
                .show()
            
        }
    }

    private fun fetchApi() {
        lifecycleScope.launch {
            try {
                val response = viewModel.callPaymentFetchApi()
                if (response.isSuccessful && response.body() != null) {
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
                        "Something went wrong. Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e : Exception) {
                Toast.makeText(
                    activity,
                    "Something went wrong. Please try again",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                Toast.makeText(activity, "Fetch Api completed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun presentPaymentSheet() {
        val googlePayConfiguration = PaymentSheet.GooglePayConfiguration(
            environment = PaymentSheet.GooglePayConfiguration.Environment.Test,
            countryCode = "US",
            currencyCode = "USD" // Required for Setup Intents, optional for Payment Intents
        )
        paymentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret,
            PaymentSheet.Configuration(
                merchantDisplayName = "Softglobe Technologies",
                customer = customerConfig,
                googlePay = googlePayConfiguration,
            )
        )
    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when(paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                Toast.makeText(activity, "Cancelled", Toast.LENGTH_SHORT).show()
                print("Canceled")
            }
            is PaymentSheetResult.Failed -> {
                Toast.makeText(activity, "Failed. Error: ${paymentSheetResult.error}", Toast.LENGTH_SHORT).show()
                print("Error: ${paymentSheetResult.error}")
            }
            is PaymentSheetResult.Completed -> {
                // Display for example, an order confirmation screen
                Toast.makeText(activity, "Payment Completed", Toast.LENGTH_SHORT).show()
                print("Completed")
            }
        }
    }

    private fun updateName(name : String) {
        lifecycleScope.launch {
            try {
                val response = viewModel.changeUserName(preferences.getUserEmail(), name)
                if (response.isSuccessful && response.body() != null) {
                    if (!response.body()!!.error) {
                        binding.txtName.text = name
                        Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        activity,
                        "Something went wrong. Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e : Exception) {
                Toast.makeText(
                    activity,
                    "Something went wrong. Please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}