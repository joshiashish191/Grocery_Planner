package net.softglobe.groceryplanner.view

import android.app.Activity
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
import com.google.android.gms.ads.AdRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.databinding.FragmentAccountBinding
import net.softglobe.groceryplanner.model.LoadingInstance
import net.softglobe.groceryplanner.model.Preferences
import net.softglobe.groceryplanner.model.network.NetworkUtils
import net.softglobe.groceryplanner.model.network.request.BackUpRequest
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import net.softglobe.groceryplanner.viewmodel.MainViewModelFactory


class AccountFragment : Fragment() {

    lateinit var binding : FragmentAccountBinding
    private lateinit var preferences : Preferences
    private val viewModel by viewModels<MainViewModel>{ MainViewModelFactory(activity?.baseContext!!) }

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

        if (!preferences.isPaidUser()) {
            binding.bannerAdView.visibility = View.VISIBLE
            val adRequest = AdRequest.Builder().build()
            binding.bannerAdView.loadAd(adRequest)
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

        loadAccountData()

        binding.txtEarnCoins.setOnClickListener {
            if (NetworkUtils.isNetworkConnected(requireActivity().applicationContext)) {
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
            } else {
                AlertDialog.Builder(context)
                    .setTitle("No Internet")
                    .setMessage("Please connect to the internet to load account details")
                    .setIcon(R.drawable.warning_icon)
                    .setPositiveButton("Ok") { dialog, which ->
                        findNavController().popBackStack()
                    }
                    .setCancelable(false)
                    .show()
            }
        }

        binding.clPlan.setOnClickListener {
            if (!preferences.isPaidUser())
                findNavController().navigate(R.id.action_accountFragment_to_upgradePlanWithCoinsFragment)
        }

        binding.txtChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_changePasswordFragment)
        }

        binding.txtLogout.setOnClickListener {
            preferences.clearAllPreferences()
            findNavController().navigate(R.id.action_accountFragment_to_loginFragment)
        }

        binding.clImportBackup.setOnClickListener {
            AlertDialog.Builder(requireActivity())
                .setTitle("Import data from Cloud")
                .setMessage("This will delete all your local data and replace with the data from cloud. If you have some newly added data that is not yet saved to cloud, then please Save to cloud first. Are you sure you want to import?")
                .setPositiveButton("Yes") {dialog, position ->
                    importData()
                }
                .setNegativeButton("Cancel") {dialog, position ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.clExportBackup.setOnClickListener {
            AlertDialog.Builder(requireActivity())
                .setTitle("Save to Cloud")
                .setMessage("This will delete all the data you have on the cloud and save this newer one. Are you sure you want to continue?")
                .setPositiveButton("Yes") {dialog, position ->
                    exportBackup()
                }
                .setNegativeButton("Cancel") {dialog, position ->
                    dialog.dismiss()
                }
                .show()
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

    private fun loadAccountData() {
        if (NetworkUtils.isNetworkConnected(requireActivity().applicationContext)) {
            LoadingInstance.showLoading(requireActivity())
            lifecycleScope.launch {
                try {
                    val response = viewModel.getUserDetails(preferences.getUserEmail())
                    if (response.isSuccessful && response.body() != null) {
                        if (!response.body()!!.result.error) {
                            binding.txtName.text = response.body()!!.user.name
                            binding.txtEmail.text = response.body()!!.user.email
                            if (preferences.isPaidUser() && response.body()!!.user.planExpirationDate != null) {
                                binding.txtPlanExpiry.visibility = View.VISIBLE
                                binding.txtPlanExpiry.text =
                                    "Valid till ${response.body()!!.user.planExpirationDate}"
                            }
                            binding.txtCoins.text = response.body()!!.user.coins.toString()+" coins"
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
                    Toast.makeText(
                        activity,
                        "Something went wrong. Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    LoadingInstance.hideLoading()
                }
            }
        } else {
            AlertDialog.Builder(context)
                .setTitle("No Internet")
                .setMessage("Please connect to the internet to load account details")
                .setIcon(R.drawable.warning_icon)
                .setPositiveButton("Ok") { dialog, which ->
                    findNavController().popBackStack()
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun updateName(name : String) {
        lifecycleScope.launch {
            try {
                LoadingInstance.showLoading(requireActivity())
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
            } finally {
                LoadingInstance.hideLoading()
            }
        }
    }

    private fun exportBackup() {
        if (preferences.isPaidUser()) {
            lifecycleScope.launch {
                val groceryList = viewModel.getGroceryListWithoutObserver()
                val modificationsList =  viewModel.getAllModificationsListWithoutObserver()
                val backUpOperationsRequest = BackUpRequest(
                    groceryList, modificationsList, preferences.getUserEmail(), authToken = preferences.getAuthToken()
                )
                try {
                    LoadingInstance.showLoading(requireActivity())
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
                } finally {
                    LoadingInstance.hideLoading()
                }
            }
        } else {
            findNavController().navigate(R.id.action_accountFragment_to_upgradePlanWithCoinsFragment)
            Toast.makeText(activity, "Please upgrade to use this feature!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun importData() {
        if (preferences.isPaidUser()) {
            lifecycleScope.launch {
                try {
                    LoadingInstance.showLoading(requireActivity())
                    val response = viewModel.importBackupFromServer(preferences.getUserEmail())
                    if (response.isSuccessful && response.body() != null) {
                        if (!response.body()!!.error) {
                            viewModel.clearAllGroceryData()
                            viewModel.clearAllModifications()
                            val groceryList = response.body()!!.groceryList
                            val modificationsList = response.body()!!.modificationsList
                            if (!groceryList.isNullOrEmpty()) {
                                groceryList.forEach { grocery ->
                                    viewModel.insertGroceryItemOnly(grocery)
                                }
                            }
                            if (!modificationsList.isNullOrEmpty()) {
                                modificationsList.forEach { modification ->
                                    viewModel.insertModification(modification)
                                }
                            }
                            Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_SHORT)
                                .show()
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
                } finally {
                    LoadingInstance.hideLoading()
                }
            }
        } else {
            findNavController().navigate(R.id.action_accountFragment_to_upgradePlanWithCoinsFragment)
            Toast.makeText(activity, "Please upgrade to use this feature!", Toast.LENGTH_SHORT)
                .show()
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
                    } catch (e: Exception) {
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