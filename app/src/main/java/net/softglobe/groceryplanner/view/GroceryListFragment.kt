package net.softglobe.groceryplanner.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.databinding.FragmentGroceryListBinding
import net.softglobe.groceryplanner.model.adapters.GroceryListAdapter
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import net.softglobe.groceryplanner.viewmodel.MainViewModelFactory

class GroceryListFragment : Fragment() {

    private lateinit var binding : FragmentGroceryListBinding

    private val viewModel by viewModels<MainViewModel>{ MainViewModelFactory(activity?.baseContext!!) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_grocery_list, container, false)
        // Inflate the layout for this fragment
        initView()
        return binding.root
    }

    private fun initView() {
        viewModel.getGroceryList().observe(viewLifecycleOwner) {
            binding.rvGroceryList.apply {
                adapter = GroceryListAdapter(requireActivity(), viewModel)
                layoutManager = LinearLayoutManager(activity?.baseContext!!)
                (binding.rvGroceryList.adapter as GroceryListAdapter).submitList(it)
            }
        }

        binding.btnAddRecord.setOnClickListener {
            findNavController().navigate(R.id.action_groceryListFragment_to_addGroceryFragment)
        }

    }
}