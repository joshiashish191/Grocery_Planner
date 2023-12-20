package net.softglobe.groceryplanner.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import kotlinx.coroutines.launch
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.databinding.FragmentLoginBinding
import net.softglobe.groceryplanner.model.LoadingInstance
import net.softglobe.groceryplanner.model.Preferences
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import net.softglobe.groceryplanner.viewmodel.MainViewModelFactory


class LoginFragment : Fragment() {

    private lateinit var binding : FragmentLoginBinding

    private val viewModel by viewModels<MainViewModel>{ MainViewModelFactory(activity?.baseContext!!) }
    private lateinit var preferences : Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //(activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_login, container, false)
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
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotBlank() && password.isNotBlank()) {
                lifecycleScope.launch {
                    try {
                        LoadingInstance.showLoading(requireActivity())
                        val response = viewModel.loginUser(email, password)
                        if (response.isSuccessful && response.body() != null) {
                            if (!response.body()!!.result.error) {
                                Toast.makeText(activity, response.body()!!.result.message, Toast.LENGTH_SHORT).show()
                                preferences.setUserEmail(email)
                                preferences.setUserLoginStatus(true)
                                preferences.setAuthToken(response.body()!!.user.authToken)
                                if (response.body()!!.user.isPaidUser == 0)
                                    preferences.setUserPaidStatus(false)
                                else
                                    preferences.setUserPaidStatus(true)
                                findNavController().navigate(R.id.action_loginFragment_to_accountFragment)
                            } else {
                                Toast.makeText(activity, response.body()!!.result.message, Toast.LENGTH_SHORT).show()
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
            } else {
                Toast.makeText(activity, "Please enter all the fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.register.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.forgotPass.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
    }
}