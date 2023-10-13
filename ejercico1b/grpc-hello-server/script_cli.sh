#!/bin/bash

# NUMERO DE CONEXIONES A REALIZAR
CONEXIONES=$1

# COMANDO A EJECUTAR DEL CLIENTE
CLIENTE="mvn -DskipTests exec:java -Dexec.mainClass=pdytr.example.grpc.Client"

for((i=0; i < $CONEXIONES; i++))
do
    echo "CONEXION $i"
    $CLIENTE | grep 'DEADLINE_EXCEEDED'
done
