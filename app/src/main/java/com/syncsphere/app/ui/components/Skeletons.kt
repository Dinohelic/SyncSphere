package com.syncsphere.app.ui.components

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.syncsphere.app.ui.theme.Dimens

@Composable
fun SkeletonTaskCard(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition()
    val alpha = transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(800, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.spacing_sm),
        shape = RoundedCornerShape(Dimens.card_corner),
        color = Color.Transparent
    ) {
        Column(modifier = Modifier
            .padding(Dimens.spacing)
        ) {
            Spacer(modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(18.dp)
                .background(Color.LightGray, RoundedCornerShape(6.dp))
                .alpha(alpha.value)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .background(Color.LightGray, RoundedCornerShape(6.dp))
                .alpha(alpha.value)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Spacer(modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(12.dp)
                .background(Color.LightGray, RoundedCornerShape(6.dp))
                .alpha(alpha.value)
            )
        }
    }
}
