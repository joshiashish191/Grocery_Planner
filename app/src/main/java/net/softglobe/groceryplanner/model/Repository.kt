package net.softglobe.groceryplanner.model

import androidx.lifecycle.LiveData

class Repository(private val groceryDao: GroceryDao) {

    fun getGroceryList() : LiveData<List<Grocery>> {
        return groceryDao.getGroceryList()
    }

    suspend fun getGroceryListWithoutObserver() : List<Grocery> {
        return groceryDao.getGroceryListWithoutObserver()
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

    fun getSortedGroceryListByName() : LiveData<List<Grocery>> {
        return groceryDao.getSortedGroceryListByName()
    }

    fun getSortedGroceryListByDateAdded() : LiveData<List<Grocery>> {
        return groceryDao.getSortedGroceryListByDateAdded()
    }

    fun getLowStockGroceryItems() : LiveData<List<Grocery>> {
        return groceryDao.getLowStockGroceryItems()
    }

    suspend fun getGroceryItemsCount() : Int {
        return groceryDao.getGroceryItemsCount()
    }

    suspend fun getModificationsCountByGroceryItemId(groceryItemId : Int) : Int {
        return groceryDao.getModificationsCountByGroceryItemId(groceryItemId)
    }

    fun getAllModificationsList() : LiveData<List<Modification>> {
        return groceryDao.getAllModificationsList()
    }

    suspend fun clearAllModifications() {
        groceryDao.clearAllModifications()
    }

    suspend fun clearAllGroceryData() {
        groceryDao.clearAllGroceryData()
    }

    suspend fun getAllModificationsListWithoutObserver() : List<Modification> {
        return groceryDao.getAllModificationsListWithoutObserver()
    }
}