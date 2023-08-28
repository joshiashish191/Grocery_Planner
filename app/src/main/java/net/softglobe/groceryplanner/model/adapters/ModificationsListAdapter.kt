package net.softglobe.groceryplanner.model.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.databinding.ItemModificationBinding
import net.softglobe.groceryplanner.model.Modification
import java.text.SimpleDateFormat
import java.util.Locale

class ModificationsListAdapter : ListAdapter<Modification, ModificationsListAdapter.MyViewHolder>(DiffModification()) {
    lateinit var binding : ItemModificationBinding
    inner class MyViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        fun bind(modification: Modification) {
            binding.modification = modification
            binding.date.text = ""+SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.ENGLISH).format(modification.modifiedOn)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_modification, parent, false)
        return MyViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
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