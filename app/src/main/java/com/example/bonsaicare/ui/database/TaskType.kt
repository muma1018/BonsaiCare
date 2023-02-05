package com.example.bonsaicare.ui.database

import androidx.annotation.NonNull
import androidx.room.*

@Entity(tableName = "task_type_table")
data class TaskType(

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    var name: String = "default task type",

    @ColumnInfo(name = "description")
    var description: String = "Sorry! No task description for this task available."
)

