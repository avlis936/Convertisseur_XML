@echo off
REM Force console en UTF-8 puis recompile et lance l'application avec le classpath local
chcp 65001 > nul
setlocal
set BASE=%~dp0

REM Se placer dans le répertoire du projet (pratique si appel depuis ailleurs)
cd /d "%BASE%"

REM Recompiler le projet (skip tests) ; si échec, quitter avec message
echo Recompilation du projet (mvn package)...
call mvn -f "%BASE%pom.xml" -DskipTests package
set MVN_RC=%ERRORLEVEL%
echo Retour Maven : %MVN_RC%
if %MVN_RC% NEQ 0 (
  echo Erreur pendant la compilation. Appuyez sur une touche pour quitter...
  pause
  endlocal
  exit /b 1
)

REM Construire le classpath (target/classes + tous les jars de target/dependency)
set CP=%BASE%target\classes;%BASE%target\dependency\*

echo Classpath utilise : %CP%

echo Lancement de l'application...
java -Dfile.encoding=UTF-8 -cp "%CP%" main.Application

echo Execution terminee.
endlocal
pause
