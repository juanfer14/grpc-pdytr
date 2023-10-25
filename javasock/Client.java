/*
 * Client.java
 * Just sends stdin read data to and receives back some data from the server
 *
 * usage:
 * java Client serverhostname port
 */

import java.io.*;
import java.net.*;

public class Client
{
  public static void main(String[] args) throws IOException
  {
    /* Check the number of command line parameters */
    if ( 
      (args.length != 3) || 
      (Integer.valueOf(args[1]) <= 0) ||  
      (Integer.valueOf(args[2]) <= 0)
    )
    {
      System.out.println("3 arguments needed: serverhostname port size_buffer");
      System.exit(1);
    }

    /* OBTENGO LOS VALORES PASADOS POR ARGUMENTO */
    int port = Integer.valueOf(args[1]);
    int size_buffer = Integer.valueOf(args[2]);

    /* The socket to connect to the echo server */
    Socket socketwithserver = null;

    try /* Connection with the server */
    { 
      socketwithserver = new Socket(args[0], port);
    }
    catch (Exception e)
    {
      System.out.println("ERROR connecting");
      System.exit(1);
    } 

    /* Streams from/to server */
    DataInputStream  fromserver;
    DataOutputStream toserver;

    /* Streams for I/O through the connected socket */
    fromserver = new DataInputStream(socketwithserver.getInputStream());
    toserver   = new DataOutputStream(socketwithserver.getOutputStream());

    /* Buffer to use with communications (and its length) */
    byte[] buffer = new byte[size_buffer];

    /* LLENO EL BUFFER */
    for (int i = 0; i < buffer.length; i++) {
    	buffer[i] = (byte) (i % 256);
    }
    
    /* COMIENZO A TOMAR EL TIEMPO */
    long tiempo_antes = System.nanoTime();

    /* 
	  Send read data to server 
	  VERIFICO QUE SE ESCRIBAN TODOS LOS BYTES
    */
    int escritos = toserver.size();
    while(escritos != size_buffer){
        toserver.write(buffer, 0, buffer.length);
        escritos += toserver.size();
    }
    
    /* Recv data back from server (get space) */
    buffer = new byte[size_buffer];

    /* VERIFICO QUE SE LEAN TODOS LOS BYTES */
    int leidos = fromserver.read(buffer);
    while(leidos != size_buffer)
	    leidos += fromserver.read(buffer);


    /* FINALIZO DE TOMAR EL TIEMPO */
    long tiempo_despues = System.nanoTime();

    /* SACO LA DIFERENCIA DE MICROSEGUNDOS Y LO PASO A NANOSEGUNDOS */
    long tiempo_microseg = (tiempo_despues - tiempo_antes) / 1000;

    /* OBTENGO EL TIEMPO DE COMUNICACION DE AMBOS LADOS (CLIENTE Y SERVIDOR) */
    long tiempo_comunicacion = tiempo_microseg / 2;

    /* IMPRIMO EL TIEMPO DE COMUNICACION */
    System.out.println(tiempo_comunicacion);
    
    /* FINALIZO LA COMUNICACION */
    fromserver.close();
    toserver.close();
    socketwithserver.close();
  }
}
