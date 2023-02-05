package com.example.bonsaicare.ui.mygarden.treedetail.newtree


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.bonsaicare.databinding.FragmentNewTreeSpeciesBinding
import com.example.bonsaicare.ui.calendar.BonsaiViewModel
import com.example.bonsaicare.ui.calendar.BonsaiViewModelFactory
import com.example.bonsaicare.ui.calendar.BonsaiApplication
import com.example.bonsaicare.ui.database.TreeSpecies

class NewTreeSpeciesFragment : Fragment() {

    private var _binding: FragmentNewTreeSpeciesBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // Get reference to taskViewModel
    private val sharedBonsaiViewModel: BonsaiViewModel by activityViewModels {
        BonsaiViewModelFactory((requireNotNull(this.activity).application as BonsaiApplication).repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Enable 'back' button of support action bar in this fragment
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Enable the 'back button'
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {

                    // Navigate back to MyGarden Fragment
                    view.findNavController().popBackStack()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        // Set onClickListener for apply button and navigate to MyGardenFragment
        binding.buttonAdd.setOnClickListener {
                view : View ->

            // Check if strings are empty
            if (binding.insertTreeSpeciesName.text.toString().isEmpty() ||
                binding.insertTreeSpeciesNameLatin.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please fill Name and Name (latin).", Toast.LENGTH_LONG).show()

            }
            else {
                // Set parameters of temporary tree from current EditTexts
                val nameTmp = convertStringToTitleCase(binding.insertTreeSpeciesName.text.toString().trim())
                val nameLatinTmp = binding.insertTreeSpeciesNameLatin.text.toString().trim().lowercase()
                val descriptionTmp = binding.insertTreeSpeciesDescription.text.toString().trim()

                // Check if Tree Species already exists
                if (sharedBonsaiViewModel.doesTreeSpeciesExist(nameTmp)) {
                    context?.let { showToast("Tree Species '$nameTmp' already exists.", it) }
                }
                else if (sharedBonsaiViewModel.doesTreeSpeciesLatinExist(nameLatinTmp)) {
                    context?.let { showToast("Tree Species '$nameLatinTmp' already exists.", it) }
                }

                // Only accept names if they are valid
                else if (isValidString(nameTmp) && isValidString(nameLatinTmp)) {
                    Toast.makeText(requireContext(), "New Tree Species added.", Toast.LENGTH_LONG).show()

                    // Update database with treeSpecies (this will be ignored if tree species already exists
                    sharedBonsaiViewModel.insertTreeSpecies(TreeSpecies(name = nameTmp, nameLatin = nameLatinTmp, description = descriptionTmp))

                    // Just navigate back to last visited fragment (we can visit this fragment from different locations)
                    view.findNavController().popBackStack()
                }
                else {
                    Toast.makeText(requireContext(), "Please fill at least Tree Name field", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showToast(message: String, context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    // Check if a string is valid Tree Species Name format
    private fun isValidString(str: String): Boolean {
        // Use a regular expression to check if the string contains only letters, spaces, and dashes
        val regex = Regex("[A-Za-z\\s-]+")
        return regex.matches(str)
    }

    // Convert a string to title case "hi world-how are u" -> "Hi World-How Are U"
    private fun convertStringToTitleCase(str: String): String {
        // Create a new string builder to store the converted string
        val sb = StringBuilder()
        // Start at the first character of the string
        var i = 0
        // Set the first character to uppercase
        sb.append(str[i].uppercaseChar())
        i++
        // Iterate over the rest of the string
        while (i < str.length) {
            // If the previous character is a space or a dash, set the current character to uppercase
            if (str[i - 1] == ' ' || str[i - 1] == '-') {
                sb.append(str[i].uppercaseChar())
            } else {
                // Otherwise, set it to lowercase
                sb.append(str[i].lowercaseChar())
            }
            i++
        }
        return sb.toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNewTreeSpeciesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

