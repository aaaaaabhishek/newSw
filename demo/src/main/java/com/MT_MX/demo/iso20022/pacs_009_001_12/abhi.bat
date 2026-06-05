@echo off
echo Starting migration...

powershell -Command "Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $content = Get-Content $_.FullName -Raw; $content = $content -replace 'javax\.xml\.bind\.annotation\.XmlAccessType','jakarta.xml.bind.annotation.XmlAccessType'; $content = $content -replace 'javax\.xml\.bind\.annotation\.XmlAccessorType','jakarta.xml.bind.annotation.XmlAccessorType'; $content = $content -replace 'javax\.xml\.bind\.annotation\.XmlElement','jakarta.xml.bind.annotation.XmlElement'; $content = $content -replace 'javax\.xml\.bind\.annotation\.XmlType','jakarta.xml.bind.annotation.XmlType'; Set-Content $_.FullName $content }"

echo Migration completed successfully.
pause