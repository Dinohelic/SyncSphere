package com.syncsphere.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.foundation.shape.RoundedCornerShape
import com.syncsphere.app.ui.theme.Dimens

@Composable
fun StatusChip(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val bg = color.copy(alpha = 0.12f)
    Surface(
        modifier = modifier.clip(RoundedCornerShape(10.dp)),
        color = bg
    ) {
        Box(modifier = Modifier.padding(horizontal = Dimens.spacing_sm, vertical = Dimens.spacing_xs)) {
            Text(text = text, style = MaterialTheme.typography.labelSmall, color = color)
        }
    }
}
