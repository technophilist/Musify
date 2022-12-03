package com.example.musify.data.remote.response

import com.example.musify.data.utils.MapperImageSize
import org.junit.Test

class EpisodeMetadataMappingTest {


    @Test
    fun episodeMetadata_to_EpisodeSearchResult_mapping_test() {
        val mappedObject = fakeEpisodeMetadataResponse.toEpisodeSearchResult(MapperImageSize.SMALL)

        with(mappedObject.episodeReleaseDateInfo) {
            assert(day == 2)
            assert(month == "Dec")
            assert(year == 2022)
            // Dec 2 2022
        }

        with(mappedObject.episodeDurationInfo) {
            assert(hours == 3)
            assert(minutes == 24)
            // 3 hours 25 minutes
        }
    }
}