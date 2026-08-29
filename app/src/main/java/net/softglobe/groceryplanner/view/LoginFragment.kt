package net.softglobe.groceryplanner.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
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
import net.softglobe.groceryplanner.model.LoadingInstance
import net.softglobe.groceryplanner.model.Preferences
import net.softglobe.groceryplanner.ui.theme.GroceryPlannerTheme
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import net.softglobe.groceryplanner.viewmodel.MainViewModelFactory


class LoginFragment : Fragment() {

    private val viewModel by viewModels<MainViewModel>{ MainViewModelFactory(activity?.baseContext!!) }
    private lateinit var preferences : Preferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initView()
        return ComposeView(requireContext()).apply {
            setContent {
                GroceryPlannerTheme {
                    LoginView()
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun LoginView() {
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
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Login",
                        style = TextStyle(
                            fontSize = 40.sp,
                            color = colorResource(id = R.color.colorPrimary),
                            fontWeight = FontWeight.Bold
                        )
                    )

                    var email by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                        },
                        label = { Text("Enter Email") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.email_icon),
                                contentDescription = null
                            )
                        },
                        singleLine = true
                    )

                    var passwordToggleState by remember { mutableStateOf(true) }

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                        },
                        label = { Text("Enter Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.lock_icon),
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = if (passwordToggleState) painterResource(id = R.drawable.visibility) else painterResource(id = R.drawable.visibility_off),
                                contentDescription = null,
                                modifier = Modifier.clickable{
                                    passwordToggleState = !passwordToggleState
                                }
                            )
                        },
                        singleLine = true,
                        visualTransformation = if (passwordToggleState) PasswordVisualTransformation() else VisualTransformation.None
                    )

                    Button(
                        onClick = {
                            loginUser(email, password)
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                            .background(colorResource(id = R.color.colorPrimary))
                    ) {
                        Text(
                            text = "Login",
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = colorResource(id = R.color.white),
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(vertical = 15.dp)
                        )
                    }

                    Text(
                        text = "Forgot Password?",
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = colorResource(id = R.color.colorPrimary)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .clickable {
                                findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
                            },
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Bold
                    )

                    // Push the "Register Here" text to the bottom of the Column
                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Don't have an account?",
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 18.sp
                            )
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = "Register Here",
                            style = TextStyle(
                                color = colorResource(id = R.color.colorPrimary),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .clickable {
                                    findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
                                }
                        )
                    }

                    if (!preferences.isPaidUser()) {
                        AndroidView(
                            factory = { context ->
                                AdView(context).apply {
                                    setAdSize(AdSize.BANNER)
                                    adUnitId = "your-ad-id"
                                    loadAd(AdRequest.Builder().build())
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                }
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        if (email.isNotBlank() && password.isNotBlank()) {
            lifecycleScope.launch {
                try {
                    LoadingInstance.showLoading(requireActivity())
                    val response = viewModel.loginUser(email, password)
                    if (response.isSuccessful && response.body() != null) {
                        if (!response.body()!!.result.error) {
                            Toast.makeText(
                                activity,
                                response.body()!!.result.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            preferences.setUserEmail(email)
                            preferences.setUserLoginStatus(true)
                            preferences.setAuthToken(response.body()!!.user.authToken)
                            if (response.body()!!.user.isPaidUser == 0)
                                preferences.setUserPaidStatus(false)
                            else
                                preferences.setUserPaidStatus(true)
                            findNavController().navigate(R.id.action_loginFragment_to_accountFragment)
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
            Toast.makeText(activity, "Please enter all the fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initView() {
        preferences = Preferences(activity?.applicationContext!!)
    }
}