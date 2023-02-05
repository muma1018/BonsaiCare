package com.example.bonsaicare.ui.calendar

import com.example.bonsaicare.ui.database.Task
import androidx.room.ColumnInfo
import com.example.bonsaicare.ui.database.TaskType

// Each card has
// - Task type (spruce)
// - List of up to multiple tasks
class CardTaskType(
    val taskType: TaskType,
    val hardinessZone: String,
    val listOfTasks: MutableList<Task>,

    @ColumnInfo(name = "card")
    val cardName: String
)

// Define function to create Cards from Tasks
fun createCardsByTaskType(allCalendarTasks: List<Task>): List<CardTaskType> {

    // Initialize allCards list
    var allCards: MutableList<CardTaskType> = mutableListOf()

    // Loop over tasks and create all cards
    for (task in allCalendarTasks) {

        var cardFound = false
        var currentCardPosition = 0

        // Loop over cards and look for existent card of current askType
        for (card in allCards) {

            // Compare taskType from task to taskTypes from card
            if (card.taskType.name == task.taskType.name && card.hardinessZone == task.hardinessZone) {

                // Add task to existing card
                val cardUpdated = addTask2CardTaskType(task, card)

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

        // If card of current taskType is not existent, create new card
        if (!cardFound || allCards.size == 0 ) {
            // Create new card with current task
            val newCard = CardTaskType(task.taskType, task.hardinessZone, mutableListOf(task), "defaultCardName")

            // Add new card to list of cards
            allCards.add(newCard)
        }
    }
    // Get list of selected trees
    // Sort cards by their task type and hardiness zone ("7b".dropLast(1).toInt -> 7)
    allCards = allCards.sortedWith(compareBy({ it.taskType.name }, { it.hardinessZone.dropLast(1).toInt() })) as MutableList<CardTaskType>
    return allCards
}

// Define function to add task to card
private fun addTask2CardTaskType(myTask: Task, myCard: CardTaskType): CardTaskType{
    // Add task to list of tasks of card
    myCard.listOfTasks.add(myTask)
    return myCard
}

