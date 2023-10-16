package net.softglobe.groceryplanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import net.softglobe.groceryplanner.databinding.FragmentForgotPasswordBinding
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import net.softglobe.groceryplanner.viewmodel.MainViewModelFactory

class ForgotPasswordFragment : Fragment() {

    lateinit var binding : FragmentForgotPasswordBinding
    private val viewModel by viewModels<MainViewModel>{ MainViewModelFactory(activity?.baseContext!!) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_forgot_password, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.submitEmailBtn.setOnClickListener {
            val email = binding.etEnterEmail.text.toString()
            val code = getRandomString()
            if (email.isNotBlank()) {
                lifecycleScope.launch {
                    try {
                        val response = viewModel.forgotPassword(email, code)
                        if (response.isSuccessful && response.body() != null) {
                            if (!response.body()!!.error) {
                                Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                                binding.mailSentText.visibility = View.VISIBLE
                                binding.etCode.visibility = View.VISIBLE
                                binding.submitCodeBtn.visibility = View.VISIBLE

                                binding.etEnterEmail.isEnabled = false
                                binding.submitEmailBtn.isEnabled = false
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
            }
        }
    }

    private fun getRandomString(): String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..6)
            .map { charset.random() }
            .joinToString("")
    }
}