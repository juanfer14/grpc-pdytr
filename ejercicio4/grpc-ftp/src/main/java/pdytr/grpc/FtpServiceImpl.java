package pdytr.grpc;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


import io.grpc.stub.StreamObserver;

import com.google.protobuf.ByteString;


public class FtpServiceImpl extends FtpServiceGrpc.FtpServiceImplBase {

	// FUNCION SACADA DE https://mkyong.com/java/java-read-a-file-from-resources-folder/
	// PARA OBTENER EL ARCHIVO DESDE /src/main/resources
	
	// LO MODIFIQUE PARA DEVOLVER SOLAMENTE UN FILE O NULL
	// 	-EN CASO DE NO EXISTIR EL ARCHIVO, DEVUELVE NULL 
    //		(SIRVE PARA PODER CREAR UN ARCHIVO SI NO EXISTE)
	// 	-EN CASO DE EXISTIR, DEVUELVE EL FILE
	//  -SI EL ARCHIVO NO EXISTE Y SE QUIERE LEER, SE DEBE MANEJAR LA EXCEPCION
	private File getFileFromResourceAsStream(String fileName) throws URISyntaxException  {

		URL resource =  getClass().getClassLoader().getResource(fileName);
		if(resource == null)
			return null;
		else
			return new File(resource.toURI());
	}

  @Override
  public void read(FtpServiceOuterClass.FtpRequestRead request,
        StreamObserver<FtpServiceOuterClass.FtpResponseRead> responseObserver) {
    
    
	// LO DEJO ACA, PARA QUE NO SEA MUY LARGO LA LINEA.
	FtpServiceOuterClass.FtpResponseRead response;

	// EN CASO DE ERROR, ENVIO ESTA RESPUESTA (BUFFER VACIO Y 0 BYTES LEIDOS)
	byte[] buffer = new byte[0];
	response = FtpServiceOuterClass.FtpResponseRead.newBuilder()
			.setData(ByteString.copyFrom(buffer))
			.setBytesReaded(0)
			.build();


	// HAGO UN TRY POR SI HAY ALGUN ERROR DE IO O EXCEPCION
	try {

		// LEO EL ARCHIVO DESDE /src/main/resources
		File file = getFileFromResourceAsStream(request.getName());

		// EN CASO DE NO EXISTIR EL ARCHIVO, LEVANTO LA EXCPECION
		if(file == null)
			throw new IOException("El archivo " + request.getName() + " no existe");

		// LO CONVIERTO EN UN DATAINPUTSTREAM
		DataInputStream dataRead = new DataInputStream(new FileInputStream(file));

		// ASIGNO AL BUFFER LA CANTIDAD DE DATOS NECESARIOS
		buffer = new byte[(int)request.getBytes()];

		// LEO EL ARCHIVO DESDE LA POSICION INDICADA Y LA CANTIDAD INDICADA, Y GUARDO
		// LA CANTIDAD DE BYTES QUE EFECTIVAMENTE SE LEYERON
		int readed = dataRead.read(buffer, (int)request.getPosition(), (int) request.getBytes());
		
		// CIERRO EL DATAINPUTSTREAM
		dataRead.close();	
		
		// DEVUELVO COMO RESPUESTA EL BUFFER Y LA CANTIDAD DE BYTES LEIDOS
		response = FtpServiceOuterClass.FtpResponseRead.newBuilder()
			.setData(ByteString.copyFrom(buffer))
			.setBytesReaded(readed)
			.build();

		System.out.println("ARCHIVO LEIDO Y ENVIADO CON EXITO!");
		
	}
	catch(IOException e){
		// EN EL CASO DE NO EXISTIR EL ARCHIVO, SE LEVANTA ESTA EXCEPCION
		System.err.println("Error de lectura del archivo: " + e.getMessage());
		
	}
	catch(Exception e){
		// EN EL CASO DE HABER UN ERROR (EJ. UNAVAILABLE), SE LEVANTA ESTA EXCEPCION
		System.err.println("Se produjo una excepcion: " + e.getMessage());

	}

	// Use responseObserver to send a single response back
	responseObserver.onNext(response);

	// When you are done, you must call onCompleted.
	responseObserver.onCompleted();
}
    

}