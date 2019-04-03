import java.net.*;
import java.nio.file.Files;
import java.io.*;
import java.util.*;

public class ClientHandler extends Thread
{
    private Socket socket = null; //= null; ???
    private String fileDir;

    public ClientHandler()
    {
		super("ClientHandler");
		String dir = System.getProperty("user.dir"); //check if server end works
		this.fileDir = dir.concat("/serverFiles"); //server should be reset if file system moves
    }

    public ClientHandler(Socket socket)
    {
		super("ClientHandler");
		this.socket = socket;
		String dir = System.getProperty("user.dir");
		this.fileDir = dir.concat("/serverFiles"); //server should be reset if file system moves
    }

    private File[] getFileList() //raw array not reccomended!
    {
      //System.out.println(System.getProperty("user.dir"));
      //System.out.println(fileDir);
	  	File folder = new File(fileDir);
	    File[] listOfFiles = folder.listFiles();
	    int length = 0;
	    //switch // switch cases

	    if (listOfFiles == null) //error exception
	    {
	      System.out.println("no folder found"); //throw no files found excption!!
	    }

	    /*else
	    {
	      length += listOfFiles.length;
	      System.out.println(length);
	    }

	    for (int i = 0; i < listOfFiles.length; i++)
	    {
	    	System.out.println(listOfFiles[i].getName());
	    }*/

	    return listOfFiles; //fix
    }

   /* private byte[] getFile(String fileName) //check if file exists
    {
    	String slash = "/";
    	fileName = slash.concat(fileName);
    	System.out.println(fileDir.concat(fileName));
    	FileReader file = new FileReader(fileDir.concat(fileName));

    	//String filePath = fileDir.concat(slash.concat(fileName)); //clean up
    	byte[] byteFile = Files.readAllBytes(new File(fileDir.concat(fileName)).toPath()); //clean up
    	return byteFile;
    }*/

    private void placeFiles(byte[] file, String fileName) throws IOException
    {
      //change NAME and check for file existing
    	String slash = "/";
    	fileName = slash.concat(fileName);
    	System.out.println(fileDir.concat(fileName));
    	//String filePath = fileDir.concat(slash.concat(fileName)); //clean up
    	File newFile = new File(fileDir.concat(slash.concat(fileName))); //check
    	System.out.println(newFile.getName());
  	  FileOutputStream fileOutput = new FileOutputStream(newFile, false); //false to overwrite

  	  fileOutput.write(file);
  	  fileOutput.flush(); //check if required
  	  fileOutput.close();
    }

    private void placeFile(String fileName) throws IOException
    {
    	String slash = "/";
    	fileName = slash.concat(fileName);
    	System.out.println(fileDir.concat(fileName));
    	//String filePath = fileDir.concat(slash.concat(fileName)); //clean up
    	byte[] byteFile = Files.readAllBytes(new File(fileDir.concat(fileName)).toPath()); //clean up
    	File newFile = new File(fileDir.concat(slash.concat("test.jpg")));
    	System.out.println(newFile.getName());
  	  	FileOutputStream fileOutput = new FileOutputStream(newFile);

  	  	fileOutput.write(byteFile);
  	  	fileOutput.flush(); //check if required
  	  	fileOutput.close();
    }


    public void run()
    {

		try
		{
        // chain a writing stream
          //DataOutputStream socketOutput= new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        // chain a reading stream
          DataInputStream socketInput = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
          DataOutputStream socketOutput= new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

	        InetAddress inet = socket.getInetAddress();
	        Date date = new Date();
          System.out.println("in thread");
          int commandSize = socketInput.readInt();
          //System.out.println(Integer.toString(commandSize));
          byte[] byteCommand = new byte[commandSize];
          socketInput.read(byteCommand);
          String command = new String(byteCommand);
          System.out.println(command);
          System.out.println(byteCommand.length);

          if (command.equals("list"))
          {
            File[] directory = getFileList();
            socketOutput.writeInt(directory.length); //indicates how many iterations client will have to do
            for (int i = 0; i < directory.length; i++)
            {
              byte[] name = directory[i].getName().getBytes();
              socketOutput.writeInt(name.length);
              socketOutput.write(name);
            }
            socketOutput.close();

            //System.exit(1);
          }

          else if (command.equals("put"))
          {
            int nameSize = socketInput.readInt();
            byte[] byteName = new byte[nameSize];
            socketInput.read(byteName);
            String fileName = new String(byteName);
            System.out.println(fileName);

            int fileSize = socketInput.readInt();
            byte[] byteFile= new byte[fileSize];
            socketInput.read(byteFile);
            placeFiles(byteFile, fileName);
          }

          else if (command.equals("get"))
          {
            int nameSize = socketInput.readInt();
            byte[] byteName = new byte[nameSize];
            socketInput.read(byteName);
            String fileName = new String(byteName);
            System.out.println(fileName);

            String slash = "/";
          	fileName = slash.concat(fileName);
          	System.out.println(fileDir.concat(fileName));
            //if file doesnt exist!!!!!!!!!
          	File file = new File(fileDir.concat(fileName));
            byte[] byteFile = Files.readAllBytes(file.toPath()); //clean up
            socketOutput.writeInt(byteFile.length);
            socketOutput.write(byteFile);
            socketOutput.close();

          }


	        /*String argument = in.readLine();//check correct usage
	        System.out.println(argument);
	        System.out.println("\nDate " + date.toString() );
	        System.out.println("Connection made from " + inet.getHostName());
	        System.out.println(argument);

	        if (argument.equals("list"))
	        {
	        	File[] fileList = getFileList();
	        	for (int i = 0; i < fileList.length; i++)
	        	{
	        		out.println(fileList[i].getName());
	        	}
            out.close();
	        }*/

	        //placeFile("SWJTU.jpg");
			  //out.close();
		    //in.close();

		    //socket.close();

		}

		catch (IOException e)
		{
		    e.printStackTrace();
		}
    }

    public static void main(String[] args)
    {
    	ClientHandler thr = new ClientHandler();
    	thr.run();
    }
}
