package com.example.bonsaicare.ui.calendar

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.lifecycle.*
import com.example.bonsaicare.R
import com.example.bonsaicare.ui.database.*
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

/**
 * View Model to keep a reference to the word repository and
 * an up-to-date list of all words.
 */

class BonsaiViewModel(private val repository: BonsaiRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allTasks: LiveData<List<Task>> = repository.allTasks.asLiveData()

    // Get my trees from database
    val allTrees: LiveData<List<Tree>> = repository.allTrees

    // Get my all tree species from database
    val allTreeSpecies: LiveData<List<TreeSpecies>> = repository.allTreeSpecies.asLiveData()

    // Date formatter
    val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Index of current selected tree (e.g. for editTree Fragment)
    var currentTreeIndex: Int = 0

    // Name of current tree (e.g. for editTree Fragment)
    var currentTreeName: String = "TreeUnderConstruction"

    // Trees under construction
    var newTreeUnderConstruction = Tree()
    var editTreeUnderConstruction = Tree()

    // Index of current selected images of current tree (e.g. editTree Fragment, image gallery)
    var currentImageIndex: Int = 0

    // Flag to handle status of a new tree being added with NewTreeFragment
    var treeUnderConstructionFlag: Boolean = false

    // Initialize variables to remember selected spinner items used in New/Edit Tree and New Task
    var rememberSelectedSpinnerTreeSpecies: String = ""

    // Tree species of trees in my garden
    val myGardenTreeSpecies: MutableCollection<String> = mutableSetOf()
    // myTreeSpecies + myGardenTreeSpecies + tree types from calendar
    val allTaskTreeSpecies: MutableCollection<String> = mutableSetOf()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insertTask(task: Task) = viewModelScope.launch {
        repository.insertTask(task)
    }

    fun insertTreeSpecies(treeSpecies: TreeSpecies) = viewModelScope.launch {
        repository.insertTreeSpecies(treeSpecies)
    }

    fun insertTree(tree: Tree) = viewModelScope.launch {
        repository.insertDatabaseTree(tree)
    }

    fun clear() = viewModelScope.launch{
        repository.clear()
    }

    fun delete(task: Task) = viewModelScope.launch {
        repository.delete(task)
    }

    // Add tree species if not already in list
    fun addUniqueTreeSpeciesToListOfTreeSpecies(listOfTreeSpecies: MutableCollection<String>, treeSpecies: String) {
        if (treeSpecies !in listOfTreeSpecies)
            listOfTreeSpecies.add(treeSpecies)
    }

    // Add tree species if not already in list
    fun removeUniqueTreeSpeciesFromListOfTreeSpecies(listOfTreeSpecies: MutableCollection<String>, treeSpecies: String) {
        if (treeSpecies in listOfTreeSpecies)
            listOfTreeSpecies.remove(treeSpecies)
    }

    // Function to get unique treeSpecies from list of tasks
    fun getTreeSpeciesNameFromTasks(tasks: List<Task>) : List<String> {
        val listOfTreeSpecies = mutableListOf<String>()

        // Loop over tasks and add unique treeSpecies to list
        for (task in tasks)
            if (task.treeSpecies.name !in listOfTreeSpecies) {
                listOfTreeSpecies.add(task.treeSpecies.name)
            }
        return listOfTreeSpecies
    }


    fun getTreeSpeciesByName(name: String): TreeSpecies? {
        return repository.getTreeSpeciesByName(name)
    }

    fun getTreeSpeciesByNameLatin(name: String): TreeSpecies? {
        return repository.getTreeSpeciesByNameLatin(name)
    }

    fun getAllTreeSpeciesNamesLatin(): LiveData<List<String>> {
        return repository.getAllTreeSpeciesNamesLatin()
    }

    fun getAllTreeSpeciesNames(): LiveData<List<String>> {
        return repository.getAllTreeSpeciesNames()
    }

    fun getCorrespondingLatin(name: String) : String {
        return repository.getCorrespondingLatin(name)
    }

    fun getCorrespondingName(name: String) : String {
        return repository.getCorrespondingName(name)
    }

    fun doesTreeSpeciesExist(name: String) : Boolean {
        return repository.doesTreeSpeciesExist(name)
    }

    fun doesTreeSpeciesLatinExist(name: String) : Boolean {
        return repository.doesTreeSpeciesLatinExist(name)
    }

    fun getTaskTypeByName(name: String) : TaskType {
        return repository.getTaskTypeByName(name)
    }

    fun getALlTaskTypes() : LiveData<List<TaskType>> {
        return repository.getALlTaskTypes()
    }

    fun getNumberOfTrees() : Int {
        return repository.numberOfTrees
    }

    fun getTreeAtIndex(currentTreeIndex: Int): Tree {
        return repository.getTreeAtIndex(currentTreeIndex)
    }

    fun getDatabaseTreeByName(treeName: String): Tree {
        return repository.getDatabaseTreeByName(treeName)
    }

    fun deleteDatabaseTreeByName(name: String) {
        repository.deleteDatabaseTreeByName(name)
    }

    fun insertImageIntoTreeByName(treeName: String, imageUri: Uri) {
        repository.insertImageIntoTreeByName(treeName, imageUri)
    }

    fun resetNewTreeUnderConstruction(context: Context) {
        // Reset tree under construction
        newTreeUnderConstruction = Tree()

        // Set default image
        newTreeUnderConstruction.imagesUri = mutableListOf(
            Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(context.packageName)
                .path(R.drawable.trees_icon_colored.toString())
                .build())
    }

    fun resetSettings() {
        repository.resetSettings()
    }

    fun setSettingsHardinessZone(hardinessZone: String) {
        repository.setSettingsHardinessZone(hardinessZone)
    }

    // Do not show alert update dialog
    fun updateUsersDoNotShowWelcomeAlert(doNotShowAlert: Boolean) {
        repository.setSettingsDoNotShowWelcomeAlert(doNotShowAlert)
    }

    fun getHardinessZonesFromTasks(tasks: List<Task>?): List<String> {
        val hardinessZones = mutableListOf<String>()
        if (tasks != null) {
            for (task in tasks) {
                if (task.hardinessZone !in hardinessZones) {
                    hardinessZones.add(task.hardinessZone)
                }
            }
        }
        return hardinessZones
    }


    // Get active filtered tree species
    fun getSettingsActiveFilteredTreeSpecies(): MutableSet<String> {
        return repository.getSettingsActiveFilteredTreeSpecies().split(',').map { it.trim() }.toMutableSet()
    }

    // Get active filtered hardiness zones
    fun getSettingsActiveFilteredHardinessZones(): MutableSet<String> {
        return repository.getSettingsActiveFilteredHardinessZones().split(',').map { it.trim() }.toMutableSet()
    }

    fun getSettingsDoNotShowAlert(): Boolean {
        return repository.getSettingsDoNotShowAlert()
    }

    fun getSettingsFirstAppStartUp(): Boolean {
        return repository.getSettingsFirstAppStartUp()
    }

    fun setSettingsFirstAppStartUp(firstAppStartUp: Boolean) {
        repository.setSettingsFirstAppStartUp(firstAppStartUp)
    }

    fun setSettingsActiveFilteredTreeSpecies(listOfTreeSpeciesTmp: String) {
        repository.setSettingsActiveFilteredTreeSpecies(listOfTreeSpeciesTmp)
    }

    fun setSettingsActiveFilteredHardinessZones(hardinessZones: String) {
        repository.setSettingsActiveFilteredHardinessZones(hardinessZones)
    }

    fun setSettingsActiveRadioButtonTreeSpecies(activeButtonText: String) {
        repository.setSettingsActiveRadioButtonTreeSpecies(activeButtonText)
    }

    fun setSettingsActiveRadioButtonHardinessZones(activeButtonText: String) {
        repository.setSettingsActiveRadioButtonHardinessZones(activeButtonText)
    }

    fun getSettingsHardinessZone(): String {
        return repository.getSettingsHardinessZone()
    }

    fun getSettingsActiveRadioButtonTreeSpecies(): String {
        return repository.getSettingsActiveRadioButtonTreeSpecies()
    }

    fun getSettingsActiveRadioButtonHardinessZones(): String {
        return repository.getSettingsActiveRadioButtonHardinessZones()
    }

}

class BonsaiViewModelFactory(private val repository: BonsaiRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BonsaiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BonsaiViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}