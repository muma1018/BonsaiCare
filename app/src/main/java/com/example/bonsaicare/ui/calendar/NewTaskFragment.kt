package com.example.bonsaicare.ui.calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.bonsaicare.R
import com.example.bonsaicare.databinding.FragmentNewTaskBinding
import com.example.bonsaicare.ui.database.Task
import com.example.bonsaicare.ui.database.TreeSpecies


// Todo MVP3: User can create new TaskType

class NewTaskFragment : Fragment() {

    private var _binding: FragmentNewTaskBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // Get reference to taskViewModel
    private val viewModel: BonsaiViewModel by activityViewModels {
        BonsaiViewModelFactory((requireNotNull(this.activity).application as BonsaiApplication).repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Enable action bars 'back' button of support action bar in this fragment
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // This callback will only be called when MyFragment is at least started
        // Enable the phones 'back' button
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Ask user if they want to discard changes and then navigate back (if no)
                    dialogSaveChanges()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        // Get reference to spinner in layout
        val spinnerHardinessZone: Spinner = view.findViewById(R.id.spinner_hardiness_zone)

        // Get array of hardiness zones from resources.xml file
        val hardinessZones = resources.getStringArray(R.array.array_of_hardiness_zones)

        // Build adapter for hardiness zones
        val hardinessZoneArrayAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, hardinessZones)

        // Set adapter for spinner
        spinnerHardinessZone.adapter = hardinessZoneArrayAdapter

        // Set click listener for add button
        binding.buttonSave.setOnClickListener {

            // Get values from EditTexts fields
            val treeSpecies = binding.spinnerTreeSpecies.selectedItem.toString()
            val treeSpeciesLatin = binding.spinnerTreeSpeciesLatin.selectedItem.toString()

            // Get taskType from spinner
            val taskType = viewModel.getTaskTypeByName(binding.spinnerTaskType.selectedItem.toString())

            val shortDescription = binding.insertShortDescription.text.toString()
            val longDescription = binding.insertLongDescription.text.toString()
            val listOfDates = binding.insertListOfDates.text.toString()
            val listOfLabels = binding.insertListOfLabels.text.toString()

            // Get hardiness zone from spinner
            val hardinessZone = binding.spinnerHardinessZone.selectedItem.toString()

            // Check if all necessary fields have been filled to create a task
            if ((treeSpecies == "" && treeSpeciesLatin == "") || longDescription == "" ||
                listOfDates == "" || hardinessZone == "") {

                // Send toast to user
                Toast.makeText(requireContext(), "All * fields have to be filled", Toast.LENGTH_LONG).show()
                }
            else {
                // Check if tree exists in database, by its name or its latin name
                var treeSpeciesTmp = if (treeSpecies != "") {
                    // These return null if tree was not found
                    viewModel.getTreeSpeciesByName(treeSpecies)
                } else {
                    // Get tree species from database by latin name, returns null if tree was not found
                    viewModel.getTreeSpeciesByNameLatin(treeSpeciesLatin)
                }

                // No tree species found in database, create own from user input
                if (treeSpeciesTmp == null) {
                    treeSpeciesTmp = TreeSpecies(treeSpecies, treeSpeciesLatin)

                    // Add new tree species from user to database
                    viewModel.insertTreeSpecies(treeSpeciesTmp)
                }

                // These are used to create a Task and check if it is a valid task
                var newTask = Task()
                var newTaskSuccess = true

                try {
                    // Create new task
                    newTask = Task(treeSpecies = treeSpeciesTmp, taskType = taskType, shortDescription = shortDescription, longDescription = longDescription, listOfDates = listOfDates, listOfLabels = listOfLabels, hardinessZone = hardinessZone)

                    // Split the string into an array of integers
                    val myArray = newTask.listOfDates.split(",").map { it.trim().toInt() }

                    // Check if any integer in the array is greater than 12
                    val result = myArray.any { it > 12 }
                    if (result) {
                        newTaskSuccess = false
                    }
                }
                catch (e: Exception) {
                    newTaskSuccess = false
                }

                if (newTaskSuccess) {
                    // Add new task
                    viewModel.insertTask(newTask)

                    // Show toast to user
                    Toast.makeText(requireContext(), "Task saved successfully", Toast.LENGTH_LONG).show()

                    // Reset selected spinner tree species
                    viewModel.rememberSelectedSpinnerTreeSpecies = ""

                    // Navigate back to Calendar
                    findNavController().navigate(R.id.action_navigation_new_task_to_calendar)
                }
                else {
                    // Send toast to user and stay in fragment
                    Toast.makeText(requireContext(), "The list of dates must not contain any number greater than 12.", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Set onClickListener for Add new Tree Species button
        // -> Navigate to New Tree Species Fragment
        binding.btnNewTreeSpecies.setOnClickListener {
                view : View ->
            view.findNavController().navigate(R.id.action_navigation_new_task_to_new_tree_species)
        }

        // Initialize TaskType Spinner
        val spinnerTaskType: Spinner = view.findViewById(R.id.spinner_task_type)

        // Set adapter for spinner
        val allTaskTypesArrayAdapter = context?.let {
            ArrayAdapter<Any>(it, android.R.layout.simple_spinner_dropdown_item)
        }

        // Get all task types from database
        viewModel.getALlTaskTypes()
            .observe(viewLifecycleOwner) { names ->
                names?.forEach {
                    // Populate both spinners with tree species
                    allTaskTypesArrayAdapter?.add(it.name)
                }
            }

        // Set adapter for Spinner Task Type as allTaskTypesArrayAdapter
        spinnerTaskType.adapter = allTaskTypesArrayAdapter

        // Get reference to spinners
        val spinnerTreeSpecies: Spinner = view.findViewById(R.id.spinner_tree_species)
        // Latin Spinner
        val spinnerTreeSpeciesLatin: Spinner = view.findViewById(R.id.spinner_tree_species_latin)

        // Set adapter for spinner
        val allTreeSpeciesArrayAdapter = context?.let {
            ArrayAdapter<Any>(it, android.R.layout.simple_spinner_dropdown_item)
        }
        // Set adapter for Latin spinner
        val allTreeSpeciesLatinArrayAdapter = context?.let {
            ArrayAdapter<Any>(it, android.R.layout.simple_spinner_dropdown_item)
        }

        // Get all tree species from database
        viewModel.allTreeSpecies
            .observe(viewLifecycleOwner) { names ->
                names?.forEach {
                    // Populate both spinners with tree species
                    allTreeSpeciesArrayAdapter?.add(it.name)
                }
            }

        // Get all tree species latin from database
        viewModel.getAllTreeSpeciesNamesLatin()
            .observe(viewLifecycleOwner) { names ->
                names?.forEach {
                    // Populate both spinners with tree species
                    allTreeSpeciesLatinArrayAdapter?.add(it)
                }
            }

        // Boolean flag to track if a spinner item is selected by the user or by the app (indirect
        // through user)
        var updatingSelectedItem = false
        var updatingSelectedItemLatin = false

        // We need two extra flags to avoid bad looping with the spinners
        var initialSelectedItem = true

        // Set spinner adapters
        spinnerTreeSpecies.adapter = allTreeSpeciesArrayAdapter
        spinnerTreeSpeciesLatin.adapter = allTreeSpeciesLatinArrayAdapter

        // Handle the selected item
        spinnerTreeSpecies.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    // If this is the first startup of this fragment just set the latin spinner and pass
                    if (initialSelectedItem) {

                        // Set selected item to selected Spinner Tree Species from viewModel if not ""
                        if (viewModel.rememberSelectedSpinnerTreeSpecies != "") {
                            spinnerTreeSpecies.setSelection(allTreeSpeciesArrayAdapter!!.getPosition(viewModel.rememberSelectedSpinnerTreeSpecies))
                        }

                        // Change latin spinner selected item once
                        val selectedItem = parent.getItemAtPosition(position).toString()

                        // Get corresponding latin selected item
                        val itemNameLatin = viewModel.getCorrespondingLatin(selectedItem)

                        // Set latin spinner to corresponding latin tree species
                        spinnerTreeSpeciesLatin.setSelection(allTreeSpeciesLatinArrayAdapter!!.getPosition(itemNameLatin))
                    }
                    // Change the latin spinner's selected item and avoid infinite loop by using a flag
                    else if (!updatingSelectedItem && !updatingSelectedItemLatin) {
                        // Change flag to true
                        updatingSelectedItemLatin = true

                        // Get selected item
                        val selectedItem = parent.getItemAtPosition(position).toString()

                        // Get corresponding latin selected item
                        val itemNameLatin = viewModel.getCorrespondingLatin(selectedItem)

                        // Set latin spinner to corresponding latin tree species
                        spinnerTreeSpeciesLatin.setSelection(allTreeSpeciesLatinArrayAdapter!!.getPosition(itemNameLatin))
                    } else if (updatingSelectedItem && !updatingSelectedItemLatin) {
                        updatingSelectedItem = false
                        updatingSelectedItemLatin = false
                    }
                }

                // Set selected item in viewModel, so we can remember it when coming back from e.g. NewTreeSpeciesFragment
                viewModel.rememberSelectedSpinnerTreeSpecies = parent?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // Handle the selected item of spinner
        spinnerTreeSpeciesLatin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    // We need these two flags to avoid bad loop from the spinners (one flag for
                    // start-up of this fragment and one flag for initial spinner selection from
                    // normal tree species spinner
                    if (initialSelectedItem) {
                        initialSelectedItem = false

                    // Change the latin spinner's selected item and avoid infinite loop by using a flag
                    } else if (!updatingSelectedItem && !updatingSelectedItemLatin) {
                        // Change flag to true
                        updatingSelectedItem = true
                        // Get selected item
                        val selectedItem = parent.getItemAtPosition(position).toString()
                        // Get corresponding latin selected item
                        val itemName = viewModel.getCorrespondingName(selectedItem)
                        // Set latin spinner to corresponding latin tree species
                        spinnerTreeSpecies.setSelection(allTreeSpeciesArrayAdapter!!.getPosition(itemName))
                    } else if (!updatingSelectedItem && updatingSelectedItemLatin) {
                        updatingSelectedItem = false
                        updatingSelectedItemLatin = false
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNewTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun dialogSaveChanges() {
        // Ask the user if they really want to go back and discard changes
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Discard changes?")
        builder.setMessage("Do you want to discard changes to the new task?")
        builder.setPositiveButton("Yes") { _, _ ->

            // Reset selected spinner tree species
            viewModel.rememberSelectedSpinnerTreeSpecies = ""

            // Navigate to MyGarden Fragment
            view?.findNavController()?.navigate(R.id.action_navigation_new_task_to_calendar)
        }
        builder.setNegativeButton("No") { _, _ ->
            // Do nothing
        }

        // Show the dialog
        val dialog = builder.create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}