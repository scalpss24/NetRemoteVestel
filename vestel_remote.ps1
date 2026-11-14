# Виртуальный пульт для Vestel MB130 (Smart Center API, порт 56789)
# Запуск: powershell -ExecutionPolicy Bypass -File vestel_remote.ps1 [-Ip 192.168.1.2]
param([string]$Ip = '192.168.1.2')
[Console]::OutputEncoding = [Text.Encoding]::UTF8

function Send-Key([int]$code) {
    $body = "<?xml version=`"1.0`" ?><remote><key code=`"$code`"/></remote>"
    try {
        $r = Invoke-WebRequest -Uri "http://${Ip}:56789/apps/SmartCenter" -Method Post `
             -ContentType 'text/xml' -Body $body -TimeoutSec 5 -UseBasicParsing
        Write-Host "  -> $code : HTTP $($r.StatusCode)" -ForegroundColor Green
    } catch { Write-Host "  -> $code : $($_.Exception.Message)" -ForegroundColor Red }
}

# Коды = 1000 + код RC5. Проверенные: 1016/1017. Остальные — типичные для Vestel, уточняйте.
$map = @{
    'UpArrow'='1020'; 'DownArrow'='1019'; 'LeftArrow'='1021'; 'RightArrow'='1022'
    'Enter'='1053'; 'Backspace'='1010'; 'Escape'='1037'
    'M'='1048'; 'H'='1046'; 'P'='1012'; 'X'='1013'; 'I'='1015'
    'Add'='1016'; 'OemPlus'='1016'; 'Subtract'='1017'; 'OemMinus'='1017'
    'PageUp'='1032'; 'PageDown'='1033'
}

Write-Host @"
Vestel remote -> $Ip
  Стрелки = навигация   Enter = OK      Backspace = Back   Esc = Exit
  M = Menu   H = Home/Smart   P = Power   X = Mute   I = Info
  +/- = громкость   PgUp/PgDn = канал   0-9 = цифры
  C = ввести произвольный код (например 1053)   Q = выход
"@
while ($true) {
    $k = [Console]::ReadKey($true)
    $name = $k.Key.ToString()
    if ($name -eq 'Q') { break }
    if ($name -eq 'C') { $c = Read-Host 'Код'; if ($c -match '^\d+$') { Send-Key $c }; continue }
    if ($k.KeyChar -match '[0-9]') { Send-Key (1000 + [int][string]$k.KeyChar); continue }
    if ($map.ContainsKey($name)) { Write-Host $name -NoNewline; Send-Key $map[$name] }
}
