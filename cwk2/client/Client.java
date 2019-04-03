import java.io.*;
import java.net.*;
import java.nio.file.Files;

public class Client {
	//change names
    private Socket socket = null;
    private DataOutputStream socketOutput = null;
    private DataInputStream socketInput = null;

    public void playKnockKnock(String[] commands)
    {

        try
        {
            socket = new Socket( "localhost", 8888 );
            socketOutput= new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            socketInput = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
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
          //Clean UP
          try
          {
            if (commands[0].equals("list"))
            {
                byte[] list = commands[0].getBytes();
                System.out.println(" String Length: " + Integer.toString(list.length));
                System.out.println(commands[0]);
                socketOutput.writeInt(list.length);
                socketOutput.write(list);
                System.out.println("here");
                socketOutput.flush();
                int listSize = socketInput.readInt();
                System.out.println("unblocked");
                System.out.println("List size: " + Integer.toString(listSize));
                for (int i = 0; i < listSize; i++)
                {
                  int fileNameSize = socketInput.readInt();
                  byte[] byteFileName = new byte[fileNameSize];
                  socketInput.read(byteFileName);
                  String listItem = new String(byteFileName);
                  System.out.println(listItem);
                }
                socketOutput.close();
                System.exit(1);
            }

            else if (commands[0].equals("put")) //error handling on commands input
            {
              String dir = System.getProperty("user.dir");
              String fileLocation = dir.concat("/clientFiles");
              fileLocation = fileLocation.concat("/");
              fileLocation = fileLocation.concat(commands[1]);
              System.out.println(fileLocation);

              byte[] put = commands[0].getBytes();
              System.out.println(" String Length: " + Integer.toString(put.length));
              System.out.println(commands[0]);
              socketOutput.writeInt(put.length);
              socketOutput.write(put);

              File file = new File(fileLocation);
              byte[] byteFile = Files.readAllBytes(file.toPath());
              byte[] name = commands[1].getBytes();
              System.out.println("Name: " + commands[1]);
              System.out.println("Length: " + Integer.toString(name.length));
              socketOutput.writeInt(name.length);
              socketOutput.write(name);
              System.out.println("File Length: " + Integer.toString(byteFile.length));
              socketOutput.writeInt(byteFile.length);
              socketOutput.write(byteFile);
              socketOutput.close();
            }

            else if (commands[0].equals("get"))
            {
              byte[] get = commands[0].getBytes();
              System.out.println(" String Length: " + Integer.toString(get.length));
              System.out.println(commands[0]);
              socketOutput.writeInt(get.length);
              socketOutput.write(get);
              byte[] name = commands[1].getBytes();
              socketOutput.writeInt(name.length);
              socketOutput.write(name);
              socketOutput.flush();

              int fileSize = socketInput.readInt();
              byte[] byteFile= new byte[fileSize];
              socketInput.read(byteFile);
              if (byteFile.length > 0)
              {
                System.out.println("file recieved");
              }

              String dir = System.getProperty("user.dir");
              String fileLocation = dir.concat("/clientFiles");
              fileLocation = fileLocation.concat("/");
              fileLocation = fileLocation.concat(commands[1]);
              System.out.println(fileLocation);

              File newFile = new File(fileLocation); //check
            	System.out.println(newFile.getName());
          	  FileOutputStream fileOutput = new FileOutputStream(newFile, false); //false to overwrite

          	  fileOutput.write(byteFile);
          	  fileOutput.flush(); //check if required
          	  fileOutput.close();
              socketOutput.close();
            }
          }
          catch (IOException e)
          {
            System.out.println("File Not Found!"); //check
          }


        //socketOutput.println(commands[0]); //assuming only one command

          //socketOutput.close();
          //socketInput.close();
          //socket.close();
        /*catch (IOException e)
        {
            System.err.println("I/O exception during execution\n");
            System.exit(1);
        }*/

        /*String fromServer;
        try
        {
          while ((fromServer = socketInput.readLine()) != null) //check for what has been returned //while(true)
          {
            //fromServer = socketInput.readLine();
            System.out.println(fromServer);
          }
          socketOutput.close();
          socketInput.close();
          socket.close();
        }

        catch (IOException e)
        {
          System.out.println(e.getMessage());
          System.out.println("failed");
        }
        return;*/

        // chain a reader from the keyboard
        //BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        //String fromServer;
        //String fromUser;

        // communication loop

        // read from server
    }

    public static void main(String[] args)
    {
      Client kkc = new Client();
      kkc.playKnockKnock(args);
      Runtime.getRuntime().exit(1);
    }

}
