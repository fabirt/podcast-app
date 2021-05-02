package com.fabirt.podcastapp.domain.model

data class Podcast (
    val id: String,
    val image: String,
    val thumbnail: String,
    val titleOriginal: String,
    val listennotesURL: String,
    val publisherOriginal: String
)
