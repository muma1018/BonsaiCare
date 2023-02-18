package com.example.bonsaicare.ui.calendar

import android.app.Application
import com.example.bonsaicare.ui.database.BonsaiDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class BonsaiApplication : Application() {
    // No need to cancel this scope as it'll be torn down with the process
    private val applicationScope = CoroutineScope(SupervisorJob())

    lateinit var database: BonsaiDB
    lateinit var repository: BonsaiRepository

    // Using 'by lazy' so the database and the repository are only created when they're needed
    // rather than when the application starts
    // We do not 'by lazy' here, because we need to init the database and the repository directly
    override fun onCreate() {
        super.onCreate()
        database = BonsaiDB.getInstance(this, applicationScope)
        repository = BonsaiRepository(
            database.taskDao(),
            database.treeSpeciesDao(),
            database.taskTypeDao(),
            database.treesDao(),
            database.userSettingsDao())
    }
}