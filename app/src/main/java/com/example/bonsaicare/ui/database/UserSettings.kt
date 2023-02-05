package com.example.bonsaicare.ui.database
import androidx.room.*

// In this table with work with one row only (Primary Key = 0
@Entity(tableName = "user_settings_table")
@TypeConverters(StringListTypeConverter::class, StringSetTypeConverter::class)
class UserSettings {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "hardiness_zone")
    var hardinessZone: String = "8a"

    @ColumnInfo(name = "do_not_show_alert")
    var doNotShowAlert: Boolean = false

    @ColumnInfo(name = "first_app_start_up")
    var firstAppStartUp: Boolean = true

    // List of Tree Species actively set in the calendar filter
    @ColumnInfo(name = "active_filtered_tree_species")
    // Todo: Could this be a parameter in the TreeSpecies class (Boolean: Filtered)?
    var activeFilteredTreeSpecies: MutableSet<String> = mutableSetOf("")

    // List of Hardiness Zones actively set in the calendar filter
    @ColumnInfo(name = "active_filtered_hardiness_zones")
    var activeFilteredHardinessZones: MutableList<String> = mutableListOf("")

    // Needed to track selection of radio button - Value can be "All Trees", "My Trees" or "None"
    @ColumnInfo(name = "active_radio_button_tree_species")
    var activeRadioButtonTreeSpecies: String = "All Trees"

    // Needed to track selection of radio button - Value can be "All" or "None"
    @ColumnInfo(name = "active_radio_button_hardiness_zones")
    var activeRadioButtonHardinessZones: String = "All"

}

// Type Converter for mutableListOf<String>
class StringListTypeConverter {
    @TypeConverter
    fun fromStringList(stringList: MutableList<String>): String {
        return stringList.joinToString(separator = ",") { it }
    }

    @TypeConverter
    fun toStringList(listString: String): MutableList<String> {
        return listString.split(",").map { it }.toMutableList()
    }
}

class StringSetTypeConverter {
    @TypeConverter
    fun fromStringSet(stringSet: MutableSet<String>): String {
        return stringSet.joinToString(separator = ",") { it }
    }

    @TypeConverter
    fun toStringSet(stringSet: String): MutableSet<String> {
        return stringSet.split(",").map { it }.toMutableSet()
    }
}
