import java.io.*;
import java.net.*;
import java.nio.file.Files;

public class Client
{
    private Socket socket = null;
    private DataOutputStream socketOutput = null;
    private DataInputStream socketInput = null;

    public void runClient(String[] commands)
    {
        try
        {
            socket = new Socket( "localhost", 8888 );
            socketOutput= new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            socketInput = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        }

        catch (UnknownHostException e)
        {
            System.err.println("Host not found.");
            System.exit(1);
        }

        catch (IOException e)
        {
            System.err.println("Failed to connect to host.\n");
            System.exit(1);
        }

        try
        {
          if (commands[0].equals("list"))
          {
            //send list command
            byte[] list = commands[0].getBytes();
            socketOutput.writeInt(list.length);
            socketOutput.write(list);
            socketOutput.flush();
            //recieve list command
            int listSize = socketInput.readInt();
            for (int i = 0; i < listSize; i++)
            {
              int fileNameSize = socketInput.readInt();
              byte[] byteFileName = new byte[fileNameSize];
              socketInput.read(byteFileName);
              String listItem = new String(byteFileName);
              System.out.println(listItem);
            }

            socketOutput.flush();
            System.exit(1);
          }

          else if (commands[0].equals("put"))
          {
            if (commands.length < 2) //checks if filename given
            {
              System.out.println("Invalid command.");
              System.exit(1);
            }
            try
            {
              //get working directory
              String dir = System.getProperty("user.dir");
              String fileLocation = dir.concat("/clientFiles");
              fileLocation = fileLocation.concat("/");
              fileLocation = fileLocation.concat(commands[1]);

              byte[] put = commands[0].getBytes();
              socketOutput.writeInt(put.length);
              socketOutput.write(put);

            //send file
              File file = new File(fileLocation);
              if (file.exists() == false)
              {
                System.out.println("File not found."); //exit proram if file not found locally
                System.exit(1);
              }

              byte[] byteFile = Files.readAllBytes(file.toPath());
              byte[] name = commands[1].getBytes();
              socketOutput.writeInt(name.length);
              socketOutput.write(name);
              socketOutput.writeInt(byteFile.length);
              socketOutput.write(byteFile);
              socketOutput.flush();
              System.out.println("Success.");
            }

            catch (FileNotFoundException f)
            {
              System.out.println("File not found.");
            }

            catch (IOException e)
            {
              System.out.println("Server communication error.");
            }
          }

          else if (commands[0].equals("get"))
          {
            if (commands.length < 2)
            {
              System.out.println("Invalid command.");
              System.exit(1);
            }

            byte[] get = commands[0].getBytes();
            socketOutput.writeInt(get.length);
            socketOutput.write(get);

            byte[] name = commands[1].getBytes();
            socketOutput.writeInt(name.length);
            socketOutput.write(name);
            socketOutput.flush();

            int fileSize = socketInput.readInt();
            if (fileSize == -999) //exits program if file not on server
            {
              System.out.println("File not found. Use list command to see available files.");
              System.exit(1);
            }

            byte[] byteFile= new byte[fileSize];
            socketInput.read(byteFile);
            if (byteFile.length > 0)
            {
              System.out.println("File recieved");
            }

            String dir = System.getProperty("user.dir");
            String fileLocation = dir.concat("/clientFiles");
            fileLocation = fileLocation.concat("/");
            fileLocation = fileLocation.concat(commands[1]);

            File newFile = new File(fileLocation);
          	System.out.println(newFile.getName());
        	  FileOutputStream fileOutput = new FileOutputStream(newFile, false); //false to overwrite

        	  fileOutput.write(byteFile);
        	  fileOutput.flush();
        	  fileOutput.close();
            socketOutput.flush();
          }

          else
          {
            System.out.println("Invalid command.");
          }
          socketOutput.close();
          socketInput.close();
        }
        catch (IOException e)
        {
          System.out.println("Server communication error.");
        }
    }

    public static void main(String[] args)
    {
      Client cli = new Client();
      cli.runClient(args);
      Runtime.getRuntime().exit(1);
    }
}
