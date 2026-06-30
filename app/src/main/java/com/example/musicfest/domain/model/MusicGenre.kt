package com.example.musicfest.domain.model

import com.example.musicfest.R

enum class MusicGenre (val genre: Int, val color: Int) {
    INDIE(R.string.indie, R.color.indie),
    POP(R.string.pop, R.color.pop),
    ROCK(R.string.rock, R.color.rock),
    HEAVY_METAL(R.string.heavy_metal, R.color.heavy_metal),
    TECHNO(R.string.techno, R.color.techno),
    EDM(R.string.edm, R.color.edm),
    REGGAETON(R.string.reggaeton, R.color.reggaeton),
    TRAP(R.string.trap, R.color.trap),
    HIP_HOP(R.string.hip_hop, R.color.hip_hop),
    JAZZ(R.string.jazz, R.color.jazz),
    BLUES(R.string.blues, R.color.blues),
    FLAMENCO(R.string.flamenco, R.color.flamenco),
    FOLK(R.string.folk, R.color.folk),
    REGGAE(R.string.reggae, R.color.reggae),
    PUNK(R.string.punk, R.color.punk),
    FUNK(R.string.funk, R.color.funk),
    HARDSTYLE(R.string.hardstyle, R.color.hardstyle),
}