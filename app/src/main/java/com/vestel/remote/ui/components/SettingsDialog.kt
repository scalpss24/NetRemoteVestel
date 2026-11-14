package com.vestel.remote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.vestel.remote.data.TvInfo
import com.vestel.remote.ui.theme.AccentBlue
import com.vestel.remote.ui.theme.AccentGreen
import com.vestel.remote.ui.theme.RemoteButton
import com.vestel.remote.ui.theme.RemoteButtonBorder
import com.vestel.remote.ui.theme.RemoteCard
import com.vestel.remote.ui.theme.RemoteSurface
import com.vestel.remote.ui.theme.TextMuted
import com.vestel.remote.ui.theme.TextPrimary
import com.vestel.remote.ui.theme.TextSecondary

@Composable
fun SettingsDialog(
    currentIp: String,
    isScanning: Boolean,
    discoveredDevices: List<TvInfo>,
    onSaveIp: (String) -> Unit,
    onStartScan: () -> Unit,
    onDismiss: () -> Unit
) {
    var ipInput by remember { mutableStateOf(currentIp) }
    val focusManager = LocalFocusManager.current

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(RemoteSurface)
                .border(1.dp, RemoteButtonBorder, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Подключение к ТВ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "ТВ Vestel MB130 принимает команды по HTTP на порт 56789. Укажите локальный IP адрес:",
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                // Поле ввода IP
                OutlinedTextField(
                    value = ipInput,
                    onValueChange = { ipInput = it },
                    label = { Text("IP адрес ТВ") },
                    placeholder = { Text("192.168.1.2") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = RemoteButtonBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = AccentBlue
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Кнопка автопоиска
                Button(
                    onClick = onStartScan,
                    enabled = !isScanning,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RemoteCard,
                        contentColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(
                            color = AccentBlue,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Сканирование Wi-Fi сети...")
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Автопоиск телевизора")
                    }
                }

                // Список найденных устройств
                if (discoveredDevices.isNotEmpty()) {
                    Text(
                        text = "Найденные устройства:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(discoveredDevices) { device ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(RemoteButton)
                                    .clickable {
                                        ipInput = device.ip
                                        onSaveIp(device.ip)
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Tv,
                                    contentDescription = null,
                                    tint = AccentGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = device.friendlyName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${device.ip} • ${device.tvVersion}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                                if (ipInput == device.ip) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Выбран",
                                        tint = AccentBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Кнопки Сохранить / Закрыть
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Отмена", color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSaveIp(ipInput)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                    ) {
                        Text("Сохранить")
                    }
                }
            }
        }
    }
}
