package com.example.bonsaicare.ui.calendar

import android.app.AlertDialog
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.bonsaicare.R
import com.example.bonsaicare.databinding.FragmentCalendarBinding

// Todo: ON first startup the Filters 'All Trees' and 'All' hardiness zones are ticked, but no trees are shown, fix that
//  Also change 'All Trees' to 'All' and 'My Trees' to 'Own'?
class CalendarFragment : Fragment() {

    // Init viewModel
    private val viewModel: BonsaiViewModel by activityViewModels {
        BonsaiViewModelFactory((requireNotNull(this.activity).application as BonsaiApplication).repository)
    }

    private var _binding: FragmentCalendarBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // This callback will only be called when MyFragment is at least started
        // Enable the 'back button'
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Navigate to MyGarden Fragment
                    view.findNavController().navigate(R.id.action_navigation_calendar_to_my_garden)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        // Set onClickListener for btnFilter to navigate to FilterFragment
        binding.imgBtnTreeFilter.setOnClickListener {
                view : View ->
            view.findNavController().navigate(R.id.action_navigation_calendar_to_filter)
        }

        // If this is the first start up of the app, active filtered tree species is "1STARTUP"
        if (viewModel.getSettingsFirstAppStartUp()) {
            // Set first app start up to false
            viewModel.setSettingsFirstAppStartUp(false)

            // Get all tasks from database and add tree species to active filtered tree species
            viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
                val treeSpeciesList = viewModel.getTreeSpeciesNameFromTasks(tasks)

                // Create temporary list of active filtered tree species
                val listOfTreeSpeciesTmp = viewModel.getTreeSpeciesNameFromTasks(tasks).toMutableSet()

                // Loop over tree species and add to list of active filtered tree species
                for (treeSpecies in treeSpeciesList) {
                    // Update temporary list of active filtered tree species
                    viewModel.addUniqueTreeSpeciesToListOfTreeSpecies(listOfTreeSpeciesTmp, treeSpecies = treeSpecies)
                }
                // Set active filtered tree species from temporary list of tree species
                viewModel.setSettingsActiveFilteredTreeSpecies(listOfTreeSpeciesTmp.joinToString { "," })
            }
        }

        // Show tasks in recyclerView
        val tasksRecyclerView = binding.recyclerViewTasks
        // Set size fixed
        tasksRecyclerView.setHasFixedSize(true)

        // Set card filter type
        var cardFilterType = "tree"

        // Initialize adapters
        val cardsTreeAdapter = CardListTreeSpeciesAdapter(viewModel)
        val cardsTaskAdapter = CardListTaskTypeAdapter(viewModel)

        // Show cards according to cardFilterType
        // Create ItemAdapter instance that expects two params: the context (this) of this activity and the cards in cardsDataset
        // Assign the ItemAdapter object to the adapter property of recyclerView
        if (cardFilterType == "tree") {
            // Show cards by tree type
            tasksRecyclerView.adapter = cardsTreeAdapter

            // Observe all tasks
            viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
                // Convert tasks to cards
                val cards = createCardsByTreeType(tasks)

                // Try catch to prevent crash when no active filtered tree species are set
                try {
                    // Filter cards with list from activeFilterTreeTypes
                    for (i in cards.indices.reversed()) {
                        // Remove task if its treeType is not in activeFilterTreeTypes or not in activeFilterHardinessZones
                        if (cards[i].treeSpecies.name !in viewModel.getSettingsActiveFilteredTreeSpecies() || cards[i].hardinessZone !in viewModel.getSettingsActiveFilteredHardinessZones() ) {
                            cards.removeAt(i)
                        }
                    }

                    // Update the cached copy of the words in the adapter.
                    cards.let { cardsTreeAdapter.submitList(it) }

                } catch (e: Exception) {
                    // Do nothing
                }
            }

        } else {
            // Show cards by task type
            tasksRecyclerView.adapter = cardsTaskAdapter
            viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
                val tasksTmp = tasks.toMutableList()

                // Filter cards with list from activeFilterTreeTypes
                for (i in tasksTmp.indices.reversed()) {
                    // Remove task if its treeType is not in activeFilterTreeTypes or not in activeFilterHardinessZones
                    if (tasksTmp[i].treeSpecies.name !in viewModel.getSettingsActiveFilteredTreeSpecies() || tasksTmp[i].hardinessZone !in viewModel.getSettingsActiveFilteredHardinessZones() ) {
                        tasksTmp.removeAt(i)
                    }
                }

                // Convert tasks to cards
                val cards = createCardsByTaskType(tasksTmp)

                // Update the cached copy of the items in the adapter.
                cards.let { cardsTaskAdapter.submitList(it) }
            }
        }

        // Get reference to imageBtnCardFilter
        val imageBtnCardFilter: ImageButton = binding.imgBtnCardTypeFilter

        // Set onClickListener imageBtnCardFilter
        imageBtnCardFilter.setOnClickListener {

            // Set cardFilterType to tree/task and restart recyclerView
            if (cardFilterType == "tree") {
                cardFilterType = "task"
                // Show cards by tree type
                tasksRecyclerView.adapter = cardsTaskAdapter
                viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
                    val tasksTmp = tasks.toMutableList()

                    // Filter cards with list from activeFilterTreeTypes
                    for (i in tasksTmp.indices.reversed()) {
                        // Remove task if its treeType is not in activeFilterTreeTypes
                        if (tasksTmp[i].treeSpecies.name !in viewModel.getSettingsActiveFilteredTreeSpecies()) {
                            tasksTmp.removeAt(i)
                        }
                    }

                    // Convert tasks to cards
                    val cards = createCardsByTaskType(tasksTmp)

                    // Update the cached copy of the words in the adapter
                    cards.let { cardsTaskAdapter.submitList(it) }
                }
                // Change drawable of imageBtn to Trees
                imageBtnCardFilter.setImageResource(R.drawable.trees_icon_colored)

            } else {
                cardFilterType ="tree"
                // Show cards by task type
                tasksRecyclerView.adapter = cardsTreeAdapter
                viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->

                    // Convert tasks to cards
                    val cards = createCardsByTreeType(tasks)

                    // Remove cards that are not in activeFilterTreeTypes
                    // Loop over cards
                    for (i in cards.indices.reversed()) {
                        // Remove card if its treeType is not in activeFilterTreeTypes
                        if (cards[i].treeSpecies.name !in viewModel.getSettingsActiveFilteredTreeSpecies()) {
                            cards.removeAt(i)
                        }
                    }

                    // Update the cached copy of the words in the adapter.
                    cards.let { cardsTreeAdapter.submitList(it) }
                }
                // Change drawable of imageBtn to Tasks
                imageBtnCardFilter.setImageResource(R.drawable.tasks_icon_colored)
            }
        }

        // Make the new task button grayscale convert from color to grayscale
        val matrix = ColorMatrix()
        matrix.setSaturation(0f)
        val filter = ColorMatrixColorFilter(matrix)
        binding.imgBtnAddNewTask.colorFilter = filter

        // Variable to store boolean if New Task feature is active
        // Todo MVP3: Enable CreateNewTask feature for MVP3
        val newTaskFeatureActive = false

        // Create onClickListener on AddNewTask button to start new task activity
        binding.imgBtnAddNewTask.setOnClickListener {
            // If newTaskFeatureActive is false, show dialog
            if (!newTaskFeatureActive) {
                // Pop up dialog to tell the user that this feature will be activated in a future version
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Feature not available")
                builder.setMessage("This feature will be activated in a future version. \n" +
                        "Consider donating to support the development of this app.")
                builder.setPositiveButton("OK") { _, _ ->
                    // Do nothing
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
                //return@setOnClickListener
            }
            // If newTaskFeatureActive is true, navigate to new task fragment
            // Navigate to new task fragment
            //findNavController().navigate(R.id.action_navigation_calendar_to_new_task)
        }

        // Update recycler view
        cardsTaskAdapter.notifyDataSetChanged()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}