package com.example.bonsaicare

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bonsaicare.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //// Bottom Navigation
        // Set up all the bottom navigation
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_calendar, R.id.navigation_my_garden, R.id.navigation_aboutus)
        )

        setSupportActionBar(findViewById(R.id.my_toolbar))

        // We disabled the action bar - because it did not fit the App Layout well
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Setup Navigation to be hidden when not in main fragments (calendar, my garden, about us)
        setupNav()

    }

    // This was included so the back button works (e.g. from FilterFragment 'back' to CalendarFragment)
    override fun onSupportNavigateUp(): Boolean {
        val navcon = this.findNavController(R.id.nav_host_fragment_activity_main)
        return navcon.navigateUp()
    }

    private fun setupNav() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        findViewById<BottomNavigationView>(R.id.nav_view)
            .setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_calendar -> showBottomNav()
                R.id.navigation_my_garden -> showBottomNav()
                R.id.navigation_aboutus -> showBottomNav()
                else -> hideBottomNav()
            }
        }
    }

    private fun showBottomNav() {
        binding.navView.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        binding.navView.visibility = View.GONE
    }





}