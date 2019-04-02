import java.io.*;
import java.net.*;

public class Client {
	//change names 
    private Socket kkSocket = null;
    private PrintWriter socketOutput = null;
    private BufferedReader socketInput = null;

    public void playKnockKnock() {

        try 
        {

            // try and create the socket
            kkSocket = new Socket( "localhost", 8888 );

            // chain a writing stream
            socketOutput = new PrintWriter(kkSocket.getOutputStream(), true);

            // chain a reading stream
            socketInput = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));

        } 
        
        catch (UnknownHostException e) 
        {
            System.err.println("Don't know about host.\n");
            System.exit(1);
        } 
        
        catch (IOException e) 
        {
            System.err.println("Couldn't get I/O for the connection to host.\n");
            System.exit(1);
        }

        // chain a reader from the keyboard
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String fromServer;
        String fromUser;

        // communication loop
        
        // read from server
        try {
          while ((fromServer = socketInput.readLine()) != null) {

              // echo server string
              System.out.println("Server: " + fromServer);
              if (fromServer.equals("Bye."))
                  break;
		   
              // client types in response 
              fromUser = stdIn.readLine();

    	      if (fromUser != null) {
                  // echo client string
                  System.out.println("Client: " + fromUser);
                  // write to server
                  socketOutput.println(fromUser);
              }
          }
          socketOutput.close();
          socketInput.close();
          stdIn.close();
          kkSocket.close();
        }
        catch (IOException e) {
            System.err.println("I/O exception during execution\n");
            System.exit(1);
        }
    }

    public static void main(String[] args) 
    {
      Client kkc = new Client();
      kkc.playKnockKnock();
    }

}