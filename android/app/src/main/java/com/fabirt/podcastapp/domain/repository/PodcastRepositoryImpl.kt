package com.fabirt.podcastapp.domain.repository

import com.fabirt.podcastapp.data.datastore.PodcastDataStore
import com.fabirt.podcastapp.data.network.service.PodcastService
import com.fabirt.podcastapp.domain.model.PodcastSearch
import com.fabirt.podcastapp.error.Failure
import com.fabirt.podcastapp.util.Either
import com.fabirt.podcastapp.util.left
import com.fabirt.podcastapp.util.right

class PodcastRepositoryImpl(
    private val service: PodcastService,
    private val dataStore: PodcastDataStore
) : PodcastRepository {

    companion object {
        private const val TAG = "PodcastRepository"
    }

    override suspend fun searchPodcasts(
        query: String,
        type: String
    ): Either<Failure, PodcastSearch> {
        return try {
            val canFetchAPI = dataStore.canFetchAPI()
            if (canFetchAPI) {
                val result = service.searchPodcasts(query, type).asDomainModel()
                dataStore.storePodcastSearchResult(result)
                right(result)
            } else {
                right(dataStore.readLastPodcastSearchResult())
            }
        } catch (e: Exception) {
            left(Failure.UnexpectedFailure)
        }
    }
}