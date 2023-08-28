package net.softglobe.groceryplanner.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Grocery::class, Modification::class], version = 1)
@TypeConverters(value = [DataTypeConverters::class])
abstract class GroceryDatabase : RoomDatabase() {
    abstract val groceryDao : GroceryDao
    companion object{
        private var instance : GroceryDatabase? = null

        fun getInstance(context: Context) : GroceryDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context, GroceryDatabase::class.java, "grocery.db").build()
            }
            return instance!!
        }

    }
}
