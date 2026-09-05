package net.softglobe.groceryplanner.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.model.Constants.KEY_IS_FROM_FORGOT_PASS_SCREEN
import net.softglobe.groceryplanner.model.Constants.KEY_EMAIL
import net.softglobe.groceryplanner.model.LoadingInstance
import net.softglobe.groceryplanner.ui.theme.GroceryPlannerTheme
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import net.softglobe.groceryplanner.viewmodel.MainViewModelFactory

class ForgotPasswordFragment : Fragment() {

    private val viewModel by viewModels<MainViewModel>{ MainViewModelFactory(activity?.baseContext!!) }

    private var code : String? =  null
    private var isMailSent by mutableStateOf(false)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        return ComposeView(requireContext()).apply {
            setContent {
                GroceryPlannerTheme {
                    ForgotPasswordView()
                }
            }
        }
    }

    private fun verifyCode(userEnteredCode: String, email: String) {
        if (userEnteredCode == code) {
            val bundle = Bundle()
            bundle.putBoolean(KEY_IS_FROM_FORGOT_PASS_SCREEN, true)
            bundle.putString(KEY_EMAIL, email)
            findNavController().navigate(R.id.action_forgotPasswordFragment_to_changePasswordFragment, bundle)
        } else {
            Toast.makeText(activity, "Incorrect code entered. Please try again", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getRandomString(): String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..6)
            .map { charset.random() }
            .joinToString("")
    }

    private fun forgotPassword(email : String) {
        code = getRandomString()
        if (email.isNotBlank()) {
            lifecycleScope.launch {
                try {
                    LoadingInstance.showLoading(requireActivity())
                    val response = viewModel.forgotPassword(email, code!!)
                    if (response.isSuccessful && response.body() != null) {
                        if (!response.body()!!.error) {
                            Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                            isMailSent = true
                        } else {
                            Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(activity, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show()
                    }
                } catch (e : Exception) {
                    Toast.makeText(activity, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show()
                } finally {
                    LoadingInstance.hideLoading()
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun ForgotPasswordView() {
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
                        "Forgot Password",
                        style = TextStyle(
                            fontSize = 25.sp,
                            color = Color.Black
                        )
                    )
                    Text(
                        "Enter email below and we will send you one time code to reset your password",
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp)
                    )

                    var email by remember { mutableStateOf("") }

                    OutlinedTextField(
                        enabled = !isMailSent,
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

                    Button(
                        enabled = !isMailSent,
                        onClick = {
                            forgotPassword(email)
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

                    if (isMailSent) {
                        Text(
                            "Email sent successfully. Please check your inbox and enter one time code below.",
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp)
                        )

                        var otp by remember { mutableStateOf("") }

                        OutlinedTextField(
                            value = otp,
                            onValueChange = {
                                otp = it
                            },
                            label = { Text("Enter one time code") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.lock_icon),
                                    contentDescription = null
                                )
                            },
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                verifyCode(otp, email)
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
                    }
                }
            }
        }
    }
}