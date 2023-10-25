package pdytr.example.grpc;
import io.grpc.*;

import java.util.concurrent.TimeUnit;

import com.google.protobuf.ByteString;



public class Client
{
    
    public static void main( String[] args ) throws Exception
    {
		// VERIFICO LA CANTIDAD DE ARGUMENTOS PASADOS POR PARAMETRO
		int size_buffer = Integer.parseInt(args[0]);
		if ( (args.length != 1) || (size_buffer < 0)){
			System.out.println("1 arguments needed: size_buffer");
			System.exit(1);
		}

      // Channel is the abstraction to connect to a service endpoint
      // Let's use plaintext communication because we don't have certs
      final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8080")
        .usePlaintext(true)
        .build();

      int deadlineMs = 2*1000;

      try {
		// It is up to the client to determine whether to block the call
		// Here we create a blocking stub, but an async stub,
		// or an async stub with Future are always possible.
		DataServiceGrpc.DataServiceBlockingStub stub = DataServiceGrpc.newBlockingStub(channel);

		// BUFFER UTILIZADO PARA LA COMUNICACION
		byte[] buffer = new byte[size_buffer];

		// LLENO EL BUFFER
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = (byte) (i % 256);
		}

		/* COMIENZO A TOMAR EL TIEMPO */
    	long tiempo_antes = System.nanoTime();

		// 1. SE ENVIAN TODOS LOS DATOS AL SERVIDOR
		DataServiceOuterClass.DataRequest request = DataServiceOuterClass.DataRequest.newBuilder()
			.setData(ByteString.copyFrom(buffer))
			.build();

		// 2. SE RECIBE NUEVAMENTE LOS DATOS ENVIADOS
		DataServiceOuterClass.DataResponse response =  stub
			.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS)
			.send(request);

		/* FINALIZO DE TOMAR EL TIEMPO */
		long tiempo_despues = System.nanoTime();

		/* SACO LA DIFERENCIA DE MICROSEGUNDOS Y LO PASO A NANOSEGUNDOS */
		long tiempo_microseg = (tiempo_despues - tiempo_antes) / 1000;
	
		/* OBTENGO EL TIEMPO DE COMUNICACION DE AMBOS LADOS (CLIENTE Y SERVIDOR) */
		long tiempo_comunicacion = tiempo_microseg / 2;

		// DATOS DE LA RESPUESTA
		//System.out.println(response);
	
		/* IMPRIMO EL TIEMPO DE COMUNICACION */
		System.out.println(tiempo_comunicacion + " - TIME");
		
		


		// A Channel should be shutdown before stopping the process.
		channel.shutdownNow();
      }
      catch (Exception e){
		System.err.println("Se produjo una excepcion: " + e.getMessage());
		System.err.println("Se procede a cerrar el canal...");
		channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
      }
    }
}