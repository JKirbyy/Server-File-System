import java.io.*;
public class ListStuff
{
  public static void main (String[] args)
  {
    File folder = new File("/serverFiles");
    File[] listOfFiles = folder.listFiles();
    int length = 0;
    if (listOfFiles.length == null)
    {
        System.out.println("no folder found");
    }
    else
    {
      length += listOfFiles.length;
      System.out.println(length);
    }
    /*for (int i = 0; i < listOfFiles.length; i++)
    {
      System.out.println(listOfFiles[i].getName());
    }*/
  }
}
