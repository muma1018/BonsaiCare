package com.example.bonsaicare.ui.database

import androidx.annotation.NonNull
import androidx.room.*

@Entity(tableName = "tree_species_table")
data class TreeSpecies (
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    var name: String = "default tree species",

    @ColumnInfo(name = "name_latin")
    var nameLatin: String = "default tree species latin",

    @ColumnInfo(name = "description")
    var description: String = "default description",

    // Todo MVP3?: Parameter not used yet
    @ColumnInfo(name = "filtered")
    var filtered: Boolean = true
)
