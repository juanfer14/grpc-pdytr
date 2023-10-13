#!/bin/bash

# Número de conexiones que deseas establecer
NUM_CONEXIONES=50

# Ruta al programa cliente C
CLIENTE_BIN="java Client"

# Didreccion IP del server
DIRECCION=$1

# Puerto desde donde comienza la conexion
PUERTO=$2

# Tamaño del buffer a leer
SIZE_BUFFER=$3

# Iniciar las conexiones
for ((i=0; i<$NUM_CONEXIONES; i++)); do
    AUX_PUERTO=$(($PUERTO + i))
    $CLIENTE_BIN $DIRECCION $AUX_PUERTO $SIZE_BUFFER
    sleep 2
done
