package com.example.musicfest.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicfest.R
import com.example.musicfest.databinding.FragmentHomeBinding
import com.example.musicfest.domain.model.FestivalModel
import com.example.musicfest.ui.home.adapter.HomeAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var homeAdapter: HomeAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }
    
    private fun onItemSelected(festival: FestivalModel) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToDetailActivity(festival.name)
        )
    }
    
    private fun initUi() {
        initUiState()
    }
    
    private fun initUiState() {
        lifecycleScope.launch { 
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.state.collect { 
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
        binding.tvError.visibility = View.VISIBLE
    }
    
    private fun loadingState() {
        binding.tvError.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }
    
    private fun successState(success: FestivalListState.Success) {
        binding.progressBar.visibility = View.GONE
        binding.tvError.visibility = View.GONE
        homeAdapter = HomeAdapter(success.festivals, onItemSelected = {festival-> onItemSelected(festival) })
        binding.rvFestivals.layoutManager = GridLayoutManager(context, 1)
        binding.rvFestivals.adapter = homeAdapter
    }
}