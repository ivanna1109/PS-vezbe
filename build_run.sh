#!/bin/bash

set -e

echo "1. Instaliranje shared-modules..."
cd shared-module && mvn clean install && cd ..

echo "2. Build gateway-service..."
cd gateway-service && mvn clean package -DskipTests && cd ..

echo "3. Build order-service..."
cd order-services && mvn clean package -DskipTests && cd ..

echo "4. Build restaurant-service..."
cd restaurant-service && mvn clean package -DskipTests && cd ..

echo "5. Gašenje prethodnog stanja aplikacije..."
docker-compose down

echo "6. Pokretanje sistema (Mikroservisi, RabbitMQ, Zipkin)..."
docker-compose up --build -d

echo "Čekam 15 sekundi da se RabbitMQ, Zipkin i servisi skroz usaglase..."
sleep 15

echo "7. Čišćenje zaostalih poruka iz redova..."
docker exec rabbitmq rabbitmqctl purge_queue student_queue || echo "Queue student_queue još ne postoji"
docker exec rabbitmq rabbitmqctl purge_queue order_feedback_queue || echo "Queue order_feedback_queue još ne postoji"

echo "SPREMNO!"
