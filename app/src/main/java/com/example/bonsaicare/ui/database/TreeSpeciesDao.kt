package com.example.bonsaicare.ui.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TreeSpeciesDao {

    // Convenience methods - let you insert, update, and delete rows in your database without writing any SQL code.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(treeSpecies: TreeSpecies)

    @Update
    fun update(name: TreeSpecies)

    /**
     * Deletes all values from the table. This does not delete the table, only its contents.
     */
    // Delete everything from the table
    @Query("DELETE FROM tree_species_table")
    fun clear()

    /**
     * Selects and returns all rows in the table, sorted by start time in descending order.
     */
    // Get all tree species from database
    @Query("SELECT * FROM tree_species_table ORDER BY name ASC")
    fun getAllTreeSpecies(): Flow<List<TreeSpecies>>  // Flow is Kotlin's LiveData

    // Get all tree species names from database
    @Query("SELECT name FROM tree_species_table ORDER BY name")
    fun getAllTreeSpeciesNames(): LiveData<List<String>>  // Flow is Kotlin's LiveData

    // Get all tree species names from database
    @Query("SELECT name_latin FROM tree_species_table ORDER BY name_latin")
    fun getAllTreeSpeciesNamesLatin(): LiveData<List<String>>  // Flow is Kotlin's LiveData

    // Get tree species by name
    @Query("SELECT name, name_latin, restricted, description, filtered FROM tree_species_table WHERE name = :name")
    fun getTreeSpeciesByName(name: String): TreeSpecies?

    // Get tree species by latin name
    @Query("SELECT name, name_latin, restricted, description, filtered FROM tree_species_table WHERE name_latin = :name")
    fun getTreeSpeciesByNameLatin(name: String): TreeSpecies?

    // Insert tree species from task table into tree species table
    @Query("INSERT INTO tree_species_table (name, name_latin, restricted, description, filtered) SELECT DISTINCT tree_species_name, tree_species_name_latin, tree_species_restricted, tree_species_description, tree_species_filtered FROM task_table")
    fun insertAllDistinctTreeSpeciesFromTaskTable()

    // Return corresponding latin name to name
    @Query("SELECT name_latin FROM tree_species_table WHERE name = :name")
    fun getCorrespondingLatin(name: String): String

    // Return corresponding name to latin name
    @Query("SELECT name FROM tree_species_table WHERE name_latin = :name")
    fun getCorrespondingName(name: String): String

    // Check if tree species already exists
    @Query("SELECT EXISTS(SELECT 1 FROM tree_species_table WHERE name = :name LIMIT 1)")
    fun doesTreeSpeciesExist(name: String): Boolean

    // Return corresponding name to latin name
    @Query("SELECT EXISTS(SELECT 1 FROM tree_species_table WHERE name_latin = :name LIMIT 1)")
    fun doesTreeSpeciesLatinExist(name: String): Boolean

    // That works     @Query("SELECT name_latin FROM tree_species_table ORDER BY name_latin")
    @Query("SELECT tree_species_table.* FROM tree_species_table JOIN trees_table ON tree_species_table.name = trees_table.tree_species_name")
    fun getAllMyTreeSpecies(): LiveData<List<TreeSpecies>>

    // Select all tree species from tree species table that are not in trees table
    @Query("SELECT tree_species_table.* FROM tree_species_table LEFT JOIN trees_table ON tree_species_table.name = trees_table.tree_species_name WHERE trees_table.tree_species_name IS NULL")
    fun getAllTreeSpeciesNotInTreesTable(): LiveData<List<TreeSpecies>>

    @Transaction
    @Query("SELECT * FROM tree_species_table")
    fun getTreeSpeciesWithTreesFromTreeSpeciesTable(): List<TreeSpeciesWithTrees>


    /**
     * Delete task from database
     */
    @Delete
    fun deleteTreeType(treeSpeciesData: TreeSpecies)


}

