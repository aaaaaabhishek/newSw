
@echo off
echo Starting migration...

powershell -Command "Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $content = Get-Content $_.FullName -Raw; $content = $content -replace 'jakarta.xml.namespace.QName','javax.xml.namespace.QName';$content = $content -replace 'jakarta.xml.namespace','javax.xml.namespace';$content = $content -replace 'javax\.xml\.bind','jakarta.xml.bind'; $content = $content -replace 'package com\.ab','package com.MT_MX.demo.iso20022.pacs'; Set-Content $_.FullName $content }"

echo Migration completed successfully.
pause