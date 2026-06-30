package com.example.musicfest.ui.home

import com.example.musicfest.domain.model.FestivalModel

sealed class FestivalListState {
    object Loading: FestivalListState()
    object Error: FestivalListState()
    class Success (val festivals: List<FestivalModel>): FestivalListState()
    data class SuccessDetail(val festival: FestivalModel) : FestivalListState()
}