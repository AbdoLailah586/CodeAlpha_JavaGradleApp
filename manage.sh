#!/usr/bin/env bash
ACTION="${1:-help}"

case "$ACTION" in
    build)
        echo "--> Building Java Application and Docker Image..."
        docker compose build
        ;;
    test)
        echo "--> Running JUnit 5 Automated Tests via Gradle..."
        docker run --rm -v "${PWD}:/home/gradle/src" -w /home/gradle/src gradle:8.7-jdk17-alpine gradle test --info
        ;;
    start)
        echo "--> Starting Java Application..."
        docker compose up -d
        echo "--> Java Web App is live at http://localhost:8081"
        ;;
    stop)
        echo "--> Stopping Java Container..."
        docker compose stop
        ;;
    restart)
        echo "--> Restarting Java Container..."
        docker compose restart
        ;;
    status)
        echo "--> Container Process Status:"
        docker ps -a --filter "name=codealpha-java-app"
        ;;
    logs)
        echo "--> Streaming Application Logs..."
        docker compose logs -f
        ;;
    test-endpoints)
        echo "--> Testing Health Endpoint..."
        curl -s http://localhost:8081/api/health
        echo -e "\n--> Testing Info Endpoint..."
        curl -s http://localhost:8081/api/info
        echo -e "\n--> Testing Greet Endpoint..."
        curl -s "http://localhost:8081/api/greet?name=Abdol"
        echo ""
        ;;
    clean)
        echo "--> Cleaning up Docker containers and images..."
        docker compose down --rmi local --volumes --remove-orphans
        ;;
    *)
        echo "Usage: ./manage.sh {build|test|start|stop|restart|status|logs|test-endpoints|clean}"
        ;;
esac
