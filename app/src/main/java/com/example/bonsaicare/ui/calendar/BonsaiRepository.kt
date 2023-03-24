package com.example.bonsaicare.ui.calendar

import android.net.Uri
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.bonsaicare.ui.database.*
import kotlinx.coroutines.flow.Flow

/**
 * Abstracted Repository as promoted by the Architecture Guide.
 * https://developer.android.com/topic/libraries/architecture/guide.html
 */
class BonsaiRepository(private val taskDao: TaskDao,
                       private val treeSpeciesDao: TreeSpeciesDao,
                       private val taskTypeDao: TaskTypeDao,
                       private val databaseTreesDao: TreesDao,
                       private val userSettingsDao: UserSettingsDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()
    val allTrees: LiveData<List<Tree>> = databaseTreesDao.getAllTrees()
    val allTreeSpecies: Flow<List<TreeSpecies>> = treeSpeciesDao.getAllTreeSpecies()

    val numberOfTrees: LiveData<Int> = databaseTreesDao.getCount()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertTask(task: Task) {
        taskDao.insert(task)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertTreeSpecies(treeSpecies: TreeSpecies) {
        treeSpeciesDao.insert(treeSpecies)
    }

    fun insertDatabaseTree(databaseTree: Tree) {
        databaseTreesDao.insert(databaseTree)
    }

    fun clear(){
        taskDao.clear()
    }

    fun delete(task: Task){
        taskDao.deleteTask(task)
    }

    fun getAllTreeSpeciesNamesLatin(): LiveData<List<String>> {
        return treeSpeciesDao.getAllTreeSpeciesNamesLatin()
    }

    fun getAllMyTreeSpecies(): LiveData<List<TreeSpecies>> {
        return treeSpeciesDao.getAllMyTreeSpecies()
    }

    fun getAllDatabaseTrees(): LiveData<List<Tree>> {
        return databaseTreesDao.getAllTrees()
    }

    fun getAllTreesWithSpecies(): LiveData<List<TreeSpeciesWithTrees>> {
        return databaseTreesDao.getAllTreesWithSpecies()
    }

    fun getAllTreeSpeciesNotInTreesTable(): LiveData<List<TreeSpecies>> {
        return treeSpeciesDao.getAllTreeSpeciesNotInTreesTable()
    }

    fun getTreeSpeciesWithTreesFromTreeSpeciesTable(): List<TreeSpeciesWithTrees> {
        return treeSpeciesDao.getTreeSpeciesWithTreesFromTreeSpeciesTable()
    }

    fun getAllTreeSpeciesNames(): LiveData<List<String>> {
        return treeSpeciesDao.getAllTreeSpeciesNames()
    }

    fun getCorrespondingLatin(name: String): String {
        return treeSpeciesDao.getCorrespondingLatin(name)
    }

    fun getCorrespondingName(name: String): String {
        return treeSpeciesDao.getCorrespondingName(name)
    }

    fun doesTreeSpeciesExist(name: String): Boolean {
        return treeSpeciesDao.doesTreeSpeciesExist(name)
    }

    fun doesTreeSpeciesLatinExist(name: String): Boolean {
        return treeSpeciesDao.doesTreeSpeciesLatinExist(name)
    }

    fun getTaskTypeByName(name: String): TaskType {
        return taskTypeDao.getTaskTypeByName(name)
    }

    fun getALlTaskTypes(): LiveData<List<TaskType>> {
        return taskTypeDao.getAllTaskTypes()
    }

    fun getTreeSpeciesByName(name: String): TreeSpecies? {
        return treeSpeciesDao.getTreeSpeciesByName(name)
    }
    fun getTreeSpeciesByNameLatin(name: String): TreeSpecies? {
        return treeSpeciesDao.getTreeSpeciesByNameLatin(name)
    }

    fun getTreeAtIndex(currentTreeIndex: Int): Tree {
        return databaseTreesDao.getTreeAtIndex(currentTreeIndex)
    }

    fun getDatabaseTreeByName(treeName: String): Tree {
        return databaseTreesDao.getTreeByName(treeName)
    }

    fun deleteDatabaseTreeByName(treeName: String) {
        databaseTreesDao.deleteTreeByName(treeName)
    }


    fun insertImageIntoTreeByName(treeName: String, imageUri: Uri) {
        val tree = databaseTreesDao.getTreeByName(treeName)
        tree.imagesUri.add(imageUri)
        databaseTreesDao.updateDatabaseTree(tree)
    }

    fun setSettingsHardinessZone(hardinessZone: String) {
        userSettingsDao.setSettingsHardinessZone(hardinessZone)
    }

    fun setSettingsDoNotShowWelcomeAlert(doNotShowAlert: Boolean) {
        userSettingsDao.setSettingsDoNotShowWelcomeAlert(doNotShowAlert)
    }

    // Reset settings to default (show alert)
    fun resetSettings() {
        userSettingsDao.resetSettings()
    }

    fun getSettingsActiveFilteredTreeSpecies(): String {
        return userSettingsDao.getSettingsActiveFilteredTreeSpecies()
    }

    fun getSettingsActiveFilteredHardinessZones(): String {
        return userSettingsDao.getSettingsActiveFilteredHardinessZones()
    }

    fun getSettingsDoNotShowAlert(): Boolean {
        return userSettingsDao.getSettingsDoNotShowAlert()
    }

    fun getSettingsFirstAppStartUp(): Boolean {
        return userSettingsDao.getSettingsFirstAppStartUp()
    }

    fun setSettingsFirstAppStartUp(firstAppStartUp: Boolean) {
        userSettingsDao.setSettingsFirstAppStartUp(firstAppStartUp)
    }

    fun setSettingsActiveFilteredTreeSpecies(listOfTreeSpeciesTmp: String) {
        userSettingsDao.setSettingsActiveFilteredTreeSpecies(listOfTreeSpeciesTmp)
    }

    fun setSettingsActiveFilteredHardinessZones(hardinessZones: String) {
        userSettingsDao.setSettingsActiveFilterHardinessZones(hardinessZones)
    }

    fun setSettingsActiveRadioButtonTreeSpecies(activeButtonText: String) {
        userSettingsDao.setSettingsActiveRadioButtonTreeSpecies(activeButtonText)
    }

    fun setSettingsActiveRadioButtonHardinessZones(activeButtonText: String) {
        userSettingsDao.setSettingsActiveRadioButtonHardinessZones(activeButtonText)
    }

    fun getSettingsHardinessZone(): String {
        return userSettingsDao.getSettingsHardinessZone()
    }

    fun getSettingsActiveRadioButtonTreeSpecies(): String {
        return userSettingsDao.getSettingsActiveRadioButtonTreeSpecies()
    }

    fun getSettingsActiveRadioButtonHardinessZones(): String {
        return userSettingsDao.getSettingsActiveRadioButtonHardinessZones()
    }

    fun getRequestCode(): Int {
        return userSettingsDao.getRequestCode()
    }
}
