#!/bin/bash

# Lista mikroservisa kroz koje skripta treba da prođe
SERVISI=("gateway" "order-service" "restaurant-service")

echo "Započinjemo simulaciju build procesa..."

# For petlja prolazi kroz niz
for SERVIS in "${SERVISI[@]}"
do
    echo "--------------------------------------"
    echo "U direktorijumu: ./$SERVIS"
    
    # Simuliramo kreiranje target foldera kao što to radi Maven
    echo "Kreiranje 'target' direktorijuma..."
    mkdir -p "./$SERVIS/target"
    
    # Simuliramo build
    echo "Kompajliram kod za $SERVIS..."
    sleep 2
    
    echo "Uspešno generisan $SERVIS.jar fajl!"
done

echo "--------------------------------------"
echo "Sve aplikacije su uspešno build-ovane!"
