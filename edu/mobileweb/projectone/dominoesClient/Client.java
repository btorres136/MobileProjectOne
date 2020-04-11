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
	
    // Data input stream and output streams for data transfer
	private 	DataInputStream 	socketInputStream;
    private 	DataOutputStream 	socketOutputStream;

    //socket connections
    private     Socket              socket;
    private     InetAddress         serveAddress;
	
	// Connection parameters
	private 	String 				serverAddressStr;
    private		int 				serverPort;
    
    //Strings to manage the information sent from the server.
    private     String              pieceListStr;
    private     String              gameBoard;

    //Boolean to manege the while lops of play and turn.
    private     boolean             turn = false;
    private     boolean             cont = true;
    
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
    /**
     * <h3>Client</h3>
     * <p>Default constructor. Sets the socket connections with the server addres and port.</p>
     * @param serveAddressStr
     * @param serverPort
     */
    public Client(String serveAddressStr, int serverPort) {
        this.serverAddressStr = serveAddressStr;
        this.serverPort=serverPort;
        try {
            this.serveAddress = InetAddress.getByName(serverAddressStr);
            this.socket = new Socket(serveAddress,serverPort);    
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * <h3>Play</h3>
     * <p>Initializes the data input and output streams and waits for the commands of the server.</p>
     */
    public void play(){
        int readComand;
        try {
            //ServerSocket server = new ServerSocket();
            socketInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            socketOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            System.out.println("Connected to the game...");
            
            while (cont) {
                turn = false;
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
                    case Codes.ENDGAME:     this.endgame();
                                            cont = false;
                                            break;
                    case Codes.CLOSECONNECTION: this.exit();
                                                break;
                    case Codes.WRONGCOMMAND:
                                        System.out.println("Command is not valid.");
                                        break;
                }
            }
            this.exit();
            socketInputStream.close();
            socketOutputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * <h3>EndGame</h3>
     * <p>Recives the person who won or if you won at the end of the game, including the manner in which the game was won.</p>
     */
    public void endgame(){
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

    /**
     * <h3>PutPieceLeft</h3>
     * <p>Promts the choice of which piece the user wants to play on the left side of the board.
     * Sends it to the server and waits for respons to indicate if the piece is playable or not.
     * Turn will be set to true if the play is valid, if the play is not valid turn will be false.</p>
     */
    public void putPieceLeft(){
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
                    //socketOutputStream.writeInt(Codes.OK);
                    //socketOutputStream.flush();
                    turn = true;
                }else if(read == Codes.NOP){
                    System.out.println("The piece is not playable plese select again.");
                    //socketOutputStream.writeInt(Codes.NOP);
                    //socketOutputStream.flush();
                    turn = false;
                }

            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <h3>PutPieceRight</h3>
     * <p>Promts the choice of which piece the user wants to play on the right side of the board.
     * Sends it to the server and waits for respons to indicate if the piece is playable or not.
     * Turn will be set to true if the play is valid, if the play is not valid turn will be false.</p>
     */
    public void putPieceRight(){
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
                    //socketOutputStream.writeInt(Codes.OK);
                    //socketOutputStream.flush();
                    turn = true;
                }else if(read == Codes.NOP){
                    System.out.println("The piece is not playable plese select again.");
                    //socketOutputStream.writeInt(Codes.NOP);
                    //socketOutputStream.flush();
                    turn = false;
                }
                
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <h3>Pass</h3>
     * <p>Sends the pass command to the server and waits to see if the player has a playable piece or dosent.
     * Will set turn to false if the player hase a playable piece, 
     * turn will be set to true if the player dosent have a playable piece.</p>
     */
    public void pass(){
        int read=0;
        try {
            socketOutputStream.writeInt(Codes.PASS);
            socketOutputStream.flush();
            
            read = socketInputStream.readInt();

            if(read == Codes.OK){
                read = socketInputStream.readInt();
                System.out.println("read is: " + read);            
                if(read == Codes.NOP){
                    System.out.println("You have a playable piece pick again.");
                    //socketOutputStream.writeInt(Codes.NOP);
                    //socketOutputStream.flush();
                    turn = false;
                }else if(read == Codes.OK){
                    System.out.println("Youre pased youre turn is over.");
                    //socketOutputStream.writeInt(Codes.OK);
                    //socketOutputStream.flush();
                    turn = true;
                }
            }
           
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    /**
     * <h3>Update</h3>
     * <p>Recives whose turn it is</p>
     */
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

    /**
     * <h3>Turn</h3>
     * <p>This function promts the menu and hapens when its the turn of the player.
     * Gives 3 options: </p>
     * <ol>
     *      <li>Left ,initiates the command to put the piece on the left side of the board.</li>
     *      <li>Right ,initiates the command to put the piece on the right side of the board.</li>
     *      <li>Pass ,initiates the command pass</li>
     * </ol>
     * <p>This function is control with a do while loop with a bollean(turn) variable thats affected by each of the previous choices.</p>
     */
    public void turn(){
        byte[] buffer = new byte[Codes.BUFFER_SIZE];
        int read;
        int arguments;
        try {
            socketOutputStream.writeInt(Codes.OK);
            socketOutputStream.flush();
            
            read = socketInputStream.read(buffer);
            System.out.println(new String(buffer).trim());
            do {
                System.out.println("TURN: " + turn);
                System.out.print("-------------------------------------Menu------------------------------------- \n"
                + "The game board: \n" + this.gameBoard
                + "\nThese are your pieces: \n" + this.pieceListStr 
                + "\nSelect where do you want to put the piece on the board (Acording to number). \n" 
                + " 1.Left \n 2.Right \n 3.pass \n $>");
                Scanner reader = new Scanner(System.in);
                arguments = reader.nextInt();
                
                switch(arguments){
                    case 1: 
                    this.putPieceLeft();
                    break;
                    case 2:
                    this.putPieceRight();
                    break;
                    case 3: 
                    this.pass();
                    break;
                    default: 
                    System.out.println("SELECT A VALID OPTION");
                    break;
                }
            } while (!turn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <h3>Exit</h3>
     * <p>Sends the exit command to the server.</p>
     */
    public void exit(){
        try {
            socketOutputStream.writeInt(Codes.CLOSECONNECTION);
            socketOutputStream.flush();

            int read = socketInputStream.readInt();
            
            if(read == Codes.OK){
                System.out.println("Thanks for playing.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <h3>SeeTabel</h3>
     * <p>Recives the table from the server and prints it on the screen.</p>
     */
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
    /**
     * <h3>SendPiece</h3>
     * <p>Recives the player pieces from the server as a string and stores them.</p>
     */
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