package com.example.musify.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.musify.R
import com.example.musify.domain.HomeFeedCarousel
import com.example.musify.domain.HomeFeedCarouselCardInfo
import com.example.musify.domain.HomeFeedFilters
import com.example.musify.ui.components.*

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    timeBasedGreeting: String,
    homeFeedFilters: List<HomeFeedFilters>,
    currentlySelectedHomeFeedFilter: HomeFeedFilters,
    onHomeFeedFilterClick: (HomeFeedFilters) -> Unit,
    carousels: List<HomeFeedCarousel>,
    onHomeFeedCarouselCardClick: (HomeFeedCarouselCardInfo) -> Unit,
    isLoading:Boolean,
    isErrorMessageVisible: Boolean,
) {
    val lazyColumState = rememberLazyListState()
    val isStatusbarSpacerVisible = remember {
        derivedStateOf { lazyColumState.firstVisibleItemIndex > 1 }
    }
    val lazyColumBottomPaddingValues = remember {
        MusifyBottomNavigationConstants.navigationHeight + MusifyMiniPlayerConstants.miniPlayerHeight
    }
    val errorMessageItem = @Composable { modifier: Modifier ->
        Box(modifier = modifier) {
            DefaultMusifyErrorMessage(
                title = "Oops! Something doesn't look right",
                subtitle = "Please check the internet connection",
                modifier = Modifier.align(Alignment.Center)
            )
        }

    }
    Box {
        LazyColumn(
            state = lazyColumState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = lazyColumBottomPaddingValues)
        ) {
            item {
                HeaderRow(
                    timeBasedGreeting = timeBasedGreeting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 32.dp)
                )
            }
            stickyHeader {
                if (isStatusbarSpacerVisible.value) {
                    Spacer(
                        modifier = Modifier
                            .background(MaterialTheme.colors.background)
                            .fillMaxWidth()
                            .windowInsetsTopHeight(WindowInsets.statusBars)
                    )
                }
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colors.background)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (homeFeedFilter in homeFeedFilters) {
                        FilterChip(
                            text = homeFeedFilter.title ?: continue,
                            onClick = { onHomeFeedFilterClick(homeFeedFilter) },
                            isSelected = homeFeedFilter == currentlySelectedHomeFeedFilter
                        )
                    }
                }
            }
            if (isErrorMessageVisible) {
                item {
                    errorMessageItem(
                        Modifier
                            .fillParentMaxSize()
                            .padding(bottom = lazyColumBottomPaddingValues)
                    )
                }
            } else {
                // not using keys because the items do not change
                items(carousels) { carousel ->
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = carousel.title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.h5
                    )
                    CarouselLazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        carousel = carousel,
                        onHomeFeedCardClick = { onHomeFeedCarouselCardClick(it) }
                    )
                }
            }
        }
        DefaultMusifyLoadingAnimation(
            isVisible = isLoading,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


@ExperimentalMaterialApi
@Composable
private fun CarouselLazyRow(
    carousel: HomeFeedCarousel,
    onHomeFeedCardClick: (HomeFeedCarouselCardInfo) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp)
) {
    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // not using keys because the items do not change
        items(carousel.associatedCards) { card ->
            HomeFeedCard(
                imageUrlString = card.imageUrlString,
                caption = card.caption,
                onClick = { onHomeFeedCardClick(card) }
            )
        }
    }
}

@Composable
private fun HeaderRow(modifier: Modifier = Modifier, timeBasedGreeting: String) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = timeBasedGreeting,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.h5
        )

        Row {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_listening_history),
                    contentDescription = null
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = null
                )
            }
        }
    }
}
