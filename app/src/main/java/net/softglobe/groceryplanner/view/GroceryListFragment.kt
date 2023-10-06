package net.softglobe.groceryplanner.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.databinding.FragmentGroceryListBinding
import net.softglobe.groceryplanner.model.Grocery
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
            setRecyclerViewForGroceryList(it)
        }

        binding.btnAddRecord.setOnClickListener {
            findNavController().navigate(R.id.action_groceryListFragment_to_addGroceryFragment)
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
                            setRecyclerViewForGroceryList(result)
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
                                            setRecyclerViewForGroceryList(it)
                                        }
                                    }
                                    1 -> {
                                        viewModel.getSortedGroceryListByDateAdded().observe(viewLifecycleOwner) {
                                            setRecyclerViewForGroceryList(it)
                                        }
                                    }
                                    2 -> {
                                        viewModel.getLowStockGroceryItems().observe(viewLifecycleOwner) {
                                            setRecyclerViewForGroceryList(it)
                                        }
                                    }
                                }
                            }
                            .show()
                    }
                }
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        //endregion
    }

    private fun setRecyclerViewForGroceryList(it: List<Grocery>?) {
        binding.rvGroceryList.apply {
            adapter = GroceryListAdapter(requireActivity(), viewModel)
            layoutManager = LinearLayoutManager(activity?.baseContext!!)
            (binding.rvGroceryList.adapter as GroceryListAdapter).submitList(it)
            if (it?.isEmpty()!!)
                binding.noItemsText.visibility = View.VISIBLE
            else
                binding.noItemsText.visibility = View.GONE
        }
    }
}