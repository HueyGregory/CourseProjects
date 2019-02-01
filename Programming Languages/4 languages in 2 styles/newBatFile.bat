@echo off
:: taking input
:start
echo starting program

set arg4=""
set cmd1=%1
set arg2=%2
set arg3=%3
set arg4=%5
echo arg4: %arg4%
if /I %cmd1%==grep (
	set WordToSearchFor=%arg2%
	set File=%arg3%
	if not defined arg4 ( set Type=grep ) else ( set Type=both )
) else if /I %cmd1%==wc (
	set File=%arg2%
	set Type=wc
)

echo Type: %Type%
echo WordToSearchFor: %WordToSearchFor%
echo File: %File%

if Not Exist %File% (
	echo Error File Does NOT Exist
	echo.
	echo start over
	goto end
)

call :languageJava
echo.
call :languageC
echo.
call :languageJavascript
echo.
call :languagePython
echo.
goto end


:compileJavaOO
echo.
echo Now compiling java OO code
javac OO/Java/"Java OO"/src/interfaces/*.java OO/Java/"Java OO"/src/interfaceImplementations/*.java OO/Java/"Java OO"/src/*.java
cd OO\Java\"Java OO"\src
goto :eof


:compileJavaFunctional
echo.
echo Now compiling java Functional code
javac Functional/Java/"Java Functional"/src/functional/MainClassFunctional.java
cd Functional\Java\"Java Functional"\src\functional
goto :eof


:languageJava
echo.
call :compileJavaOO
if /I %Type%==grep (
	echo Java OO Grep
	java MainClassOO grep %WordToSearchFor% ..\..\..\..\%File%
) else if /I %Type%==both (
	echo Java OO Both
	java MainClassOO grep %WordToSearchFor% ..\..\..\..\%File% "|" wc
) else if /I %Type%==wc (
	echo Java OO WC
	java MainClassOO wc ..\..\..\..\%File%
) else ( echo Error )
REM changing directory back to original one
cd ..\..\..\..
echo.
call :compileJavaFunctional
if /I %Type%==grep (
	echo Java Functional Grep
	java MainClassFunctional grep %WordToSearchFor% ..\..\..\..\..\%File%
) else if /I %Type%==both (
	echo Java OO Both
	java MainClassFunctional grep %WordToSearchFor% ..\..\..\..\..\%File% "|" wc
) else if /I %Type%==wc (
	echo Java Functional WC
	java MainClassFunctional wc ..\..\..\..\..\%File%
)
REM changing directory back to original one
cd ..\..\..\..\..
goto :eof
	
	
	
:languageJavascript
cd OO\Javascript\Javascript OO
echo.
if /I %Type%==grep (
	echo Javascript OO Grep
	node --experimental-modules MainClass.mjs grep %WordToSearchFor% ..\..\..\%File%
) else if /I %Type%==both (
	echo Javascript OO Both
	node --experimental-modules MainClass.mjs grep %WordToSearchFor% ..\..\..\%File% "|" wc
) else if /I %Type%==wc (
	echo Javascript OO WC
	node --experimental-modules MainClass.mjs wc ..\..\..\%File%
)
cd ..\..\..
echo.
if /I %Type%==grep (
	echo Javascript Functional Grep
	node Functional\Javascript\Main.js grep %WordToSearchFor% %File%
) else if /I %Type%==both (
	echo Javascript OO Both
	node Functional\Javascript\Main.js grep %WordToSearchFor% %File% "|" wc
) else if /I %Type%==wc (
	echo Javascript Functional WC
	node Functional\Javascript\Main.js wc %File%
)
goto :eof



:languagePython
echo.
if /I %Type%==grep (
	echo Python OO Grep
	python "OO\Python\Python OO\main.py" grep %WordToSearchFor% %File%
) else if /I %Type%==both (
	echo Python OO Both
	python "OO\Python\Python OO\main.py" grep %WordToSearchFor% %File% "|" wc
) else if /I %Type%==wc (
	echo Python OO WC
	python "OO\Python\Python OO\main.py" wc %File%
)
echo.
if /I %Type%==grep (
	echo Python Functional Grep
	python "Functional\Python\Functional Python\Main.py" grep %WordToSearchFor% %File%
) else if /I %Type%==both (
	echo Python OO Both
	python "Functional\Python\Functional Python\Main.py" grep %WordToSearchFor% %File% "|" wc
) else if /I %Type%==wc (
	echo Python Functional WC
	python "Functional\Python\Functional Python\Main.py" wc %File%
)
goto :eof


:compileCImperative
echo.
echo Now compiling c imperative code
cd Imperative/C
gcc functionsUsedByEveryone.h functionsUsedByEveryone.c main.c -o main
goto :eof

:compileCFunctional
echo.
echo Now compiling c functional code
cd Functional/C
gcc functionsUsedByEveryone.h functionsUsedByEveryone.c main.c -o main
goto :eof

:languageC
echo.
call :compileCImperative
if /I %Type%==grep (
	echo C Imperative Grep
	main grep %WordToSearchFor% ..\..\%File%
) else if /I %Type%==both (
	echo C Imperative Both
	main grep %WordToSearchFor% ..\..\%File% "|" wc
) else if /I %Type%==wc (
	echo C Imperative WC
	main wc ..\..\%File%
)
::changing directory back to original one
cd ..\..
echo.
call :compileCFunctional
if /I %Type%==grep (
	echo C Functional Grep
	main grep %WordToSearchFor% ..\..\%File%
) else if /I %Type%==both (
	echo C OO Both
	main grep %WordToSearchFor% ..\..\%File% "|" wc
) else if /I %Type%==wc (
	echo C Functional WC
	main wc ..\..\%File%
)
::changing directory back to original one
cd ..\..
goto :eof

:end