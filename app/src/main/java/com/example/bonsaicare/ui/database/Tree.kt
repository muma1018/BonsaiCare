package com.example.bonsaicare.ui.database

import android.net.Uri
import androidx.room.*
import java.time.LocalDate
import com.example.bonsaicare.R


@Entity(tableName = "trees_table")
@TypeConverters(UriListTypeConverter::class)
data class Tree(

    @PrimaryKey
    @ColumnInfo(name = "tree_name")
    var name: String = "default tree name",

    @ColumnInfo(name = "tree_species_name")
    var treeSpeciesName: String = "default tree species",

    @Embedded
    var treeSpecies: TreeSpecies = TreeSpecies(name="default tree species", nameLatin = "default tree species latin", description = "default short description for tree species"),

    @ColumnInfo(name = "imagesResourceIdDefault")
    var imagesResourceIdDefault: Int = R.drawable.trees_icon_colored,

    @ColumnInfo(name = "imagesUri")
    var imagesUri: MutableList<Uri> = mutableListOf(),

    // This index specifies which image from imagesUri is the thumbnail image
    @ColumnInfo(name = "thumbnail_image_index")
    var thumbnailImageIndex: Int = 0,

    @TypeConverters(LocalDateTypeConverter::class)
    @ColumnInfo(name = "date_of_birth")
    var dateOfBirth: LocalDate = LocalDate.of(2000, 1, 1),

    @TypeConverters(LocalDateTypeConverter::class)
    @ColumnInfo(name = "date_of_acquisition")
    var dateOfAcquisition: LocalDate = LocalDate.of(2001, 12, 31),

    @ColumnInfo(name = "style")
    var style: String = "",

    @ColumnInfo(name = "short_description")
    var shortDescription: String = "You can add a short description here",
)

// TypeConverter Uri
class UriListTypeConverter {
    @TypeConverter
    fun fromUriList(uriList: MutableList<Uri>): String {
        return uriList.joinToString(separator = ",") { it.toString() }
    }

    @TypeConverter
    fun toUriList(uriListString: String): MutableList<Uri> {
        return uriListString.split(",").map { Uri.parse(it) }.toMutableList()
    }
}










