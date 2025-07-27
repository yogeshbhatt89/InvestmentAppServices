@echo off
setlocal enabledelayedexpansion

:: Colors for output
set "GREEN=[32m"
set "YELLOW=[33m"
set "NC=[0m"

echo %YELLOW%🚀 Starting project setup...%NC%

:: 1. Set up git hooks
echo.
echo %YELLOW%🔧 Setting up git hooks...%NC%
if not exist ".githooks" (
    echo ❌ Error: .githooks directory not found. Please make sure you're in the project root.
    exit /b 1
)

:: Set git to use our hooks directory
git config core.hooksPath .githooks

:: Make sure the pre-push hook is executable
if exist ".githooks\pre-push" (
    echo %GREEN%✓ Git hooks configured successfully%NC%
) else (
    echo ❌ Failed to find pre-push hook
    exit /b 1
)

:: 2. Install Maven wrapper if not present
if not exist "mvnw.cmd" (
    echo.
    echo %YELLOW%📦 Installing Maven Wrapper...%NC%
    mvn -N wrapper:wrapper
    if !errorlevel! equ 0 (
        echo %GREEN%✓ Maven Wrapper installed%NC%
    ) else (
        echo ❌ Failed to install Maven Wrapper
        exit /b 1
    )
)

:: 3. Install dependencies
echo.
echo %YELLOW%📦 Installing Maven dependencies...%NC%
call mvnw clean install -DskipTests
if !errorlevel! equ 0 (
    echo %GREEN%✓ Dependencies installed successfully%NC%
) else (
    echo ❌ Failed to install dependencies
    exit /b 1
)

:: 4. Format code
echo.
echo %YELLOW%✨ Formatting code...%NC%
call mvnw spotless:apply
if !errorlevel! equ 0 (
    echo %GREEN%✓ Code formatted successfully%NC%
) else (
    echo ❌ Failed to format code
    exit /b 1
)

echo.
echo %GREEN%✅ Setup completed successfully!%NC%
echo You can now start developing. Happy coding! 🚀
echo.

echo %YELLOW%Next steps:%NC%
echo 1. Configure your environment variables in application-local.properties
echo 2. Run the application with: mvnw spring-boot:run
echo.
echo For more information, check the README.md file

pause
