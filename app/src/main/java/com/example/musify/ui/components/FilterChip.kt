package com.example.musify.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * A filter chip composable.
 * @param text the text that is displayed within chip.
 * @param onClick callback that will be executed when the user clicks
 * the chip.
 * @param modifier Modifier to be applied to the button
 * @param isSelected indicates whether the filter chip is selected or not.
 * Based on the selected state of the composable, different styles will be
 * applied.
 */
@Composable
fun FilterChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = if (isSelected) MaterialTheme.colors.primary.copy(alpha = 0.7f)
            else MaterialTheme.colors.surface,
        ),
        border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colors.primary)
        else ButtonDefaults.outlinedBorder,
        content = { Text(text = text, color = Color.White) }
    )
}