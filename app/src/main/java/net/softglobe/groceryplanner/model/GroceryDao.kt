package net.softglobe.groceryplanner.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GroceryDao {

    @Query("SELECT * FROM Grocery")
    fun getGroceryList() : LiveData<List<Grocery>>

    @Query("SELECT * FROM Grocery")
    suspend fun getGroceryListWithoutObserver() : List<Grocery>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroceryItem(grocery: Grocery) : Long

    @Insert
    suspend fun insertModification(modification: Modification)

    @Query("DELETE FROM Grocery WHERE id = :id")
    suspend fun deleteGroceryItem(id: Int)

    @Query("DELETE FROM Modification WHERE itemId = :id")
    suspend fun deleteAllModificationsByGroceryItemId(id : Int)

    @Query("DELETE FROM Modification WHERE id = :id")
    suspend fun deleteSingleModificationEntry(id : Int)

    @Update
    suspend fun updateGroceryItem(grocery: Grocery)

    @Query("SELECT * FROM Grocery WHERE id = :id")
    suspend fun getGroceryItemById(id : Int) : Grocery

    @Query("SELECT * FROM Modification WHERE itemId = :id ORDER BY modifiedOn DESC")
    fun getModificationsList(id :Int) : LiveData<List<Modification>>

    @Query("SELECT * FROM Grocery WHERE name LIKE '%' ||:query|| '%'")
    fun searchGroceryListByQuery(query: String) : LiveData<List<Grocery>>

    @Query("SELECT * FROM Grocery ORDER BY name COLLATE NOCASE ASC")
    fun getSortedGroceryListByName() : LiveData<List<Grocery>>

    @Query("SELECT * FROM Grocery ORDER BY addedOn DESC")
    fun getSortedGroceryListByDateAdded() : LiveData<List<Grocery>>

    @Query("SELECT * FROM Grocery WHERE quantity <= lowStockValue")
    fun getLowStockGroceryItems() : LiveData<List<Grocery>>

    @Query("SELECT COUNT(*) FROM Grocery")
    suspend fun getGroceryItemsCount() : Int

    @Query("SELECT COUNT(*) FROM Modification WHERE itemId=:groceryItemId")
    suspend fun getModificationsCountByGroceryItemId(groceryItemId : Int) : Int

    @Query("SELECT * FROM Modification")
    fun getAllModificationsList() : LiveData<List<Modification>>

    @Query("DELETE FROM Modification")
    suspend fun clearAllModifications()

    @Query("DELETE FROM Grocery")
    suspend fun clearAllGroceryData()

    @Query("SELECT * FROM Modification")
    suspend fun getAllModificationsListWithoutObserver() : List<Modification>

}