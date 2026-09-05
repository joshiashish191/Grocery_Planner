package net.softglobe.groceryplanner.view

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
import kotlinx.coroutines.launch
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.model.Constants.KEY_EMAIL
import net.softglobe.groceryplanner.model.Constants.KEY_IS_FROM_FORGOT_PASS_SCREEN
import net.softglobe.groceryplanner.model.LoadingInstance
import net.softglobe.groceryplanner.model.Preferences
import net.softglobe.groceryplanner.ui.theme.GroceryPlannerTheme
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import net.softglobe.groceryplanner.viewmodel.MainViewModelFactory

class ChangePasswordFragment : Fragment() {

    private val viewModel by viewModels<MainViewModel>{ MainViewModelFactory(activity?.baseContext!!) }
    private lateinit var preferences : Preferences
    private var isFromForgotPassScreen by mutableStateOf(false)
    private var email = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        initView()
        return ComposeView(requireContext()).apply {
            setContent {
                GroceryPlannerTheme {
                    ChangePasswordView()
                }
            }
        }
    }

    private fun initView() {
        preferences = Preferences(activity?.applicationContext!!)

        if (arguments != null) {
            isFromForgotPassScreen = arguments?.getBoolean(KEY_IS_FROM_FORGOT_PASS_SCREEN)!!
            if (isFromForgotPassScreen) {
                email = arguments?.getString(KEY_EMAIL)!!
            }
        }
    }

    @Preview
    @Composable
    private fun ChangePasswordView() {
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
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Change Password",
                        style = TextStyle(
                            fontSize = 25.sp,
                            color = Color.Black
                        )
                    )

                    var currentPassword by remember { mutableStateOf("") }

                    if (!isFromForgotPassScreen) {
                        OutlinedTextField(
                            value = currentPassword,
                            onValueChange = {
                                currentPassword = it
                            },
                            label = { Text("Current Password") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.lock_icon),
                                    contentDescription = null
                                )
                            },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation()
                        )
                    }

                    var newPassword by remember { mutableStateOf("") }

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                        },
                        label = { Text("New Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.lock_icon),
                                contentDescription = null
                            )
                        },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )

                    var confirmNewPassword by remember { mutableStateOf("") }

                    OutlinedTextField(
                        value = confirmNewPassword,
                        onValueChange = {
                            confirmNewPassword = it
                        },
                        label = { Text("Re-Enter New Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.lock_icon),
                                contentDescription = null
                            )
                        },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Button(
                        onClick = {
                            if (isFromForgotPassScreen) {
                                resetPassword(newPassword, confirmNewPassword)
                            } else {
                                changePassword(currentPassword, newPassword, confirmNewPassword)
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .background(colorResource(id = R.color.colorPrimary))
                    ) {
                        Text(
                            text = "Submit",
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = colorResource(id = R.color.white),
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

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
                        )
                    }
                }
            }
        }
    }

    /**
     * Function to reset the password of the user with forgot password option
     */
    private fun resetPassword(newPassword: String, confirmNewPassword : String) {
        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmNewPassword)) {
            Toast.makeText(activity, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (newPassword == confirmNewPassword) {
            lifecycleScope.launch {
                try {
                    LoadingInstance.showLoading(requireActivity())
                    val response =
                        viewModel.resetPassword(email, newPassword)
                    if (response.isSuccessful && response.body() != null) {
                        if (!response.body()!!.error) {
                            Toast.makeText(
                                activity,
                                response.body()!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigate(R.id.action_changePasswordFragment_to_loginFragment)
                        } else {
                            Toast.makeText(
                                activity,
                                response.body()!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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
            Toast.makeText(
                activity,
                "Passwords doesn't match. Please verify again.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Function to change the password of the user
     */
    private fun changePassword(oldPassword: String, newPassword: String, confirmNewPassword: String) {
        if (oldPassword.isBlank()) {
            Toast.makeText(activity, "Please enter Current Password", Toast.LENGTH_SHORT).show()
        } else if (newPassword.isBlank()) {
            Toast.makeText(activity, "Please enter New Password", Toast.LENGTH_SHORT).show()
        } else if (confirmNewPassword.isBlank()) {
            Toast.makeText(activity, "Please confirm New Password", Toast.LENGTH_SHORT).show()
        } else {
            if (newPassword == confirmNewPassword) {
                lifecycleScope.launch {
                    try {
                        LoadingInstance.showLoading(requireActivity())
                        val response =
                            viewModel.changePassword(
                                preferences.getUserEmail(),
                                oldPassword,
                                newPassword,
                                preferences.getAuthToken()
                            )
                        if (response.isSuccessful && response.body() != null) {
                            if (!response.body()!!.error) {
                                Toast.makeText(
                                    activity,
                                    response.body()!!.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                findNavController().navigate(R.id.action_changePasswordFragment_to_accountFragment)
                            } else {
                                Toast.makeText(
                                    activity,
                                    response.body()!!.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
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
                Toast.makeText(
                    activity,
                    "Passwords don't match. Please verify again.",
                    Toast.LENGTH_SHORT
                ).show()
                Toast.makeText(activity, "Passwords don't match", Toast.LENGTH_SHORT).show()
            }
        }
    }

}