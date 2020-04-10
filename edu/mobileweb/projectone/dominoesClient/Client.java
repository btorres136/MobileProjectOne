package edu.mobileweb.projectone.dominoesClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.print.DocFlavor.STRING;

import edu.mobileweb.projectone.DominoServerApp;

//
//import com.sun.org.apache.bcel.internal.classfile.Code;

import edu.mobileweb.projectone.dominoesServer.Piece;
import edu.mobileweb.projectone.dominoesServer.PieceList;
import edu.mobileweb.projectone.transferCodes.Codes;
//import jdk.internal.util.xml.impl.Input;
import javafx.scene.control.TableView.ResizeFeatures;

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

    private     String              pieceListStr;
    private     String              gameBoard;

    private     int                 id;

    
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
            
            do {
                System.out.println("Waiting for command in client");
                readComand = socketInputStream.readInt();
                System.out.println("Read comand client: " + readComand);
                switch(readComand)
                {
                    case Codes.LEFT:        this.putPieceLeft();
                                            break;
                    case Codes.RIGHT:       this.putPieceRight();
                                            break;
                    case Codes.PASS:        this.pass();
                                            break;
                    case Codes.UPDATE:      this.update();
                                            break;
                    case Codes.SEETABLE:    this.seeTable();
                                            break;
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

    public int putPieceLeft(){
        int read = 0;
        int piece;
        try {
            socketOutputStream.writeInt(Codes.LEFT);
            socketOutputStream.flush();
            
            read = socketInputStream.readInt();

            if(read == Codes.OK){
                //System.out.println("The game board: \n" + this.gameBoard);
                //System.out.println("These are your pieces: \n" + this.pieceListStr );
                System.out.print("Select the piece you want to play acording to the location. left to right (1-7) \n $>");
                Scanner in = new Scanner(System.in);
                piece = in.nextInt();

                socketOutputStream.writeInt(piece);
                socketOutputStream.flush();

                read = socketInputStream.readInt();
                System.out.println("read is: " + read);
                   
                if(read == Codes.OK){
                    System.out.println("Youre turn is over.");
                    socketOutputStream.writeInt(Codes.OK);
                    socketOutputStream.flush();
                }else if(read == Codes.NOP){
                    System.out.println("The piece is not playable plese select again.");
                    socketOutputStream.writeInt(Codes.NOP);
                    socketOutputStream.flush();
                }

            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return read;
    }

    public int putPieceRight(){
        int read =0;
        int piece;
        try {
            socketOutputStream.writeInt(Codes.RIGHT);
            socketOutputStream.flush();
            
            read = socketInputStream.readInt();

            if(read == Codes.OK){
                //System.out.println("The game board: \n" + this.gameBoard);
                //System.out.println("These are your pieces: \n" + this.pieceListStr );
                System.out.print("Select the piece you want to play acording to the location. left to right (1-7) \n $>");
                Scanner in = new Scanner(System.in);
                piece = in.nextInt();

                socketOutputStream.writeInt(piece);
                socketOutputStream.flush();

                read = socketInputStream.readInt();
                System.out.println("read is: " + read);
                   
                if(read == Codes.OK){
                    System.out.println("Youre turn is over.");
                    socketOutputStream.writeInt(Codes.OK);
                    socketOutputStream.flush();
                }else if(read == Codes.NOP){
                    System.out.println("The piece is not playable plese select again.");
                    socketOutputStream.writeInt(Codes.NOP);
                    socketOutputStream.flush();
                }
                
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return read;
    }

    public int pass(){
        int read=0;
        try {
            socketOutputStream.writeInt(Codes.PASS);
            socketOutputStream.flush();
            
            read = socketInputStream.readInt();

            if(read == Codes.OK){
                read = socketInputStream.readInt();
                System.out.println("read is: " + read);            
                if(read == Codes.OK){
                    System.out.println("Youre pased youre turn is over.");
                    socketOutputStream.writeInt(Codes.OK);
                    socketOutputStream.flush();
    
                }else if(read == Codes.NOP){
                    System.out.println("You have a playable piece pick again.");
                    socketOutputStream.writeInt(Codes.NOP);
                    socketOutputStream.flush();
                }
            }
           
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return read;
    }

    public void update(){
        byte[] buffer = new byte[Codes.BUFFER_SIZE];
        int read;
        try {
            socketOutputStream.writeInt(Codes.OK);
            socketOutputStream.flush();

            read = socketInputStream.read(buffer);
            System.out.println(new String(buffer).trim());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void turn(){
        byte[] buffer = new byte[Codes.BUFFER_SIZE];
        int read;

        
        int arguments;
        int res = 0;
        try {
            socketOutputStream.writeInt(Codes.OK);
            socketOutputStream.flush();
            
            read = socketInputStream.read(buffer);
            System.out.println(new String(buffer).trim());
            do {
                
                System.out.print("-------------------------------------Menu------------------------------------- \n"
                + "The game board: \n" + this.gameBoard
                + "\nThese are your pieces: \n" + this.pieceListStr 
                + "\nSelect where do you want to put the piece on the board (Acording to number). \n" 
                + " 1.Left \n 2.Right \n 3.pass \n $>");
                Scanner reader = new Scanner(System.in);
                arguments = reader.nextInt();
                
                
                //this.putPiece(arguments);
                
                switch(arguments){
                    case 1: 
                    res = this.putPieceLeft();
                    break;
                    case 2:
                    res = this.putPieceRight();
                    break;
                    case 3: 
                    res = this.pass();
                    break;
                    default:
                    res = 0; 
                    System.out.println("SELECT A VALID OPTION");
                    break;
                }
                System.out.println("res: " + res);
               
            } while (res != Codes.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
    public String readCommands(Scanner reader){
        System.out.println("This are your pieces: \n" + this.pieceListStr);
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
    }*/

    public void exit(){
        try {
            socketOutputStream.writeInt(Codes.CLOSECONNECTION);
            socketOutputStream.flush();

            int read = socketInputStream.readInt();
            if(read == Codes.OK){
                socketOutputStream.close();
                socketInputStream.close();
                System.out.println("Thanks for playing.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get the table
    public void seeTable(){
        
        byte[] buffer = new byte[Codes.BUFFER_SIZE];
        int read; 
        
        try {
            socketOutputStream.writeInt(Codes.OK);
            socketOutputStream.flush();

            read = socketInputStream.read(buffer);
            this.gameBoard = new String(buffer).trim();
            System.out.println("The current table is: " + this.gameBoard);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*This comand will work from server to client one time at the begining of the game 
      from the client side it will recive the 7 pices sent by the server and print them on 
      the terminal*/
    public void sendPice(){
        byte[] buffer = new byte[Codes.BUFFER_SIZE];
        int read;
        try {

            socketOutputStream.writeInt(Codes.OK);
            socketOutputStream.flush();
            
            //recive pices and print
            read = socketInputStream.read(buffer);
            this.pieceListStr = new String(buffer).trim();

        } catch (Exception e) {
            System.out.print("Error from sendPiece in Client: ");
            e.printStackTrace();
        }
        
    }
}