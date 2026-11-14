package com.vestel.remote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vestel.remote.data.RemoteKey
import com.vestel.remote.ui.theme.AccentBlue
import com.vestel.remote.ui.theme.RemoteButton
import com.vestel.remote.ui.theme.RemoteButtonBorder
import com.vestel.remote.ui.theme.RemoteCard
import com.vestel.remote.ui.theme.TextPrimary

@Composable
fun DPadControl(
    modifier: Modifier = Modifier,
    onKeyClick: (RemoteKey) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .size(240.dp)
            .shadow(12.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(RemoteCard, RemoteButton)
                )
            )
            .border(2.dp, RemoteButtonBorder, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Кнопка ВВЕРХ
        DPadArrowButton(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 12.dp),
            icon = Icons.Default.KeyboardArrowUp,
            contentDescription = "Вверх",
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onKeyClick(RemoteKey.UP)
            }
        )

        // Кнопка ВНИЗ
        DPadArrowButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-12).dp),
            icon = Icons.Default.KeyboardArrowDown,
            contentDescription = "Вниз",
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onKeyClick(RemoteKey.DOWN)
            }
        )

        // Кнопка ВЛЕВО
        DPadArrowButton(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 12.dp),
            icon = Icons.Default.KeyboardArrowLeft,
            contentDescription = "Влево",
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onKeyClick(RemoteKey.LEFT)
            }
        )

        // Кнопка ВПРАВО
        DPadArrowButton(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-12).dp),
            icon = Icons.Default.KeyboardArrowRight,
            contentDescription = "Вправо",
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onKeyClick(RemoteKey.RIGHT)
            }
        )

        // Центральная кнопка OK
        Box(
            modifier = Modifier
                .size(82.dp)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(AccentBlue, Color(0xFF2563EB))
                    )
                )
                .border(2.dp, Color(0x8093C5FD), CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true, color = Color.White)
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onKeyClick(RemoteKey.OK)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "OK",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun DPadArrowButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true, color = AccentBlue)
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = TextPrimary,
            modifier = Modifier.size(36.dp)
        )
    }
}
