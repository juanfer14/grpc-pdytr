package pdytr.grpc;
import io.grpc.*;
import pdytr.grpc.FtpServiceOuterClass.FtpResponseRead;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import java.nio.file.Files;
import java.nio.ByteBuffer;
import com.google.protobuf.ByteString;

//ARCHIVOS
import java.nio.file.Path;
import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.nio.charset.StandardCharsets;



// ENLACES
import java.net.URI;
import java.net.URL;

// EXCEPCIONES
import java.io.IOException;
import java.net.URISyntaxException;


public class Client
{
    private static final String ORIGIN_FOLDER = "C:\\Users\\usuario\\Desktop\\Origen\\"; // Ruta de origen donde se leeran los archivos


    public static void main( String[] args ) throws Exception
    {
     
		System.out.println(args[0]);
		if (
			(args.length != 4) ||
    		(!args[0].equals("read") && !args[0].equals("write")) ||
    		(Integer.parseInt(args[2]) < 0) ||
    		(Integer.parseInt(args[3]) < 0)
	
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
				int offset = Integer.parseInt(args[2]);
				int bytes_leer = Integer.parseInt(args[3]);

				FtpServiceOuterClass.FtpRequestRead request =
					FtpServiceOuterClass.FtpRequestRead.newBuilder()
		  			.setName(args[1])
					.setPosition(offset)
					.setBytes(bytes_leer)
		  			.build();

				// Finally, make the call using the stub
				
				// OBTENGO UN ITERADOR, PARA PODER RECIBIR LOS PAQUETES DE RESPUESTA DEL SERVER
				Iterator<FtpResponseRead> responseIterator = stub
					.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS)
					.read(request);

				// ITERO POR CADA UNA DE LAS RESPUESTAS QUE VAN LLEGANDOS
				System.out.println("Esta es la respuesta: ");
				int i = 0;
				while (responseIterator.hasNext()) {
					System.out.println("ITERACION " + (++i));
					FtpResponseRead response = responseIterator.next();
					// Do something with the response object
					//System.out.println(response);
				}

			
				break;
			case "write":
				
				//leo las variables de la consulta
				String arch = args[1];
				int cant_escribir = Integer.parseInt(args[2]);
				//byte[] buffer = args[3].getBytes();
				
				// Obtener los datos del archivo
				byte[] datosDelArchivo = null;

				//Lee el archivo del directorio local
				try {
					// SUPONEMOS, ALEGREMENTE QUE EL ARCHIVO EXISTE, Y ESTA EN SRC/MAIN/RESOURCES
					File file = getFileFromResourceAsStream(arch);

					try (DataInputStream dataRead = new DataInputStream(new FileInputStream(file))) {
						// Lee los datos del archivo y los almacena en el arreglo datosDelArchivo
						datosDelArchivo = new byte[cant_escribir];
						dataRead.read(datosDelArchivo);
					}	
				} catch (URISyntaxException e) {

					System.err.println("Error de lectura del archivo: " + e.getMessage()); 
					return;
				}
				ByteString buffer_datos;

				if (datosDelArchivo == null) {
					buffer_datos = ByteString.EMPTY;
				} else {
            		buffer_datos = ByteString.copyFrom(datosDelArchivo);
				}

				// Imprimir los datos del ByteString
				System.out.println("Datos leiodos desde el directorio local: " + buffer_datos.toStringUtf8()+"\n");

				/*
				size /()
				100 -> 10 - 10 - 10 - 10 ... 10 - 10

				while()
					transmitir;
					
				byteString.substring(start, end).copyTo(target)

				
				*/
				int total_enviar =   buffer_datos.size();
				int tamanio_bloque = 1024;
				int bytes_faltantes =total_enviar;
				int bytes_enviados = 0;
				int start = 0;
				int end = 0;

				ByteBuffer paquete = null;

				ByteString paqueteString;


				while (bytes_faltantes > 0 ){
					//armo bloque a pasar
					if (bytes_faltantes > 1024) {
						end = start + 1024;
					}else{
						end = start + bytes_faltantes;
					}
					
					buffer_datos.substring(start, end).copyTo(paquete);

					if (paquete == null) {
						paqueteString = ByteString.EMPTY;
					} else {
						paqueteString = ByteString.copyFrom(paquete);
					}

					//Genera la consulta a hacer al server
					FtpServiceOuterClass.WriteRequest write_request =
						FtpServiceOuterClass.WriteRequest.newBuilder()
							.setArchivoDatos(paqueteString)
							.setNombreArchivo(arch)
							.build();

					//Envia los datos al request al server
					FtpServiceOuterClass.WriteResponse cant_escritos = 
						stub.write(write_request);

					start += cant_escritos.getCantEscritos();

					bytes_enviados += cant_escritos.getCantEscritos();

					bytes_faltantes -= cant_escritos.getCantEscritos();

					System.out.println("La cantidad de bytes escritos leídos fue:" + cant_escritos.getCantEscritos());
				}
					


				 
		
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

	private static File getFileFromResourceAsStream(String fileName) throws URISyntaxException  {

		URL resource = Client.class.getClassLoader().getResource(fileName);

		if(resource == null)
			return null;
		else
			return new File(resource.toURI());
	}

	// Función para leer los bytes desde un archivo dado una posición y cantidad de bytes
    private static byte[] leerBytesDesdeArchivo(File archivo, int pos, int cant_leer) throws IOException {
        byte[] datosDelArchivo = new byte[cant_leer];
        java.io.RandomAccessFile raf = new java.io.RandomAccessFile(archivo, "r");

        try {
            raf.seek(pos);
            raf.readFully(datosDelArchivo);
        } finally {
            raf.close();
        }

        return datosDelArchivo;
    }
}