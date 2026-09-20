package net.softglobe.groceryplanner.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.model.Grocery
import net.softglobe.groceryplanner.model.Modification
import net.softglobe.groceryplanner.model.Preferences
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import net.softglobe.groceryplanner.viewmodel.MainViewModelFactory
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AddGroceryFragment : Fragment() {

    private lateinit var currentGroceryItem : Grocery
    var id: Int? = null
    private lateinit var preferences : Preferences
    private var totalGroceryItemsCount : Int? = null

    var itemName by  mutableStateOf("")
    var itemDescription by  mutableStateOf("")
    var quantity by mutableStateOf("")
    var unit by mutableStateOf("")
    var storedAt by mutableStateOf("")
    var lowStockValue by mutableStateOf("")
    private var modificationsList by mutableStateOf<List<Modification>>(emptyList())

    private val viewModel by viewModels<MainViewModel> { MainViewModelFactory(activity?.baseContext!!) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        inItView()
        return ComposeView(requireContext()).apply {
            setContent {
                AddGroceryView()
            }
        }
    }

    @Preview
    @Composable
    private fun AddGroceryView() {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = {
                            itemName = it
                        },
                        label = { Text("Item Name*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = itemDescription,
                        onValueChange = {
                            itemDescription = it
                        },
                        label = { Text("Item Description*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp)
                    ) {
                        OutlinedTextField(
                            value = quantity,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            onValueChange = {
                                quantity = it
                            },
                            label = { Text("Quantity*") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = unit,
                            onValueChange = {
                                unit = it
                            },
                            label = { Text("Unit*") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = storedAt,
                        onValueChange = {
                            storedAt = it
                        },
                        label = { Text("Where it is kept") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = lowStockValue,
                        onValueChange = {
                            lowStockValue = it
                        },
                        label = { Text("Low stock indication value") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            shape = RoundedCornerShape(8.dp),
                            onClick = {
                                saveGroceryItem()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save")
                        }

                        OutlinedButton(
                            shape = RoundedCornerShape(8.dp),
                            onClick = {
                                checkModificationsOnBackPressed()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        ) {
                            Text("Cancel")
                        }
                    }

                    if (id != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Last Modifications",
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    color = Color.Black,
                                )
                            )
                            TextButton(
                                onClick = {
                                    AlertDialog.Builder(activity)
                                        .setTitle("Confirm delete")
                                        .setMessage("Do you really want to clear all the modification records? This action can't be undone!")
                                        .setPositiveButton("Yes, I confirm") { dialog, position ->
                                            id?.let { id ->
                                                viewModel.deleteAllModificationsByGroceryItemId(
                                                    id
                                                )
                                            }
                                        }
                                        .setNegativeButton("Cancel") { dialog, position -> }
                                        .show()
                                }
                            ) {
                                Text(
                                    text = "CLEAR ALL",
                                    style = TextStyle(
                                        fontSize = 15.sp,
                                        color = colorResource(id = R.color.colorPrimary),
                                        fontWeight = FontWeight.Bold
                                    ),
                                )
                            }
                        }
                    }
                }

                if (id != null) {
                    if (modificationsList.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        ) {
                            items(modificationsList) { modificationRecord ->
                                ModificationItemView(modificationRecord)
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            NoRecordsView()
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                if (!preferences.isPaidUser()) {
                    AndroidView(
                        factory = { context ->
                            AdView(context).apply {
                                setAdSize(AdSize.BANNER)
                                adUnitId = getString(R.string.banner_ad_unit_ad)
                                loadAd(AdRequest.Builder().build())
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    )
                }
            }
        }
    }

    /**
    * Modification item layout composable view
    * */
    @Composable
    private fun ModificationItemView(item : Modification) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    text = SimpleDateFormat(
                        "dd/MM/yyyy, HH:mm", Locale.ENGLISH
                    ).format(item.modifiedOn),
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = Color.Black,
                    )
                )
                Text(
                    text = item.description,
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = Color.Black,
                    )
                )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                )
            }
        }
    }


    /***
    * No records view composable
    * */
    @Preview(showBackground = true)
    @Composable
    private fun NoRecordsView() {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No records found",
                style = TextStyle(
                    fontSize = 20.sp,
                    color = Color.Black,
                )
            )
        }
    }

    private fun inItView() {
        preferences = Preferences(activity?.applicationContext!!)

        arguments?.let {
            id = it.getInt("id")
        }

        if (id != null) {
            lifecycleScope.launch {
                val item = viewModel.getGroceryItem(id!!)
                currentGroceryItem = item
                if (item.lowStockValue != 0.0) {
                    lowStockValue = item.lowStockValue.toString()
                }
                itemName = item.name
                itemDescription = item.description
                quantity = item.quantity.toString()
                unit = item.unit
                storedAt = item.storedAt
            }
            val itemId = id

            if (itemId != null) {
                viewModel.getModificationsList(itemId)
            }

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.modificationsList.collect { list ->
                        modificationsList = list
                    }
                }
            }
        } else {
            lifecycleScope.launch(Dispatchers.IO) {
                totalGroceryItemsCount = viewModel.getGroceryItemsCount()
            }
        }

        //region Back pressed event logic
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                checkModificationsOnBackPressed()
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
        //endregion
    }

    private fun checkModificationsOnBackPressed() {
        if (id != null) {
            if (isAnyFieldModified(itemName, itemDescription, quantity, unit, storedAt, lowStockValue)
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
            true
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
        val modificationMsg: String

        if (itemName.isBlank()) {
            Toast.makeText(activity, "Please enter Item Name", Toast.LENGTH_SHORT).show()
        } else if (quantity.isBlank()) {
            Toast.makeText(activity, "Please enter Item Quantity", Toast.LENGTH_SHORT).show()
        } else if (unit.isBlank()) {
            Toast.makeText(activity, "Please enter Item Unit", Toast.LENGTH_SHORT).show()
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
                        itemName,
                        itemDescription,
                        Date(),
                        quantity.toDouble(),
                        unit,
                        storedAt,
                        newLowStockValue
                    ),
                    Modification(Date(), modificationMsg)
                )
            } else {
                modificationMsg = trackModificationsAndCreateEditMessage(itemName, itemDescription, quantity, unit, storedAt, lowStockValue)
                viewModel.insertGroceryItem(
                    Grocery(
                        itemName,
                        itemDescription,
                        Date(),
                        quantity.toDouble(),
                        unit,
                        storedAt,
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

    private fun trackModificationsAndCreateEditMessage(
        name: String,
        description: String,
        quantity: String,
        unit: String,
        storedAt: String,
        lowStockValue: String
    ): String {
        var result = ""
        if (!isAnyFieldModified(name, description, quantity, unit, storedAt, lowStockValue)) {
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
            if (!currentGroceryItem.storedAt.equals(storedAt)) {
                if (result.isNotBlank())
                    result += ", "
                result += "Item stored location modified ${currentGroceryItem.storedAt} to $storedAt"
            }
            if (isLowStockValueModified(lowStockValue)) {
                if (result.isNotBlank())
                    result += ", "
                val format = DecimalFormat("0.#")
                var newLowStockValue = "0"
                if (!lowStockValue.isBlank())
                    newLowStockValue = format.format(lowStockValue.toDouble())
                result += "Item low stock value modified ${format.format(currentGroceryItem.lowStockValue)} to $newLowStockValue"
            }
        }
        return result
    }
}