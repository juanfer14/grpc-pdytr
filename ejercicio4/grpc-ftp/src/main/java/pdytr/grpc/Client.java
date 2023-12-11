package pdytr.grpc;

// GRPC
import io.grpc.*;
import pdytr.grpc.FtpServiceOuterClass.FtpResponseRead;

// UNIT
import java.util.concurrent.TimeUnit;

// BYTEBUFFER
import java.nio.ByteBuffer;
import com.google.protobuf.ByteString;

//ARCHIVOS
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.nio.charset.StandardCharsets;

// ENLACES
import java.net.URI;
import java.net.URL;

// EXCEPCIONES
import java.io.IOException;
import java.net.URISyntaxException;

// EXP.REG
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Client
{
	private static final int BLOQUE = 1024;

	private static URL getFileResources(String fileName){
		return Client.class.getClassLoader().getResource(fileName);
	}
	
	private static File getFileFromResourceAsStream(String fileName) throws URISyntaxException {
		URL resource = getFileResources(fileName);
		
		if (resource == null)
			return null;
		else
			return new File(resource.toURI());
	}

	private static boolean archivoValido(String nombre){
		// Declaro el patron
		String patron = ".*\\.(jpg|JPG|gif|GIF|doc|DOC|pdf|PDF|mp3|MP3|mp4|MP4)$";

        // Compilar el patrón
        Pattern pattern = Pattern.compile(patron);

		// Verificar si el nombre del archivo coincide con el patrón
		return pattern.matcher(nombre).matches();
	}

	private static void read(String n, int deadlineMs, FtpServiceGrpc.FtpServiceBlockingStub stub){
		// NOMBRE DEL ARCHIVO
		String nombreArchivo = n;

		// PREGUNTO AL SERVIDOR EL TAM. DEL ARCHIVO
		FtpServiceOuterClass.FtpRequestSize requestSize =
			FtpServiceOuterClass.FtpRequestSize.newBuilder()
				.setName(nombreArchivo)
				.build();

		// RECIBO LA RESPUESTA DEL TAM. DEL ARCHIVO
		FtpServiceOuterClass.FtpResponseSize responseSize = stub
			.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS)
			.ask(requestSize);


		// POSICION DESDE DONDE SE COMIENZA A LEER
		int offset = 0;
		// BYTES QUE SE DEBEN LEER
		long bytes_leer  = responseSize.getBytes();

		// SI EL ARCHIVO NO EXISTE (LENGTH == 0), SE TERMINA 
		// LA EJECUCION DEL PROCESO, YA QUE NO EXISTE EL ARCHIVO EN EL SERVIDOR
		if(bytes_leer <= 0){
			System.err.println("El archivo a leer no existe");
			System.exit(1);
		}


		// SE CREA UN ARCHIVO, A MODO DE EJEMPLO, PARA TENER UN FEEDBACK DE LA LECTURA
		int punto = nombreArchivo.indexOf(".");
		String nombre = nombreArchivo.substring(0, punto);
		String extension = nombreArchivo.substring(punto);
		String nombreCopia = nombre + "-copia" + extension;
		System.out.println(nombreCopia);


		// ABRO EL ARCHIVO
		try {
			File file = getFileFromResourceAsStream(nombreCopia);
			
			// SI EL ARCHIVO NO EXITE, SE LO CREA
			if(file == null){
				URL resources = getFileResources(".");
				file = new File(resources.toURI().getPath() + nombreCopia);
			}

			// IMPRIMO EL PATH ABSOLUTO
			System.out.println(file.getAbsolutePath());


			System.out.println("BYTES A LEER: " + bytes_leer);

			// INSTANCIO UN FileOutputStream, PARA ESCRIBIR EN EL ARCHIVO DE COPIA
			FileOutputStream fos = new FileOutputStream(file);
		
			// Finally, make the call using the stub
			while(bytes_leer > 0){
				FtpServiceOuterClass.FtpRequestRead requestRead =
					FtpServiceOuterClass.FtpRequestRead.newBuilder()
					.setName(nombreArchivo)
					.setPosition(offset)
					.setBytes(bytes_leer)
					.build();

				FtpServiceOuterClass.FtpResponseRead responseRead = stub
					.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS)
					.read(requestRead);

				int leidos = (int) responseRead.getBytesReaded();
				System.out.println("BYTES LEIDOS: " + leidos);

				if(leidos != -1){
					fos.write(responseRead.getData().toByteArray());
					offset += leidos;
					bytes_leer -= leidos;
				}
				else
					bytes_leer = 0;
					
				System.out.println("BYTES QUE FALTAN PARA LEER: " + bytes_leer);
				
			}
			fos.close();

		} catch(URISyntaxException e){
			// ERROR LANZADO AL ABRIR EL ARCHIVO
			System.err.println("Error en el enlace de busqueda: " + e.getMessage());
		} catch(FileNotFoundException e){
			// ERROR LANZADO AL INSTANCIAR UN FOS
			System.err.println("Error el archivo no ha sido encontrado: " + e.getMessage());
		} catch(IOException e){
			// ERROR AL REALIZAR EL WRITE EN EL ARCHIVO
			System.err.println("Error al escribir en el archivo: " + e.getMessage());
		}
		
		

	}

	public static void write(String n, FtpServiceGrpc.FtpServiceBlockingStub stub){
		//leo las variables de la consulta
		String arch = n;
		
		// Obtener los datos del archivo
		byte[] datosDelArchivo = null;

		//Lee el archivo del directorio local
		try {
			// SUPONEMOS, ALEGREMENTE QUE EL ARCHIVO EXISTE, Y ESTA EN SRC/MAIN/RESOURCES
			File file = getFileFromResourceAsStream(arch);
			int cant_escribir = 0;
			if(file != null)
				cant_escribir = (int) file.length();

			try (DataInputStream dataRead = new DataInputStream(new FileInputStream(file))) {
				// Lee los datos del archivo y los almacena en el arreglo datosDelArchivo
				datosDelArchivo = new byte[cant_escribir];
				dataRead.read(datosDelArchivo);
			}	
		} catch (URISyntaxException e) {
			System.err.println("Error de lectura del archivo: " + e.getMessage()); 
			return;
		} catch(IOException e) {
			System.err.println("Error al instanciar el DataInputStream: " + e.getMessage());
		}

		ByteString buffer_datos;
		ByteString paqueteString;

		if (datosDelArchivo == null) {
			buffer_datos = ByteString.EMPTY;
		} else {
			System.out.println("Cantidad de datos a enviar: "+ datosDelArchivo.length+" bytes");
			buffer_datos = ByteString.copyFrom(datosDelArchivo);
			//Imprimir los datos del ByteString
			//System.out.println("Datos leiodos desde el directorio local: " + buffer_datos.toStringUtf8()+"\n");
		}

		int bytes_faltantes = buffer_datos.size();
		int start = 0;
		int end = 0;
		
		while (bytes_faltantes > 0 ){
			//armo bloque a pasar
			if (bytes_faltantes > BLOQUE) {
				end = start + BLOQUE;
			}else{
				end = start + bytes_faltantes;
			}
			paqueteString = buffer_datos.substring(start, end);

			System.out.println("   # Tamaño del paquete a enviar: "+paqueteString.size()+ " bytes ");

			//Genera la consulta a hacer al server
			FtpServiceOuterClass.WriteRequest write_request =
				FtpServiceOuterClass.WriteRequest.newBuilder()
					.setArchivoDatos(paqueteString)
					.setNombreArchivo(arch)
					.build();

			System.out.println("Enviando...");
			
			//Envia los datos al request al server
			FtpServiceOuterClass.WriteResponse cant_escritos = 
				stub.write(write_request);

			int bytes_escritos = cant_escritos.getCantEscritos();
			if(bytes_escritos != -1){
				start += bytes_escritos;
				bytes_faltantes -= bytes_escritos;
				System.out.println("La cantidad de bytes escritos fueron: " + cant_escritos.getCantEscritos());
				System.out.println("Faltan escribir: " + bytes_faltantes);
			}
			else
				bytes_faltantes = 0;
		}	
	}

    public static void main( String[] args ) throws Exception
    {

		String operacion = args[0];
		String nombreArchivo = args[1];
		System.out.println(operacion);
		if (
			(args.length != 2) ||
    		(!operacion.equals("read") && !operacion.equals("write")) ||
			(!archivoValido(nombreArchivo))
		)
		{
			System.out.println("2 arguments needed: write/read name");
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

			switch(operacion){
				case "read":
					read(nombreArchivo, deadlineMs, stub);
					break;
				case "write":
					write(nombreArchivo, stub);
					break;
			}
					
			// A Channel should be shutdown before stopping the process.
			channel.shutdownNow();
			
		}
		catch (Exception e){
			System.err.println("Se produjo una excepcion: en el cliete: " + e.getMessage());
			System.err.println("Se procede a cerrar el canal...");
			channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);

		}
    }
}