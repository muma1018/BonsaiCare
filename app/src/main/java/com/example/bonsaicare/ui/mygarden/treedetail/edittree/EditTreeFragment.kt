package com.example.bonsaicare.ui.mygarden.treedetail.edittree

import ImageGalleryAdapter
import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.bonsaicare.R
import com.example.bonsaicare.databinding.FragmentEditTreeBinding
import com.example.bonsaicare.ui.calendar.BonsaiApplication
import com.example.bonsaicare.ui.calendar.BonsaiViewModel
import com.example.bonsaicare.ui.calendar.BonsaiViewModelFactory
import com.example.bonsaicare.ui.convertDateStringToIsoFormat
import com.example.bonsaicare.ui.database.TreeSpecies
import com.example.bonsaicare.ui.isValidDateString
import java.time.LocalDate

class EditTreeFragment : Fragment() {

    private var _binding: FragmentEditTreeBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // Use the contentResolver to set up our image Uri with the value options. This is the Uri that will be used to assign the location of the resulting image.
    // For better camera stuff documentation see: NewTreeFragment.kt
    private var imageUri: Uri? = null

    // Use arbitrary Integer for image capture code
    private val CAMERA_PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    private val LOAD_GALLERY_IMAGE_CODE = 1002

    // Get reference to taskViewModel
    private val viewModel: BonsaiViewModel by activityViewModels {
        BonsaiViewModelFactory((requireNotNull(this.activity).application as BonsaiApplication).repository)
    }

    override fun onResume() {
        super.onResume()
        // Update thumbnail image index on resume (from ImageViewFullScreenFragment)
        viewModel.editTreeUnderConstruction.thumbnailImageIndex = viewModel.currentImageIndex

        // Update the recycler view whenever we resume to this fragment
        // e.g. when coming back from gallery or camera taking picture
        binding.recyclerViewEditTreeImageGallery.adapter?.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Enable 'back' button of support action bar in this fragment
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // This callback will only be called when MyFragment is at least started
        // Enable the device's 'back button'
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Ask user if they really want to leave fragment without saving changes
                    dialogSaveChanges()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        // Create temporary value for current tree
        viewModel.editTreeUnderConstruction = viewModel.getTreeAtIndex(viewModel.currentTreeIndex)

        // Get name of the tree from database
        viewModel.currentTreeName = viewModel.editTreeUnderConstruction.name

        // Populate EditText fields with parameters from tree under construction
        binding.editMyTreeName.setText(viewModel.editTreeUnderConstruction.name)

        binding.editShortDescription.setText(viewModel.editTreeUnderConstruction.shortDescription)
        binding.editMyTreeStyle.setText(viewModel.editTreeUnderConstruction.style)
        binding.editDateOfBirth.setText(viewModel.editTreeUnderConstruction.dateOfBirth.toString())
        binding.editDateOfAcquisition.setText(viewModel.editTreeUnderConstruction.dateOfAcquisition.toString())

        // Image Gallery
        // Get all images from current tree
        val myGalleryImages = viewModel.editTreeUnderConstruction.imagesUri
        // Pass the image list to Adapter
        val adapterImageGallery = ImageGalleryAdapter(myGalleryImages)

        // Set adapter for image gallery
        binding.recyclerViewEditTreeImageGallery.adapter = adapterImageGallery

        // If an image in the gallery is clicked, set current Image index and go to full screen fragment
        adapterImageGallery.apply { onImageClick = {
            // Set current image index to clicked image
            viewModel.currentImageIndex = it

            // Navigate to specific tree card
            findNavController().navigate(R.id.action_navigation_edit_tree_to_image_view_full_screen)
            }
        }

        // Set up Date Picker for Date of Birth
        binding.imageButtonDateOfBirthPicker.setOnClickListener {
            // Show a date picker dialog when the button is clicked
            val datePickerDialog = context?.let { it1 -> DatePickerDialog(it1) }
            datePickerDialog?.setOnDateSetListener { _, year, month, day ->
                // This block is called when the user selects a date
                val date = "$year-${month + 1}-$day"
                binding.editDateOfBirth.setText(date)
            }
            datePickerDialog?.show()
        }

        // Set up Date Picker for Date of Acquisition
        binding.imageButtonDataOfAcquisitionPicker.setOnClickListener {
            // Show a date picker dialog when the button is clicked
            val datePickerDialog = context?.let { it1 -> DatePickerDialog(it1) }
            datePickerDialog?.setOnDateSetListener { _, year, month, day ->
                // This block is called when the user selects a date
                val date = "$year-${month + 1}-$day"
                binding.editDateOfAcquisition.setText(date)
            }
            datePickerDialog?.show()
        }

        // Get global date format
        val dateFormat = viewModel.dateFormat

        // Set onClickListener for Add new Tree Species button
        // -> Navigate to New Tree Species Fragment
        binding.btnNewTreeSpecies.setOnClickListener {
                view : View ->
            view.findNavController().navigate(R.id.action_navigation_edit_tree_to_new_tree_species)
        }

        // Set onClickListener for save button and navigate to MyGardenFragment
        binding.buttonSave.setOnClickListener {
                view : View ->

            // Get tree name field
            val treeName = binding.editMyTreeName.text.toString()

            // Convert dates from edit text to LocalDate and check if they are valid
            var dateOfBirthString = convertDateStringToIsoFormat(binding.editDateOfBirth.text.toString())
            var dateOfAcquisitionString = convertDateStringToIsoFormat(binding.editDateOfAcquisition.text.toString())

            // Tree name must not be ""
            if (treeName != "") {
                // Tree with that name must not exist already or it must be the same tree
                if (viewModel.getDatabaseTreeByName(treeName) == null  || viewModel.currentTreeName == treeName) {
                    if (isValidDateString(dateOfBirthString) && isValidDateString(
                            dateOfAcquisitionString
                        )
                    ) {
                        // Change current trees params to the values of EditTexts and Spinners
                        viewModel.editTreeUnderConstruction.name = treeName
                        viewModel.editTreeUnderConstruction.treeSpecies.name = binding.spinnerEditTreeSpecies.selectedItem.toString()
                        viewModel.editTreeUnderConstruction.treeSpecies.nameLatin = binding.spinnerEditTreeSpeciesLatin.selectedItem.toString()
                        viewModel.editTreeUnderConstruction.treeSpecies.description = binding.editShortDescription.text.toString()
                        viewModel.editTreeUnderConstruction.style = binding.editMyTreeStyle.text.toString()

                        // Set Dates from EditTexts / DatePickers with formatted dates
                        viewModel.editTreeUnderConstruction.dateOfBirth =
                            LocalDate.parse(dateOfBirthString, dateFormat)
                        viewModel.editTreeUnderConstruction.dateOfAcquisition =
                            LocalDate.parse(dateOfAcquisitionString, dateFormat)

                        // Delete 'old' tree from database
                        viewModel.deleteDatabaseTreeByName(viewModel.currentTreeName)

                        // Insert 'new' (current) tree into database
                        viewModel.insertTree(viewModel.editTreeUnderConstruction)

                        // Update database with treeSpecies (this will be ignored if tree species already exists
                        viewModel.insertTreeSpecies(
                            TreeSpecies(
                                name = viewModel.editTreeUnderConstruction.treeSpecies.name,
                                nameLatin = viewModel.editTreeUnderConstruction.treeSpecies.nameLatin,
                                description = viewModel.editTreeUnderConstruction.treeSpecies.description
                            )
                        )

                        // Give informative toast
                        Toast.makeText(requireContext(), "Changes saved", Toast.LENGTH_LONG).show()

                        // Reset selected spinner tree species
                        viewModel.rememberSelectedSpinnerTreeSpecies = ""

                        // Navigate back to TreeDetail Fragment with pop backstack
                        view.findNavController().popBackStack()

                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Date of Birth and/or Date of Acquis. not valid.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Tree name already exists.", Toast.LENGTH_LONG).show()
                }
            }
            else {
                Toast.makeText(requireContext(), "Please fill at least Tree Name field.", Toast.LENGTH_LONG).show()
            }
        }

        // Set click listener for the image capture button
        view.findViewById<ImageButton>(R.id.image_capture_button).setOnClickListener {
            // Request permission
            val permissionGranted = requestCameraPermission()
            if (permissionGranted) {
                // Open the camera interface
                openCameraInterface()
            }
        }

        // Add images from gallery
        view.findViewById<ImageButton>(R.id.btn_add_images_from_gallery).setOnClickListener {
            // Open gallery interface
            //val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            //startActivityForResult(gallery, LOAD_GALLERY_IMAGE_CODE)
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                type = "image/*"
                startActivityForResult(this, LOAD_GALLERY_IMAGE_CODE)
            }
        }

        // Spinner stuff
        val spinner: Spinner = view.findViewById(R.id.spinner_edit_tree_species)
        val spinnerLatin: Spinner = view.findViewById(R.id.spinner_edit_tree_species_latin)

        // Set spinner adapters
        val allTreeSpeciesArrayAdapter = context?.let {
            ArrayAdapter<Any>(it, android.R.layout.simple_spinner_dropdown_item)
        }
        // Set Adapter for Latin Spinner
        val allTreeSpeciesLatinArrayAdapter = context?.let {
            ArrayAdapter<Any>(it, android.R.layout.simple_spinner_dropdown_item)
        }

        // Get all tree species from database and add to spinner
        viewModel.getAllTreeSpeciesNames()
            .observe(viewLifecycleOwner) { names ->
                names?.forEach {
                    // Populate both spinners with tree species
                    allTreeSpeciesArrayAdapter?.add(it)
                }
            }

        // Get all tree species latin from database and add to spinner
        viewModel.getAllTreeSpeciesNamesLatin()
            .observe(viewLifecycleOwner) { names ->
                names?.forEach {
                    // Populate both spinners with tree species
                    allTreeSpeciesLatinArrayAdapter?.add(it)
                }
            }

        // We need extra flag to avoid bad looping with the spinners
        var initialSelectedItem = true

        // Set spinner adapters
        spinner.adapter = allTreeSpeciesArrayAdapter
        spinnerLatin.adapter = allTreeSpeciesLatinArrayAdapter

        // Handle the selected item
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {

                    if (initialSelectedItem) {
                        initialSelectedItem = false

                        // Set selected item to selected Spinner Tree Species from viewModel if not ""
                        if (viewModel.rememberSelectedSpinnerTreeSpecies != "") {
                            spinner.setSelection(allTreeSpeciesArrayAdapter!!.getPosition(
                                    viewModel.rememberSelectedSpinnerTreeSpecies)
                            )
                        }
                    } else {

                        // Get both spinners selected items
                        val selectedItem = spinner.selectedItem.toString()
                        val selectedItemLatin = spinnerLatin.selectedItem.toString()

                        // If spinner is currently not equal trees tree species
                        if (viewModel.rememberSelectedSpinnerTreeSpecies != selectedItem) {

                            // Set selected item to selected Spinner Tree Species from viewModel if not ""
                            if (viewModel.rememberSelectedSpinnerTreeSpecies != "") {
                                spinner.setSelection(allTreeSpeciesArrayAdapter!!.getPosition(selectedItem))
                                // Remember selected item
                                viewModel.rememberSelectedSpinnerTreeSpecies = selectedItem
                            }
                        }
                        // Get corresponding latin selected item
                        val itemNameLatin = viewModel.getCorrespondingLatin(selectedItem)

                        // If latin spinner does not equal trees tree species latin
                        if (selectedItemLatin != itemNameLatin) {

                            // Set latin spinner to corresponding latin tree species
                            spinnerLatin.setSelection(
                                allTreeSpeciesLatinArrayAdapter!!.getPosition(itemNameLatin)
                            )
                        }
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // We need a flag to check if the spinner is being initialized
        var initialTriggerFromNormalSpinner = true

        // Handle the selected item
        spinnerLatin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {

                    // Check if spinner is being initialized
                    if (initialTriggerFromNormalSpinner) {
                        initialTriggerFromNormalSpinner = false

                        // Set selected item to selected Spinner Tree Species from viewModel if not ""
                        if (viewModel.rememberSelectedSpinnerTreeSpecies != "") {
                            spinnerLatin.setSelection(allTreeSpeciesLatinArrayAdapter!!.getPosition(
                                viewModel.getCorrespondingLatin(viewModel.rememberSelectedSpinnerTreeSpecies))
                            )
                        }
                    }

                    // Get both spinners selected items
                    val selectedItem = spinner.selectedItem.toString()
                    val selectedItemLatin = spinnerLatin.selectedItem.toString()

                    // Update selected item of normal spinner if it is not already set to the correct species
                    if (selectedItem != viewModel.getCorrespondingName(selectedItemLatin)) {
                        // Get corresponding latin selected item
                        val itemName = viewModel.getCorrespondingName(selectedItemLatin)
                        // Set latin spinner to corresponding latin tree species
                        spinner.setSelection(allTreeSpeciesArrayAdapter!!.getPosition(itemName))

                        // Set view model to remember selected item
                        viewModel.rememberSelectedSpinnerTreeSpecies = spinner.selectedItem.toString()
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun requestCameraPermission(): Boolean {
        var permissionGranted = false
        // If system os is Marshmallow or Above, we need to request runtime permission
        val cameraPermissionNotGranted = checkSelfPermission(activity as Context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
        if (cameraPermissionNotGranted){
            val permission = arrayOf(Manifest.permission.CAMERA)
            // Display permission dialog
            requestPermissions(permission, CAMERA_PERMISSION_CODE)
        }
        else{
            // Permission already granted
            permissionGranted = true
        }
        return permissionGranted
    }

    // Process user's response about permissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode === CAMERA_PERMISSION_CODE) {
            if (grantResults.size === 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Permission was granted
                openCameraInterface()
            }
            else{
                // Permission was denied
                showAlert("Camera permission was denied. Unable to take a picture.")
            }
        }
    }

    // Utility method to show alert
    private fun showAlert(message: String) {
        val builder = AlertDialog.Builder(activity as Context)
        builder.setMessage(message)
        builder.setPositiveButton(R.string.ok_button_title, null)
        val dialog = builder.create()
        dialog.show()
    }

    // Request interface for camera and let Android OS choose suitable apps (camera app)
    // Camera will be opened and show the view finder
    private fun openCameraInterface() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, R.string.take_picture)
        values.put(MediaStore.Images.Media.DESCRIPTION, R.string.take_picture_description)
        imageUri = activity?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        // Create camera intent
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Add image URI as an extra to the intent
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        // Launch intent with arbitrary image capture code
        startActivityForResult(intent, IMAGE_CAPTURE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Callback from camera intent
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_CAPTURE_CODE){
            // Add camera image to list of images of current tree
            imageUri?.let { viewModel.editTreeUnderConstruction.imagesUri.add(it) }

            // Add image to corresponding database tree
            imageUri?.let {
                viewModel.insertImageIntoTreeByName(viewModel.currentTreeName, it)
            }
        }
        else if (resultCode == Activity.RESULT_OK && requestCode == LOAD_GALLERY_IMAGE_CODE) {

            // Add multiple selected gallery images to list of images of current tree
            val selectedImages = data?.clipData?.let { clipData ->
                (0 until clipData.itemCount).map { clipData.getItemAt(it).uri }
            } ?: data?.data?.let { listOf(it) }
            selectedImages?.let {
                viewModel.editTreeUnderConstruction.imagesUri.addAll(it)

                // Insert all images to list
                for (image in it) {
                    viewModel.insertImageIntoTreeByName(viewModel.editTreeUnderConstruction.name, image)
                }
            }
        }
        else {
            // Failed to take picture
            showAlert("Failed to take or load picture.")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditTreeBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun dialogSaveChanges() {
        // Todo MVP3: Only ask the user if they made changes (applies also for NewTreeFragment.kt)
        // Ask the user if they really want to go back and discard changes
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Discard changes?")
        builder.setMessage("Do you want to discard changes to the tree?")
        builder.setPositiveButton("Yes") { _, _ ->

            // Navigate to MyGarden Fragment
            view?.findNavController()?.popBackStack()
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

