package net.softglobe.groceryplanner.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Modification(
    val modifiedOn : Date,
    val description : String,
    var itemId : Int? = null,
    @PrimaryKey(autoGenerate = true)
    val id : Int? = null
)