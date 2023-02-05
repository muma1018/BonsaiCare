package com.example.bonsaicare.ui.database

import androidx.room.*

@Dao
interface UserSettingsDao {

    // Convenience methods - let you insert, update, and delete rows in your database without writing any SQL code.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userSettings: UserSettings)

    @Update
    fun update(userSettings: UserSettings)

    // Delete everything from the table
    @Query("DELETE FROM user_settings_table WHERE id = 0")
    fun clear()

    // Initialize the table with default values, this should only be used once
    // "1STARTUP" is a placeholder for the first startup, which gets replaced with the actual list of all tree species available in the task database
    @Query("INSERT INTO user_settings_table (id, hardiness_zone, do_not_show_alert, first_app_start_up, active_filtered_tree_species, active_filtered_hardiness_zones, active_radio_button_tree_species, active_radio_button_hardiness_zones) VALUES (0, '8a', 0, 1, '1STARTUP', 'TEST,TEST', 'All Trees', 'All')")
    fun initializeUserSettings()

    // Reset DoNotShowAlert and ActiveFilterTreeSpecies
    @Query("UPDATE user_settings_table SET hardiness_zone = '8a', do_not_show_alert = 0, active_filtered_tree_species = '1STARTUP', active_filtered_hardiness_zones = 'TEST,TEST', active_radio_button_hardiness_zones = 'All', active_radio_button_tree_species = 'All Trees', active_filtered_hardiness_zones = 'All' WHERE id = 0")
    fun resetSettings()

    // Set first start up
    @Query("UPDATE user_settings_table SET first_app_start_up = :firstAppStartUp WHERE id = 0")
    fun setSettingsFirstStartUp(firstAppStartUp: Boolean)

    //// Getters ////
    // Get hardiness zone
    @Query("SELECT hardiness_zone FROM user_settings_table WHERE id = 0")
    fun getSettingsHardinessZone(): String

    // Get do not show alert
    @Query("SELECT do_not_show_alert FROM user_settings_table WHERE id = 0")
    fun getSettingsDoNotShowAlert(): Boolean

    // Get first app start up
    @Query("SELECT first_app_start_up FROM user_settings_table WHERE id = 0")
    fun getSettingsFirstAppStartUp(): Boolean

    // Get all UserSettings from id 0
    @Query("SELECT * FROM user_settings_table WHERE id = 0")
    fun getSettingsDatabase(): UserSettings

    // Get active filter tree species
    @Query("SELECT active_filtered_tree_species FROM user_settings_table WHERE id = 0")
    fun getSettingsActiveFilteredTreeSpecies(): String

    // Get active filter hardiness zones
    @Query("SELECT active_filtered_hardiness_zones FROM user_settings_table WHERE id = 0")
    fun getSettingsActiveFilteredHardinessZones(): String

    // Get all UserSettings in id 0
    @Query("SELECT * FROM user_settings_table WHERE id = 0")
    fun getUserSettings(): UserSettings

    // Get number of rows
    @Query("SELECT COUNT(*) FROM user_settings_table WHERE id = 0")
    fun getRowCountUserSettings(): Int

    //// Setters ////
    // Setter for hardiness zone
    // Update users hardiness zone
    @Query("UPDATE user_settings_table SET hardiness_zone = :hardinessZone WHERE id = 0")
    fun setSettingsHardinessZone(hardinessZone: String)

    // Update users do not show alert
    @Query("UPDATE user_settings_table SET do_not_show_alert = :doNotShowAlert WHERE id = 0")
    fun setSettingsDoNotShowWelcomeAlert(doNotShowAlert: Boolean)

    // Set first startup
    @Query("UPDATE user_settings_table SET first_app_start_up = :firstAppStartUp WHERE id = 0")
    fun setSettingsFirstAppStartUp(firstAppStartUp: Boolean)

    // Set active filtered tree species
    @Query("UPDATE user_settings_table SET active_filtered_tree_species = :activeFilteredTreeSpecies WHERE id = 0")
    fun setSettingsActiveFilteredTreeSpecies(activeFilteredTreeSpecies: String)

    // Update hardiness zones' of activeFilterHardinessZones with string of hardiness zones
    @Query("UPDATE user_settings_table SET active_filtered_hardiness_zones = :activeFilterHardinessZones WHERE id = 0")
    fun setSettingsActiveFilterHardinessZones(activeFilterHardinessZones: String)

    //// Re-setters ////
    // Reset settings to default
    @Query("UPDATE user_settings_table SET do_not_show_alert = 0 WHERE id = 0")
    fun resetDoNotShowAlert()


    //// Radio buttons Getters and Setters ////
    // Get active radio button tree species
    @Query("SELECT active_radio_button_tree_species FROM user_settings_table WHERE id = 0")
    fun getSettingsActiveRadioButtonTreeSpecies(): String

    // Set active radio button tree species
    @Query("UPDATE user_settings_table SET active_radio_button_tree_species = :activeRadioButton WHERE id = 0")
    fun setSettingsActiveRadioButtonTreeSpecies(activeRadioButton: String)

    // Get active radio button hardiness zones
    @Query("SELECT active_radio_button_hardiness_zones FROM user_settings_table WHERE id = 0")
    fun getSettingsActiveRadioButtonHardinessZones(): String

    // Set active filtered hardiness zones
    @Query("UPDATE user_settings_table SET active_radio_button_hardiness_zones = :activeRadioButton WHERE id = 0")
    fun setSettingsActiveRadioButtonHardinessZones(activeRadioButton: String)

}

