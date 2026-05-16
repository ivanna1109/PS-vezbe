#!/bin/bash

set -e

echo "1. Instaliranje shared-modules..."
cd shared-module && mvn clean install && cd ..

echo "2. Build order-service..."
cd order-services && mvn clean package -DskipTests && cd ..

echo "3. Build restaurant-service..."
cd restaurant-service && mvn clean package -DskipTests && cd ..

#echo "4. Čišćenje starih kontejnera i volumena..."
#docker-compose down -v

echo "5. Run Docker conts..."
docker-compose up --build -d

echo "15 sekundi da se RabbitMQ i servisi skroz usaglase..."
sleep 15

echo "6. Čišćenje zaostalih poruka iz redova..."
docker exec rabbitmq rabbitmqctl purge_queue student_queue || echo "Queue student_queue još ne postoji"
docker exec rabbitmq rabbitmqctl purge_queue order_feedback_queue || echo "Queue order_feedback_queue još ne postoji"

echo "SPREMNO!"
