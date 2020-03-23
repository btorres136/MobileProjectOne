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

//
//import com.sun.org.apache.bcel.internal.classfile.Code;

import edu.mobileweb.projectone.dominoesServer.Piece;
import edu.mobileweb.projectone.dominoesServer.PieceList;
import edu.mobileweb.projectone.transferCodes.Codes;
//import jdk.internal.util.xml.impl.Input;

/**
 * <h3>Client Class</h3>
 * @author Brian D. Torres Alvarado
 * @author Joel E. Martinez
 */
public class Client {
    //List of valid commmands on the protocol
	private enum ValidCommands	{
	    seeTable, putPiece, cantBePlay, exit 
	}
	
	//Current command
    private		 int 				currentCommand;
    
    // Data stream and output streams for data transfer
	private 	DataInputStream 	socketInputStream;
	private 	DataOutputStream 	socketOutputStream;
	
	// Connection parameters
	private 	String 				serverAddressStr;
    private		int 				serverPort;

    private     PieceList           pieceList = new PieceList();

    public String getServerAddresStr(){
        return this.serverAddressStr;
    }

    public int getServerPort(){
        return this.serverPort;
    }
    
    public static void main(String []args){
        Client client = new Client("127.0.0.1", 1234);

        System.out.println("Connecting to the game");

        client.play();
    }

    public Client(String serveAddressStr, int serverPort) {
        this.serverAddressStr = serveAddressStr;
        this.serverPort=serverPort;
    }
    

    public void play(){

        
        int readComand;
        try {
            //ServerSocket server = new ServerSocket();
            InetAddress serveAddress = InetAddress.getByName(serverAddressStr);
            Socket socket = new Socket(serveAddress,serverPort);
            socketInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            socketOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            System.out.println("Connected to the game...");
            
            //Wait for pieces
            do {
                readComand = socketInputStream.readInt();
                System.out.println("Read comand client: " + readComand);
                switch(readComand)
                {
                    case Codes.SENDPIECE:   this.sendPice();
                                            break;
                    case Codes.TURN:        this.turn();
                                            break;
                    case Codes.CLOSECONNECTION: this.exit();
                                                break;
                    case Codes.WRONGCOMMAND:
                                        System.out.println("Command is not valid.");
                                        break;
                }
            } while (readComand != Codes.CLOSECONNECTION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void putPiece(String arguments){
        byte[] buffer = new byte[Codes.BUFFER_SIZE];
        int read; 
        Piece selectedPiece = new Piece();
        try {
            socketOutputStream.writeInt(Codes.PUTPIECE);
            socketOutputStream.flush();

            //wait for ok
            read = socketInputStream.readInt();

            if(read == Codes.OK){

                int  index = Integer.parseInt(arguments);
                selectedPiece = this.pieceList.getPiece(index - 1);
                selectedPiece.printPiece();
                socketOutputStream.write(selectedPiece.getPiece().getBytes());
                socketOutputStream.flush();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void turn(){
        System.out.println("Entered Turn in client");
        byte[] buffer = new byte[Codes.BUFFER_SIZE];
        int read;

        Scanner reader = new Scanner(System.in);
        String arguments = "";
        try {
            socketOutputStream.writeInt(Codes.OK);
            socketOutputStream.flush();

            read = socketInputStream.read(buffer);
            System.out.println(new String(buffer));

            arguments = readCommands(reader);
            
            switch(currentCommand){
                case Codes.PUTPIECE: 
                this.putPiece(arguments);
                break;
                case Codes.CLOSECONNECTION: exit();
                break;
                case Codes.WRONGCOMMAND: System.out.println(arguments + " is not a valid command");                    
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String readCommands(Scanner reader){

        System.out.println("Plays: \n 1.putPiece");
        
        

        System.out.println("This are your pieces: \n" + this.pieceList.getList());
        System.out.println("From left to right select your piece acording to number...");
        System.out.print(".......................... \n>");
        LinkedList<String> commandList = new LinkedList<String>();
        String command = reader.nextLine();
        String result = "";

        StringTokenizer commandStr = new StringTokenizer(command);

        while (commandStr.hasMoreTokens()) {
            //System.out.println(commandStr.nextToken());
            commandList.add(commandStr.nextToken());
        }


        try {
            switch(ValidCommands.valueOf(commandList.get(0))){
                case seeTable:  currentCommand = Codes.SEETABLE;
                                result = commandList.get(1);
                                break;
                case putPiece: currentCommand = Codes.PUTPIECE;
                                result = commandList.get(1);
                                break;
                case cantBePlay: currentCommand = Codes.CANTBEPLAY;
                                 result = commandList.get(1);
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
            System.out.println("Thanks for playing.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get the table
    public void seeTable(){
        byte[] buffer = new byte[Codes.BUFFER_SIZE]; 
        try {
            int read = 0;
            //int totalRead =0;

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
    /*This comand will work from server to client one time at the begining of the game 
      from the client side it will recive the 7 pices sent by the server and print them on 
      the terminal*/
    public void sendPice(){
        System.out.println("Enter the sendPiece command in the Client");
        byte[] buffer = new byte[Codes.BUFFER_SIZE];
        int read;
        String pieceList;
        PieceList list = new PieceList();
        try {

            socketOutputStream.writeInt(Codes.OK);
            socketOutputStream.flush();
            
            //recive pices and print
            read = socketInputStream.read(buffer);
            pieceList = new String(buffer);
            System.out.println("> Your pieces are: " + pieceList);
            this.pieceList = list.StrToPiceList(pieceList);
            System.out.println(this.pieceList.getList());
            
        } catch (Exception e) {
            System.out.print("Error from sendPiece in Client: ");
            e.printStackTrace();
        }
        
    }
}