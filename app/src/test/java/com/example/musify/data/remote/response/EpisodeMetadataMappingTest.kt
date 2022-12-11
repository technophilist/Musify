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

    @Test
    fun episodeMetadataMappingTest_responseWithLessThan1Minute_returnsObjectWith1Minute() {
        // given an episode with a duration of 30 seconds (less than 1 minute)
        val response = fakeEpisodeMetadataResponse.copy(durationMillis = 30_000)
        // when mapping it to an instance of EpisodeSearchResult
        val mappedObject = response.toEpisodeSearchResult(MapperImageSize.SMALL)
        // the seconds must be set to 1
        assert(mappedObject.episodeDurationInfo.minutes == 1)
    }
}