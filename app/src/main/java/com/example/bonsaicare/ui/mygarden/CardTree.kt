package com.example.bonsaicare.ui.mygarden

import android.net.Uri
import androidx.room.ColumnInfo
import com.example.bonsaicare.ui.calculateAge
import com.example.bonsaicare.ui.database.Tree
import com.example.bonsaicare.ui.database.TreeSpecies

class CardDatabaseTree(
    val name: String,
    var thumbnailImage: Uri,  // This is the resource id of the image
    val treeSpecies: TreeSpecies,
    val age: Int,

    @ColumnInfo(name = "card")
    val cardName: String
)

// Define function to create Cards from Tasks
fun createCardsFromTrees(allDatabaseTrees: List<Tree>): MutableList<CardDatabaseTree> {

    // Initialize allCards list
    val allCards: MutableList<CardDatabaseTree> = mutableListOf()

    // Loop over trees and create all cards
    for (tree in allDatabaseTrees) {

        // Create card from tree
        val newCard = CardDatabaseTree(name = tree.name, thumbnailImage = tree.imagesUri[tree.thumbnailImageIndex],  treeSpecies = tree.treeSpecies, age = calculateAge(tree.dateOfBirth), cardName = "defaultCardName")

        // Add new card to list of cards
        allCards.add(newCard)
    }
    // Get list of selected trees
    return allCards
}


