package com.example.musicfest.ui.pictures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicfest.databinding.FragmentPicturesBinding
import com.example.musicfest.ui.home.adapter.ArtistsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PicturesFragment : Fragment() {

    private var _binding: FragmentPicturesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PicturesViewModel by viewModels()
    private lateinit var artistsAdapter: ArtistsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPicturesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        artistsAdapter = ArtistsAdapter()
        binding.rvArtists.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = artistsAdapter
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is ArtistsState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.rvArtists.visibility = View.GONE
                        }
                        is ArtistsState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvArtists.visibility = View.VISIBLE
                            artistsAdapter.updateArtists(state.artists)
                        }
                        is ArtistsState.Empty -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvArtists.visibility = View.GONE
                        }
                        is ArtistsState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}