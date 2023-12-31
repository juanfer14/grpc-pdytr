package pdytr.grpc;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import java.util.Arrays;
//ARCHIVOS
import java.nio.file.Path;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.FileReader;

import java.nio.charset.StandardCharsets;

import java.io.File;

import java.io.FileOutputStream;

import java.nio.file.Files;
import java.nio.file.Paths;

import io.grpc.stub.StreamObserver;

import com.google.protobuf.ByteString;


public class FtpServiceImpl extends FtpServiceGrpc.FtpServiceImplBase {

	private static final int BLOQUE = 1024;

	private URL getFileResources(String fileName){
		return getClass().getClassLoader().getResource(fileName);
	}

	// FUNCION SACADA DE https://mkyong.com/java/java-read-a-file-from-resources-folder/
	// PARA OBTENER EL ARCHIVO DESDE /src/main/resources
	
	// LO MODIFIQUE PARA DEVOLVER SOLAMENTE UN FILE O NULL
	// 	-EN CASO DE NO EXISTIR EL ARCHIVO, DEVUELVE NULL 
    //		(SIRVE PARA PODER CREAR UN ARCHIVO SI NO EXISTE)
	// 	-EN CASO DE EXISTIR, DEVUELVE EL FILE
	//  -SI EL ARCHIVO NO EXISTE Y SE QUIERE LEER, SE DEBE MANEJAR LA EXCEPCION
	private File getFileFromResourceAsStream(String fileName) throws URISyntaxException  {

		URL resource = getFileResources(fileName);
		
		if(resource == null){
			return null;
		}
		return new File(resource.toURI());

			
	}

	@Override
  	public void ask(FtpServiceOuterClass.FtpRequestSize request,
        StreamObserver<FtpServiceOuterClass.FtpResponseSize> responseObserver) {

			// POR DEFAULT, DEVUELVO 0 BYTES, SI EL ARCHIVO 
			// NO EXISTE O SE LEVANTA UNA EXCEPCION
			long bytes = 0;
			System.out.println(request.getName());
			try {
				File file = getFileFromResourceAsStream(request.getName());
				if(file != null)
					bytes = file.length();
			} catch(URISyntaxException e){
				System.err.println(e.getMessage());
			}
			
			// You must use a builder to construct a new Protobuffer object
			FtpServiceOuterClass.FtpResponseSize response = 
			FtpServiceOuterClass.FtpResponseSize.newBuilder()
		  		.setBytes(bytes)
			  	.build();
	  
		  // Use responseObserver to send a single response back
		  responseObserver.onNext(response);
		  // When you are done, you must call onCompleted.
		  responseObserver.onCompleted();
	}

  	@Override
  	public void read(FtpServiceOuterClass.FtpRequestRead request,
        StreamObserver<FtpServiceOuterClass.FtpResponseRead> responseObserver) {
    
		// LO DEJO ACA, PARA QUE NO SEA MUY LARGA LA LINEA.
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

			// REALIZO EL OFFSET DEL LADO DEL SERVIDOR
			dataRead.skipBytes((int)request.getPosition());

			// OBTENGO DEL REQUEST, LA CANTIDAD DE BYTES A LEER
			int bytes_leer = (int)request.getBytes();

			// SI LA CANTIDAD DE BYTES A LEER, ES MAYOR QUE EL TAM. DE BLOQUE
			// ENTONCES ENVIO EL TAM. DE BLOQUE
			if(bytes_leer > BLOQUE)
				bytes_leer = BLOQUE;

			System.out.println("BYTES A LEER " + bytes_leer);

			// ASIGNO AL BUFFER LA CANTIDAD DE DATOS NECESARIOS
			buffer = new byte[bytes_leer];

			// LEO EL ARCHIVO DESDE LA POSICION INDICADA Y LA CANTIDAD INDICADA, Y GUARDO
			// LA CANTIDAD DE BYTES QUE EFECTIVAMENTE SE LEYERON
			int readed = dataRead.read(buffer);
			
			// CIERRO EL DATAINPUTSTREAM
			dataRead.close();	
			
			// DEVUELVO COMO RESPUESTA EL BUFFER Y LA CANTIDAD DE BYTES LEIDOS
			response = FtpServiceOuterClass.FtpResponseRead.newBuilder()
				.setData(ByteString.copyFrom(buffer))
				.setBytesReaded(readed)
				.build();

			System.out.println("ARCHIVO LEIDO Y ENVIADO CON EXITO!");
			
		}
		catch(URISyntaxException e){
			System.err.println("Error en la URI del archivo: " + e.getMessage());
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

    
	@Override
  	public void write(FtpServiceOuterClass.WriteRequest request,
        StreamObserver<FtpServiceOuterClass.WriteResponse> responseObserver) {
	
		// Obtengo los datos del archivo desde el request
        ByteString archivoDatos = request.getArchivoDatos();
        String nombreArchivo = request.getNombreArchivo();

		// SE CREA UN ARCHIVO, A MODO DE EJEMPLO, PARA TENER UN FEEDBACK DE LA LECTURA
		int punto = nombreArchivo.indexOf(".");
		String nombre = nombreArchivo.substring(0, punto);
		String extension = nombreArchivo.substring(punto);
		nombreArchivo = nombre + "-copia" + extension;
		

        byte[] byteArray = archivoDatos.toByteArray();

        // Imprimir los datos del ByteString
        // System.out.println("Datos que recibe el servidor: " + archivoDatos.toStringUtf8());
    
        int cantLeidos = archivoDatos.size();

		System.out.println("El tamaño del paquete recibido es: "+archivoDatos.size());

        // Genera una ruta de destino para guardar el archivo

		// DECLARO LA RESPUESTA A DEVOLVER
		FtpServiceOuterClass.WriteResponse response;

		// INSTANCIO UNA RESPUESTA POR DEFAULT, EN EL CASO DE HABER UNA EXCEPCION
		response = FtpServiceOuterClass.WriteResponse.newBuilder()
				.setCantEscritos(-1)
				.build();

		// ESCRIBO EL ARCHIVO EN /src/main/resources
		try {
            // Obtener el archivo desde la carpeta "resources"
            File file = getFileFromResourceAsStream(nombreArchivo);

			// SI EL ARCHIVO NO EXISTE, SE LO CREA
			if(file == null)
				try {
					URL resources = getFileResources(".");
					file = new File(resources.toURI().getPath() + nombreArchivo);
					file.createNewFile();
				} catch (IOException e) {
					System.err.println("Error al crear el archivo: " + e.getMessage());
				}
			

			// SE INSTANCIO UN FileOutputStream PARA ESCRIBIR EN MODO APPEND
			try (FileOutputStream fileOutputStream = new FileOutputStream(file, true)) {
                	fileOutputStream.write(byteArray);
					fileOutputStream.flush();
            } catch (IOException e) {
				System.err.println("Ocurrió un error en el server...\n\n");
				e.printStackTrace();
			}

			// Crea una respuesta para notificar la cantidad de bytes leídos
        	response = FtpServiceOuterClass.WriteResponse.newBuilder()
				.setCantEscritos(cantLeidos)
				.build();

		} catch (URISyntaxException e){
			System.err.println("Error en la URI del archivo: " + e.getMessage());
		} catch(Exception e){
			System.err.println("Se produjo una excepcion: " + e.getMessage());
		}
		
		// Use responseObserver to send a single response back
		responseObserver.onNext(response);

		// When you are done, you must call onCompleted.
		responseObserver.onCompleted();
  }

}