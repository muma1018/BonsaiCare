package com.example.bonsaicare.ui.calendar

import android.app.Application
import com.example.bonsaicare.ui.database.BonsaiDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class BonsaiApplication : Application() {
    // No need to cancel this scope as it'll be torn down with the process
    private val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { BonsaiDB.getInstance(this, applicationScope) }
    val repository by lazy { BonsaiRepository(
        database.taskDao(),
        database.treeSpeciesDao(),
        database.taskTypeDao(),
        database.treesDao(),
        database.userSettingsDao()) }
}