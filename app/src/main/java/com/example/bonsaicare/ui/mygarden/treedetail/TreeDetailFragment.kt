package com.example.bonsaicare.ui.mygarden.treedetail

import ImageGalleryAdapter
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.bonsaicare.R
import com.example.bonsaicare.databinding.FragmentTreeDetailCardBinding
import com.example.bonsaicare.ui.calculateAge
import com.example.bonsaicare.ui.calendar.BonsaiViewModel
import com.example.bonsaicare.ui.calendar.BonsaiViewModelFactory
import com.example.bonsaicare.ui.calendar.BonsaiApplication
import com.example.bonsaicare.ui.mygarden.MyGardenFragment


class TreeDetailFragment : Fragment() {

    private var _binding: FragmentTreeDetailCardBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // Get reference to sharedBonsaiViewModel
    private val viewModel: BonsaiViewModel by activityViewModels {
        BonsaiViewModelFactory((requireNotNull(this.activity).application as BonsaiApplication).repository)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val fragment = MyGardenFragment()
                this.fragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.action_navigation_tree_detail_card_to_navigation_my_garden, fragment)
                    ?.addToBackStack(null)
                    ?.commit()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Disable 'back' button of support action bar in this fragment
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // This callback will only be called when MyTreeCardFragment(?) is at least started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    view.findNavController().navigate(R.id.action_navigation_tree_detail_card_to_navigation_my_garden)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        // Get current tree as a temporary parameter
        val currentTreeTmp = viewModel.getTreeAtIndex(viewModel.currentTreeIndex)

        // Set fragment title in support action bar to currents tree name
        (activity as AppCompatActivity?)!!.supportActionBar?.title = currentTreeTmp.name

        // Populate layout with current tree data
        binding.imageViewTree.setImageURI(currentTreeTmp.imagesUri[currentTreeTmp.thumbnailImageIndex])
        binding.textViewTreeSpecies.text = currentTreeTmp.treeSpecies.name
        binding.textViewTreeSpeciesLatin.text = currentTreeTmp.treeSpecies.nameLatin
        binding.textViewTreeShortDescription.text = currentTreeTmp.shortDescription
        binding.textViewTreeStyle.text = currentTreeTmp.style
        // Set dates as strings
        binding.textViewTreeDateOfBirth.text =  "${currentTreeTmp.dateOfBirth.year}-${currentTreeTmp.dateOfBirth.monthValue}-${currentTreeTmp.dateOfBirth.dayOfMonth}" + " (${calculateAge(currentTreeTmp.dateOfBirth)} yrs)"
        binding.textViewTreeDateOfAcquisition.text = "${currentTreeTmp.dateOfAcquisition.year}-${currentTreeTmp.dateOfAcquisition.monthValue}-${currentTreeTmp.dateOfAcquisition.dayOfMonth}"  + " (${calculateAge(currentTreeTmp.dateOfAcquisition)} yrs)"

        // Image Gallery
        // Get all images from current tree
        val myGalleryImages = currentTreeTmp.imagesUri
        // Pass the image list to Adapter
        val adapterImageGallery = ImageGalleryAdapter(myGalleryImages)

        // Set adapter for image gallery
        binding.recyclerViewTreeDetailImageGallery.adapter = adapterImageGallery

        // If an image in the gallery is clicked
        adapterImageGallery.apply { onImageClick = {
            // Set current image index to clicked image
            viewModel.currentImageIndex = it

            // Navigate to specific tree card
            findNavController().navigate(R.id.action_navigation_new_tree_to_image_view_full_screen)
            }
        }

        // Create onClickListener on Edit button, move to EditTree Fragment
        binding.btnEdit.setOnClickListener {
            // Set rememberSelectedSpinnerTreeSpecies of current tree
            viewModel.rememberSelectedSpinnerTreeSpecies = currentTreeTmp.treeSpecies.name

            view.findNavController().navigate(R.id.action_navigation_tree_detail_to_navigation_edit_tree)
        }

        // Set click listener to delete button and delete current tree, then navigate to myGardenFragment
        val buttonDelete = binding.btnDelete
        buttonDelete.setOnClickListener {

            // build alert dialog
            val dialogBuilder = AlertDialog.Builder(requireContext())

            // set message of alert dialog
            dialogBuilder.setMessage("Do you really want to delete this tree?")
                // if the dialog is cancelable
                .setCancelable(false)
                // negative button text and action
                .setNegativeButton("No!") { dialog, _ ->
                    dialog.cancel()
                }
                // Delete current tree and navigate back to my garden
                .setPositiveButton("Yes") { _, _ ->
                    // Delete current tree from database
                    viewModel.deleteDatabaseTreeByName(currentTreeTmp.name)
                    view.findNavController()
                        .navigate(R.id.action_navigation_tree_detail_card_to_navigation_my_garden)
                }

            // Create dialog box
            val alert = dialogBuilder.create()

            // Set title for alert dialog box
            alert.setTitle("Attention!")

            // Show alert dialog
            alert.show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTreeDetailCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}