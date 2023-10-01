package net.softglobe.groceryplanner.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GroceryDao {

    @Query("SELECT * FROM Grocery")
    fun getGroceryList() : LiveData<List<Grocery>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroceryItem(grocery: Grocery) : Long

    @Insert
    suspend fun insertModification(modification: Modification)

    @Query("DELETE FROM Grocery WHERE id = :id")
    suspend fun deleteGroceryItem(id: Int)

    @Query("DELETE FROM Modification WHERE itemId = :id")
    suspend fun deleteModifications(id : Int)

    @Update
    suspend fun updateGroceryItem(grocery: Grocery)

    @Query("SELECT * FROM Grocery WHERE id = :id")
    suspend fun getGroceryItemById(id : Int) : Grocery

    @Query("SELECT * FROM Modification WHERE itemId = :id ORDER BY modifiedOn DESC")
    fun getModificationsList(id :Int) : LiveData<List<Modification>>

    @Query("SELECT * FROM Grocery WHERE name LIKE '%' ||:query|| '%'")
    fun searchGroceryListByQuery(query: String) : LiveData<List<Grocery>>
}