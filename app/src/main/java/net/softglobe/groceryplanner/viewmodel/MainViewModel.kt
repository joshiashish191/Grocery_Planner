package net.softglobe.groceryplanner.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.softglobe.groceryplanner.model.Grocery
import net.softglobe.groceryplanner.model.GroceryDao
import net.softglobe.groceryplanner.model.GroceryDatabase
import net.softglobe.groceryplanner.model.Modification
import net.softglobe.groceryplanner.model.Repository

class MainViewModel(context: Context) : ViewModel() {
    private var groceryDao : GroceryDao
    private var repository : Repository

    init {
        groceryDao = GroceryDatabase.getInstance(context).groceryDao
        repository = Repository(groceryDao)
    }

    fun getGroceryList() : LiveData<List<Grocery>> {
        return repository.getGroceryList()
    }

    fun getModificationsList(id : Int) : LiveData<List<Modification>> {
        return repository.getModificationsList(id)
    }

    fun insertGroceryItem(grocery: Grocery, modification: Modification) : Long {
        var id = 0L
        viewModelScope.launch(Dispatchers.IO) {
            id = repository.insertGroceryItem(grocery)
            modification.itemId = id.toInt()
            repository.insertModification(modification)
        }
        return id
    }

    fun deleteGroceryItem(id : Int) {
        viewModelScope.launch {
            repository.deleteGroceryItem(id)
            repository.deleteAllModificationsByGroceryItemId(id)
        }
    }

    fun deleteSingleModificationEntry(id : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSingleModificationEntry(id)
        }
    }

    fun deleteAllModificationsByGroceryItemId(id : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllModificationsByGroceryItemId(id)
        }
    }

    suspend fun getGroceryItem(id : Int) : Grocery = repository.getGroceryItemById(id)

    fun searchGroceryListByQuery(query : String) : LiveData<List<Grocery>> {
        return repository.searchGroceryListByQuery(query)
    }

    fun getSortedGroceryListByName() : LiveData<List<Grocery>> {
        return repository.getSortedGroceryListByName()
    }

    fun getSortedGroceryListByDateAdded() : LiveData<List<Grocery>> {
        return repository.getSortedGroceryListByDateAdded()
    }

    fun getLowStockGroceryItems() : LiveData<List<Grocery>> {
        return repository.getLowStockGroceryItems()
    }
}