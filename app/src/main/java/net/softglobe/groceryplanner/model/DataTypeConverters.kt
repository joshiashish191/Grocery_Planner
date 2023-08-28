package net.softglobe.groceryplanner.model

import androidx.room.TypeConverter
import java.util.*

class DataTypeConverters {

    @TypeConverter
    fun fromDateToLongConverter(date : Date) : Long {
        return date.time
    }

    @TypeConverter
    fun fromLongToDateConverter(date : Long) : Date {
        return Date(date)
    }
}