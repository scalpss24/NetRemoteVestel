package com.vestel.remote.data

/**
 * Коды клавиш для протокола Vestel SmartCenter (MB130 и совместимые шасси).
 * Отправляются в теле XML: <?xml version="1.0" ?><remote><key code="XXXX"/></remote>
 */
enum class RemoteKey(
    val code: Int,
    val title: String,
    val description: String = ""
) {
    // Питание и звук
    POWER(1012, "Power", "Включение / Режим ожидания"),
    MUTE(1013, "Mute", "Отключение звука"),
    VOLUME_UP(1016, "Vol +", "Увеличение громкости"),
    VOLUME_DOWN(1017, "Vol -", "Уменьшение громкости"),

    // Навигация (D-Pad)
    UP(1020, "▲", "Вверх"),
    DOWN(1019, "▼", "Вниз"),
    LEFT(1021, "◀", "Влево"),
    RIGHT(1022, "▶", "Вправо"),
    OK(1053, "OK", "Подтверждение / Выбор"),

    // Системные и меню
    BACK(1010, "Back", "Назад"),
    EXIT(1037, "Exit", "Выход"),
    MENU(1048, "Menu", "Главное меню"),
    HOME(1046, "Home", "Smart Hub / Домой"),
    INFO(1015, "Info", "Информация о канале/передаче"),
    SOURCE(1056, "Source", "Выбор источника входа (HDMI/TV/AV)"),

    // Переключение каналов
    CHANNEL_UP(1032, "P +", "Следующий канал"),
    CHANNEL_DOWN(1033, "P -", "Предыдущий канал"),

    // Цифровая клавиатура (0–9 = 1000 + цифра)
    NUM_0(1000, "0"),
    NUM_1(1001, "1"),
    NUM_2(1002, "2"),
    NUM_3(1003, "3"),
    NUM_4(1004, "4"),
    NUM_5(1005, "5"),
    NUM_6(1006, "6"),
    NUM_7(1007, "7"),
    NUM_8(1008, "8"),
    NUM_9(1009, "9"),

    // Мультимедиа
    PLAY(1025, "Play", "Воспроизведение"),
    PAUSE(1049, "Pause", "Пауза"),
    STOP(1024, "Stop", "Стоп"),
    REWIND(1027, "Rew", "Перемотка назад"),
    FAST_FORWARD(1028, "Fwd", "Перемотка вперед"),

    // Цветные кнопки телетекста/функций
    RED(1055, "Красная"),
    GREEN(1054, "Зеленая"),
    YELLOW(1050, "Желтая"),
    BLUE(1051, "Синяя");

    companion object {
        fun fromDigit(digit: Int): RemoteKey? = when (digit) {
            0 -> NUM_0
            1 -> NUM_1
            2 -> NUM_2
            3 -> NUM_3
            4 -> NUM_4
            5 -> NUM_5
            6 -> NUM_6
            7 -> NUM_7
            8 -> NUM_8
            9 -> NUM_9
            else -> null
        }
    }
}
