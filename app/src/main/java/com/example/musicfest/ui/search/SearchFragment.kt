package com.example.musicfest.ui.search

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicfest.R
import com.example.musicfest.databinding.FragmentHomeBinding
import com.example.musicfest.databinding.FragmentSearchBinding
import com.example.musicfest.domain.model.FestivalModel
import com.example.musicfest.domain.model.MusicGenre
import com.example.musicfest.ui.home.FestivalListState
import com.example.musicfest.ui.home.adapter.HomeAdapter
import com.example.musicfest.ui.search.adapter.SearchAdapter
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    
    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var searchAdapter: SearchAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        searchAdapter = SearchAdapter(emptyList(), onItemSelected = { onItemSelected() })
        binding.rvResults.layoutManager = GridLayoutManager(context, 1)
        binding.rvResults.adapter = searchAdapter

        searchViewModel.loadInitialResults()

        binding.sv.isIconifiedByDefault = false
        binding.sv.clearFocus()
        binding.sv.queryHint = getString(R.string.search_hint)
        setupSearchView(binding.sv)
        
        loadGenres()
        
        initUiState()
    }
    
    private fun setupSearchView(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchInDatabase(it) }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if(!query.isNullOrBlank()) {
                    searchInDatabase(query)
                } else {
                    searchViewModel.loadInitialResults()
                }
                return true
            }
        })
    }
    
    private fun loadGenres() {
        MusicGenre.entries.forEach { genre->
            addGenreChip(genre)
        }
    }
    
    private fun addGenreChip(genre: MusicGenre) {
        val chip = Chip(context).apply {
            text = getString(genre.genre)
            isCheckable = true
            chipBackgroundColor = ColorStateList.valueOf(Color.TRANSPARENT)
            chipStrokeWidth = 2f
            chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(context, genre.color))
            setCheckedIconVisible(false)
            
            setOnCheckedChangeListener { _, isChecked -> 
                if (isChecked) {
                    chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, genre.color))
                    searchViewModel.onSelectedGenre(genre, isChecked)
                } else {
                    chipBackgroundColor = ColorStateList.valueOf(Color.TRANSPARENT)
                    searchViewModel.onSelectedGenre(genre, isChecked)
                }
            }
        }
        binding.chipGroup.addView(chip)
    }
    
    private fun searchInDatabase(query: String) {
        searchViewModel.search(query)
    }
    
    private fun initUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.state.collect { 
                    when(it) {
                        is FestivalListState.Error -> errorState()
                        is FestivalListState.Loading -> loadingState()
                        is FestivalListState.Success -> successState(it)
                        else -> {}
                    }
                }
            }
        }
    }

    private fun errorState() {
        binding.progressBar.visibility = View.GONE
        binding.rvResults.visibility = View.GONE
        binding.tvError.visibility = View.VISIBLE
    }

    private fun loadingState() {
        binding.tvError.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun successState(success: FestivalListState.Success) {
        binding.progressBar.visibility = View.GONE
        binding.tvError.visibility = View.GONE
        if (success.festivals.isNotEmpty()) {
            searchAdapter.updateList(success.festivals)
            binding.rvResults.visibility = View.VISIBLE
            binding.tvNoResults.visibility = View.GONE
        } else {
            binding.tvNoResults.visibility = View.VISIBLE
            binding.rvResults.visibility = View.GONE
        }
    }
    
    private fun onItemSelected() {
        
    }
}