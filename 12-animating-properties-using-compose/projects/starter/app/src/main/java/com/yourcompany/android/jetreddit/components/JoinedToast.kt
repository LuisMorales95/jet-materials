package com.yourcompany.android.jetreddit.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yourcompany.android.jetreddit.R

@Composable
fun JoinedToast(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { +40 }) + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        ToastContent()
    }
}

@Composable
fun ToastContent() {
    val shape = RoundedCornerShape(4.dp)
    Box(
        modifier = Modifier
            .clip(shape)
            .background(Color.White)
            .border(1.dp, Color.Black, shape)
            .height(40.dp)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_planet),
                contentDescription = "Subreddit Icon"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "You have joined this community!")
        }
    }
}

@Preview
@Composable
fun JoinedToastPreview() {
    JoinedToast(visible = true)
}