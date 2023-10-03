package net.softglobe.groceryplanner.model.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.databinding.ItemModificationBinding
import net.softglobe.groceryplanner.model.Modification
import net.softglobe.groceryplanner.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class ModificationsListAdapter(val id: Int, val viewModel: MainViewModel) : ListAdapter<Modification, ModificationsListAdapter.MyViewHolder>(DiffModification()) {
    lateinit var binding : ItemModificationBinding
    lateinit var mContext : Context
    inner class MyViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        fun bind(modification: Modification) {
            binding.modification = modification
            binding.date.text = ""+SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.ENGLISH).format(modification.modifiedOn)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_modification, parent, false)
        mContext = parent.context
        return MyViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
        val currentModItem = getItem(position)

        holder.itemView.setOnLongClickListener {
            openChooserDialog(currentModItem.id!!)
            return@setOnLongClickListener true
        }
    }

    private fun openChooserDialog(id : Int) {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle("Choose an action")
        val options = arrayOf("Delete")
        builder.setItems(options) { dialog, position ->
            if (position == 0) {
                deleteModification(id)
            }
        }
        builder.show()
    }

    private fun deleteModification(id: Int) {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle("Delete Grocery Item")
        builder.setMessage(R.string.delete_modification_warning)
            .setPositiveButton("Yes") { dialog, position ->
                viewModel.deleteSingleModificationEntry(id)
            }
            .setNegativeButton("No") { dialog, position ->
            }
        builder.show()
    }

    class DiffModification : DiffUtil.ItemCallback<Modification>() {
        override fun areItemsTheSame(oldItem: Modification, newItem: Modification): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Modification, newItem: Modification): Boolean {
            return oldItem == newItem
        }
    }
}