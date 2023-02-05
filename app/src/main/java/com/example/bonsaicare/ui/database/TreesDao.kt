package com.example.bonsaicare.ui.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TreesDao {
    // Convenience methods - let you insert, update, and delete rows in your database without writing any SQL code.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(tree: Tree)

    @Update
    fun updateDatabaseTree(tree: Tree)

    /**
     * Delete all DatabaseTrees from database
     */
    @Query("DELETE FROM trees_table")
    fun deleteAllTrees()

    // Get databaseTree by index
    @Query("SELECT * FROM trees_table ORDER BY tree_name LIMIT 1 OFFSET :index")
    fun getTreeAtIndex(index: Int): Tree

    // Get databaseTree by name
    @Query("SELECT * FROM trees_table WHERE tree_name = :name")
    fun getTreeByName(name: String): Tree

    /**
     * Selects and returns all rows in the table, sorted by start time in descending order.
     */
    // Get all data from trees table
    @Query("SELECT * FROM trees_table ORDER BY tree_name ASC")
    fun getAllTrees(): LiveData<List<Tree>>  // Flow is Kotlin's LiveData

    @Query("SELECT * FROM trees_table INNER JOIN tree_species_table ON trees_table.tree_species_name = tree_species_table.name")
    fun getAllTreesWithSpecies(): LiveData<List<TreeSpeciesWithTrees>>

    @Transaction
    @Query("SELECT * FROM trees_table")
    fun getUsersWithPlaylists(): List<TreeSpeciesWithTrees>

    @Query("SELECT COUNT(*) FROM trees_table")
    fun getCount(): Int

    /**
     * Delete DatabaseTree from database
     */
    @Delete
    fun deleteTree(databaseTree: Tree)

    // Delete databaseTree by name
    @Query("DELETE FROM trees_table WHERE tree_name = :name")
    fun deleteTreeByName(name: String)
}