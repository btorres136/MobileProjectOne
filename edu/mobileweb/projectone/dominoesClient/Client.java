package edu.mobileweb.projectone.dominoesClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.StringTokenizer;

import com.sun.org.apache.bcel.internal.classfile.Code;

import edu.mobileweb.projectone.transferCodes.Codes;

/**
 * <h3>Client Class</h3>
 * @author Brian D. Torres Alvarado
 * @author Joel E. Martinez
 */
public class Client {
    //List of valid commmands on the protocol
	private enum ValidCommands	{
	    seeTable, putPice, cantBePlay, exit 
	}
	
	//Current command
    private		 int 				currentCommand;
    
    // Data stream and output streams for data transfer
	private 	DataInputStream 	socketInputStream;
	private 	DataOutputStream 	socketOutputStream;
	
	// Connection parameters
	private 	String 				serverAddressStr;
    private		int 				serverPort;

    public String getServerAddresStr(){
        return this.serverAddressStr;
    }

    public int getServerPort(){
        return this.serverPort;
    }
    
    public static void main(String []args){
        Client client = new Client("127.0.0.1", 1234);
        

    }

    public Client(String serveAddressStr, int serverPort) {
        this.serverAddressStr = serveAddressStr;
        this.serverPort=serverPort;
    }
    

    public void play(){

        String arguments = "";
        Scanner reader = new Scanner(System.in);
        try {
            //ServerSocket server = new ServerSocket();
            InetAddress serveAddress = InetAddress.getByName(serverAddressStr);
            Socket socket = new Socket(serveAddress,serverPort);
            socketInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            socketOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            System.out.println("Connected to the game...");

            do {
                arguments = readCommands(reader);

                switch(currentCommand)
                {
                    case Codes.SEETABLE:    seeTable(arguments);
                                            break;

                    case Codes.PUTPIECE:    putPice(arguments);
                                            break;

                    case Codes.CANTBEPLAY:  cantBePlay();
                                            break;

                    case Codes.CLOSECONNECTION: exit();
                                                break;
                    case Codes.WRONGCOMMAND:
                                        System.out.println(arguments + " is not a valid command");
                                        break;

                }


            } while (currentCommand != Codes.CLOSECONNECTION);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readCommands(Scanner reader){

        System.out.println(".......................... \n>");

        LinkedList<String> commandList = new LinkedList<String>();
        String command = reader.nextLine();
        String result = "";

        StringTokenizer commandStr = new StringTokenizer(command);

        while (commandStr.hasMoreTokens()) {
            commandList.add(commandStr.nextToken());
        }


        try {
            switch(ValidCommands.valueOf(commandList.get(0))){
                case seeTable: if(commandList.size()>1){
                                currentCommand = Codes.SEETABLE;
                                result = commandList.get(1);
                                } else {
                                    currentCommand = Codes.WRONGCOMMAND;
                                }
                                break;
                case putPice: if(commandList.size()>1){
                                currentCommand = Codes.PUTPIECE;
                                result = commandList.get(1);
                                } else {
                                    currentCommand = Codes.WRONGCOMMAND;
                                }
                                break;
                case cantBePlay: if(commandList.size()>1){
                                currentCommand = Codes.CANTBEPLAY;
                                result = commandList.get(1);
                                } else {
                                    currentCommand = Codes.WRONGCOMMAND;
                                }
                                break;
                case exit:      currentCommand = Codes.CLOSECONNECTION;
                                break;
                default:        currentCommand = Codes.WRONGCOMMAND;
                                result = command;
                                break;

            }
        } catch (Exception e) {
            currentCommand = Codes.WRONGCOMMAND;
            result = command;
            e.printStackTrace();
        }       

        return result;
    }

    public void exit(){
        try {
            socketOutputStream.writeInt(Codes.CLOSECONNECTION);
            socketOutputStream.flush();

            int read = socketInputStream.readInt();
            System.out.println("Thanks for playing " + read);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void seeTable(String arguments){
        byte[] buffer = new byte[Codes.BUFFER_SIZE]; 
        try {
            int read = 0;
            int totalRead =0;

            // Send the comand
            System.out.println("This is the latest table...");
            socketOutputStream.writeInt(Codes.SEETABLE);
            socketOutputStream.flush();

            //Wating for ok

            read = socketInputStream.readInt(); 

            if (read == Codes.OK){
                //Recive the pice list string of the table.
                String tablePicesStr = socketInputStream.readUTF();
                System.out.println("Table... \n" + tablePicesStr);
            }
            else{
                System.out.println("Something hapended, error:" + read);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void putPice(String arguments){}

    public void cantBePlay(){}




}