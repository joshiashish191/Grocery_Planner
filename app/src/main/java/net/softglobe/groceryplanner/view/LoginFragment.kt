package net.softglobe.groceryplanner.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.databinding.FragmentLoginBinding
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import net.softglobe.groceryplanner.viewmodel.MainViewModelFactory

class LoginFragment : Fragment() {

    private lateinit var binding : FragmentLoginBinding

    private val viewModel by viewModels<MainViewModel>{ MainViewModelFactory(activity?.baseContext!!) }

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

        binding.btnLogin.setOnClickListener {
            var email = binding.etEmail.text.toString()
            var password = binding.etPassword.text.toString()

            if (email.isNotBlank() && password.isNotBlank()) {
                lifecycleScope.launch {
                    val response = viewModel.loginUser(email, password)
                    if (response.isSuccessful && response.body() != null) {
                        if (!response.body()!!.result.error) {
                            Toast.makeText(activity, response.body()!!.result.message, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(activity, response.body()!!.result.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(activity, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(activity, "Please enter all the fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}