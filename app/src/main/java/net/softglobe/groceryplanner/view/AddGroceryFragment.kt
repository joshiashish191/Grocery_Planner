package net.softglobe.groceryplanner.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.databinding.FragmentAddGroceryBinding
import net.softglobe.groceryplanner.model.Grocery
import net.softglobe.groceryplanner.model.Modification
import net.softglobe.groceryplanner.model.Preferences
import net.softglobe.groceryplanner.model.adapters.ModificationsListAdapter
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import net.softglobe.groceryplanner.viewmodel.MainViewModelFactory
import java.text.DecimalFormat
import java.util.Date


class AddGroceryFragment : Fragment() {

    private lateinit var binding: FragmentAddGroceryBinding
    private lateinit var currentGroceryItem : Grocery
    var id: Int? = null
    private lateinit var preferences : Preferences
    private var totalGroceryItemsCount : Int? = null

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
        preferences = Preferences(activity?.applicationContext!!)
        if (!preferences.isPaidUser()) {
            binding.bannerAdView.visibility = View.VISIBLE
            val adRequest = AdRequest.Builder().build()
            binding.bannerAdView.loadAd(adRequest)
        }

        arguments?.let {
            id = it.getInt("id")
        }

        if (id != null) {
            binding.modTitle.visibility = View.VISIBLE
            binding.clrAllModBtn.visibility = View.VISIBLE
            binding.rvModifications.visibility = View.VISIBLE
            lifecycleScope.launch {
                val item = viewModel.getGroceryItem(id!!)
                currentGroceryItem = item
                binding.grocery = item
                if (item.lowStockValue != 0.0)
                    binding.etLowStockValue.setText(item.lowStockValue.toString())
                binding.etQty.setText(item.quantity.toString())
            }
            val itemId = id
            viewModel.getModificationsList(id!!).observe(viewLifecycleOwner) {
                if (it.isEmpty()) {
                    binding.noRecordsTitle.visibility = View.VISIBLE
                    binding.clrAllModBtn.isEnabled = false
                }
                else {
                    binding.noRecordsTitle.visibility = View.GONE
                    binding.clrAllModBtn.isEnabled = true
                }
                binding.rvModifications.apply {
                    adapter = ModificationsListAdapter(itemId!!, viewModel)
                    layoutManager = LinearLayoutManager(activity?.baseContext!!)
                    (binding.rvModifications.adapter as ModificationsListAdapter).submitList(it)
                }
            }
        } else {
            lifecycleScope.launch(Dispatchers.IO) {
                totalGroceryItemsCount = viewModel.getGroceryItemsCount()
            }
        }

        binding.btnSave.setOnClickListener {
            saveGroceryItem()
        }

        binding.btnCancel.setOnClickListener {
            checkModificationsOnBackPressed()
        }

        binding.clrAllModBtn.setOnClickListener {
            AlertDialog.Builder(activity)
                .setTitle("Confirm delete")
                .setMessage("Do you really want to clear all the modification records? This action can't be undone!")
                .setPositiveButton("Yes, I confirm") {dialog, position ->
                    id?.let { id ->
                        viewModel.deleteAllModificationsByGroceryItemId(id)
                    }
                }
                .setNegativeButton("Cancel") {dialog, position -> }
                .show()
        }


        //region Back pressed event logic
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                checkModificationsOnBackPressed()
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
        //endregion

        callOnTextChangeListeners()
    }

    private fun checkModificationsOnBackPressed() {
        if (id != null) {
            val name = binding.etName.text.toString()
            val description = binding.etDesc.text.toString()
            val quantity = binding.etQty.text.toString()
            val unit = binding.etUnit.text.toString()
            val keptAt = binding.etKeptAt.text.toString()
            val lowStockValue = binding.etLowStockValue.text.toString()

            if (isAnyFieldModified(name, description, quantity, unit, keptAt, lowStockValue)
            ) {
                showConfirmChangesDialog()
            } else
                findNavController().popBackStack()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun isAnyFieldModified(
        name: String,
        description: String,
        quantity: String,
        unit: String,
        keptAt: String,
        lowStockValue: String,
    ): Boolean {
        return !currentGroceryItem.name.equals(name) ||
                 !currentGroceryItem.description.equals(description) ||
                isQuantityModified(quantity) ||
                 !currentGroceryItem.unit.equals(unit) ||
                 !currentGroceryItem.storedAt.equals(keptAt) ||
                isLowStockValueModified(lowStockValue)
    }

    private fun isLowStockValueModified(lowStockValue :String): Boolean {
        return if (lowStockValue.isBlank()) {
            currentGroceryItem.lowStockValue  != 0.0
        } else {
            currentGroceryItem.lowStockValue != lowStockValue.toDouble()
        }
    }

    private fun isQuantityModified(quantity :String): Boolean {
        return if (quantity.isBlank()) {
            return true
        } else {
            currentGroceryItem.quantity != quantity.toDouble()
        }
    }

    private fun showConfirmChangesDialog() {
        AlertDialog.Builder(activity)
            .setTitle("Confirm changes")
            .setMessage("Do you want to save the changes you made?")
            .setPositiveButton("Yes") {dialog, position ->
                saveGroceryItem()
            }
            .setNegativeButton("No") {dialog, position ->
                findNavController().popBackStack()
            }
            .show()
    }

    private fun saveGroceryItem() {
        val name = binding.etName.text.toString().trim()
        val description = binding.etDesc.text.toString().trim()
        val quantity = binding.etQty.text.toString().trim()
        val unit = binding.etUnit.text.toString().trim()
        val keptAt = binding.etKeptAt.text.toString().trim()
        val lowStockValue = binding.etLowStockValue.text.toString().trim()
        val modificationMsg: String

        if (name.isBlank()) {
            Toast.makeText(activity, "Please enter Item Name", Toast.LENGTH_SHORT).show()
            binding.tilName.error = "Item Name is required"
        } else if (quantity.isBlank()) {
            Toast.makeText(activity, "Please enter Item Quantity", Toast.LENGTH_SHORT).show()
            binding.tilQty.error = "Quantity is required"
        } else if (unit.isBlank()) {
            Toast.makeText(activity, "Please enter Item Unit", Toast.LENGTH_SHORT).show()
            binding.tilUnit.error = "Unit is required"
        } else {
            var newLowStockValue = 0.0
            if (lowStockValue.isNotBlank()) {
                newLowStockValue = lowStockValue.toDouble()
            }

            var toastMsg = "Item Added"
            if (id == null) {
                if (!preferences.isPaidUser()
                    && totalGroceryItemsCount!! >= Integer.parseInt(preferences.getGroceryRecordsLimitForFree())) {
                    if (!preferences.isUserLoggedIn()) {
                        findNavController().navigate(R.id.action_addGroceryFragment_to_loginFragment)
                        Toast.makeText(activity, "Please login to continue", Toast.LENGTH_SHORT).show()
                    } else {
                        findNavController().navigate(R.id.action_addGroceryFragment_to_upgradePlanFragment)
                    }
                    return
                }
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
                modificationMsg = trackModificationsAndCreateEditMessage(name, description, quantity, unit, keptAt, lowStockValue)
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

    private fun callOnTextChangeListeners() {
        binding.etName.doOnTextChanged { text, start, before, count ->
            if (count > 0) {
                binding.tilName.error = null
                binding.tilName.isErrorEnabled = false
            }
        }
        binding.etQty.doOnTextChanged { text, start, before, count ->
            if (text?.length!! > 0) {
                binding.tilDesc.error = null
                binding.tilDesc.isErrorEnabled = false
            }
        }
        binding.etUnit.doOnTextChanged { text, start, before, count ->
            if (count > 0) {
                binding.tilUnit.error = null
                binding.tilUnit.isErrorEnabled = false
            }
        }
    }

    private fun trackModificationsAndCreateEditMessage(
        name: String,
        description: String,
        quantity: String,
        unit: String,
        keptAt: String,
        lowStockValue: String
    ): String {
        var result = ""
        if (!isAnyFieldModified(name, description, quantity, unit, keptAt, lowStockValue)) {
            result = "No changes made"
        } else {
            if (!currentGroceryItem.name.equals(name))
                result += "Item name modified ${currentGroceryItem.name} to $name"
            if (!currentGroceryItem.description.equals(description)) {
                if (result.isNotBlank())
                    result += ", "
                result += "Item description modified ${currentGroceryItem.description} to $description"
            }
            if (isQuantityModified(quantity)) {
                if (result.isNotBlank())
                    result += ", "
                val format = DecimalFormat("0.#")
                result += "Item quantity modified ${format.format(currentGroceryItem.quantity)} to ${format.format(quantity.toDouble())}"
            }
            if (!currentGroceryItem.unit.equals(unit)) {
                if (result.isNotBlank())
                    result += ", "
                result += "Item unit modified ${currentGroceryItem.unit} to $unit"
            }
            if (!currentGroceryItem.storedAt.equals(keptAt)) {
                if (result.isNotBlank())
                    result += ", "
                result += "Item stored location modified ${currentGroceryItem.storedAt} to $keptAt"
            }
            if (isLowStockValueModified(lowStockValue)) {
                if (result.isNotBlank())
                    result += ", "
                val format = DecimalFormat("0.#")
                var newLowStockvalue = "0"
                if (!lowStockValue.isBlank())
                    newLowStockvalue = format.format(lowStockValue.toDouble())
                result += "Item low stock value modified ${format.format(currentGroceryItem.lowStockValue)} to $newLowStockvalue"
            }
        }
        return result
    }
}