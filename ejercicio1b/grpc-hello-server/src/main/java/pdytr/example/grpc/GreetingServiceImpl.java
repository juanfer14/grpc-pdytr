package pdytr.example.grpc;

import io.grpc.stub.StreamObserver;

public class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
  private static final int tiempo = 9;
  @Override
  public void greeting(GreetingServiceOuterClass.HelloRequest request,
        StreamObserver<GreetingServiceOuterClass.HelloResponse> responseObserver) {
	
    // HelloRequest has toString auto-generated.
    System.out.println(request);
	

   try {
		System.out.println("PROBANDO UN SLEEP DE " + tiempo + " SG. EN EL SERVER");
		Thread.sleep(tiempo*1000);

		// You must use a builder to construct a new Protobuffer object
		GreetingServiceOuterClass.HelloResponse response = 
		GreetingServiceOuterClass.HelloResponse.newBuilder()
		.setGreeting("Hello there, " + request.getName())
		.build();

		// Use responseObserver to send a single response back
		responseObserver.onNext(response);


		// When you are done, you must call onCompleted.
		responseObserver.onCompleted();
    }
    catch(InterruptedException e){
		System.err.println("Se produjo una excepcion: " + e.getMessage());
		System.exit(0);
    }

  }
}