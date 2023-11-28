package net.softglobe.groceryplanner.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.databinding.FragmentPaymentSuccessBinding

class PaymentSuccessFragment : Fragment() {
    lateinit var binding : FragmentPaymentSuccessBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater,
            R.layout.fragment_payment_success, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.btnContinueToApp.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}