package net.softglobe.groceryplanner.view

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.databinding.FragmentAddGroceryBinding
import net.softglobe.groceryplanner.model.Grocery
import net.softglobe.groceryplanner.model.Modification
import net.softglobe.groceryplanner.model.adapters.ModificationsListAdapter
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import net.softglobe.groceryplanner.viewmodel.MainViewModelFactory
import java.util.Date


class AddGroceryFragment : Fragment() {

    private val TAG: String = "AddGroceryFragment"
    private lateinit var binding: FragmentAddGroceryBinding
    var id: Int? = null

    private val viewModel by viewModels<MainViewModel> { MainViewModelFactory(activity?.baseContext!!) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_add_grocery, container, false)
        inItView()
        return binding.root
    }

    private fun inItView() {
        arguments?.let {
            id = it.getInt("id")
        }

        if (id == null) {
            binding.etEditInfo.visibility = View.GONE
        } else {
            binding.modTitle.visibility = View.VISIBLE
            binding.rvModifications.visibility = View.VISIBLE
            lifecycleScope.launch {
                val item = viewModel.getGroceryItem(id!!)
                binding.grocery = item
                if (item.lowStockValue != 0.0)
                    binding.etLowStockValue.setText(item.lowStockValue.toString())
                binding.etQty.setText(item.quantity.toString())
            }
            Log.d(TAG, "id: $id")
            viewModel.getModificationsList(id!!).observe(viewLifecycleOwner) {
                Log.d(TAG, "ModificationsList $it")
                binding.rvModifications.apply {
                    adapter = ModificationsListAdapter()
                    layoutManager = LinearLayoutManager(activity?.baseContext!!)
                    (binding.rvModifications.adapter as ModificationsListAdapter).submitList(it)
                }
            }
        }

        binding.btnSave.setOnClickListener {
            saveGroceryItem()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun saveGroceryItem() {
        val name = binding.etName.text.toString()
        val description = binding.etDesc.text.toString()
        val quantity = binding.etQty.text.toString()
        val unit = binding.etUnit.text.toString()
        val keptAt = binding.etKeptAt.text.toString()
        val lowStockValue = binding.etLowStockValue.text.toString()
        var modificationMsg = binding.etEditInfo.text.toString()
        if (name.isNullOrEmpty()) {
            Toast.makeText(activity, "Please enter Item Name", Toast.LENGTH_SHORT).show()
        } else if (quantity.isNullOrEmpty()) {
            Toast.makeText(activity, "Please enter Item Quantity", Toast.LENGTH_SHORT).show()
        } else if (unit.isNullOrEmpty()) {
            Toast.makeText(activity, "Please enter Item Unit", Toast.LENGTH_SHORT).show()
        } else {
            var newLowStockValue = 0.0
            if (!lowStockValue.isEmpty()) {
                newLowStockValue = lowStockValue.toDouble()
            }

            var toastMsg = "Item Added"
            if (id == null) {
                modificationMsg = "Item Added"
                viewModel.insertGroceryItem(
                    Grocery(
                        name,
                        description,
                        Date(),
                        quantity.toDouble(),
                        unit,
                        keptAt,
                        newLowStockValue
                    ),
                    Modification(Date(), modificationMsg)
                )
            } else {
                if (TextUtils.isEmpty(modificationMsg))
                    modificationMsg = "No Info provided"
                viewModel.insertGroceryItem(
                    Grocery(
                        name,
                        description,
                        Date(),
                        quantity.toDouble(),
                        unit,
                        keptAt,
                        newLowStockValue,
                        id
                    ),
                    Modification(Date(), modificationMsg)
                )
                toastMsg = "Item Modified"
            }
            findNavController().popBackStack()
            Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
        }
    }
}