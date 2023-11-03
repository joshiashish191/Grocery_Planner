package net.softglobe.groceryplanner.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.databinding.FragmentProfileBinding
import net.softglobe.groceryplanner.model.Preferences

class ProfileFragment : Fragment() {

    lateinit var binding : FragmentProfileBinding
    private lateinit var preferences : Preferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater,
            R.layout.fragment_profile, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        preferences = Preferences(activity?.applicationContext!!)
        binding.txtChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_changePasswordFragment)
        }

        binding.txtLogout.setOnClickListener {
            preferences.clearAllPreferences()
            findNavController().navigate(R.id.action_accountFragment_to_loginFragment)
        }
    }
}