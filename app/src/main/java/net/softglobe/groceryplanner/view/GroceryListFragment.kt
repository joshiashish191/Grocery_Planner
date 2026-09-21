package net.softglobe.groceryplanner.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.model.Grocery
import net.softglobe.groceryplanner.model.Preferences
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import net.softglobe.groceryplanner.viewmodel.MainViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class GroceryListFragment : Fragment() {

    private val viewModel by viewModels<MainViewModel>{ MainViewModelFactory(activity?.baseContext!!) }
    private lateinit var preferences : Preferences
    private var groceryList by mutableStateOf<List<Grocery>>(emptyList())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        initView()
        return ComposeView(requireContext()).apply {
            setContent {
                GroceryListView()
            }
        }
    }

    @Preview
    @Composable
    private fun GroceryListView() {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        findNavController().navigate(R.id.action_groceryListFragment_to_addGroceryFragment)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add grocery item"
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                ) {
                    items(groceryList.size) {
                        GroceryListItem(groceryList[it])
                    }
                }

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                if (::preferences.isInitialized && !preferences.isPaidUser()) {
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

    private fun deleteGroceryItem(id: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Grocery Item")
        builder.setMessage(R.string.delete_warning)
            .setPositiveButton("Yes") { dialog, which ->
                viewModel.deleteGroceryItem(id)
                Toast.makeText(requireContext(), "Item deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, which ->
            }
        builder.show()
    }

    private fun openChooserDialog(id : Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose an action")
        val options = arrayOf("Delete")
        builder.setItems(options) { dialog, position ->
            if (position == 0) {
                deleteGroceryItem(id)
            }
        }
        builder.show()
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun GroceryListItem(
        groceryItem : Grocery
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            border = if (groceryItem.quantity <= groceryItem.lowStockValue) BorderStroke(2.dp, Color.Red) else null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = {
                            val bundle = Bundle()
                            bundle.putInt("id", groceryItem.id!!)
                            findNavController().navigate(
                                R.id.action_groceryListFragment_to_addGroceryFragment,
                                bundle
                            )
                        },
                        onLongClick = {
                            openChooserDialog(groceryItem.id!!)
                        }
                    )
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = groceryItem.name,
                        style = TextStyle(
                            fontSize = 25.sp,
                            color = Color.Black
                        )
                    )

                    Text(
                        text = "${groceryItem.quantity} ${groceryItem.unit}",
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = if (groceryItem.quantity <= groceryItem.lowStockValue) Color.Red else Color.Black
                        )
                    )
                }

                Text(
                    text = groceryItem.description,
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                )

                Text(
                    text = "Modified on: ${
                        SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(
                            groceryItem.addedOn
                        )
                    }",
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                )

                if (groceryItem.quantity <= groceryItem.lowStockValue) {
                    Text(
                        text = "Only ${groceryItem.lowStockValue} ${groceryItem.unit} left",
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = Color.Red
                        )
                    )
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun GroceryListItemPreview() {
        Grocery(
            name = "Rava",
            description = "Barik Rava",
            addedOn = java.util.Date("01-08-2023"),
            quantity = 2.0,
            unit = "Kg",
            storedAt = "Wordrobe",
            lowStockValue = 1.0
        )
    }

    private fun initView() {
        preferences = Preferences(activity?.applicationContext!!)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.groceryList.collectLatest { list ->
                    groceryList = list
                }
            }
        }

        //options Menu region
        val menuHost : MenuHost = requireActivity()
        menuHost.addMenuProvider(object  : MenuProvider {
            lateinit var searchView : SearchView
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
                searchView = menu.findItem(R.id.search).actionView as SearchView
                searchView.queryHint = "Search grocery item..."
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
                        viewModel.searchGroceryListByQuery(newText).observe(viewLifecycleOwner)  {result ->
                            groceryList = result
                        }
                        return false
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.sort -> {
                        val sortOptions = arrayOf("Alphabetically", "Date Added", "Only low stock items")
                        AlertDialog.Builder(activity)
                            .setTitle("Sort items by")
                            .setItems(sortOptions) {dialog, position ->
                                when (position) {
                                    0 -> {
                                        //sort items alphabetically by item name
                                        viewModel.getSortedGroceryListByName().observe(viewLifecycleOwner) {
                                            groceryList = it
                                        }
                                    }
                                    1 -> {
                                        viewModel.getSortedGroceryListByDateAdded().observe(viewLifecycleOwner) {
                                            groceryList = it
                                        }
                                    }
                                    2 -> {
                                        viewModel.getLowStockGroceryItems().observe(viewLifecycleOwner) {
                                            groceryList = it
                                        }
                                    }
                                }
                            }
                            .show()
                    }

                    R.id.account -> {
                        if (preferences.isUserLoggedIn())
                            findNavController().navigate(R.id.action_groceryListFragment_to_accountFragment)
                        else
                            findNavController().navigate(R.id.action_groceryListFragment_to_loginFragment)
                    }

                    R.id.about_app -> {
                        findNavController().navigate(R.id.action_groceryListFragment_to_aboutAppFragment)
                    }
                }
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        //endregion
    }
}