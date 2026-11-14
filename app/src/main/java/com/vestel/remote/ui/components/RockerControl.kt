package com.vestel.remote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vestel.remote.data.RemoteKey
import com.vestel.remote.ui.theme.AccentCyan
import com.vestel.remote.ui.theme.RemoteButton
import com.vestel.remote.ui.theme.RemoteButtonBorder
import com.vestel.remote.ui.theme.TextMuted
import com.vestel.remote.ui.theme.TextPrimary

@Composable
fun RockersSection(
    modifier: Modifier = Modifier,
    onKeyClick: (RemoteKey) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Громкость: Vol+ / Vol-
        RockerPill(
            title = "VOL",
            topIcon = Icons.Default.Add,
            bottomIcon = Icons.Default.Remove,
            onTopClick = { onKeyClick(RemoteKey.VOLUME_UP) },
            onBottomClick = { onKeyClick(RemoteKey.VOLUME_DOWN) }
        )

        // Переключение каналов: CH+ / CH-
        RockerPill(
            title = "CH",
            topIcon = Icons.Default.KeyboardArrowUp,
            bottomIcon = Icons.Default.KeyboardArrowDown,
            onTopClick = { onKeyClick(RemoteKey.CHANNEL_UP) },
            onBottomClick = { onKeyClick(RemoteKey.CHANNEL_DOWN) }
        )
    }
}

@Composable
fun RockerPill(
    title: String,
    topIcon: ImageVector,
    bottomIcon: ImageVector,
    onTopClick: () -> Unit,
    onBottomClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .width(74.dp)
            .height(150.dp)
            .shadow(6.dp, RoundedCornerShape(37.dp))
            .clip(RoundedCornerShape(37.dp))
            .background(RemoteButton)
            .border(1.5.dp, RemoteButtonBorder, RoundedCornerShape(37.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Верхняя половина
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true, color = AccentCyan)
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onTopClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = topIcon,
                contentDescription = "$title +",
                tint = TextPrimary,
                modifier = Modifier.size(28.dp)
            )
        }

        // Центральная подпись
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 1.sp
        )

        // Нижняя половина
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true, color = AccentCyan)
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onBottomClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = bottomIcon,
                contentDescription = "$title -",
                tint = TextPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
