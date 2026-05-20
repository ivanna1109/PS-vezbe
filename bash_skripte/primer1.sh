#!/bin/bash

# Definisanje obične promenljive
FAKULTET="RAF"

echo "=== Programiranje sistema: Vežbe 11=== "

# Unos vrednosti tokom izvršavanja skripte
echo -n "Unesite vaše ime: "
read IME

echo "Zdravo $IME, trenutno smo na fakultetu: $FAKULTET"

# Jednostavan IF uslov (obratiti pažnju na razmake unutar zagrada!)
if [ "$IME" == "Ivana" ]; then
    echo "Status: Pokrenuto u profesorskom modu."
else
    echo "Status: Pokrenuto u studentskom modu."
fi
