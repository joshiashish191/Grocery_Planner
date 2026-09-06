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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.model.LoadingInstance
import net.softglobe.groceryplanner.model.Preferences
import net.softglobe.groceryplanner.model.network.NetworkUtils
import net.softglobe.groceryplanner.model.network.request.BackUpRequest
import net.softglobe.groceryplanner.ui.theme.GroceryPlannerTheme
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import net.softglobe.groceryplanner.viewmodel.MainViewModelFactory


class AccountFragment : Fragment() {

    private lateinit var preferences : Preferences
    private val viewModel by viewModels<MainViewModel>{ MainViewModelFactory(activity?.baseContext!!) }
    private var userName by mutableStateOf("")
    private var userEmail by mutableStateOf("")
    private var userCoins by mutableStateOf("0")
    private var planName by mutableStateOf("")
    private var planDescriptionId by mutableIntStateOf(0)
    private var planValidity by mutableStateOf("")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        initView()
        return ComposeView(requireContext()).apply {
            setContent {
                GroceryPlannerTheme {
                    AccountView()
                }
            }
        }
    }

    @Preview
    @Composable
    private fun AccountView() {
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
                    Text(
                        "Account",
                        style = TextStyle(
                            fontSize = 25.sp,
                            color = Color.Black,
                        )
                    )

                    Text(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(),
                        text = "Plan",
                        style = TextStyle(
                            color = colorResource(R.color.colorPrimaryDark),
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .clickable{
                                if (!preferences.isPaidUser()) {
                                    findNavController().navigate(R.id.action_accountFragment_to_upgradePlanWithCoinsFragment)
                                }
                            }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.star_icon),
                            contentDescription = "Plan icon",
                        )
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = planName,
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 20.sp
                            )
                        )
                    }

                    Text(
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .fillMaxWidth()
                            .clickable{
                                openGainCoinsDialog()
                            },
                        text = stringResource(planDescriptionId),
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 18.sp
                        )
                    )

                    if (preferences.isPaidUser()) {
                        Text(
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .fillMaxWidth(),
                            text = planValidity,
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier
                            .padding(vertical = 5.dp),
                    )

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        text = "Profile",
                        style = TextStyle(
                            color = colorResource(R.color.colorPrimaryDark),
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable{
                                openEditNameDialog()
                            }
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .weight(1f),
                            text = userName,
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 20.sp
                            )
                        )
                        Icon(
                            painter = painterResource(R.drawable.edit_pen_icon),
                            contentDescription = "Edit icon",
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(24.dp)
                        )
                    }

                    Text(
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .fillMaxWidth(),
                        text = userEmail,
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 20.sp
                        )
                    )

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        text = "Coins",
                        style = TextStyle(
                            color = colorResource(R.color.colorPrimaryDark),
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(top = 5.dp),
                            text = "$userCoins coins",
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 20.sp
                            )
                        )
                        Spacer(
                            modifier = Modifier.padding(start = 10.dp)
                        )
                        Button(
                            onClick = {
                                openGainCoinsDialog()
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Gain Coins",
                                style = TextStyle(
                                    color = Color.White,
                                )
                            )
                        }
                    }

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        text = "Backups",
                        style = TextStyle(
                            color = colorResource(R.color.colorPrimaryDark),
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .clickable{
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
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.cloud_download_icon),
                            contentDescription = "Import Data from Cloud",
                        )
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = "Import Data from Cloud",
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 20.sp
                            )
                        )
                    }

                    Text(
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .fillMaxWidth()
                            .clickable{
                                openGainCoinsDialog()
                            },
                        text = stringResource(R.string.import_description),
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 18.sp
                        )
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .padding(vertical = 5.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable{
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
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.cloud_backup_icon),
                            contentDescription = "Save backup to cloud",
                        )
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = "Save backup to cloud",
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 20.sp
                            )
                        )
                    }

                    Text(
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .fillMaxWidth()
                            .clickable{
                                openGainCoinsDialog()
                            },
                        text = stringResource(R.string.export_description),
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 18.sp
                        )
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .padding(vertical = 5.dp)
                    )

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        text = "Settings",
                        style = TextStyle(
                            color = colorResource(R.color.colorPrimaryDark),
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .clickable{
                                findNavController().navigate(R.id.action_accountFragment_to_changePasswordFragment)
                            }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.password_icon),
                            contentDescription = "Change Password",
                        )
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = "Change Password",
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 20.sp
                            )
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier
                            .padding(vertical = 5.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .clickable{
                                preferences.clearAllPreferences()
                                findNavController().navigate(R.id.action_accountFragment_to_loginFragment)
                            }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.logout_icon),
                            contentDescription = "Logout",
                        )
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = "Logout",
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 20.sp
                            )
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier
                            .padding(vertical = 5.dp)
                    )

                    if (!preferences.isPaidUser()) {
                        AndroidView(
                            factory = { context ->
                                AdView(context).apply {
                                    setAdSize(AdSize.BANNER)
                                    adUnitId = getString(R.string.banner_ad_unit_ad)
                                    loadAd(AdRequest.Builder().build())
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        )
                    }

                }
            }
        }
    }

    private fun initView() {
        preferences = Preferences(activity?.applicationContext!!)

        if (preferences.isPaidUser()) {
            planName = "Premium Member"
            planDescriptionId = R.string.already_premium_plan_description
        } else {
            planName = "Sparkle Plan"
            planDescriptionId = R.string.upgrade_plan_description
        }

        loadAccountData()
    }

    private fun openEditNameDialog() {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_change_name, null)
        val newName = view.findViewById<EditText>(R.id.et_new_name)
        AlertDialog.Builder(requireActivity())
            .setTitle("Change Name")
            .setPositiveButton("Change") { dialog, position ->
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

    private fun openGainCoinsDialog() {
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

    private fun loadAccountData() {
        if (NetworkUtils.isNetworkConnected(requireActivity().applicationContext)) {
            LoadingInstance.showLoading(requireActivity())
            lifecycleScope.launch {
                try {
                    val response = viewModel.getUserDetails(preferences.getUserEmail())
                    if (response.isSuccessful && response.body() != null) {
                        if (!response.body()!!.result.error) {
                            userName = response.body()!!.user.name
                            userEmail = response.body()!!.user.email
                            if (preferences.isPaidUser() && response.body()!!.user.planExpirationDate != null) {
                                planValidity =
                                    "Valid till ${response.body()!!.user.planExpirationDate}"
                            }
                            userCoins = response.body()!!.user.coins.toString()
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
                        userName = name
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