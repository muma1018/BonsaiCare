package com.example.bonsaicare.ui.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskTypeDao {

    // Convenience methods - let you insert, update, and delete rows in your database without writing any SQL code.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(taskType: TaskType)

    @Update
    fun update(taskType: TaskType)

    // Delete everything from the table
    @Query("DELETE FROM task_type_table")
    fun clear()

    // Get all data from task table
    @Query("SELECT * FROM task_type_table ORDER BY name ASC")
    fun getAllTaskTypes(): LiveData<List<TaskType>>

    @Query("SELECT name, description FROM task_type_table WHERE name = :name")
    fun getTaskTypeByName(name: String): TaskType

    @Delete
    fun deleteTaskType(taskType: TaskType)

}