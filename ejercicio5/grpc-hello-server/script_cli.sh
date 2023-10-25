#!/bin/bash

# NUMERO DE CONEXIONES A REALIZAR
CONEXIONES=$1

# NUMERO DE BYTES A ENVIAR
SIZE=$2

# COMANDO A EJECUTAR DEL CLIENTE
CLIENTE="mvn -DskipTests exec:java -Dexec.mainClass=pdytr.example.grpc.Client -Dexec.args=\"$SIZE\""

CANT=0

for((i=0; i < $CONEXIONES; i++))
do
    CANT=$((CANT+1))
    $CLIENTE | grep "TIME"
    sleep 1
done

echo $CANT