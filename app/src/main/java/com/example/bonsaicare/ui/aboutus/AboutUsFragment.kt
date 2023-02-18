package com.example.bonsaicare.ui.aboutus

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.bonsaicare.databinding.FragmentAboutusBinding
import com.example.bonsaicare.BuildConfig
import com.example.bonsaicare.R
import com.example.bonsaicare.ui.calendar.BonsaiApplication
import com.example.bonsaicare.ui.calendar.BonsaiViewModel
import com.example.bonsaicare.ui.calendar.BonsaiViewModelFactory
import java.io.File


class AboutUsFragment : Fragment() {
    // This fragment contains information about the app and the developers as well as settings

    // Init viewModel
    private val viewModel: BonsaiViewModel by activityViewModels {
        BonsaiViewModelFactory((requireNotNull(this.activity).application as BonsaiApplication).repository)
    }

    private var _binding: FragmentAboutusBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAboutusBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set text for Thank You! textView
        binding.textThankYouForUsingOurApp.text = resources.getString(R.string.text_thank_you_for_using_our_app)

        // Todo MVP3: Implement the 'hidden' feature for hidden fragment - e.g. gives access to all calendars?
        // Initialize a counter variable to keep track of the number of times the TextView has been pressed
        var pressCounter = 0

        // Set an OnClickListener on the TextView
        binding.textThankYouForUsingOurApp.setOnClickListener {
            // Increment the counter
            pressCounter++
            // If the counter is five, open the fragment
            if (pressCounter == 7) {
                // Reset the counter
                pressCounter = 0

                // Navigate to Hidden Fragment
                view.findNavController().navigate(R.id.action_navigation_aboutus_to_hidden)

            }
        }

        // Check if device is in night or day mode, then set dark mode button to true or false
        when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {binding.compoundButtonDarkMode.isChecked = true}
            Configuration.UI_MODE_NIGHT_NO -> {binding.compoundButtonDarkMode.isChecked = false}
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {}
        }

        // Set onCheckedChangeListener for compound button dark mode
        binding.compoundButtonDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Update hardiness zone in database
        binding.spinnerHardinessZone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setSettingsHardinessZone(parent?.getItemAtPosition(position).toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        // Get reference to spinner for hardiness zones in layout
        val spinnerHardinessZone: Spinner = view.findViewById(R.id.spinner_hardiness_zone)

        // Get array of hardiness zones from resources.xml file
        val hardinessZones = resources.getStringArray(R.array.array_of_hardiness_zones)

        // Build adapter for hardiness zones
        val hardinessZoneArrayAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, hardinessZones)

        // Set adapter for spinner
        spinnerHardinessZone.adapter = hardinessZoneArrayAdapter

        // Get hardiness zone from database UserSettings
        val hardinessZone = viewModel.getSettingsHardinessZone()

        // Set initial value for spinner by String hardinessZone
        binding.spinnerHardinessZone.setSelection(hardinessZoneArrayAdapter.getPosition(hardinessZone))

        // Set click listener for feedback button
        binding.buttonSendFeedback.setOnClickListener {
            sendFeedback()
        }

        // Set click listener for donate button
        binding.buttonPaypalDonate.setOnClickListener {
            donate()
        }

        // Set click listener for reset settings button
        binding.buttonResetSettings.setOnClickListener {
            resetSettings()
        }

        // Set click listener for export database button
        binding.buttonExportDatabase.setOnClickListener {
            exportDatabase()
        }

        // Set version name in "App Information" TextView, use resource string with placeholders
        binding.textViewInformationAppInformation.text = resources.getString(R.string.text_app_version, BuildConfig.VERSION_NAME)
    }

    // Donate via pay-pal-me
    private fun donate() {
        // Get paypal intent
        val paypalIntent = requireContext().packageManager.getLaunchIntentForPackage("com.paypal.android.p2pmobile")
        // Open paypal if installed, else open browser
        if (paypalIntent != null) {
            startActivity(paypalIntent)
        } else {
            // This also asks me to open the app after clicking on 'use browser'
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("http://www.paypal.me/markus88mueller"))
            startActivity(browserIntent)
        }
    }

    // Submit the order by sharing out the order details to another app via an implicit intent.
    private fun sendFeedback() {
        // Set emails to send feedback to
        val eMailIds: Array<String> = arrayOf("bonsaicarecalendar@gmail.com")

        val intent = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_SUBJECT, "Feedback")
            .putExtra(Intent.EXTRA_EMAIL, eMailIds)

        activity?.packageManager?.let { packageManager ->
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
    }

    // Reset settings to default
    private fun resetSettings() {
        // Show alert if they really want to reset settings
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Reset Settings")
        builder.setMessage("Are you sure you want to reset settings to default? (filters will be reset, alerts will be shown again)")
        builder.setPositiveButton("Yes") { _, _ ->
            // Reset settings to default
            viewModel.resetSettings()
            // Show toast
            Toast.makeText(requireContext(),  "Settings reset successfully", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No") { _, _ ->
            // Do nothing
        }
        builder.show()
    }

    // Function to export database
    private fun exportDatabase() {
        // Get database path
        val dbPath = requireContext().getDatabasePath("bonsai_database")

        // Get file name
        val fileName = "bonsai_database.db"

        // Get file path
        val filePath = File(requireContext().filesDir, fileName)

        // Copy database to file path
        dbPath.copyTo(filePath, true)

        // Get uri for file
        val uri = FileProvider.getUriForFile(requireContext(), "com.example.bonsaicare.fileprovider", filePath)

        // Create intent
        val intent = Intent(Intent.ACTION_SEND)
            .setType("application/octet-stream")
            .putExtra(Intent.EXTRA_STREAM, uri)

        // Start activity
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}