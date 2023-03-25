package com.example.bonsaicare.ui.calendar.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.bonsaicare.R
import com.example.bonsaicare.databinding.FragmentFilterBinding
import com.example.bonsaicare.ui.calendar.BonsaiViewModel
import com.example.bonsaicare.ui.calendar.BonsaiViewModelFactory
import com.example.bonsaicare.ui.calendar.BonsaiApplication

class FilterFragment : Fragment() {

    private var _binding: FragmentFilterBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // Get reference to taskViewModel
    private val viewModel: BonsaiViewModel by activityViewModels {
        BonsaiViewModelFactory((requireNotNull(this.activity).application as BonsaiApplication).repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Enable 'back' button of support action bar in this fragment
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    view.findNavController()
                        .navigate(R.id.action_navigation_filter_to_navigation_calendar)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        // Show action bar in this fragment
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()

        // Set (check true) active radioButton
        setActiveRadioButtonTreeSpeciesFromSettings()
        setActiveRadioButtonHardinessZoneFromSettings()

        // Set onClickListener for FilterAll Trees
        binding.filterAllTreeSpecies.setOnClickListener {
            // Check all check boxes
            val childCount = binding.linearLayoutTreeSelection.childCount
            for (count in 0..childCount) {
                val checkboxTmp = binding.linearLayoutTreeSelection.getChildAt(count)
                if (checkboxTmp is CheckBox) {
                    checkboxTmp.isChecked = true
                }
            }
        }

        // Set onClickListener for RadioBtn to set from my trees
        binding.filterMyTreesTreeSpecies.setOnClickListener {
            // Observe number of My Trees
            viewModel.getNumberOfTrees().observe(this) { value ->
                val numberTrees = value ?: 0

                // Todo MVP2: Why do we have to loop over them and are not able to get them directly from DB?
                //  Can it be substituted with code below that is commented out? It's crashing but might be close
                // Loop over all trees and add them to a list
                val allTrees = mutableListOf<String>()

                // Loop over trees and add to List
                for (i in 0 until numberTrees) {
                    allTrees += viewModel.getTreeAtIndex(i).treeSpecies.name
                }

                val childCount = binding.linearLayoutTreeSelection.childCount
                for (count in 0..childCount) {
                    val checkboxTmp = binding.linearLayoutTreeSelection.getChildAt(count)
                    if (checkboxTmp is CheckBox) {

                        checkboxTmp.isChecked = checkboxTmp.text in allTrees
                    }
                }
            }
        }

//        binding.filterMyTreesTreeSpecies.setOnClickListener {
//            viewModel.getAllDatabaseTrees().observe(this) { listTree ->
//
//                val childCount = binding.linearLayoutTreeSelection.childCount
//                for (count in 0..childCount) {
//                    val checkboxTmp = binding.linearLayoutTreeSelection.getChildAt(count)
//                    if (checkboxTmp is CheckBox) {
//
//                        checkboxTmp.isChecked = checkboxTmp.text in listTree[count].treeSpecies.name
//                    }
//                }
//            }
//        }

        // Set onClickListener for FilterNone Trees
        binding.filterNoneTreeSpecies.setOnClickListener{
            // Uncheck all check boxes
            val childCount = binding.linearLayoutTreeSelection.childCount
            for (count in 0..childCount) {
                val checkboxTmp = binding.linearLayoutTreeSelection.getChildAt(count)
                if (checkboxTmp is CheckBox) {
                    checkboxTmp.isChecked = false
                }
            }
        }

        // Set onClickListener for FilterAll hardiness zones
        binding.filterAllHardinessZones.setOnClickListener{
            // Check all check boxes
            val childCount = binding.linearLayoutHardinessZonesSelection.childCount
            for (count in 0..childCount) {
                val checkboxTmp = binding.linearLayoutHardinessZonesSelection.getChildAt(count)

                // Get hardiness zones from Tasks
                viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
                    val hardinessZonesTasks = viewModel.getHardinessZonesFromTasks(tasks)

                    // Set true if checkboxTmp is true and if hardiness zone is in list of hardiness zones
                    if (checkboxTmp is CheckBox && checkboxTmp.text in hardinessZonesTasks) {
                        checkboxTmp.isChecked = true
                    }
                }
            }
        }

        // Set onClickListener for RadioBtn to set from my trees
        binding.filterMyHardinessZone.setOnClickListener {
            // Check all check boxes that share their text with a String in myTreeSpecies
            val childCount = binding.linearLayoutHardinessZonesSelection.childCount
            for (count in 0..childCount) {
                val checkboxTmp = binding.linearLayoutHardinessZonesSelection.getChildAt(count)
                if (checkboxTmp is CheckBox) {
                    // Check the checkbox if its text equals the users hardiness zone
                    checkboxTmp.isChecked = checkboxTmp.text == viewModel.getSettingsHardinessZone()
                }
            }
        }

        // Set onClickListener for FilterNone hardiness zones
        binding.filterNoneHardinessZones.setOnClickListener{
            // Uncheck all check boxes
            val childCount = binding.linearLayoutHardinessZonesSelection.childCount
            for (count in 0..childCount) {
                val checkboxTmp = binding.linearLayoutHardinessZonesSelection.getChildAt(count)
                if (checkboxTmp is CheckBox) {
                    checkboxTmp.isChecked = false
                }
            }
        }

        // Set onClickListener for btnApply to apply filter and navigate toCalendarFragment
        binding.btnApplyFilter.setOnClickListener {
                view : View ->
            // Update activeFilter
            updateActiveFilterTreeSpecies()

            // Update activeFilterHardinessZones
            updateActiveFilterHardinessZones()

            // Set active radio button tree species temp from selected radio button
            val selectedId: Int = binding.radioGroupFilterTreesSpecies.checkedRadioButtonId
            val radioButtonTreeSpecies: RadioButton? = binding.radioGroupFilterTreesSpecies.findViewById(selectedId)

            // Get active hardiness zone filter from radio group
            val selectedIdHardinessZone: Int = binding.radioGroupFilterHardinessZones.checkedRadioButtonId
            val radioButtonHardinessZone: RadioButton? = binding.radioGroupFilterHardinessZones.findViewById(selectedIdHardinessZone)

            // Set active filtered radio button tree species and hardiness zone
            viewModel.setSettingsActiveRadioButtonTreeSpecies(radioButtonTreeSpecies?.text.toString())
            viewModel.setSettingsActiveRadioButtonHardinessZones(radioButtonHardinessZone?.text.toString())

            // Navigate to calendar fragment
            view.findNavController().navigate(R.id.action_navigation_filter_to_navigation_calendar)
        }

        // Get all tasks from database and create checkbox for each unique tree species
        viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            val treeSpeciesList = viewModel.getTreeSpeciesNameFromTasks(tasks)
            // Add checkbox for each tree species
            for (treeSpecies in treeSpeciesList) {
                addCheckboxAndCheckFilteredTreeSpecies(treeSpecies)
            }
        }

        // Get hardinessZones from resources string as a list
        val hardinessZones = resources.getStringArray(R.array.array_of_hardiness_zones).toList()

        // Add checkboxes for hardiness zones
        addCheckboxesAndCheckFilteredHardinessZones(hardinessZones)
    }

    override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Add check boxes for tree species programmatically
    private fun addCheckboxAndCheckFilteredTreeSpecies(treeSpecies: String) {
        // Create CheckBox for input string
        val checkBox = CheckBox(context)

        // Set checkbox text to tree species
        checkBox.text = treeSpecies

        // If the active radioButton is "My Trees", then add/remove tree species accordingly
        if (binding.radioGroupFilterTreesSpecies.checkedRadioButtonId == binding.filterMyTreesTreeSpecies.id) {

            // Set myTreeSpecies as activeFilter
            for (treeSpeciesTmp in viewModel.myGardenTreeSpecies) {
                if (treeSpeciesTmp in viewModel.allTaskTreeSpecies) {
                    viewModel.addUniqueTreeSpeciesToListOfTreeSpecies(viewModel.getSettingsActiveFilteredTreeSpecies(), treeSpeciesTmp)
                } else {
                    viewModel.removeUniqueTreeSpeciesFromListOfTreeSpecies(viewModel.getSettingsActiveFilteredTreeSpecies(), treeSpeciesTmp)
                }
            }
        }

        // Check all from activeFilter
        if (checkBox.text in viewModel.getSettingsActiveFilteredTreeSpecies()) {
            checkBox.isChecked = true
        }

        // Add CheckBox to LinearLayout
        binding.linearLayoutTreeSelection.addView(checkBox)
    }

    // Add checkbox for hardiness zones programmatically
    private fun addCheckboxesAndCheckFilteredHardinessZones(hardinessZones: List<String>) {
        // Get all unique hardiness zones from database
        viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            val hardinessZonesTasks = viewModel.getHardinessZonesFromTasks(tasks)

            // Add checkbox for each hardiness zone
            for (hardinessZone in hardinessZones) {

                val checkBox = CheckBox(context)

                // Set checkbox text to hardiness zone
                checkBox.text = hardinessZone

                // Check if hardiness zone is in list of hardiness zones from Tasks
                if (hardinessZone in hardinessZonesTasks) {
                    // Check checkbox if in active filtered hardiness zones
                    if (hardinessZone in viewModel.getSettingsActiveFilteredHardinessZones()) {
                        checkBox.isChecked = true
                    }
                }
                else {
                    // If hardiness zone is not present in Tasks, uncheck checkbox etc
                    // Set check to false
                    checkBox.isChecked = false
                    // Disable checkbox
                    checkBox.isEnabled = false
                    // Set alpha to 0.5
                    checkBox.alpha = 0.5f
                }
                // Add checkbox to layout
                binding.linearLayoutHardinessZonesSelection.addView(checkBox)
            }
        }
    }

    private fun setActiveRadioButtonTreeSpeciesFromSettings() {
        // Remember and set the active radio button and check it
        val childCountRadio = binding.radioGroupFilterTreesSpecies.childCount
        for (count in 0..childCountRadio) {
            val radioButtonTmp = binding.radioGroupFilterTreesSpecies.getChildAt(count)
            if (radioButtonTmp is RadioButton) {
                if (viewModel.getSettingsActiveRadioButtonTreeSpecies() == radioButtonTmp.text) {
                    // Check the radio button in the layout
                    radioButtonTmp.isChecked = true

                    // Set activeRadioButton in viewModel
                    viewModel.setSettingsActiveRadioButtonTreeSpecies(radioButtonTmp.text as String)
                    break
                }
            }
        }
    }

    private fun setActiveRadioButtonHardinessZoneFromSettings() {
        // Remember and set the active radio button and check it
        val childCountRadio = binding.radioGroupFilterHardinessZones.childCount
        for (count in 0..childCountRadio) {
            val radioButtonTmp = binding.radioGroupFilterHardinessZones.getChildAt(count)
            if (radioButtonTmp is RadioButton) {
                if (viewModel.getSettingsActiveRadioButtonHardinessZones() == radioButtonTmp.text) {
                    // Check the radio button in the layout
                    radioButtonTmp.isChecked = true

                    // Set activeRadioButton in viewModel
                    viewModel.setSettingsActiveRadioButtonHardinessZones(radioButtonTmp.text as String)
                    break
                }
            }
        }
    }

    // Update activeFilteredTreeSpecies with current state of check boxes being checked/unchecked
    private fun updateActiveFilterTreeSpecies() {
        // Get all check boxes
        val childCount = binding.linearLayoutTreeSelection.childCount

        // Init empty temporary list of active filtered tree species
        val activeFilterTreeSpeciesTmp = mutableListOf<String>()

        // Loop over check boxes
        for (count in 0..childCount) {
            val checkboxTmp = binding.linearLayoutTreeSelection.getChildAt(count)
            // Check if child is a checkbox
            if (checkboxTmp is CheckBox) {
                // Check if check box is checked
                if (checkboxTmp.isChecked) {
                    // Add tree species to activeFilterTreeSpecies
                    activeFilterTreeSpeciesTmp.add(checkboxTmp.text.toString())
                }
            }
        }
        // Update activeFilterTreeSpecies in database
        viewModel.setSettingsActiveFilteredTreeSpecies(activeFilterTreeSpeciesTmp.joinToString(","))
    }

    // Update activeFilterHardinessZones with current state of check boxes being checked/unchecked
    private fun updateActiveFilterHardinessZones() {
        // Get all check boxes
        val childCount = binding.linearLayoutHardinessZonesSelection.childCount

        // Init empty temporary list of active filtered hardiness zones
        val activeFilterHardinessZonesTmp = mutableListOf<String>()

        // Loop over check boxes
        for (count in 0..childCount) {
            val checkboxTmp = binding.linearLayoutHardinessZonesSelection.getChildAt(count)
            // Check if child is a checkbox
            if (checkboxTmp is CheckBox) {
                // Check if check box is checked
                if (checkboxTmp.isChecked) {
                    // Add tree species to activeFilterTreeSpecies
                    activeFilterHardinessZonesTmp.add(checkboxTmp.text.toString())
                }
            }
        }
        // Update activeFilterTreeSpecies in database
        viewModel.setSettingsActiveFilteredHardinessZones(activeFilterHardinessZonesTmp.joinToString(","))
    }
}