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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
fun NumberPad(
    modifier: Modifier = Modifier,
    onKeyClick: (RemoteKey) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(RemoteCard)
            .border(1.dp, RemoteButtonBorder, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ряд 1: 1, 2, 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NumberButton(digit = 1) { onKeyClick(RemoteKey.NUM_1) }
            NumberButton(digit = 2) { onKeyClick(RemoteKey.NUM_2) }
            NumberButton(digit = 3) { onKeyClick(RemoteKey.NUM_3) }
        }

        // Ряд 2: 4, 5, 6
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NumberButton(digit = 4) { onKeyClick(RemoteKey.NUM_4) }
            NumberButton(digit = 5) { onKeyClick(RemoteKey.NUM_5) }
            NumberButton(digit = 6) { onKeyClick(RemoteKey.NUM_6) }
        }

        // Ряд 3: 7, 8, 9
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NumberButton(digit = 7) { onKeyClick(RemoteKey.NUM_7) }
            NumberButton(digit = 8) { onKeyClick(RemoteKey.NUM_8) }
            NumberButton(digit = 9) { onKeyClick(RemoteKey.NUM_9) }
        }

        // Ряд 4: Back, 0, Exit
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionButton(label = "BACK") {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onKeyClick(RemoteKey.BACK)
            }
            NumberButton(digit = 0) { onKeyClick(RemoteKey.NUM_0) }
            ActionButton(label = "EXIT") {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onKeyClick(RemoteKey.EXIT)
            }
        }
    }
}

@Composable
private fun NumberButton(
    digit: Int,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(RemoteButton)
            .border(1.dp, RemoteButtonBorder, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true, color = AccentBlue)
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit.toString(),
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ActionButton(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(RemoteButton.copy(alpha = 0.5f))
            .border(1.dp, RemoteButtonBorder, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true, color = Color.White)
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
