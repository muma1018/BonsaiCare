package com.example.bonsaicare.ui.mygarden.treedetail.newtree

import ImageGalleryAdapter
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
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
import com.example.bonsaicare.R
import com.example.bonsaicare.databinding.FragmentNewTreeBinding
import com.example.bonsaicare.ui.calendar.BonsaiApplication
import com.example.bonsaicare.ui.calendar.BonsaiViewModel
import com.example.bonsaicare.ui.calendar.BonsaiViewModelFactory
import com.example.bonsaicare.ui.convertDateStringToIsoFormat
import com.example.bonsaicare.ui.database.Tree
import com.example.bonsaicare.ui.database.TreeSpecies
import com.example.bonsaicare.ui.isValidDateString
import java.time.LocalDate

class NewTreeFragment : Fragment() {

    override fun onResume() {
        super.onResume()

        // Update the recycler view whenever we resume to this fragment
        // e.g. when coming back from gallery or camera taking picture
        binding.recyclerViewNewTreeImageGallery.adapter?.notifyDataSetChanged()
    }

    private var _binding: FragmentNewTreeBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // Camera Stuff
    // https://medium.com/@chris_42047/taking-camera-pictures-with-android-in-kotlin-6b2920a58ebe
    // Let’s link up the xml widget definitions with variables in the ViewController. Open up the FirstFragment.kt, create variables for the image and button at the top of the ViewController class definition and then add the assignments in the onViewCreated lifecycle method.

    // Use the contentResolver to set up our image Uri with the value options. This is the Uri that will be used to assign the location of the resulting image.
    private var imageUri: Uri? = null

    // Use arbitrary Integer for image capture code
    private val CAMERA_PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    private val LOAD_GALLERY_IMAGE_CODE = 1002

    // Get reference to taskViewModel
    private val viewModel: BonsaiViewModel by activityViewModels {
        BonsaiViewModelFactory((requireNotNull(this.activity).application as BonsaiApplication).repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // Disable 'back' button of support action bar in this fragment
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // This callback will only be called when MyFragment is at least started
        // Enable the 'back button'
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Ask user if they really want to leave and discard changes
                    dialogSaveChanges()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        // If new tree is currently not being constructed, create a tree under construction
        if (!viewModel.treeUnderConstructionFlag) {
            // Create default database tree
            viewModel.newTreeUnderConstruction = Tree()
            // Set a default image
            viewModel.newTreeUnderConstruction.imagesUri = mutableListOf(
                Uri.Builder()
                    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                    .authority(context!!.packageName)
                    .path(R.drawable.trees_icon_colored.toString())
                    .build())

            // Set newTreeUnderConstruction flag to true
            viewModel.treeUnderConstructionFlag = true
        }

        // Image Gallery
        val adapterImageGallery = ImageGalleryAdapter(viewModel.newTreeUnderConstruction.imagesUri)

        // Set adapter for image gallery
        binding.recyclerViewNewTreeImageGallery.adapter = adapterImageGallery

        // If an image in the gallery is clicked, make full screen
        adapterImageGallery.apply { onImageClick = {
            // Set current image index to clicked image
            viewModel.currentImageIndex = it

            // Navigate to full screen image
            view.findNavController().navigate(R.id.action_navigation_new_tree_to_image_view_full_screen)
            }
        }

        // Set onClickListener for Add new Tree Species button
        // -> Navigate to New Tree Species Fragment
        binding.btnNewTreeSpecies.setOnClickListener {
            view : View ->
            view.findNavController().navigate(R.id.action_navigation_new_tree_to_new_tree_species)
        }

        // Set up Date Picker for Date of Birth
        binding.imageButtonDateOfBirthPicker.setOnClickListener {
            // Show a date picker dialog when the button is clicked
            val datePickerDialog = context?.let { it1 -> DatePickerDialog(it1) }
            datePickerDialog?.setOnDateSetListener { _, year, month, day ->
                // This block is called when the user selects a date
                val date = "$year-${month + 1}-$day"
                binding.editTextDateOfBirth.setText(date)
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
                binding.editTextDateOfAcquisition.setText(date)
            }
            datePickerDialog?.show()
        }

        // Get global date format
        val dateFormat = viewModel.dateFormat

        // Set onClickListener for apply button and navigate to MyGardenFragment
        binding.buttonAdd.setOnClickListener {
                view : View ->

            // Insert a new databaseTree
            val treeSpeciesTmp = TreeSpecies(
                name = binding.spinnerTreeSpecies.selectedItem.toString(),
                nameLatin = binding.spinnerTreeSpeciesLatin.selectedItem.toString(),
                description = binding.insertShortDescription.text.toString())

            viewModel.newTreeUnderConstruction.name = binding.insertMyTreeName.text.toString()
            viewModel.newTreeUnderConstruction.treeSpecies = treeSpeciesTmp
            viewModel.newTreeUnderConstruction.dateOfBirth = LocalDate.parse(convertDateStringToIsoFormat(binding.editTextDateOfBirth.text.toString()))
            viewModel.newTreeUnderConstruction.dateOfAcquisition = LocalDate.parse(convertDateStringToIsoFormat(binding.editTextDateOfAcquisition.text.toString()))
            viewModel.newTreeUnderConstruction.style = binding.insertMyTreeStyle.text.toString()

            // Convert dates from edit text to LocalDate and check if they are valid
            var dateOfBirthString = convertDateStringToIsoFormat(binding.editTextDateOfBirth.text.toString())
            var dateOfAcquisitionString = convertDateStringToIsoFormat(binding.editTextDateOfAcquisition.text.toString())

            // Add tree if all necessary fields are filled
            if (viewModel.newTreeUnderConstruction.name != "") {
                // Only accept tree if its name is not an empty String "" and if date of birth/acq. set correctly
                if (isValidDateString(dateOfBirthString) && isValidDateString(dateOfAcquisitionString)) {
                    // Only allow to add tree if tree with that name is not yet present in database
                    if (viewModel.getDatabaseTreeByName(viewModel.newTreeUnderConstruction.name) == null) {
                        // Delete the default tree from database
                        //sharedBonsaiViewModel.deleteDatabaseTreeByName(sharedBonsaiViewModel.currentTreeName)

                        // Insert new tree into database
                        viewModel.insertTree(viewModel.newTreeUnderConstruction)

                        // Add tree species to database if it is not yet present
                        if (viewModel.getTreeSpeciesByName(treeSpeciesTmp.name) == null) {
                            viewModel.insertTreeSpecies(treeSpeciesTmp)
                        }

                        // Add tree species to active filter
                        var treeSpeciesTmp = viewModel.getSettingsActiveFilteredTreeSpecies()
                        // If tree species is not yet present in active filter, add it
                        if (!treeSpeciesTmp.contains(viewModel.newTreeUnderConstruction.treeSpecies.name)) {
                            treeSpeciesTmp.add(viewModel.newTreeUnderConstruction.treeSpecies.name)
                            viewModel.setSettingsActiveFilteredTreeSpecies(treeSpeciesTmp.joinToString(separator = ","))
                        }

                        // Informative toast
                        Toast.makeText(requireContext(), "Tree added to garden", Toast.LENGTH_LONG).show()

                        // Set tree under construction flag to false
                        viewModel.treeUnderConstructionFlag = false

                        // Reset tree under construction in viewModel
                        context?.let { viewModel.resetNewTreeUnderConstruction(it) }

                        // Navigate to My Garden Fragment
                        view.findNavController().navigate(R.id.action_navigation_new_tree_to_my_garden)
                    } else {
                        // Show error message
                        Toast.makeText(context, "Tree with that name already exists", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    // Show error message
                    Toast.makeText(requireContext(), "Date of Birth and/or Date of Acquis. not valid.", Toast.LENGTH_LONG).show()
                }
            }
            else {
                // Show error message
                Toast.makeText(requireContext(), "Please fill at least Tree Name field", Toast.LENGTH_LONG).show()
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
        val spinner: Spinner = view.findViewById(R.id.spinner_tree_species)
        val spinnerLatin: Spinner = view.findViewById(R.id.spinner_tree_species_latin)

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

        // We need two extra flags to avoid bad looping with the spinners
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

        // Handle the selected item of spinner
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
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
        }
        else{
            // Android version earlier than M > no need to request permission
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Callback from camera intent
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_CAPTURE_CODE){
            // Add camera image to list of images of current tree
            imageUri?.let { viewModel.newTreeUnderConstruction.imagesUri.add(it) }
        }
        else if (resultCode == Activity.RESULT_OK && requestCode == LOAD_GALLERY_IMAGE_CODE) {
            // Add multiple selected gallery images to list of images of current tree
            val selectedImages = data?.clipData?.let { clipData ->
                (0 until clipData.itemCount).map { clipData.getItemAt(it).uri }
            } ?: data?.data?.let { listOf(it) }
            selectedImages?.let { viewModel.newTreeUnderConstruction.imagesUri.addAll(it) }
        } else {
            // Failed to take picture
            showAlert("Failed to take or load picture.")
        }
        // Notify adapter that dataset changed (updates images recycler view)
        binding.recyclerViewNewTreeImageGallery.adapter?.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNewTreeBinding.inflate(inflater, container, false)
        return binding.root
    }


    fun dialogSaveChanges() {
        // Ask the user if they really want to go back and discard changes
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Discard changes?")
        builder.setMessage("Do you want to discard changes to the new tree?")
        builder.setPositiveButton("Yes") { dialog, which ->
            // Delete the temporary tree (located at the end of myTrees list)
            viewModel.deleteDatabaseTreeByName(viewModel.currentTreeName)

            // Set newTreeUnderConstruction flag to false
            viewModel.treeUnderConstructionFlag = false

            // Reset selected spinner tree species
            viewModel.rememberSelectedSpinnerTreeSpecies = ""

            // Navigate to MyGarden Fragment
            view?.findNavController()?.navigate(R.id.action_navigation_new_tree_to_my_garden)
        }
        builder.setNegativeButton("No") { dialog, which ->
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

