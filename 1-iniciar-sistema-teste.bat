@echo off
REM Sobe a COPIA de teste do sistema em http://localhost:3002 (banco cartel_dos_cortes_teste).
REM Deixe esta janela aberta enquanto roda os testes.
cd /d "%~dp0sistema-copia"
npx next dev -p 3002
