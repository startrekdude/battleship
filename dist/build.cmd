@echo off

:: ENV VARS
set JRE=C:\Program Files\Java\jre-10.0.2
set JDK=C:\Program Files\Java\jdk-10.0.2

echo Building Win64 installer for Battleship...

echo Copying required files to appdir...
copy ..\Battleship.jar appdir
copy ..\THIRD-PARTY.txt appdir
copy ..\Battleship-Guide.pdf appdir

echo Building JRE...
rmdir /q /s "appdir\jre-10.0.2"
"%JDK%\bin\jlink.exe" -G -p "%JDK%\jmods" --no-header-files --no-man-pages --compress 1 --add-modules java.desktop --output "appdir\jre-10.0.2"

echo Building launcher...
"%JRE%\bin\java.exe" -jar "launch4j\launch4j.jar" battleship-launch.xml

echo Building MSI...
echo Collecting deliverables...

wix\heat dir appdir -ag -srd -cg BattleshipProgram -dr INSTALLDIR -out BattleshipProgram.wxs -swall
wix\candle -arch x64 BattleshipProgram.wxs

echo Compiling installer script...
wix\candle -arch x64 Battleship.wxs

echo Linking installer...
wix\light -b appdir -ext WixUIExtension -ext WixUtilExtension Battleship.wixobj BattleshipProgram.wixobj -o Battleship.msi
