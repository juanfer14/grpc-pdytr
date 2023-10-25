package pdytr.example.grpc;
import pdytr.example.grpc.DataServiceOuterClass;
import pdytr.example.grpc.DataServiceOuterClass.DataRequest;
import pdytr.example.grpc.DataServiceOuterClass.DataResponse;

import com.google.common.primitives.Bytes;
import com.google.protobuf.ByteString;

import io.grpc.stub.StreamObserver;

public class DataServiceImpl extends DataServiceGrpc.DataServiceImplBase {


	 
	// ENVIO DE LOS DATOS
  	@Override
	public void send(DataServiceOuterClass.DataRequest request,
	StreamObserver<DataServiceOuterClass.DataResponse> responseObserver) { 
		try {

				System.out.println(request.getData().size());

				// You must use a builder to construct a new Protobuffer object
				DataServiceOuterClass.DataResponse response = 
					DataServiceOuterClass.DataResponse.newBuilder()
					.setData(request.getData())
					.build();

				

				// Use responseObserver to send a single response back
				responseObserver.onNext(response);

				// When you are done, you must call onCompleted.
				responseObserver.onCompleted();

			} catch(Exception e){
				System.err.println("Se produjo una excepcion: " + e.getMessage());
				System.exit(0);
			}

  }

  
}
