package net.softglobe.groceryplanner.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Grocery (
    val name : String,
    val description : String,
    val addedOn: Date,
    val quantity : Double,
    val unit : String,
    val storedAt : String,
    val lowStockValue : Double,
    @PrimaryKey(autoGenerate = true)
    var id : Int? = null
)