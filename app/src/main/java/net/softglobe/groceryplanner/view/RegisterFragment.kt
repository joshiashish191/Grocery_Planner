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
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.databinding.FragmentRegisterBinding
import net.softglobe.groceryplanner.model.network.User
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import net.softglobe.groceryplanner.viewmodel.MainViewModelFactory

class RegisterFragment : Fragment() {

    private lateinit var binding : FragmentRegisterBinding

    private val viewModel by viewModels<MainViewModel>{ MainViewModelFactory(activity?.baseContext!!) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_register, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.btnRegister.setOnClickListener{
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val reenterPassword = binding.etReenterPassword.text.toString().trim()
            if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank() && reenterPassword.isNotBlank()) {
                if (!password.equals(reenterPassword)) {
                    Toast.makeText(activity, "Password doesn't match", Toast.LENGTH_SHORT).show()
                } else {
                    val user = User(email, name, password)
                    lifecycleScope.launch {
                        try {
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
                        } catch (e : Exception) {
                            Toast.makeText(activity, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(activity, "Please enter all the fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}