package com.example.bonsaicare.ui.mygarden.treedetail

import android.content.ContentResolver
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.bonsaicare.R
import com.example.bonsaicare.databinding.FragmentImageViewFullScreenBinding
import com.example.bonsaicare.ui.calendar.BonsaiViewModel
import com.example.bonsaicare.ui.calendar.BonsaiViewModelFactory
import com.example.bonsaicare.ui.calendar.BonsaiApplication


class ImageViewFullScreenFragment : Fragment() {

    private var _binding: FragmentImageViewFullScreenBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // Get reference to taskViewModel
    private val sharedBonsaiViewModel: BonsaiViewModel by activityViewModels {
        BonsaiViewModelFactory((requireNotNull(this.activity).application as BonsaiApplication).repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inflate toolbar
        binding.imageFullScreenToolbar.inflateMenu(R.menu.image_full_screen_menu)

        // Enable 'back' button of support action bar in this fragment
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set current tree
        var currentTreeTmp = if (sharedBonsaiViewModel.treeUnderConstructionFlag) {
            // Take tree from newTreeFragment
            sharedBonsaiViewModel.newTreeUnderConstruction
        } else {
            // Take tree from currentIndex (treeDetail/editTree)
            sharedBonsaiViewModel.getTreeAtIndex(sharedBonsaiViewModel.currentTreeIndex)
        }

        // Show previously clicked image in layouts ImageView
        binding.imageViewFullScreen.setImageURI(currentTreeTmp.imagesUri[sharedBonsaiViewModel.currentImageIndex])

        // Todo MVP3: Can be be handled in a cleaner way? Maybe in themes.xml? styles.xml?
        // Change color of 'thumbnail button' and 'delete button' in toolbar to gray, so it is visible in dark and light mode
        binding.imageFullScreenToolbar.menu.getItem(0).icon?.setTintList(
            ColorStateList.valueOf(resources.getColor(R.color.gray, null))
        )
        binding.imageFullScreenToolbar.menu.getItem(1).icon?.setTintList(
            ColorStateList.valueOf(resources.getColor(R.color.gray, null))
        )

        // Set on menu item click listener for toolbar items
        binding.imageFullScreenToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_set_as_thumbnail -> {
                    // Set ThumbnailImageIndex of current database tree to clicked image
                    currentTreeTmp.thumbnailImageIndex = sharedBonsaiViewModel.currentImageIndex

                    // Give user a toast about the action
                    Toast.makeText(context, "Image set as Thumbnail", Toast.LENGTH_SHORT).show()

                    // Use currentTreeTmp to update the database tree or tree under construction
                    if (sharedBonsaiViewModel.treeUnderConstructionFlag) {
                        // Take tree from newTreeFragment
                        sharedBonsaiViewModel.newTreeUnderConstruction = currentTreeTmp
                    } else {
                        // Delete old tree from database
                        sharedBonsaiViewModel.deleteDatabaseTreeByName(currentTreeTmp.name)

                        // Insert new tree into database
                        sharedBonsaiViewModel.insertTree(currentTreeTmp)
                    }

                    // Go back to last visited fragment
                    view.findNavController().popBackStack()
                }
                R.id.item_image_delete -> {
                    // Fire up a dialog box for 'yes', 'no'
                    val dialogBuilder = AlertDialog.Builder(requireContext())

                    // Set message of alert dialog
                    dialogBuilder.setMessage("Do you really want to delete this image?")
                        // If the dialog is cancelable
                        .setCancelable(false)
                        // Negative button text and action
                        .setNegativeButton("No!") { dialog, _ ->
                            dialog.cancel()
                        }
                        // Delete current tree and navigate back to my editTree
                        .setPositiveButton("Yes") { _, _ ->

                            // Delete image from database
                            //sharedBonsaiViewModel.deleteImageFromTreeByIndex(currentTreeTmp.name, sharedBonsaiViewModel.currentImageIndex)
                            currentTreeTmp.imagesUri.removeAt(sharedBonsaiViewModel.currentImageIndex)

                            // When image left to current thumbnail image is deleted, decrement thumbnail image index by 1
                            if (sharedBonsaiViewModel.currentImageIndex < currentTreeTmp.thumbnailImageIndex) {
                                //sharedBonsaiViewModel.setThumbnailImageIndex(currentTreeTmp.name, currentTreeTmp.thumbnailImageIndex - 1)
                                currentTreeTmp.thumbnailImageIndex -= 1
                            }

                            // When thumbnail image is deleted set thumbnail image to index 0
                            if (sharedBonsaiViewModel.currentImageIndex == currentTreeTmp.thumbnailImageIndex) {
                                //sharedBonsaiViewModel.setThumbnailImageIndex(currentTreeTmp.name, 0)
                                currentTreeTmp.thumbnailImageIndex = 0
                            }

                            // When last in list image is deleted, set thumbnail image index to 0 and insert new default image
                            if (currentTreeTmp.imagesUri.size == 0) {
                                // Set thumbnail image index to 0
                                //sharedBonsaiViewModel.setThumbnailImageIndex(currentTreeTmp.name, 0)
                                currentTreeTmp.thumbnailImageIndex = 0
                                // Insert new default image to imagesUri
                                currentTreeTmp.imagesUri.add(Uri.Builder()
                                    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                                    .authority(context!!.packageName)
                                    .path(R.drawable.trees_icon_colored.toString())
                                    .build())
                                Toast.makeText(context, "No images present, use default image.", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                // Toast to user
                                Toast.makeText(context, "Image deleted", Toast.LENGTH_SHORT).show()
                            }

                            // Use currentTreeTmp to update the database tree or tree under construction
                            if (sharedBonsaiViewModel.treeUnderConstructionFlag) {
                                // Take tree from newTreeFragment
                                sharedBonsaiViewModel.newTreeUnderConstruction = currentTreeTmp
                            } else {
                                // Delete old tree from database
                                sharedBonsaiViewModel.deleteDatabaseTreeByName(currentTreeTmp.name)

                                // Insert new tree into database
                                sharedBonsaiViewModel.insertTree(currentTreeTmp)
                            }

                            // Go back to last visited fragment
                            view.findNavController().popBackStack()
                        }

                    // Create dialog box
                    val alert = dialogBuilder.create()
                    // set title for alert dialog box
                    alert.setTitle("Attention!")
                    // show alert dialog
                    alert.show()

                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentImageViewFullScreenBinding.inflate(inflater, container, false)
        return binding.root
    }




}