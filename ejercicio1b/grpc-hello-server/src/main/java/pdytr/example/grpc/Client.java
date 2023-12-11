package pdytr.example.grpc;
import io.grpc.*;

import io.grpc.*;

import java.util.concurrent.TimeUnit;

public class Client
{
    

    public static void main( String[] args ) throws Exception
    {
      
      // Channel is the abstraction to connect to a service endpoint
      // Let's use plaintext communication because we don't have certs
      final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8080")
        .usePlaintext(true)
        .build();

      //@Option(name="--deadline_ms", usage="Deadline in milliseconds.")
      int deadlineMs = 10*1000;

	  System.out.println("PROBANDO DEADLINE DE " + deadlineMs/1000 + " SG, EN EL CLIENTE" );

      try {
		// It is up to the client to determine whether to block the call
		// Here we create a blocking stub, but an async stub,
		// or an async stub with Future are always possible.
		GreetingServiceGrpc.GreetingServiceBlockingStub stub = 
			GreetingServiceGrpc.newBlockingStub(channel);

		GreetingServiceOuterClass.HelloRequest request =
			GreetingServiceOuterClass.HelloRequest.newBuilder()
		  		.setName("Ray")
		  		.build();

		// Finally, make the call using the stub
		GreetingServiceOuterClass.HelloResponse response = 
			stub.withDeadlineAfter( 
				deadlineMs,TimeUnit.MILLISECONDS
			)
			.greeting(request);

		System.out.println(response);

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