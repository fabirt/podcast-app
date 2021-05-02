package com.fabirt.podcastapp.domain.repository

import com.fabirt.podcastapp.data.network.service.PodcastService
import com.fabirt.podcastapp.domain.model.PodcastSearch
import com.fabirt.podcastapp.error.Failure
import com.fabirt.podcastapp.util.Either
import com.fabirt.podcastapp.util.left
import com.fabirt.podcastapp.util.right

class PodcastRepositoryImpl(
    private val service: PodcastService
) : PodcastRepository {

    override suspend fun searchPodcasts(
        query: String,
        type: String
    ): Either<Failure, PodcastSearch> {
        return try {
            val result = service.searchPodcasts(query, type)
            right(result.asDomainModel())
        } catch (e: Exception) {
            left(Failure.UnexpectedFailure)
        }
    }
}