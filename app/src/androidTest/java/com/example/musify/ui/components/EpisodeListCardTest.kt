package com.example.musify.ui.components

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.musify.domain.SearchResult
import org.junit.Rule
import org.junit.Test

class EpisodeListCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    private val fakeEpisodeSearchResult = SearchResult.EpisodeSearchResult(
        id = "testId",
        episodeContentInfo = SearchResult.EpisodeSearchResult.EpisodeContentInfo(
            "title",
            "description",
            imageUrlString = ""
        ),
        episodeReleaseDateInfo = SearchResult.EpisodeSearchResult.EpisodeReleaseDateInfo(
            month = "May",
            day = 1,
            year = 2022
        ),
        episodeDurationInfo = SearchResult.EpisodeSearchResult.EpisodeDurationInfo(
            hours = 1,
            minutes = 10
        )
    )

    @ExperimentalMaterialApi
    @Test
    fun dateDurationFormattingTest_validSearchResult_isFormattedAndDisplayedCorrectly() {
        val episodeSearchResult = SearchResult.EpisodeSearchResult(
            id = "testId",
            episodeContentInfo = SearchResult.EpisodeSearchResult.EpisodeContentInfo(
                "title",
                "description",
                imageUrlString = ""
            ),
            episodeReleaseDateInfo = SearchResult.EpisodeSearchResult.EpisodeReleaseDateInfo(
                month = "Jan",
                day = 1,
                year = 2022
            ),
            episodeDurationInfo = SearchResult.EpisodeSearchResult.EpisodeDurationInfo(
                hours = 2,
                minutes = 1
            )
        )
        composeTestRule.setContent {
            EpisodeListCard(
                episodeSearchResult = episodeSearchResult,
                onClick = { /*TODO*/ }
            )
        }
        composeTestRule.onNodeWithText(text = "Jan 1, 2022 • 2 hrs 1 min").assertExists()
    }

    @ExperimentalMaterialApi
    @Test
    fun durationPluralsStringTest_episodeWith1Minute_displaysTextWithCorrectPlural() {
        val episodeSearchResult = fakeEpisodeSearchResult.copy(
            episodeDurationInfo = SearchResult.EpisodeSearchResult.EpisodeDurationInfo(
                hours = 0,
                minutes = 1
            )
        )
        composeTestRule.setContent {
            EpisodeListCard(
                episodeSearchResult = episodeSearchResult,
                onClick = { /*TODO*/ }
            )
        }
        composeTestRule.onNodeWithText(text = "1 min", substring = true).assertExists()
    }

    @ExperimentalMaterialApi
    @Test
    fun durationPluralsStringTest_episodeWithDurationMoreThan1Minute_displaysTextWithCorrectPlural() {
        val episodeSearchResult = fakeEpisodeSearchResult.copy(
            episodeDurationInfo = SearchResult.EpisodeSearchResult.EpisodeDurationInfo(
                hours = 0,
                minutes = 2
            )
        )
        composeTestRule.setContent {
            EpisodeListCard(
                episodeSearchResult = episodeSearchResult,
                onClick = { /*TODO*/ }
            )
        }
        composeTestRule.onNodeWithText(text = "2 mins", substring = true).assertExists()
    }

    @ExperimentalMaterialApi
    @Test
    fun durationPluralsStringTest_episodeWithDurationOf1hour_displaysTextWithCorrectPlural() {
        val episodeSearchResult = fakeEpisodeSearchResult.copy(
            episodeDurationInfo = SearchResult.EpisodeSearchResult.EpisodeDurationInfo(
                hours = 1,
                minutes = 0
            )
        )
        composeTestRule.setContent {
            EpisodeListCard(
                episodeSearchResult = episodeSearchResult,
                onClick = { /*TODO*/ }
            )
        }
        composeTestRule.onNodeWithText(text = "1 hr", substring = true).assertExists()
    }

    @ExperimentalMaterialApi
    @Test
    fun durationPluralsStringTest_episodeWithDurationOfMoreThan1Hour_displaysTextWithCorrectPlural() {
        val episodeSearchResult = fakeEpisodeSearchResult.copy(
            episodeDurationInfo = SearchResult.EpisodeSearchResult.EpisodeDurationInfo(
                hours = 2,
                minutes = 0
            )
        )
        composeTestRule.setContent {
            EpisodeListCard(
                episodeSearchResult = episodeSearchResult,
                onClick = { /*TODO*/ }
            )
        }
        composeTestRule.onNodeWithText(text = "2 hrs", substring = true).assertExists()
    }

    @ExperimentalMaterialApi
    @Test
    fun durationPluralsStringTest_episodeWithDurationOf0Hours_displaysTextWithCorrectPlural() {
        val episodeSearchResult = fakeEpisodeSearchResult.copy(
            episodeDurationInfo = SearchResult.EpisodeSearchResult.EpisodeDurationInfo(
                hours = 0,
                minutes = 2
            )
        )
        composeTestRule.setContent {
            EpisodeListCard(
                episodeSearchResult = episodeSearchResult,
                onClick = { /*TODO*/ }
            )
        }
        composeTestRule.onNodeWithText(text = "0 hrs 2 mins", substring = true).assertDoesNotExist()
    }
}