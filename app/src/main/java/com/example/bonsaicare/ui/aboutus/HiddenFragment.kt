package com.example.bonsaicare.ui.aboutus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.bonsaicare.R
import com.example.bonsaicare.databinding.FragmentHiddenBinding
import com.example.bonsaicare.ui.calendar.BonsaiApplication
import com.example.bonsaicare.ui.calendar.BonsaiViewModel
import com.example.bonsaicare.ui.calendar.BonsaiViewModelFactory


class HiddenFragment : Fragment() {
    // This fragment contains information about the app and the developers as well as settings

    // Init viewModel
    private val sharedBonsaiViewModel: BonsaiViewModel by activityViewModels {
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

        // Set click listener for back button
        binding.btnBack.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_hidden_to_aboutus)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}