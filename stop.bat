@echo off
title BBS System - Stop

echo Stopping backend...
taskkill /fi "WINDOWTITLE eq BBS-Backend*" /f >nul 2>&1

echo Stopping frontend...
taskkill /fi "WINDOWTITLE eq BBS-Frontend*" /f >nul 2>&1

echo.
echo All services stopped.
pause
