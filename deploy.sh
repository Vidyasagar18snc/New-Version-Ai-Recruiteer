#!/bin/bash

APP_NAME="AI-RECRUITER"
JAR_NAME="Vendor-0.0.1-SNAPSHOT.jar"
APP_PORT=8081
PROFILE="prod"
LOG_DIR="logs"
LOG_FILE="$LOG_DIR/application.log"
PID_FILE="application.pid"
JAVA_HOME="/usr/lib/jvm/java-21-openjdk-amd64"

export $(grep -v '^#' .env | xargs)

JAVA_OPTS="
-Xms512m
-Xmx1024m
-Dspring.profiles.active=$PROFILE
-Dserver.port=$APP_PORT
"

echo "Deploying $APP_NAME"

git pull origin deployement

if [ $? -ne 0 ]; then
    echo "Git pull failed"
    exit 1
fi

chmod +x gradlew

./gradlew clean build -x test

if [ $? -ne 0 ]; then
    echo "Build failed"
    exit 1
fi

mkdir -p $LOG_DIR

PID=$(lsof -ti:$APP_PORT)

if [ ! -z "$PID" ]; then
    echo "Stopping existing process on port $APP_PORT (PID: $PID)"
    kill -15 $PID
    sleep 10
    if ps -p $PID > /dev/null 2>&1; then
        kill -9 $PID
    fi
fi

nohup $JAVA_HOME/bin/java $JAVA_OPTS \
-jar build/libs/$JAR_NAME \
> $LOG_FILE 2>&1 &

NEW_PID=$!
echo $NEW_PID > $PID_FILE

echo "Started with PID $NEW_PID, waiting 15s..."
sleep 15

if ps -p $NEW_PID > /dev/null 2>&1; then
    echo "Application started successfully"
else
    echo "Application failed to start. Last 50 lines of log:"
    tail -50 $LOG_FILE
    exit 1
fi

echo "DEPLOYMENT SUCCESSFUL"