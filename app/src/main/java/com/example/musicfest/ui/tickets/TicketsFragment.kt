package com.example.musicfest.ui.tickets

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
import com.example.musicfest.data.SessionManager
import com.example.musicfest.databinding.FragmentTicketsBinding
import com.example.musicfest.ui.tickets.adapter.TicketsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TicketsFragment : Fragment() {

    private var _binding: FragmentTicketsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sessionManager: SessionManager


    private val viewModel: TicketsViewModel by viewModels()


    private lateinit var ticketsAdapter: TicketsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTicketsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        initUI()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        ticketsAdapter = TicketsAdapter()
        binding.rvTickets.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ticketsAdapter
        }
    }

    private fun initUI() {
        val currentUid = sessionManager.currentUid

        if (currentUid != null) {
            // le decimos al ViewModel que baje las entradas de este usuario
            viewModel.getUserTickets(currentUid)
        } else {
            binding.progressBar.visibility = View.GONE
            binding.tvEmptyState.text = "Inicia sesión para ver tus entradas"
            binding.tvEmptyState.visibility = View.VISIBLE
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is TicketsState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.rvTickets.visibility = View.GONE
                            binding.tvEmptyState.visibility = View.GONE
                        }
                        is TicketsState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvTickets.visibility = View.VISIBLE
                            binding.tvEmptyState.visibility = View.GONE

                            // paasamos la lista de entadas al adaptador
                            ticketsAdapter.updateTickets(state.tickets)
                        }
                        is TicketsState.Empty -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvTickets.visibility = View.GONE
                            binding.tvEmptyState.visibility = View.VISIBLE
                        }
                        is TicketsState.Error -> {
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