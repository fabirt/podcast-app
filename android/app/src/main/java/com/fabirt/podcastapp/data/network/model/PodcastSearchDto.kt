package com.fabirt.podcastapp.data.network.model

import com.fabirt.podcastapp.domain.model.PodcastSearch

data class PodcastSearchDto(
    val count: Long,
    val total: Long,
    val results: List<EpisodeDto>
) {

    fun asDomainModel() = PodcastSearch(
        count,
        total,
        results.map { it.asDomainModel() }
    )
}
