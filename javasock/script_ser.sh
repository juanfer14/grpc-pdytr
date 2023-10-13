#!/bin/bash

# Número de conexiones que deseas establecer
NUM_CONEXIONES=50

# Ruta al programa servidor C
SERVIDOR_BIN="java Server"

# Puerto desde donde se comienza las conexiones
PUERTO=$1

# Tamaño del buffer a utilizar
SIZE_BUFFER=$2

# Iniciar las conexiones
for ((i=0; i<$NUM_CONEXIONES; i++)); do
    AUX_PUERTO=$(($PUERTO + i))
    echo $AUX_PUERTO
    $SERVIDOR_BIN $AUX_PUERTO $SIZE_BUFFER
    sleep 1
done
