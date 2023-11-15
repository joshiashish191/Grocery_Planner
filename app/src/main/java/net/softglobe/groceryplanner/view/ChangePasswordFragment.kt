package net.softglobe.groceryplanner.view

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.databinding.FragmentChangePasswordBinding
import net.softglobe.groceryplanner.model.Constants.KEY_EMAIL
import net.softglobe.groceryplanner.model.Constants.KEY_IS_FROM_FORGOT_PASS_SCREEN
import net.softglobe.groceryplanner.model.Preferences
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import net.softglobe.groceryplanner.viewmodel.MainViewModelFactory

class ChangePasswordFragment : Fragment() {

    lateinit var binding : FragmentChangePasswordBinding
    private val viewModel by viewModels<MainViewModel>{ MainViewModelFactory(activity?.baseContext!!) }
    private lateinit var preferences : Preferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater,
            R.layout.fragment_change_password, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        preferences = Preferences(activity?.applicationContext!!)
        var isFromForgotPassScreen = false
        var email = ""
        if (arguments != null) {
            isFromForgotPassScreen = arguments?.getBoolean(KEY_IS_FROM_FORGOT_PASS_SCREEN)!!
            if (isFromForgotPassScreen) {
                email = arguments?.getString(KEY_EMAIL)!!
            }
        }

        if (!isFromForgotPassScreen) {
            //Normal change password scenario
            binding.etCurrentPassword.visibility = View.VISIBLE
        }

        binding.submitBtn.setOnClickListener {
            if (isFromForgotPassScreen) {
                //forgot pass scenario
                val newPassword = binding.etNewPassword.text.toString()
                val confirmNewPassword = binding.etReEnterNewPassword.text.toString()
                if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmNewPassword)) {
                    Toast.makeText(activity, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (newPassword == confirmNewPassword) {
                    lifecycleScope.launch {
                        try {
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
                        }
                    }
                } else {
                    Toast.makeText(
                        activity,
                        "Passwords doesn't match. Please verify again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val oldPassword = binding.etCurrentPassword.text.toString()
                val newPassword = binding.etNewPassword.text.toString()
                val confirmNewPassword = binding.etReEnterNewPassword.text.toString()

                if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmNewPassword)) {
                    Toast.makeText(activity, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (newPassword == confirmNewPassword) {
                    lifecycleScope.launch {
                        try {
                            val response =
                                viewModel.changePassword(preferences.getUserEmail(), oldPassword, newPassword, preferences.getAuthToken())
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
        }
    }
}