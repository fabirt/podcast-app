package com.fabirt.podcastapp.domain.repository

import com.fabirt.podcastapp.domain.model.Episode
import com.fabirt.podcastapp.domain.model.Podcast
import com.fabirt.podcastapp.domain.model.PodcastSearch
import com.fabirt.podcastapp.error.Failure
import com.fabirt.podcastapp.util.Either
import com.fabirt.podcastapp.util.left
import com.fabirt.podcastapp.util.right
import kotlinx.coroutines.delay

class PodcastRepositoryMockImpl : PodcastRepository {
    override suspend fun searchPodcasts(
        query: String,
        type: String
    ): Either<Failure, PodcastSearch> {
        delay(5000)
        //return left(Failure.UnexpectedFailure)
        return right(demoData())
    }

    private fun demoData(): PodcastSearch {
        val podcast = Podcast(
            id = "8758da9be6c8452884a8cab6373b007c",
            image = "https://cdn-images-1.listennotes.com/podcasts/the-rough-cut-PmR84dsqcbj-53MLh7NpAwm.1400x1400.jpg",
            thumbnail = "https://cdn-images-1.listennotes.com/podcasts/the-rough-cut-AzKVtPeMOL4-53MLh7NpAwm.300x300.jpg",
            titleOriginal = "The Rough Cut",
            listennotesURL = "https://www.listennotes.com/c/8758da9be6c8452884a8cab6373b007c/",
            publisherOriginal = "Matt Feury"
        )

        return PodcastSearch(
            count = 10,
            total = 9000,
            results = (1..20).map {
                Episode(
                    id = "ea09b575d07341599d8d5b71f205517b$it",
                    link = "http://theroughcutpod.com/?p=786&utm_source=listennotes.com&utm_campaign=Listen+Notes&utm_medium=website",
                    audio = "https://www.listennotes.com/e/p/ea09b575d07341599d8d5b71f205517b/",
                    image = "https://cdn-images-1.listennotes.com/podcasts/the-rough-cut-PmR84dsqcbj-53MLh7NpAwm.1400x1400.jpg",
                    podcast = podcast,
                    thumbnail = "https://cdn-images-1.listennotes.com/podcasts/the-rough-cut-AzKVtPeMOL4-53MLh7NpAwm.300x300.jpg",
                    pubDateMS = 1579507216047,
                    titleOriginal = "Star Wars - The Force Awakens",
                    listennotesURL = "https://www.listennotes.com/e/ea09b575d07341599d8d5b71f205517b/",
                    audioLengthSec = 1694,
                    explicitContent = false,
                    descriptionOriginal = "In this episode of The Rough Cut we close out our study of the final Skywalker trilogy with a look back on the film that helped the dormant franchise make the jump to lightspeed, Episode VII - The Force Awakens.  Recorded in Amsterdam in front of a festival audience in 2018, editor Maryann Brandon ACE recounts her work on The Force Awakens just as she was about to begin editing what would come to be known as Episode IX - The Rise of Skywalker.   Go back to the beginning and listen to our podcast with Star Wars and 'Empire' editor, Paul Hirsch. Hear editor Bob Ducsay talk about cutting The Last Jedi. Listen to Maryann Brandon talk about her work on The Rise of Skywalker. Get your hands on the non-linear editor behind the latest Skywalker trilogy,  Avid Media Composer! Subscribe to The Rough Cut for more great interviews with the heroes of the editing room!    "
                )
            }
        )
    }
}