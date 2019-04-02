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
		System.out.println(dir);
		this.fileDir = dir.concat("/server/serverFiles"); //server should be reset if file system moves
    }
    
    public ClientHandler(Socket socket) 
    {
		super("ClientHandler");
		this.socket = socket;
		String dir = System.getProperty("user.dir"); 
		this.fileDir = dir.concat("/server/serverFiles"); //server should be reset if file system moves
    }
    
    private File[] getFileList() //raw array not reccomended!
    {
	  	File folder = new File(fileDir);
	    File[] listOfFiles = folder.listFiles();
	    int length = 0;
	    //switch // switch cases
	    
	    if (listOfFiles == null) //error exception
	    {
	      System.out.println("no folder found");
	    }
	    
	    else
	    {
	      length += listOfFiles.length;
	      System.out.println(length);
	    }
    
	    for (int i = 0; i < listOfFiles.length; i++)
	    {
	    	System.out.println(listOfFiles[i].getName());
	    }
	    
	    return listOfFiles; //fix
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
			
		    /*PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	
	        InetAddress inet = socket.getInetAddress();
	        Date date = new Date();

	        System.out.println("\nDate " + date.toString() );
	        System.out.println("Connection made from " + inet.getHostName());*/
	        placeFile("SWJTU.jpg");
		   /* out.close();
		    in.close();
		    
		    socket.close();*/
	
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