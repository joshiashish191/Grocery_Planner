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
import net.softglobe.groceryplanner.model.network.User
import net.softglobe.groceryplanner.ui.theme.GroceryPlannerTheme
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import net.softglobe.groceryplanner.viewmodel.MainViewModelFactory

class RegisterFragment : Fragment() {

    private val viewModel by viewModels<MainViewModel>{ MainViewModelFactory(activity?.baseContext!!) }
    private lateinit var preferences: Preferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initView()
        return ComposeView(requireContext()).apply {
            setContent {
                GroceryPlannerTheme {
                    RegisterView()
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun RegisterView() {
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
                        text = "Register",
                        style = TextStyle(
                            fontSize = 40.sp,
                            color = colorResource(id = R.color.colorPrimary),
                            fontWeight = FontWeight.Bold
                        )
                    )

                    var name by remember { mutableStateOf("") }
                    var email by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    var reenterPassword by remember { mutableStateOf("") }

                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                        },
                        label = { Text("Enter Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.profile_icon),
                                contentDescription = null
                            )
                        },
                        singleLine = true
                    )

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
                                modifier = Modifier.clickable {
                                    passwordToggleState = !passwordToggleState
                                }
                            )
                        },
                        singleLine = true,
                        visualTransformation = if (passwordToggleState) PasswordVisualTransformation() else VisualTransformation.None
                    )

                    var reenterPasswordToggleState by remember { mutableStateOf(true) }

                    OutlinedTextField(
                        value = reenterPassword,
                        onValueChange = {
                            reenterPassword = it
                        },
                        label = { Text("Re-Enter Password") },
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
                                painter = if (reenterPasswordToggleState) painterResource(id = R.drawable.visibility) else painterResource(id = R.drawable.visibility_off),
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    reenterPasswordToggleState = !reenterPasswordToggleState
                                }
                            )
                        },
                        singleLine = true,
                        visualTransformation = if (reenterPasswordToggleState) PasswordVisualTransformation() else VisualTransformation.None
                    )

                    Button(
                        onClick = {
                            registerUser(name, email, password, reenterPassword)
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                            .background(colorResource(id = R.color.colorPrimary))
                    ) {
                        Text(
                            text = "Register",
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = colorResource(id = R.color.white),
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(vertical = 15.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Already have an account? ",
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 18.sp
                            )
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = "Login Here",
                            style = TextStyle(
                                color = colorResource(id = R.color.colorPrimary),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .clickable {
                                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                                }
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    if (::preferences.isInitialized && !preferences.isPaidUser()) {
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

    private fun registerUser(name: String, email: String, password: String, reenterPassword: String) {
        if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank() && reenterPassword.isNotBlank()) {
            if (!password.equals(reenterPassword)) {
                Toast.makeText(activity, "Password doesn't match", Toast.LENGTH_SHORT).show()
            } else {
                val user = User(email, name, password)
                lifecycleScope.launch {
                    try {
                        LoadingInstance.showLoading(requireActivity())
                        val response = viewModel.registerUser(user)
                        if (response.isSuccessful && response.body() != null) {
                            if (!response.body()!!.error) {
                                Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                            } else {
                                Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(activity, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(activity, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show()
                    } finally {
                        LoadingInstance.hideLoading()
                    }
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
