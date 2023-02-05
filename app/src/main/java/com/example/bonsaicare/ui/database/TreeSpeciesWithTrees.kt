package com.example.bonsaicare.ui.database

import androidx.room.Embedded
import androidx.room.Relation

data class TreeSpeciesWithTrees(
    @Embedded val tree: TreeSpecies,
    @Relation(
        parentColumn = "name",
        entityColumn = "tree_species_name"
    )
    val treeSpecies: Tree
)

