import java.net.*;
import java.nio.file.Files;
import java.io.*;
import java.util.*;

public class ClientHandler extends Thread
{
    private Socket socket = null; 
    private String fileDir;
    DataInputStream socketInput;
    DataOutputStream socketOutput;
    BufferedWriter log;

    public ClientHandler(Socket socket)
    {
  		super("ClientHandler");
  		this.socket = socket;
  		String dir = System.getProperty("user.dir");
  		this.fileDir = dir.concat("/serverFiles"); //server should be reset if file system moves
    }

    private void log (String command) //adds entry to log
    {
      InetAddress inet = socket.getInetAddress();
      Date date = new Date();
      String entry = date.toString().concat(":").concat(inet.toString()).concat(":").concat("command");

      try
      {
        log.write(entry);
        log.newLine();
        log.flush();
      }

      catch (IOException e)
      {
        System.out.println("Log failed.");
      }
    }

    private File[] getFileList() //returns list of files in server directory
    {
	  	File folder = new File(fileDir);
	    File[] listOfFiles = folder.listFiles();
	    int length = 0;

	    if (listOfFiles == null) //error exception
	    {
	      System.out.println("no folder found"); //throw no files found excption!!
	    }
	    return listOfFiles;
    }
    //gets a file by name
    private byte[] getFile(String fileName) throws IOException, FileNotFoundException
    {
      File file = new File(fileDir.concat(fileName));
      byte[] byteFile = Files.readAllBytes(file.toPath()); //clean up
      return byteFile;
    }
    //places a file in serverFiles
    private void placeFile(byte[] file, String fileName) throws IOException
    {
    	String slash = "/";
    	fileName = slash.concat(fileName);
    	File newFile = new File(fileDir.concat(slash.concat(fileName)));
    	System.out.println(newFile.getName());
  	  FileOutputStream fileOutput = new FileOutputStream(newFile, false); //false to overwrite the file if it already exists

  	  fileOutput.write(file);
  	  fileOutput.flush(); //needed?
  	  fileOutput.close();
    }

    public void run()
    {
      try
      {
        socketInput = new DataInputStream(new BufferedInputStream(socket.getInputStream())); //data stream for sake of 'polymorphism' with file types
        socketOutput = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        log = new BufferedWriter(new FileWriter(System.getProperty("user.dir").concat("/log.txt"), true));
      }

      catch (IOException e)
      {
        throw new RuntimeException("Failed to establish socket connection.");
      }

      String command = null;
      try
      {
        int commandSize = socketInput.readInt();
        byte[] byteCommand = new byte[commandSize];
        socketInput.read(byteCommand);
        command = new String(byteCommand);
      }

      catch (IOException e)
      {
        System.out.println("Client-Server communication failure.");
      }

      if (command.equals("list"))
      {
        log("list");
        File[] directory;
        directory = getFileList();

        try
        {
          socketOutput.writeInt(directory.length); //indicates how many iterations client will have to do to display list
          for (int i = 0; i < directory.length; i++)
          {
            byte[] name = directory[i].getName().getBytes();
            socketOutput.writeInt(name.length); //specify length of bytes to read for each filename
            socketOutput.write(name);
          }
          socketOutput.flush();
        }

        catch (IOException e)
        {
          System.out.println("Client-Server communication failure.");
        }
      }

      else if (command.equals("put"))
      {
        log("put");
        String fileName = null;
        byte[] byteFile = null;
        try
        {
          //get file name
          int nameSize = socketInput.readInt();
          byte[] byteName = new byte[nameSize];
          socketInput.read(byteName);
          fileName = new String(byteName);
          //get file
          int fileSize = socketInput.readInt();
          byteFile= new byte[fileSize];
          socketInput.read(byteFile);
        }

        catch (IOException e)
        {
          System.out.println("Client-Server communication failure.");
        }

        try
        {
          placeFile(byteFile, fileName);
        }

        catch (IOException e)
        {
          throw new RuntimeException("Server directory failure.");
        }
      }

      else if (command.equals("get"))
      {
        log("get");
        String fileName = null;
        try
        {
          //get file name
          int nameSize = socketInput.readInt();
          byte[] byteName = new byte[nameSize];
          socketInput.read(byteName);
          fileName = new String(byteName);

          String slash = "/";
        	fileName = slash.concat(fileName);
        }

        catch (IOException e)
        {
          System.out.println("Client-Server communication failure.");
        }

        try
        {
          File fileCheck = new File(fileDir.concat(fileName));
          if (fileCheck.exists())
          {
            byte[] byteFile = getFile(fileName);
            socketOutput.writeInt(byteFile.length);
            socketOutput.write(byteFile);
            socketOutput.flush();
          }

          else //if file doesnt exist then inform client
          {
            System.out.println("here");
            socketOutput.writeInt(-999);
            socketOutput.flush();
          }
        }

        catch (IOException e)
        {
          throw new RuntimeException("Server directory failure or file cannot be found.");
        }
      }

      else
      {
        throw new RuntimeException("Invalid command.");
      }

      try
      {
        socketOutput.close();
        socketInput.close();
      }
      catch (IOException e)
      {
        System.out.println("Error closing sockets");
      }
		}
}
