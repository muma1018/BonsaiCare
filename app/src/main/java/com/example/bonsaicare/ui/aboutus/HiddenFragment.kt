package com.example.bonsaicare.ui.aboutus

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bonsaicare.databinding.FragmentHiddenBinding
import com.example.bonsaicare.ui.calendar.BonsaiApplication
import com.example.bonsaicare.ui.calendar.BonsaiViewModel
import com.example.bonsaicare.ui.calendar.BonsaiViewModelFactory
import java.security.MessageDigest
import kotlin.math.absoluteValue


class HiddenFragment : Fragment() {
    // This fragment contains information about the app and the developers as well as settings

    // Init viewModel
    private val viewModel: BonsaiViewModel by activityViewModels {
        BonsaiViewModelFactory((requireNotNull(this.activity).application as BonsaiApplication).repository)
    }

    private var _binding: FragmentHiddenBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHiddenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Activation code
        // Get random number from user settings
        val requestCode = viewModel.getRequestCode()

        // Set request code in text view
        binding.textViewRequestCode.text = requestCode.toString()

        // Copy request code to user's clipboard
        val clipboard = context?.let { getSystemService(it, ClipboardManager::class.java) } as ClipboardManager

        // Set onClickListener to copy request code to clipboard
        binding.btnCopyRequestCodeToClipboard.setOnClickListener {
            clipboard.setPrimaryClip(ClipData.newPlainText("label", binding.textViewRequestCode.text))
            Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        // Set on click listener and activate in correct code was chosen
        binding.btnActivate.setOnClickListener {

            // Get activation code
            val activationCode = binding.editTextActivationCode.text.toString().toIntOrNull()

            if (activationCode != null) {
                // If activation code is correct
                if (binding.editTextActivationCode.text.toString().toIntOrNull() == hashActivationCode(requestCode)) {
                    Toast.makeText(context, "Additional Features are now activated!", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(context, "Activation failed, wrong Activation Code.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Insert activation Code First!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Just a dummy function to test the request code functionality
    private fun hashActivationCode(requestCode: Int): Int {
        return requestCode + 1
    }
}