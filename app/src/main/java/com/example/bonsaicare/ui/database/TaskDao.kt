package com.example.bonsaicare.ui.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// Data Access Object - DAO
/**
 * DAO: Data access object. A mapping of SQL queries to functions. When you use a DAO, you call the
 * methods, and Room takes care of the rest.
 *
 * When you use the Room persistence library to store your app's data, you interact with the stored
 * data by defining data access objects, or DAOs. Each DAO includes methods that offer abstract
 * access to your app's database. At compile time, Room automatically generates implementations of
 * the DAOs that you define.

By using DAOs to access your app's database instead of query builders or direct queries, you can
preserve separation of concerns, a critical architectural principle. DAOs also make it easier for
you to mock database access when you test your app.

When your app displays data or uses data in other ways use LiveData, which is a lifecycle library
class for data observation. If you use a return value of type LiveData in your method description,
Room generates all necessary code to update the LiveData when the database is updated.
 */
@Dao
interface TaskDao {

    // Convenience methods - let you insert, update, and delete rows in your database without writing any SQL code.
    @Insert //(onConflict = OnConflictStrategy.IGNORE)
    // Error: Type of the parameter must be a class annotated with @Entity or a collection/array of it.
    // Solution: Removed all the suspend: https://stackoverflow.com/questions/53885749/query-method-parameters-should-either-be-a-type-that-can-be-converted-into-a-dat
    fun insert(task: Task)

    /**
     * When updating a row with a value already set in a column,
     * replaces the old value with the new one.
     *
     * @param task new value to write
     */
    @Update
    fun update(task: Task)

    /**
     * Deletes all values from the table. This does not delete the table, only its contents.
     */
    // Delete everything from the table
    @Query("DELETE FROM task_table")
    fun clear()

    /**
     * Selects and returns all rows in the table, sorted by start time in descending order.
     */
    // Get all data from task table
    @Query("SELECT * FROM task_table ORDER BY tree_species_name ASC")
    fun getAllTasks(): Flow<List<Task>>  // Flow is Kotlin's LiveData

    /**
     * Delete task from database
     */
    @Delete
    fun deleteTask(task: Task)

    // Get number of tasks
    @Query("SELECT COUNT(*) FROM task_table")
    fun getNumberOfTasks(): Int

}