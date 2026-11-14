package com.vestel.remote.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Input
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.vestel.remote.ui.VestelViewModel
import com.vestel.remote.ui.theme.AccentBlue
import com.vestel.remote.ui.theme.AccentCyan
import com.vestel.remote.ui.theme.PowerRed
import com.vestel.remote.ui.theme.RemoteBackground
import com.vestel.remote.ui.theme.RemoteButton
import com.vestel.remote.ui.theme.RemoteButtonBorder
import com.vestel.remote.ui.theme.RemoteCard
import com.vestel.remote.ui.theme.RemoteSurface
import com.vestel.remote.ui.theme.StatusChecking
import com.vestel.remote.ui.theme.StatusOffline
import com.vestel.remote.ui.theme.StatusOnline
import com.vestel.remote.ui.theme.TextMuted
import com.vestel.remote.ui.theme.TextPrimary
import com.vestel.remote.ui.theme.TextSecondary

@Composable
fun RemoteScreen(viewModel: VestelViewModel) {
    val currentIp by viewModel.tvIp.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val lastMessage by viewModel.lastActionMessage.collectAsState()
    val showSettings by viewModel.showSettingsDialog.collectAsState()
    val showNumberPad by viewModel.showNumberPad.collectAsState()
    val showCustomCode by viewModel.showCustomCodeDialog.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val discoveredDevices by viewModel.discoveredDevices.collectAsState()

    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = RemoteBackground,
        topBar = {
            TopAppBarRemote(
                currentIp = currentIp,
                isOnline = isOnline,
                showNumberPad = showNumberPad,
                onToggleNumberPad = { viewModel.toggleNumberPad() },
                onOpenCustomCode = { viewModel.openCustomCodeDialog(true) },
                onOpenSettings = { viewModel.openSettings(true) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Индикатор статуса последней команды
            StatusBarTicket(message = lastMessage)

            // Верхний ряд: Power, Mute, Source
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Питание (Power)
                IconButtonPill(
                    icon = Icons.Default.PowerSettingsNew,
                    label = "POWER",
                    containerColor = PowerRed.copy(alpha = 0.2f),
                    tintColor = PowerRed,
                    borderColor = PowerRed.copy(alpha = 0.6f)
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.sendKey(RemoteKey.POWER)
                }

                // Источник (Source)
                IconButtonPill(
                    icon = Icons.Outlined.Input,
                    label = "SOURCE",
                    containerColor = RemoteButton,
                    tintColor = TextPrimary,
                    borderColor = RemoteButtonBorder
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.sendKey(RemoteKey.SOURCE)
                }

                // Без звука (Mute)
                IconButtonPill(
                    icon = Icons.Default.VolumeOff,
                    label = "MUTE",
                    containerColor = RemoteButton,
                    tintColor = TextPrimary,
                    borderColor = RemoteButtonBorder
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.sendKey(RemoteKey.MUTE)
                }
            }

            // Цветные функциональные кнопки
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ColorPillButton(Color(0xFFEF4444)) { viewModel.sendKey(RemoteKey.RED) }
                ColorPillButton(Color(0xFF10B981)) { viewModel.sendKey(RemoteKey.GREEN) }
                ColorPillButton(Color(0xFFF59E0B)) { viewModel.sendKey(RemoteKey.YELLOW) }
                ColorPillButton(Color(0xFF3B82F6)) { viewModel.sendKey(RemoteKey.BLUE) }
            }

            // Ряд быстрых кнопок: Home, Menu, Info, Exit
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(RemoteCard)
                    .border(1.dp, RemoteButtonBorder, RoundedCornerShape(16.dp))
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuickNavButton(icon = Icons.Default.Home, label = "Home") {
                    viewModel.sendKey(RemoteKey.HOME)
                }
                QuickNavButton(icon = Icons.Default.Menu, label = "Menu") {
                    viewModel.sendKey(RemoteKey.MENU)
                }
                QuickNavButton(icon = Icons.Default.Info, label = "Info") {
                    viewModel.sendKey(RemoteKey.INFO)
                }
                QuickNavButton(icon = Icons.Outlined.Close, label = "Exit") {
                    viewModel.sendKey(RemoteKey.EXIT)
                }
            }

            // Цифровая клавиатура (выдвижная)
            AnimatedVisibility(
                visible = showNumberPad,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                NumberPad(onKeyClick = { viewModel.sendKey(it) })
            }

            // D-Pad с кнопкой Back рядом
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                DPadControl(onKeyClick = { viewModel.sendKey(it) })

                // Кнопка BACK слева от D-Pad
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 8.dp, bottom = 8.dp)
                ) {
                    IconButtonPill(
                        icon = Icons.Outlined.ArrowBack,
                        label = "BACK",
                        containerColor = RemoteButton,
                        tintColor = TextPrimary,
                        borderColor = RemoteButtonBorder
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.sendKey(RemoteKey.BACK)
                    }
                }
            }

            // Секция громкости и каналов (Качельки)
            RockersSection(onKeyClick = { viewModel.sendKey(it) })

            // Мультимедиа плеер (Play, Pause, Stop, Rew, Fwd)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(RemoteCard)
                    .border(1.dp, RemoteButtonBorder, RoundedCornerShape(16.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MediaIconButton(Icons.Default.FastRewind, "Rewind") { viewModel.sendKey(RemoteKey.REWIND) }
                MediaIconButton(Icons.Default.PlayArrow, "Play") { viewModel.sendKey(RemoteKey.PLAY) }
                MediaIconButton(Icons.Default.Pause, "Pause") { viewModel.sendKey(RemoteKey.PAUSE) }
                MediaIconButton(Icons.Default.Stop, "Stop") { viewModel.sendKey(RemoteKey.STOP) }
                MediaIconButton(Icons.Default.FastForward, "Forward") { viewModel.sendKey(RemoteKey.FAST_FORWARD) }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Диалог настроек IP и автопоиска
    if (showSettings) {
        SettingsDialog(
            currentIp = currentIp,
            isScanning = isScanning,
            discoveredDevices = discoveredDevices,
            onSaveIp = { viewModel.setTvIp(it) },
            onStartScan = { viewModel.startDiscovery() },
            onDismiss = { viewModel.openSettings(false) }
        )
    }

    // Диалог ввода произвольного кода
    if (showCustomCode) {
        CustomKeyDialog(
            onSendCode = {
                viewModel.sendRawCode(it)
                viewModel.openCustomCodeDialog(false)
            },
            onDismiss = { viewModel.openCustomCodeDialog(false) }
        )
    }
}

@Composable
private fun TopAppBarRemote(
    currentIp: String,
    isOnline: Boolean?,
    showNumberPad: Boolean,
    onToggleNumberPad: () -> Unit,
    onOpenCustomCode: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Surface(
        color = RemoteSurface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Индикатор подключения и IP
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onOpenSettings() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                val statusColor = when (isOnline) {
                    true -> StatusOnline
                    false -> StatusOffline
                    null -> StatusChecking
                }
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Vestel MB130",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = currentIp,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            // Иконки управления: Цифры, произвольный код, настройки
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onToggleNumberPad) {
                    Icon(
                        imageVector = Icons.Default.Dialpad,
                        contentDescription = "Цифровая панель",
                        tint = if (showNumberPad) AccentBlue else TextSecondary
                    )
                }
                IconButton(onClick = onOpenCustomCode) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = "Произвольный код",
                        tint = TextSecondary
                    )
                }
                IconButton(onClick = onOpenSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Настройки",
                        tint = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBarTicket(message: String?) {
    if (message.isNullOrEmpty()) return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(RemoteSurface)
            .border(1.dp, RemoteButtonBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = message,
            fontSize = 12.sp,
            color = TextSecondary,
            maxLines = 1
        )
    }
}

@Composable
private fun IconButtonPill(
    icon: ImageVector,
    label: String,
    containerColor: Color,
    tintColor: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(containerColor)
                .border(1.5.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tintColor,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )
    }
}

@Composable
private fun ColorPillButton(
    color: Color,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    Box(
        modifier = Modifier
            .width(54.dp)
            .height(14.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(color)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true, color = Color.White)
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
    )
}

@Composable
private fun QuickNavButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = TextPrimary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextSecondary
        )
    }
}

@Composable
private fun MediaIconButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true, color = AccentCyan)
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = TextPrimary,
            modifier = Modifier.size(22.dp)
        )
    }
}
