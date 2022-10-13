package com.example.musify.viewmodels.homefeedviewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musify.data.repositories.homefeedrepository.HomeFeedRepository
import com.example.musify.data.repositories.homefeedrepository.ISO6391LanguageCode
import com.example.musify.data.utils.FetchedResource
import com.example.musify.data.utils.MapperImageSize
import com.example.musify.di.MusifyApplication
import com.example.musify.domain.*
import com.example.musify.viewmodels.getCountryCode
import com.example.musify.viewmodels.homefeedviewmodel.greetingphrasegenerator.GreetingPhraseGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO: UI states
@HiltViewModel
class HomeFeedViewModel @Inject constructor(
    application: Application,
    greetingPhraseGenerator: GreetingPhraseGenerator,
    private val homeFeedRepository: HomeFeedRepository,
) : AndroidViewModel(application) {

    private val _homeFeedCarousels = mutableStateOf<List<HomeFeedCarousel>>(emptyList())
    val homeFeedCarousels = _homeFeedCarousels as State<List<HomeFeedCarousel>>
    val greetingPhrase = greetingPhraseGenerator.generatePhrase()

    init {
        fetchAndAssignHomeFeedCarousels()
    }

    private fun fetchAndAssignHomeFeedCarousels() {
        viewModelScope.launch {
            val carousels = mutableListOf<HomeFeedCarousel>()
            val languageCode = getApplication<MusifyApplication>()
                .resources
                .configuration
                .locale
                .language
                .let(::ISO6391LanguageCode) // TODO test

            val newAlbums = async {
                homeFeedRepository.fetchNewlyReleasedAlbums(getCountryCode(), MapperImageSize.LARGE)
            }
            val featuredPlaylists = async {
                homeFeedRepository.fetchFeaturedPlaylistsForCurrentTimeStamp(
                    timestampMillis = System.currentTimeMillis(),
                    countryCode = getCountryCode(),
                    languageCode = languageCode
                )
            }
            val categoricalPlaylists = async {
                homeFeedRepository.fetchPlaylistsBasedOnCategoriesAvailableForCountry(
                    countryCode = getCountryCode(),
                    languageCode = languageCode
                )
            }
            featuredPlaylists.awaitFetchedResource {
                it.playlists
                    .map<SearchResult, HomeFeedCarouselCardInfo>(::toHomeFeedCarouselCardInfo)
                    .let { homeFeedCarouselCardInfoList ->
                        carousels.add(
                            HomeFeedCarousel(
                                id = "Featured Playlists",
                                title = "Featured Playlists",
                                associatedCards = homeFeedCarouselCardInfoList
                            )
                        )
                    }
            }
            newAlbums.awaitFetchedResource {
                it.map<SearchResult, HomeFeedCarouselCardInfo>(::toHomeFeedCarouselCardInfo)
                    .let { homeFeedCarouselCardInfoList ->
                        carousels.add(
                            HomeFeedCarousel(
                                id = "Newly Released Albums",
                                title = "Newly Released Albums",
                                associatedCards = homeFeedCarouselCardInfoList
                            )
                        )
                    }
            }
            categoricalPlaylists.awaitFetchedResource {
                it.map { playlistsForCategory -> playlistsForCategory.toHomeFeedCarousel() }
                    .forEach(carousels::add)
            }
            _homeFeedCarousels.value = carousels
        }
    }

    /**
     * A method that will await the result of the deferred object and execute the
     * [block] if, and only if, the call to [Deferred.await] returned an instance
     * of [FetchedResource.Success]. The [block] has a parameter that will provide
     * the [FetchedResourceType], which represents the type of data that will be
     * encapsulated within the [FetchedResource.Success] class. In other words,
     * the [block]'s parameter will contain [FetchedResource.Success.data].
     */
    private suspend fun <FetchedResourceType> Deferred<FetchedResource<FetchedResourceType, MusifyErrorType>>.awaitFetchedResource(
        block: (FetchedResourceType) -> Unit
    ) {
        val fetchedResourceResult = this.await()
        if (fetchedResourceResult !is FetchedResource.Success) return
        block(fetchedResourceResult.data)
    }

    private fun toHomeFeedCarouselCardInfo(searchResult: SearchResult): HomeFeedCarouselCardInfo =
        when (searchResult) {
            is SearchResult.AlbumSearchResult -> {
                HomeFeedCarouselCardInfo(
                    id = searchResult.id,
                    imageUrlString = searchResult.albumArtUrlString,
                    caption = searchResult.name,
                    associatedSearchResult = searchResult
                )
            }
            is SearchResult.PlaylistSearchResult -> {
                HomeFeedCarouselCardInfo(
                    id = searchResult.id,
                    imageUrlString = searchResult.imageUrlString ?: "",
                    caption = searchResult.name,
                    associatedSearchResult = searchResult
                )
            }
            else -> throw java.lang.IllegalArgumentException("The method supports only the mapping of AlbumSearchResult and PlaylistSearchResult subclasses")
        }
}