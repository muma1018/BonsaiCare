package com.example.bonsaicare.ui.mygarden

import TreeListAdapter
import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bonsaicare.R
import com.example.bonsaicare.databinding.FragmentMyGardenBinding
import com.example.bonsaicare.ui.calendar.*
import com.example.bonsaicare.ui.database.*

// Todo: See mobile version -> text does not fit the screen, just use tree name and thumbnail image
//  Or: use one tree per row!
class MyGardenFragment : Fragment() {

    // Init viewModel
    private val viewModel: BonsaiViewModel by activityViewModels {
        BonsaiViewModelFactory((requireNotNull(this.activity).application as BonsaiApplication).repository)
    }

    private var _binding: FragmentMyGardenBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMyGardenBinding.inflate(inflater, container, false)

        return binding.root
    }

    // On resume
    override fun onResume() {
        super.onResume()
        // Reset rememberSelectedSpinnerTreeSpecies
        viewModel.rememberSelectedSpinnerTreeSpecies = ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Enable 'back' button of support action bar in this fragment
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // This callback will only be called when MyGarden Fragment is at least started
        // Enable the device's 'back button'
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // This is needed otherwise it would navigate back to TreeDetailFragment
                    view.findNavController().navigate(R.id.action_navigation_my_garden_to_my_garden)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        // Add tree if not tree there yet
        viewModel.getNumberOfTrees().observe(this) {numberTrees ->
            val numTrees = numberTrees ?: 0

            if (numTrees < 1) {
                //var treeSpeciesTmp = viewModel.getTreeSpeciesByName("Chinese Juniper")!!
                var treeSpeciesTmp = TreeSpecies(name = "Chinese Juniper", nameLatin = "juniperus chinensis", restricted = false, description = "")

                if (treeSpeciesTmp != null) {
                    // Build tree
                    val testTree = Tree(
                        name = "Juniperus Chinensis #1",
                        imagesUri = mutableListOf(
                            Uri.Builder()
                                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                                .authority(context!!.packageName)
                                .path(R.drawable.tree_juniperus_chinensis_1.toString())
                                .build(),

                            Uri.Builder()
                                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                                .authority(context!!.packageName)
                                .path(R.drawable.tree_juniperus_chinensis_0.toString())
                                .build()),
                        treeSpecies = treeSpeciesTmp,
                        style = "Moyogi")
                    viewModel.insertTree(testTree)
                }

                // Create new tree Acer Palmatum
                //treeSpeciesTmp = viewModel.getTreeSpeciesByName("Japanese Maple")!!
                treeSpeciesTmp = TreeSpecies(name = "Japanese Maple", nameLatin = "acer palmatum", restricted = false, description = "")

                if (treeSpeciesTmp != null) {
                    val testTree = Tree(
                        name = "Acer Palmatum #1",
                        imagesUri = mutableListOf(
                            Uri.Builder()
                                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                                .authority(context!!.packageName)
                                .path(R.drawable.tree_acer_palmatum_1.toString())
                                .build(),
                            Uri.Builder()
                                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                                .authority(context!!.packageName)
                                .path(R.drawable.tree_acer_palmatum_2.toString())
                                .build(),
                            Uri.Builder()
                                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                                .authority(context!!.packageName)
                                .path(R.drawable.tree_acer_palmatum_0.toString())
                                .build(),
                        ),
                        treeSpecies = treeSpeciesTmp,
                        style = "Moyogi")
                    viewModel.insertTree(testTree)
                }

                // Create new tree Malus Sylvestris
                //treeSpeciesTmp = viewModel.getTreeSpeciesByName("Grab Apple")!!
                treeSpeciesTmp = TreeSpecies(name = "Crab Apple", nameLatin = "malus sylvestris", restricted = false, description = "")

                if (treeSpeciesTmp != null) {
                    val testTree = Tree(
                        name = "Malus Sylvestris #1",
                        imagesUri = mutableListOf(
                            Uri.Builder()
                                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                                .authority(context!!.packageName)
                                .path(R.drawable.tree_malus_sylvestris_0.toString())
                                .build(),
                            Uri.Builder()
                                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                                .authority(context!!.packageName)
                                .path(R.drawable.tree_malus_sylvestris_1.toString())
                                .build(),
                            Uri.Builder()
                                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                                .authority(context!!.packageName)
                                .path(R.drawable.tree_malus_sylvestris_2.toString())
                                .build(),
                        ),
                        treeSpecies = treeSpeciesTmp,
                        style = "Moyogi")
                    viewModel.insertTree(testTree)
                }
            }
        }

        // Get recycler view
        val treesRecyclerView = binding.recyclerViewMyTrees
        // Set size fixed
        treesRecyclerView.setHasFixedSize(true)

        // Initialize adapter
        val databaseTreeAdapter = TreeListAdapter(viewModel)

        // Set adapter for recyclerView
        treesRecyclerView.adapter = databaseTreeAdapter

        // This creates a vertical layout Manager
        treesRecyclerView.layoutManager = LinearLayoutManager(context)
        // This line creates two columns of tree views in recycler view
        treesRecyclerView.layoutManager = GridLayoutManager(context, 2)

        // Observe all trees
        viewModel.allTrees.observe(viewLifecycleOwner) { trees ->
            // Convert tasks to cards
            val cards = createCardsFromTrees(trees)

            // Sort cards by name
            val sortedCards = cards.sortedWith(compareBy { it.name })

            // Submit cards to adapter
            databaseTreeAdapter.submitList(sortedCards)

        }

        // Inflate the layout for this fragment
        View.inflate(context, R.layout.fragment_tree_detail_card, null)

        // Set click listener to navigate to new tree fragment
        binding.imgBtnAddNewTree.setOnClickListener {
                view : View ->
            view.findNavController().navigate(R.id.action_navigation_my_garden_to_new_tree)
        }

        // Set text for alert dialog
        val appWelcomeText =
            HtmlCompat.fromHtml(binding.root.resources.getString(R.string.introduction_text), HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

        // Show dialog box
        val dialogBuilderStartUp = AlertDialog.Builder(binding.root.context)

        // Show Welcome Alert if not False
        // Note: If user clicks super-fast (basically < 1 sec on pixel device) 'do not show again',
        // before the database is initialized, at first start-up of the app when it is fresh
        // installed, then the settings will not stick
        if (!viewModel.getSettingsDoNotShowAlert()){
            dialogBuilderStartUp.setMessage(appWelcomeText)
                // If the dialog is cancelable
                .setCancelable(true)
                // Negative button text and action
                .setNegativeButton("Thanks!") { dialog, _ ->
                    dialog.cancel()
                }
                .setNeutralButton("Don't show again"){
                        dialog, _ -> dialog.cancel()
                    // Set doNotShowAlert in UserSettings to true if user does not want to see it again
                    //viewModel.showAlert = true
                    viewModel.updateUsersDoNotShowWelcomeAlert(true)
                }
            // Create dialog box and show alert
            val alert = dialogBuilderStartUp.create()
            // Set title for alert dialog box
            alert.setTitle("Welcome to BonsaiCare!")
            // Show alert dialog
            alert.show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}