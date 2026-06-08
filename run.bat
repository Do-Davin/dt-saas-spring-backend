@echo off
title DT SaaS Spring Boot Backend

echo ========================================
echo   DT SaaS Spring Boot Backend
echo ========================================
echo.

cd /d %~dp0

echo Starting Spring Boot application...
echo.

call mvnw.cmd spring-boot:run

pause