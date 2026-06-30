package com.example.musicfest.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicfest.data.SessionManager
import com.example.musicfest.databinding.FragmentFavoritesBinding
import com.example.musicfest.ui.home.adapter.HomeAdapter
import com.example.musicfest.domain.model.FestivalModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModels()

    private lateinit var homeAdapter: HomeAdapter
    private var favoriteList: MutableList<FestivalModel> = mutableListOf()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        initUI()

        sessionManager.currentUid?.let { uid ->
            viewModel.loadFavorites(uid)
        }
    }

    private fun setupRecyclerView() {
        homeAdapter = HomeAdapter(favoriteList) { festival ->
            val action = FavoritesFragmentDirections.actionFavoritesFragmentToDetailActivity(
                name = festival.name
            )
            findNavController().navigate(action)
        }

        binding.rvFavorites.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = homeAdapter
        }
    }

    private fun initUI() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is FavoritesState.Loading -> {
                            binding.progressBar.isVisible = true
                        }
                        is FavoritesState.Success -> {
                            binding.progressBar.isVisible = false

                            // Actualizamos la lista del adapter
                            favoriteList.clear()
                            favoriteList.addAll(state.favorites)
                            homeAdapter.notifyDataSetChanged()

                            }
                        is FavoritesState.Error -> {
                            binding.progressBar.isVisible = false
                            // Aquí podrías poner un log o un aviso de error
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