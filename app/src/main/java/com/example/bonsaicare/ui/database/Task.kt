package com.example.bonsaicare.ui.database

import androidx.room.*


// Each @Entity instance represents an entity in a table. Specify the name of the table if you want
// it to be different from the name of the class.
@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true)
    var taskID: Int? = null,

    // Specify the name of the column in the table, if you want the column name to be different from the name of the member variable.
    // This is a custom class 'TreeSpecies' needed type converter to pass to database
    // From here: https://stackoverflow.com/questions/60986225/error-cannot-figure-out-how-to-save-this-field-into-database-you-can-consider
    // Embeds the TreeSpecies table with Tak table, we need a prefix because column names would be duplicated (name, name) etc
    @Embedded(prefix = "tree_species_")
    var treeSpecies: TreeSpecies = TreeSpecies(name = "default name", nameLatin = "default name latin", restricted = false, description = "default short description"),

    @Embedded(prefix = "task_type_")
    // can we avoid the 'q' here?
    var taskType: TaskType = TaskType(name = "default name", description = "This task type has no description."),

    // This is the long description for specific tree x task combo
    @ColumnInfo(name = "long_description")
    var longDescription: String = "default long description task",

    // Todo MVP3: Make short description listOf<String> to have different descriptions per label(?)
    // This is the short description for specific tree x task combo
    @ColumnInfo(name = "short_description")
    var shortDescription: String = "default short description task",

    // This needs to be an array of integers, but database cannot take arrays.
    //  So we make it a string and convert it later to List of Int
    @ColumnInfo(name = "list_of_dates")
    var listOfDates: String = "1, 2",

    // This are the list of labels for each date. If date is e.g. "2, 4", there should be three labels
    @ColumnInfo(name = "list_of_labels")
    var listOfLabels: String = "defaultLabel1, defaultLabel2",

    @ColumnInfo(name = "hardiness_zone")
    var hardinessZone: String = "7b"
)

