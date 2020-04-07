package edu.mobileweb.projectone.dominoesServer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import edu.mobileweb.projectone.DominoServerApp;
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
	//piece list of the player
	private PieceList playerPieceList;
	private DataInputStream clientsInputStreams;
	private DataOutputStream clientsOutputStreams;

	private int id;

	private boolean endGame = true;

	public void setPlayerPieceList(PieceList list){
		this.playerPieceList = list;
	}

	public PieceList getPlayerPieceList(){
		return this.playerPieceList;
	}

	
	/**
	 * <h3>Constructor</h3>
	 * <p>
	 * </p>
	 */
	public DominoesServer(Socket clientSocket, int id) {
		try {
			this.clientsInputStreams = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
			this.clientsOutputStreams = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
			this.id = id;
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.playerPieceList = new PieceList();

	}

	/***
	 * iterates over the players
	 * 
	 * @throws IOException
	 */

	public void update(int x, String gameBoard){
		int read;
		try {

			this.clientsOutputStreams.writeInt(Codes.UPDATE);
			this.clientsOutputStreams.flush();

			read = this.clientsInputStreams.readInt();

			if(read == Codes.OK){
				System.out.println("Updating player: " + (this.id+1));
				if(x != this.id){
					this.clientsOutputStreams.write(new String("Not youre turn player: " + (this.id+1) + ", it's player: " + (x+1) + " turn.").getBytes());
					this.clientsOutputStreams.flush();
				}else if(x == this.id){
					this.clientsOutputStreams.write(new String("Youre turn player: " + (this.id+1)).getBytes());
					this.clientsOutputStreams.flush();
				}

				System.out.println("Sending the table to player: " + (this.id+1));
				this.SEETABLECommand(this.clientsInputStreams, this.clientsOutputStreams, gameBoard);
				/*
				if(!gameBoard.equals("")){
					System.out.println("Sending the table to player: " + (this.id+1));
					this.SEETABLECommand(this.clientsInputStreams, this.clientsOutputStreams, gameBoard);
				}*/
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void play(int player) throws IOException
	{
		System.out.println("Entered play for player; " + (player+1));
		int readCommand;
		// Send the pieces to each player
			
		this.SENDPICECommand(player, this.clientsInputStreams, this.clientsOutputStreams);
			
		this.indicateTurn(player, this.clientsInputStreams, this.clientsOutputStreams);
			
			
		readCommand = this.clientsInputStreams.readInt();

		System.out.println("Received Command: " + readCommand);
		switch (readCommand) {
				// Put command
			case Codes.PUTPIECE:
			this.PUTPIECECommand(player, this.clientsInputStreams, this.clientsOutputStreams);
			break;

			case Codes.SENDPIECE:
			this.SENDPICECommand(player, this.clientsInputStreams, this.clientsOutputStreams);
			break;
				// Exit command
			case Codes.CLOSECONNECTION:
			this.exitCommand(player, this.clientsInputStreams, this.clientsOutputStreams);
			break;
		}
	}

	public void indicateTurn(int player, DataInputStream Input, DataOutputStream Output){
		int read;
		System.out.println("Entered indicate turn on dominoeServer");
		try {
			Output.writeInt(Codes.TURN);
			Output.flush();

			//wait for ok
			read = Input.readInt();
	
			if(read == Codes.OK){
				if(this.id != player){
					System.out.println("Its not player: " + (this.id+1) + " turn");
					Output.write(new String("Its not youre turn player: " + (this.id+1)).getBytes());
					Output.flush();
				}else if( this.id == player){
					System.out.println("Its player: " + (this.id+1)+ " turn");
					Output.write(new String("Its  youre turn player: " + (this.id+1)).getBytes());
					Output.flush();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void SEETABLECommand(DataInputStream Input, DataOutputStream Output, String gameBoard){
		System.out.println("Enterd seetable in domonesServer");
		int read;
		try{
			Output.write(Codes.SEETABLE);
			Output.flush();
			System.out.println("code.seetable has been sent from dominoesServer");

			read = Input.readInt();
			System.out.println("Read in seetable on dominoesServer: " + read);

			if(read == Codes.OK){
				System.out.println("Sending table to the player: ");
				Output.write(gameBoard.getBytes());
				Output.flush();
				System.out.println("Table has been sent to the player: ");
			}

		}catch(IOException e){
			e.printStackTrace();
		}
		System.out.println("Exit seetable in domonesServer");
	}

	public void PUTPIECECommand(int player, DataInputStream Input, DataOutputStream Output){
		int totalRead = 0;
		int read;
		Piece playedPiece = new Piece();
		try {
			Output.writeInt(Codes.OK);
			Output.flush();

			read = Input.readInt();

			playedPiece = this.playerPieceList.getPiece(read - 1);
			System.out.println("Player " + player + " wants to play: " + playedPiece.getPiece());

			if(!playedPiece.getPiece().equals("(6|6)")){
				System.out.println("Is not 2ble 6");
			}else{
				System.out.println("Is 2ble 6");
				DominoServerApp.gameBoard.addToTail(playedPiece);
				this.playerPieceList.remove(read - 1);
				System.out.println("Piece has been added to the gameboard.");
				
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
				System.out.println("Player " + player + " list: " + playerPieceList.getList());
				Output.write(playerPieceList.getList().getBytes());
				Output.flush();
			}
			System.out.println("Exit sendpiece on dominoesServer");
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
