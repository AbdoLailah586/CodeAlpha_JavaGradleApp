<#
.SYNOPSIS
    CodeAlpha Task 3 - Java Gradle App Lifecycle Helper Script (PowerShell)
#>

param (
    [Parameter(Position=0)]
    [ValidateSet("build", "test", "start", "stop", "restart", "status", "logs", "test-endpoints", "clean", "help")]
    [string]$Action = "help"
)

function Show-Help {
    Write-Host "`n=== CodeAlpha Java Gradle Application Helper ===" -ForegroundColor Cyan
    Write-Host "Usage: .\manage.ps1 [action]`n" -ForegroundColor Yellow
    Write-Host "Available Actions:"
    Write-Host "  build          - Build the Java app & Docker image using Gradle"
    Write-Host "  test           - Run JUnit 5 tests inside Gradle container"
    Write-Host "  start          - Start the containerized application"
    Write-Host "  stop           - Stop the running application"
    Write-Host "  restart        - Restart the application"
    Write-Host "  status         - Check container running state"
    Write-Host "  logs           - Stream application logs"
    Write-Host "  test-endpoints - Test REST endpoints (/api/health, /api/info, /api/greet)"
    Write-Host "  clean          - Remove containers, images, and volumes"
    Write-Host "================================================`n"
}

switch ($Action) {
    "build" {
        Write-Host "--> Building Java Application and Docker Image..." -ForegroundColor Cyan
        docker compose build
    }
    "test" {
        Write-Host "--> Running JUnit 5 Automated Tests via Gradle..." -ForegroundColor Cyan
        docker run --rm -v "${PWD}:/home/gradle/src" -w /home/gradle/src gradle:8.7-jdk17-alpine gradle test --info
    }
    "start" {
        Write-Host "--> Starting Java Application..." -ForegroundColor Green
        docker compose up -d
        Write-Host "--> Java Web App is live at http://localhost:8081" -ForegroundColor Yellow
    }
    "stop" {
        Write-Host "--> Stopping Java Container..." -ForegroundColor Yellow
        docker compose stop
    }
    "restart" {
        Write-Host "--> Restarting Java Container..." -ForegroundColor Cyan
        docker compose restart
    }
    "status" {
        Write-Host "`n--> Container Process Status:" -ForegroundColor Cyan
        docker ps -a --filter "name=codealpha-java-app"
    }
    "logs" {
        Write-Host "--> Streaming Application Logs (Ctrl+C to exit)..." -ForegroundColor Cyan
        docker compose logs -f
    }
    "test-endpoints" {
        Write-Host "`n--> Testing Health Endpoint (http://localhost:8081/api/health)..." -ForegroundColor Cyan
        try {
            $health = Invoke-RestMethod -Uri "http://localhost:8081/api/health"
            Write-Host "Health Response:" -ForegroundColor Green
            $health | ConvertTo-Json
        } catch {
            Write-Host "Error connecting to http://localhost:8081/api/health" -ForegroundColor Red
        }

        Write-Host "`n--> Testing Info Endpoint (http://localhost:8081/api/info)..." -ForegroundColor Cyan
        try {
            $info = Invoke-RestMethod -Uri "http://localhost:8081/api/info"
            Write-Host "Info Response:" -ForegroundColor Green
            $info | ConvertTo-Json
        } catch {
            Write-Host "Error connecting to http://localhost:8081/api/info" -ForegroundColor Red
        }

        Write-Host "`n--> Testing Greet Endpoint (http://localhost:8081/api/greet?name=Abdol)..." -ForegroundColor Cyan
        try {
            $greet = Invoke-RestMethod -Uri "http://localhost:8081/api/greet?name=Abdol"
            Write-Host "Greet Response:" -ForegroundColor Green
            $greet | ConvertTo-Json
        } catch {
            Write-Host "Error connecting to http://localhost:8081/api/greet" -ForegroundColor Red
        }
    }
    "clean" {
        Write-Host "--> Cleaning up Docker containers..." -ForegroundColor Yellow
        docker compose down --rmi local --volumes --remove-orphans
        Write-Host "--> Cleaned successfully." -ForegroundColor Green
    }
    Default {
        Show-Help
    }
}
