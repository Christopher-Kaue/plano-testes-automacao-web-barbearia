@echo off
REM Executa os 14 casos de teste (Selenium + TestNG). O sistema de teste precisa estar rodando (1-iniciar-sistema-teste.bat).
cd /d "%~dp0ProjetoTesteCartel"
call "..\ferramentas\apache-maven-3.9.9\bin\mvn.cmd" test
echo.
echo Relatorio: ProjetoTesteCartel\target\surefire-reports\index.html
pause
