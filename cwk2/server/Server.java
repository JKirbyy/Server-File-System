import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class Server
{

    public void runServer () throws IOException
    {
      System.out.println("Server running...");
      ServerSocket server = null;
      ExecutorService executor = null;
      try
      {
          server = new ServerSocket(8888);
      }

      catch (IOException e)
      {
          System.err.println("Could not listen on port: 8888.");
          System.exit(-1);
      }

		  executor = Executors.newFixedThreadPool(10);

		while(true)
		{
      Socket client = server.accept();
			executor.submit(new ClientHandler(client));
		}

  }

    public static void main(String[] args)
    {
      Server serv = new Server();
      try
      {
      	serv.runServer();
      }
      catch (IOException e)
      {
      	System.out.println("Error running server.");
      }
    }
}
