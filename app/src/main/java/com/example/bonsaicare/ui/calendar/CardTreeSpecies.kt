package com.example.bonsaicare.ui.calendar

import androidx.room.ColumnInfo
import com.example.bonsaicare.ui.database.Task
import com.example.bonsaicare.ui.database.TreeSpecies


class CardTreeSpecies(
    val treeSpecies: TreeSpecies,
    val hardinessZone: String,
    val listOfTasks: MutableList<Task>,

    @ColumnInfo(name = "card")
    val cardName: String
)

// Define function to create Cards from Tasks
fun createCardsByTreeType(allCalendarTasks: List<Task>): MutableList<CardTreeSpecies> {

    // Initialize allCards list
    val allCards: MutableList<CardTreeSpecies> = mutableListOf()

    // Loop over tasks and create all cards
    for (task in allCalendarTasks) {

        var cardFound = false
        var currentCardPosition = 0

        // Loop over cards and look for existent card of current treeType
        for (card in allCards) {

            // Only create card if its treeType is in list of activeFilterTreeTypes
            // Compare treeType from task to treeTypes from card
            if (card.treeSpecies.name == task.treeSpecies.name && card.hardinessZone == task.hardinessZone) {

                // Add task to existing card
                val cardUpdated = addTask2CardTreeType(task, card)

                // Delete current card
                allCards.removeAt(currentCardPosition)

                // Add card to list of all cards
                allCards.add(currentCardPosition, cardUpdated)

                // Set cardFound to true
                cardFound = true

                // Break to next task
                break
            }
            // Increment iterator
            currentCardPosition += 1
        }

        // If card of current treeType is not existent, create new card
        if (!cardFound || allCards.size ==0) {
            // Create new card with current task
            val newCard = CardTreeSpecies(TreeSpecies(name = task.treeSpecies.name, nameLatin = task.treeSpecies.nameLatin, restricted = false, description = task.treeSpecies.description), task.hardinessZone, mutableListOf(task), "defaultCardName")

            // Add new card to list of cards
            allCards.add(newCard)
        }
    }
    // Get list of selected trees
    return allCards
}


// Define function to add task to card
private fun addTask2CardTreeType(myTask: Task, myCard: CardTreeSpecies): CardTreeSpecies{
    // Add task to list of tasks of card
    myCard.listOfTasks.add(myTask)

    // Get all tasks unordered
    val allTasks = myCard.listOfTasks

    // Set order
    val desiredOrder = listOf("Repotting", "Location", "Pinching", "Pruning", "Wiring",
        "Fertilizing", "Watering", "Propagation", "Diseases", "Specifics")

    // Do the order thing
    val tasksByName = allTasks.associateBy { it.taskType.name }
    val sortedTasks = desiredOrder.map { tasksByName[it] }

    // Remove all tasks
    myCard.listOfTasks.clear()

    // Add all tasks to card
    for (task in sortedTasks) {
        if (task != null) {
            myCard.listOfTasks.add(task)
        }
    }

    return myCard
}



