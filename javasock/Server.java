/*
 * EchoServer.java
 * Just receives some data and sends back a "message" to a client
 *
 * Usage:
 * java Server port
 */

import java.io.*;
import java.net.*;

public class Server
{
  public static void main(String[] args) throws IOException
  {
    /* Check the number of command line parameters */
    if (
        (args.length != 2) ||
        (Integer.valueOf(args[0]) <= 0) ||
        (Integer.valueOf(args[1]) <= 0)

    )
    {
      System.out.println("2 arguments needed: port size_buffer");
      System.exit(1);
    }
    
    int port = Integer.valueOf(args[0]);
    int size_buffer = Integer.valueOf(args[1]);
 

    /* The server socket */
    ServerSocket serverSocket = null;    
    try
    {
      serverSocket = new ServerSocket(port);
    } 
    catch (Exception e)
    {
      System.out.println("Error on server socket");
      System.exit(1);
    }

    /* The socket to be created on the connection with the client */
    Socket connected_socket = null;

    try /* To wait for a connection with a client */
    {
      connected_socket = serverSocket.accept();
    }
    catch (IOException e)
    {
      System.err.println("Error on Accept");
      System.exit(1);
    }

    /* Streams from/to client */
    DataInputStream fromclient;
    DataOutputStream toclient;

    /* Get the I/O streams from the connected socket */
    fromclient = new DataInputStream(connected_socket.getInputStream());
    toclient   = new DataOutputStream(connected_socket.getOutputStream());

    /* Buffer to use with communications (and its length) */
    byte[] buffer = new byte[size_buffer];
   
    /* INICIO DE COMUNICACION */

    /* 
	Recv data from client 
	VERIFICO QUE SE LEAN TODOS LOS BYTES
    */
    int leidos = 0;
    while(leidos != size_buffer)
	leidos += fromclient.read(buffer);

    /* 
	Send the bytes back 
	VERIFICO QUE SE ESCRIBAN TODOS LOS BYTES
    */
    int escritos = toclient.size();
    while(escritos != size_buffer){
    	toclient.write(buffer, 0, size_buffer);
        escritos += toclient.size();
    }
    

    /* FIN COMUNICACION */


    /* Close everything related to the client connection */
    fromclient.close();
    toclient.close();
    connected_socket.close();
    serverSocket.close();
  }
}
