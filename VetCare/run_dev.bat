@echo off
TITLE VetCare Dev Runner
CLS

echo ==========================================
echo      VETCARE DEV RUNNER
echo ==========================================
echo.
echo [1] A iniciar processo de compilacao...
echo.

:: Check availability of PowerShell
WHERE powershell >nul 2>nul
IF %ERRORLEVEL% NEQ 0 (
    echo [ERRO CRITICO] PowerShell nao encontrado no sistema.
    echo Instale o PowerShell ou verifique o PATH.
    pause
    exit /b 1
)

:: Run script with explicit bypass and catch errors
powershell.exe -NoProfile -ExecutionPolicy Bypass -Command "& 'c:\Users\belar\.gemini\tools\build_deploy.ps1'; if (!$?) { exit 1 }"

IF %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERRO] O script PowerShell falhou com codigo %ERRORLEVEL%.
    echo Verifique as mensagens de erro acima.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo ------------------------------------------
echo APLICACAO A CORRER EM:
echo http://localhost:8080/
echo ------------------------------------------
echo.
echo Pode fechar esta janela para parar o processo (caso o Tomcat nao esteja em background).
echo.
pause
