package net.softglobe.groceryplanner.model

import androidx.lifecycle.LiveData

class Repository(private val groceryDao: GroceryDao) {

    fun getGroceryList() : LiveData<List<Grocery>> {
        return groceryDao.getGroceryList()
    }

    fun getModificationsList(id: Int): LiveData<List<Modification>> {
        return groceryDao.getModificationsList(id)
    }

    suspend fun insertGroceryItem(grocery: Grocery) : Long {
        return groceryDao.insertGroceryItem(grocery)
    }

    suspend fun insertModification(modification: Modification) {
        groceryDao.insertModification(modification)
    }

    suspend fun getGroceryItemById(id: Int): Grocery = groceryDao.getGroceryItemById(id)

    suspend fun deleteGroceryItem(id : Int) {
        groceryDao.deleteGroceryItem(id)
    }

    suspend fun deleteAllModificationsByGroceryItemId(id: Int) {
        groceryDao.deleteAllModificationsByGroceryItemId(id)
    }

    suspend fun deleteSingleModificationEntry(id: Int) {
        groceryDao.deleteSingleModificationEntry(id)
    }

    fun searchGroceryListByQuery(query : String) : LiveData<List<Grocery>> {
        return groceryDao.searchGroceryListByQuery(query)
    }
}