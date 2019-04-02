import java.io.*;

public class ListStuff
{


  public static void main (String[] args)
  {
	  	String dir = System.getProperty("user.dir"); //make in server consructor
	  	String fileDir = dir.concat("/server/serverFiles");
	  	System.out.println(fileDir);
	  	
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
  }
  
}