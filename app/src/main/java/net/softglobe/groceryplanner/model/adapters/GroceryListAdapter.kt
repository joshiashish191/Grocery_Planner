package net.softglobe.groceryplanner.model.adapters

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.databinding.GroceryItemBinding
import net.softglobe.groceryplanner.model.Grocery
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class GroceryListAdapter(private val mContext: Context, private val viewModel: MainViewModel) : ListAdapter<Grocery, GroceryListAdapter.GroceryViewHolder>(
    GroceryDiffUtils()
) {
    lateinit var binding: GroceryItemBinding
    inner class GroceryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind (grocery: Grocery) {
            binding.grocery = grocery
            binding.itemAddedOn.text = "Modified on: "+SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(grocery.addedOn)
            binding.quantity.text = ""+grocery.quantity+" "+grocery.unit
            if (TextUtils.isEmpty(grocery.description))
                binding.itemSubtitle.visibility = View.GONE
            if (grocery.quantity <= grocery.lowStockValue) {
                binding.lowStockWarning.text = "Only ${grocery.lowStockValue} ${grocery.unit} left"
                binding.lowStockWarning.visibility = View.VISIBLE
                binding.quantity.setTextColor(ContextCompat.getColor(mContext, R.color.red))
            } else {
                binding.lowStockWarning.visibility = View.GONE
                binding.quantity.setTextColor(ContextCompat.getColor(mContext, R.color.black))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.grocery_item, parent, false)
        return GroceryViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("id",item.id!!)
            it.findNavController().navigate(R.id.action_groceryListFragment_to_addGroceryFragment, bundle)
        }

        holder.itemView.setOnLongClickListener {
            openChooserDialog(item.id!!)
            return@setOnLongClickListener true
        }
    }

    private fun openChooserDialog(id : Int) {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle("Choose an action")
        val options = arrayOf("Delete")
        builder.setItems(options) { dialog, position ->
            if (position == 0) {
                deleteGroceryItem(id)
            }
        }
        builder.show()
    }

    private fun deleteGroceryItem(id: Int) {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle("Delete Grocery Item")
        builder.setMessage(R.string.delete_warning)
            .setPositiveButton("Yes") { dialog, which ->
                viewModel.deleteGroceryItem(id)
                Toast.makeText(mContext, "Item deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, which ->
            }
        builder.show()
    }

    class GroceryDiffUtils : DiffUtil.ItemCallback<Grocery>() {
        override fun areItemsTheSame(oldItem: Grocery, newItem: Grocery): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Grocery, newItem: Grocery): Boolean {
            return oldItem == newItem
        }

    }

    override fun getItemViewType(position: Int): Int = position
}