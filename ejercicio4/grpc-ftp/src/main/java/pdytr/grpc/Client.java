package pdytr.grpc;
import io.grpc.*;
import pdytr.grpc.FtpServiceOuterClass.FtpResponseRead;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class Client
{
    

    public static void main( String[] args ) throws Exception
    {
     
		if (
			(args.length != 4) ||
    		(!args[0].equals("read") && !args[0].equals("write")) ||
    		(Integer.parseInt(args[2]) < 0) ||
    		(Integer.parseInt(args[3]) <= 0)
	
		)
		{
			System.out.println("4 arguments needed: read name position size_bytes_read");
			System.out.println("4 arguments needed: write name size_bytes_write buffer");
			System.exit(1);
		}


      // Channel is the abstraction to connect to a service endpoint
      // Let's use plaintext communication because we don't have certs
      	final ManagedChannel channel = 
			ManagedChannelBuilder.forTarget("localhost:8080")
        	.usePlaintext(true)
        	.build();

		
	  

      //@Option(name="--deadline_ms", usage="Deadline in milliseconds.")
      int deadlineMs = 10*1000;

      try {

		// It is up to the client to determine whether to block the call
		// Here we create a blocking stub, but an async stub,
		// or an async stub with Future are always possible.
		FtpServiceGrpc.FtpServiceBlockingStub stub = 
			FtpServiceGrpc.newBlockingStub(channel);


		switch(args[0]){
			case "read":
				FtpServiceOuterClass.FtpRequestRead request =
					FtpServiceOuterClass.FtpRequestRead.newBuilder()
		  			.setName(args[1])
					.setPosition(Integer.parseInt(args[2]))
					.setBytes(Integer.parseInt(args[3]))
		  			.build();

				// Finally, make the call using the stub
				
				// OBTENGO UN ITERADOR, PARA PODER RECIBIR LOS PAQUETES DE RESPUESTA DEL SERVER
				// YA QUE POR DE DABJO SE MANEJA HTTP/2
				// Y HTTTP/2 PERMITE ENVIAR O RECIBIR VARIOS PAQUETES EN UNA MISMA CONEXION
				Iterator<FtpResponseRead> responseIterator = stub
					.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS)
					.read(request);

				// ITERO POR CADA UNA DE LAS RESPUESTAS QUE VAN LLEGANDOS
				while (responseIterator.hasNext()) {
					FtpResponseRead response = responseIterator.next();
					// Do something with the response object
					System.out.println(response);
				}

			
				break;
			case "write":
				break;
		}
				

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