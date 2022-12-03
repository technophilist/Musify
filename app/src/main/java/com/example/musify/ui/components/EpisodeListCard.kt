package com.example.musify.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

data class EpisodeDateInfo(
    val month:String,
    val day:Int,
    val year:Int
)

data class EpisodeDurationInfo(
    val hours:Int,
    val minutes:Int
)

@ExperimentalMaterialApi
@Composable
fun EpisodeListCard(
    imageUrlString: String,
    title: String,
    description: String,
    episodeDurationInfo: EpisodeDurationInfo,
    episodeDateInfo: EpisodeDateInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .then(modifier),
        elevation = 0.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(98.dp)
                    .clip(RoundedCornerShape(8.dp)),
                model = imageUrlString,
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.subtitle2,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.subtitle2.copy(
                        Color.White.copy(alpha = ContentAlpha.medium)
                    ),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "${episodeDateInfo.month} ${episodeDateInfo.day}, ${episodeDateInfo.year} • ${episodeDurationInfo.hours} hrs ${episodeDurationInfo.minutes} mins",
                    style = MaterialTheme.typography.subtitle2.copy(
                        Color.White.copy(alpha = ContentAlpha.medium)
                    ),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}