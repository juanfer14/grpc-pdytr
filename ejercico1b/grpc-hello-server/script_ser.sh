# NUMERO DE CONEXIONES A REALIZAR
CONEXIONES=$1

# CODIGO DEL SERVER A EJERCUTAR
SERVER="mvn -DskipTests package exec:java -Dexec.mainClass=pdytr.example.grpc.App"

$SERVER
