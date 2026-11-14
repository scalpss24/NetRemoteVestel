package com.vestel.remote.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vestel.remote.data.CommandResult
import com.vestel.remote.data.DeviceDiscovery
import com.vestel.remote.data.RemoteKey
import com.vestel.remote.data.TvInfo
import com.vestel.remote.data.VestelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VestelViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VestelRepository()
    private val discovery = DeviceDiscovery(application, repository)
    private val prefs = application.getSharedPreferences("vestel_remote_prefs", Context.MODE_PRIVATE)

    private val _tvIp = MutableStateFlow(prefs.getString("saved_ip", "192.168.1.2") ?: "192.168.1.2")
    val tvIp: StateFlow<String> = _tvIp.asStateFlow()

    private val _tvInfo = MutableStateFlow<TvInfo?>(null)
    val tvInfo: StateFlow<TvInfo?> = _tvInfo.asStateFlow()

    private val _isOnline = MutableStateFlow<Boolean?>(null)
    val isOnline: StateFlow<Boolean?> = _isOnline.asStateFlow()

    private val _lastActionMessage = MutableStateFlow<String?>("Готов к работе")
    val lastActionMessage: StateFlow<String?> = _lastActionMessage.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _discoveredDevices = MutableStateFlow<List<TvInfo>>(emptyList())
    val discoveredDevices: StateFlow<List<TvInfo>> = _discoveredDevices.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _showNumberPad = MutableStateFlow(false)
    val showNumberPad: StateFlow<Boolean> = _showNumberPad.asStateFlow()

    private val _showCustomCodeDialog = MutableStateFlow(false)
    val showCustomCodeDialog: StateFlow<Boolean> = _showCustomCodeDialog.asStateFlow()

    init {
        checkTvStatus()
    }

    fun setTvIp(ip: String) {
        val trimmed = ip.trim()
        if (trimmed.isNotEmpty()) {
            _tvIp.value = trimmed
            prefs.edit().putString("saved_ip", trimmed).apply()
            checkTvStatus()
        }
    }

    fun checkTvStatus() {
        viewModelScope.launch {
            _isOnline.value = null
            val info = repository.fetchDeviceInfo(_tvIp.value)
            if (info != null) {
                _tvInfo.value = info
                _isOnline.value = true
                _lastActionMessage.value = "${info.friendlyName} доступен (${info.tvVersion})"
            } else {
                // Проверим отправкой тестового пинга или оставим статус
                _isOnline.value = false
                _lastActionMessage.value = "ТВ ${_tvIp.value} не ответил на dd.xml"
            }
        }
    }

    fun sendKey(key: RemoteKey) {
        sendCode(key.code, key.title)
    }

    fun sendRawCode(code: Int) {
        sendCode(code, "Код $code")
    }

    private fun sendCode(code: Int, label: String) {
        viewModelScope.launch {
            when (val result = repository.sendKey(_tvIp.value, code)) {
                is CommandResult.Success -> {
                    _isOnline.value = true
                    _lastActionMessage.value = "✓ $label (код $code) -> HTTP ${result.httpStatus}"
                }
                is CommandResult.Error -> {
                    _isOnline.value = false
                    _lastActionMessage.value = "✕ Ошибка: ${result.message}"
                }
            }
        }
    }

    fun startDiscovery() {
        viewModelScope.launch {
            _isScanning.value = true
            _lastActionMessage.value = "Поиск ТВ в сети..."
            val devices = discovery.discoverDevices()
            _discoveredDevices.value = devices
            _isScanning.value = false

            if (devices.isNotEmpty()) {
                val first = devices.first()
                _lastActionMessage.value = "Найден ТВ: ${first.friendlyName} (${first.ip})"
            } else {
                _lastActionMessage.value = "ТВ Vestel не обнаружен автоматически. Введите IP вручную."
            }
        }
    }

    fun openSettings(show: Boolean) {
        _showSettingsDialog.value = show
    }

    fun toggleNumberPad() {
        _showNumberPad.value = !_showNumberPad.value
    }

    fun openCustomCodeDialog(show: Boolean) {
        _showCustomCodeDialog.value = show
    }
}
