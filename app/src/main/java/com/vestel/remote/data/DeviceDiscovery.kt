package com.vestel.remote.data

import android.content.Context
import android.net.wifi.WifiManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class DeviceDiscovery(
    private val context: Context,
    private val repository: VestelRepository
) {

    /**
     * Поиск телевизоров Vestel в локальной сети:
     * 1. Быстрая проверка дефолтного IP (192.168.1.2)
     * 2. SSDP M-SEARCH мультивещание (DIAL и UPnP)
     * 3. Сканирование текущей подсети по портам 56789/56790
     */
    suspend fun discoverDevices(): List<TvInfo> = withContext(Dispatchers.IO) {
        val found = mutableMapOf<String, TvInfo>()

        // 1. Проверяем default IP 192.168.1.2
        val defaultInfo = repository.fetchDeviceInfo("192.168.1.2")
        if (defaultInfo != null) {
            found["192.168.1.2"] = defaultInfo
        } else if (isPortOpen("192.168.1.2", 56789, 400)) {
            found["192.168.1.2"] = TvInfo("Vestel TV (192.168.1.2)", "MB130_VESTEL", "192.168.1.2")
        }

        // 2. SSDP / DIAL поиск
        val ssdpIps = searchSsdp()
        for (ip in ssdpIps) {
            if (!found.containsKey(ip)) {
                val info = repository.fetchDeviceInfo(ip)
                if (info != null && (info.tvVersion.contains("VESTEL", true) || info.friendlyName.contains("VESTEL", true))) {
                    found[ip] = info
                }
            }
        }

        // 3. Быстрый опрос подсети, если ничего не найдено или для полноты
        if (found.isEmpty()) {
            val subnetPrefix = getSubnetPrefix()
            if (subnetPrefix != null) {
                // Проверяем наиболее частые адреса параллельно
                val candidates = (1..30).map { "$subnetPrefix.$it" }
                val jobs = candidates.map { ip ->
                    async {
                        if (isPortOpen(ip, 56789, 300) || isPortOpen(ip, 56790, 300)) {
                            val info = repository.fetchDeviceInfo(ip)
                                ?: TvInfo("Vestel TV ($ip)", "MB130_VESTEL", ip)
                            ip to info
                        } else {
                            null
                        }
                    }
                }
                jobs.awaitAll().filterNotNull().forEach { (ip, info) ->
                    found[ip] = info
                }
            }
        }

        found.values.toList()
    }

    private fun searchSsdp(): List<String> {
        val discoveredIps = mutableSetOf<String>()
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        val multicastLock = wifiManager?.createMulticastLock("vestel_ssdp_lock")
        multicastLock?.setReferenceCounted(true)
        multicastLock?.acquire()

        try {
            val ssdpQuery = "M-SEARCH * HTTP/1.1\r\n" +
                    "HOST: 239.255.255.250:1900\r\n" +
                    "MAN: \"ssdp:discover\"\r\n" +
                    "MX: 2\r\n" +
                    "ST: urn:dial-multiscreen-org:service:dial:1\r\n\r\n"

            DatagramSocket().use { socket ->
                socket.soTimeout = 2000
                val data = ssdpQuery.toByteArray()
                val group = InetAddress.getByName("239.255.255.250")
                val packet = DatagramPacket(data, data.size, group, 1900)
                socket.send(packet)

                val buffer = ByteArray(2048)
                val responsePacket = DatagramPacket(buffer, buffer.size)

                val endTime = System.currentTimeMillis() + 2000
                while (System.currentTimeMillis() < endTime) {
                    try {
                        socket.receive(responsePacket)
                        val address = responsePacket.address.hostAddress
                        if (address != null) {
                            discoveredIps.add(address)
                        }
                    } catch (e: Exception) {
                        break
                    }
                }
            }
        } catch (e: Exception) {
            // Игнорируем сетевые ошибки SSDP
        } finally {
            try {
                if (multicastLock?.isHeld == true) {
                    multicastLock.release()
                }
            } catch (e: Exception) {
                // игнорируем
            }
        }
        return discoveredIps.toList()
    }

    private fun getSubnetPrefix(): String? {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager ?: return null
        val ipAddress = wifiManager.connectionInfo.ipAddress
        if (ipAddress == 0) return "192.168.1"
        return String.format(
            "%d.%d.%d",
            ipAddress and 0xff,
            ipAddress shr 8 and 0xff,
            ipAddress shr 16 and 0xff
        )
    }

    private fun isPortOpen(ip: String, port: Int, timeoutMs: Int): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(ip, port), timeoutMs)
                true
            }
        } catch (e: Exception) {
            false
        }
    }
}
