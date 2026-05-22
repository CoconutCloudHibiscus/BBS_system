@echo off
title BBS System - Start

set MAVEN_PATH=D:\App_develop\apache\apache-maven-3.9.11-bin\apache-maven-3.9.11\bin\mvn.cmd
set BACKEND_DIR=%~dp0backend
set FRONTEND_DIR=%~dp0frontend

if not exist "%MAVEN_PATH%" (
    echo [ERROR] Maven not found: %MAVEN_PATH%
    echo Please edit MAVEN_PATH in this script
    pause
    exit /b 1
)

echo [1/2] Starting backend (Spring Boot)...
start "BBS-Backend" cmd /k "cd /d %BACKEND_DIR% && %MAVEN_PATH% spring-boot:run"

echo       Waiting for backend to start...
timeout /t 8 /nobreak >nul

echo [2/2] Starting frontend (Vue 3 + Vite)...
start "BBS-Frontend" cmd /k "cd /d %FRONTEND_DIR% && npm run dev"

timeout /t 3 /nobreak >nul

echo.
echo ========================================
echo  Started!
echo  Backend:  http://localhost:8080
echo  Frontend: http://localhost:5173
echo  Admin:    admin / admin123
echo ========================================
echo.
echo Close this window will NOT stop services
echo Close the BBS-Backend/BBS-Frontend windows to stop
echo.
pause
