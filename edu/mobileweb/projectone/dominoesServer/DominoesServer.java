package edu.mobileweb.projectone.dominoesServer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import edu.mobileweb.projectone.transferCodes.Codes;
//import jdk.internal.util.xml.impl.Input;
import jdk.nashorn.internal.ir.IfNode;

/**
 * <h3>Dominoes Server</h3>
 * <p>
 * This class Implements a ServerSocket and implements the Rules used to
 * </p>
 * 
 * @author Brian D. Torres Alvarado
 * @author Joel E. Martinez
 * @since 1.0
 */
public class DominoesServer {
	// All pieces
	private PieceList allPieces;
	// Pieces on the table
	private PieceList gameBoard;
	// pieces assigned to each player
	private ArrayList<PieceList> playerLists;
	// first player (i.e., she has the (6,6))
	private int firstPlayer;

	/**
	 * <h3>Constructor</h3>
	 * <p>
	 * </p>
	 */
	public DominoesServer() {
		this.allPieces = new PieceList();
		this.gameBoard = new PieceList();
		this.playerLists = new ArrayList<PieceList>(4);
	}

	
	public void assignPieces()
	{
		//Random generator
		Random rand = new Random();
		int randomNum;
		/**
		 *  Generate the 28 pieces	
		 */
		for (int i = 0; i <= 6; i++)
			for(int j = i; j <= 6; j++)
			{
				this.allPieces.addToTail(new Piece(i,j));
			}
	
		/**
		 *  Random Assignment for each player
		 */
		int totalCount=28;
		for (int i = 0; i < 4; i++)
		{
			//Create each list
			this.playerLists.add(new PieceList());
			//Assign 7 pieces to each player
			for(int j = 1; j < 8; j++)
			{		
				//Select a piece from the list
				randomNum = rand.nextInt((totalCount));
				//Add the selected piece to player i
				( playerLists.get(i)).addToTail(this.allPieces.getPiece(randomNum));
				//Select the initial player
				if (this.allPieces.getPiece(randomNum).equals(6, 6))
					this.firstPlayer=i;
				//Remove the selected piece from the list of the list
				this.allPieces.remove(randomNum);
				totalCount--;
			}
			System.out.println("Player:" + i);
			playerLists.get(i).printList();
		}
	}

	/***
	 * iterates over the players
	 * 
	 * @throws IOException
	 */
	public void play(ArrayList<Socket> AllClients) throws IOException
	{
		ArrayList<DataInputStream> clientsInputStreams = new ArrayList<DataInputStream>(4);
		ArrayList<DataOutputStream> clientsOutputStreams = new ArrayList<DataOutputStream>(4);
		ArrayList<Socket> Clients = new ArrayList<Socket>(AllClients);
		for (int i = 0; i < 4; i++) {
			try {
				clientsInputStreams.add(new DataInputStream(new BufferedInputStream(Clients.get(i).getInputStream())));
				clientsOutputStreams.add(new DataOutputStream(new BufferedOutputStream(Clients.get(i).getOutputStream())));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		int readCommand;
		DataOutputStream ClientHear;
		DataInputStream ClientSpeak;
		// Send the pieces to each player
		for(int i = 0; i < 4; i++){
			ClientSpeak = clientsInputStreams.get(i);
			ClientHear = clientsOutputStreams.get(i);
			this.SENDPICECommand(i, ClientSpeak, ClientHear);
		}
		
		for(int i = 0; i < 28; i++){
			
			int player = firstPlayer+i%4;
			ClientSpeak = clientsInputStreams.get(player);
			ClientHear = clientsOutputStreams.get(player);
			
			this.indicateTurn(player, ClientSpeak, ClientHear);

			readCommand = ClientSpeak.readInt();
			System.out.println("Received Command: " + readCommand);
			switch (readCommand) {
				case Codes.SEETABLE:
				this.SEETABLECommand(ClientSpeak, ClientHear);
				break;
				// Put command
				case Codes.PUTPIECE:
				this.PUTPIECECommand(player, ClientSpeak, ClientHear, clientsOutputStreams);
				break;

				case Codes.SENDPIECE:
				this.SENDPICECommand(player, ClientSpeak, ClientHear);

				// Exit command
				case Codes.CLOSECONNECTION:
				this.exitCommand(player, ClientSpeak, ClientHear);
				break;
			}
		}
		
	}

	public void indicateTurn(int player, DataInputStream Input, DataOutputStream Output){
		System.out.println("Entered Turn in server for player: " + player);
		int read;
		try {
			Output.writeInt(Codes.TURN);
			Output.flush();

			//wait for ok
			read = Input.readInt();

			if(read == Codes.OK){
				System.out.println("Indicating turn for player: " + player);
				Output.write(new String("Its your turn player: " + (player+1)).getBytes());
				Output.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void SEETABLECommand(DataInputStream Input, DataOutputStream Output){
		byte[] buffer = new byte[Codes.BUFFER_SIZE];
		int totalRead = 0;
		int read;
		try{
			Output.writeInt(Codes.OK);
			Output.flush();
			//Output.writeUTF(gameBoard.printList());

			//wait
			read = Input.readInt();

			if(read == Codes.SEETABLE){
				//Send table

			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void PUTPIECECommand(int player, DataInputStream Input, DataOutputStream Output, ArrayList<DataOutputStream> clients){
		byte[] buffer = new byte[Codes.BUFFER_SIZE];
		int totalRead = 0;
		int read;
		Piece playedPiece = new Piece();
		try {
			Output.writeInt(Codes.OK);
			Output.flush();

			read = Input.read(buffer);
			String Piece = new String(buffer);
			System.out.println("Player " + player + " wants to play: " + Piece);
			playedPiece = playedPiece.StrToPiece(Piece);

			System.out.println("Printing playedPiece.getPiece(): "+ playedPiece.getPiece());
			
			if(playedPiece.getLeft() != 6 && playedPiece.getRight() != 6){
				// Verify the sides if the list.
				int left = playedPiece.getLeft();
				int right = playedPiece.getRight();
				
				if (left == this.gameBoard.getHead().getLeft() || right == this.gameBoard.getHead().getLeft()) {
					System.out.println("Head");
					/*
					if(left == this.gameBoard.getHead().getLeft()){
						System.out.println("Rotating and adding piece: " + playedPiece.getPiece() + "to the board");
						playedPiece.rotate();
						System.out.println("Rotating piece: " + playedPiece.getPiece());
						this.gameBoard.addToHead(playedPiece);
						System.out.println("The piece: " + playedPiece.getPiece() + " has been added to  the board.");
						System.out.println("Printing the board: ");
						this.gameBoard.printList();
					} else if(right == this.gameBoard.getHead().getLeft()){
						System.out.println("The piece: " + playedPiece.getPiece() +" is being added to the board (no piece rotation)");
						this.gameBoard.addToHead(playedPiece);
						System.out.println("Printing the board: ");
						this.gameBoard.printList();
					}*/

				} else if(right == this.gameBoard.getTail().getRight() || left == this.gameBoard.getTail().getRight()) {
					System.out.println("Tail");
					/*
					if(left == this.gameBoard.getTail().getRight()){
						System.out.println("The piece: " + playedPiece.getPiece() +" is being added to the board (no piece rotation)");
						this.gameBoard.addToTail(playedPiece);
						System.out.println("Printing the board: ");
						this.gameBoard.printList();
					} else if(right == this.gameBoard.getTail().getRight()){System.out.println("Rotating and adding piece: " + playedPiece.getPiece() + "to the board");
						playedPiece.rotate();
						System.out.println("Rotating piece: " + playedPiece.getPiece());
						this.gameBoard.addToTail(playedPiece);
						System.out.println("The piece: " + playedPiece.getPiece() + " has been added to  the board.");
						System.out.println("Printing the board: ");
						this.gameBoard.printList();
						
					}*/
				}
				
			}else{
				System.out.println("The piece (6|6) is being added to the board");
				this.gameBoard.addToHead(playedPiece);
				this.gameBoard.printList();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//This function will send the player pieces.
	public void SENDPICECommand(int player, DataInputStream Input, DataOutputStream Output){
		byte[] buffer = new byte[Codes.BUFFER_SIZE];
		int read;
		try {

			Output.writeInt(Codes.SENDPIECE);
			Output.flush();

			//Wait for ok
			read = Input.readInt();

			if(read == Codes.OK){
				//Send player pieces
				System.out.println("Player " + player + " list: " + playerLists.get(player).getList());
				Output.write(playerLists.get(player).getList().getBytes());
				Output.flush();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void exitCommand(int player, DataInputStream Input, DataOutputStream Output)
	{
		try{
			Output.writeInt(Codes.OK);
			Output.flush();
			Output.close();
			Input.close();
			System.out.println("The connection has been closed for player: " + player);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}


}
