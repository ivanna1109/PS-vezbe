#!/bin/bash

# Prekidamo skriptu istog trenutka ako bilo koja komanda baci grešku
set -e

echo "=== [1/4] ČIŠĆENJE STAROG DOCKER OKRUŽENJA ==="
echo "Zaustavljanje i brisanje starih kontejnera..."
# || true sprečava prekid skripte ako trenutno nema pokrenutih kontejnera
docker stop $(docker ps -a -q) 2>/dev/null || true
docker rm $(docker ps -a -q) 2>/dev/null || true

echo "Čišćenje neiskorišćenih Docker volumene i keša..."
docker system prune -f --volumes

echo "=== [2/4] MAVEN PAKOVANJE MIKROSERVISA ==="
# Pretpostavka je da se skripta nalazi u root folderu gde su i servisi
for SERVIS_FOLDER in order-service restaurant-service gateway; do
    if [ -d "$SERVIS_FOLDER" ]; then
        echo "Pokrećem Maven build za: $SERVIS_FOLDER..."
        # Pokrećemo Maven unutar podfoldera bez šetanja glavnog terminala
        (cd "$SERVIS_FOLDER" && mvn clean package -DskipTests)
    else
        echo "Greška: Direktorijum $SERVIS_FOLDER ne postoji!"
        exit 1
    fi
done

echo "=== [3/4] KREIRANJE NOVIH DOCKER SLIKA ==="
# Pokrećemo docker-compose build da osvežimo slike sa novim .jar fajlovima
docker-compose build

echo "=== [4/4] PODIZANJE SISTEMA ==="
docker-compose up -d

echo "=================================================="
echo " SVE JE SPREMNO! Sistem je restartovan i ažuriran!"
echo "=================================================="
