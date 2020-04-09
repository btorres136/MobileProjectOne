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
	private DataInputStream Input;
	private DataOutputStream Output;

	private int id;

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
			this.Input = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
			this.Output = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
			this.id = id;
			//this.playerPieceList = DominoServerApp.playerLists.get(id);
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
			System.out.println("Sending update code");
			Output.writeInt(Codes.UPDATE);
			Output.flush();
			System.out.println("Sent update code");
			read = Input.readInt();

			if(read == Codes.OK){
				System.out.println("Updating player: " + (this.id+1));
				if(x != this.id){
					this.Output.write(new String("Not youre turn player: " + (this.id+1) + ", it's player: " + (x+1) + " turn.").getBytes());
					this.Output.flush();   
				}else if(x == this.id){
					this.Output.write(new String("Youre turn player: " + (this.id+1)).getBytes());
					this.Output.flush();
				}

				System.out.println("Sending the table to player: " + (this.id+1));
				this.SEETABLECommand(gameBoard);
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
		//System.out.println("Entered play for player; " + (player+1));
		int readCommand;
		// Send the pieces to each player
			
		this.SENDPICECommand(player);
			
		this.indicateTurn(player);
			
			
		readCommand = this.Input.readInt();

		System.out.println("Received Command: " + readCommand);
		switch (readCommand) {
			case Codes.LEFT:
			this.putPieceLeft(player);
			break;

			case Codes.RIGHT:
			this.putPieceRight(player);
			break;

			case Codes.PASS:
			this.pass(player);
			break;
			/*
			case Codes.PUTPIECE:
			this.PUTPIECECommand(player);
			break;
			*/
			case Codes.SENDPIECE:
			this.SENDPICECommand(player);
			break;
				// Exit command
			case Codes.CLOSECONNECTION:
			this.exitCommand(player);
			break;
		}
	}

	public void indicateTurn(int player){
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

	public void SEETABLECommand(String gameBoard){
		int read;
		try{
			Output.writeInt(Codes.SEETABLE);
			Output.flush();

			read = Input.readInt();

			if(read == Codes.OK){
				System.out.println("Sending table to the player: ");
				Output.write(gameBoard.getBytes());
				Output.flush();
				System.out.println("Table has been sent to the player: ");
			}

		}catch(IOException e){
			e.printStackTrace();
		}
		//System.out.println("Exit seetable in domonesServer");
	}

	public void PUTPIECECommand(int player){
		int read;
		//Piece playedPiece = new Piece();
		try {

			Output.writeInt(Codes.OK);
			Output.flush();
			System.out.println("sent ok");
			read = Input.readInt();
			System.out.println("read in putpiece: " + read);

			switch(read){
				case 1: 
				this.putPieceLeft(player);
				break;
				case 2:
				this.putPieceRight(player);
				break;
				case 3: 
				this.pass(player);

			}
			
			/*
			playedPiece = DominoServerApp.playerLists.get(player).getPiece(read -1);
			System.out.println("Player " + player + " wants to play: " + playedPiece.getPiece());

			if(!playedPiece.getPiece().equals("(6|6)")){
				Piece head = DominoServerApp.gameBoard.getHead();
				Piece tail = DominoServerApp.gameBoard.getTail();

				if(playedPiece.getRight() == head.getLeft() || playedPiece.getLeft()== head.getLeft()){
					System.out.println("Adding piece to the left of the table.");
					if(playedPiece.getRight() == head.getLeft()){
						DominoServerApp.gameBoard.addToHead(playedPiece);
						System.out.println("Piece has been added to head of the gameboard.");
						DominoServerApp.playerLists.get(player).remove(read-1);
					}else{
						playedPiece.rotate();
						DominoServerApp.gameBoard.addToHead(playedPiece);
						System.out.println("Piece has been added to head of the gameboard.");
						DominoServerApp.playerLists.get(player).remove(read-1);
					}
				}else if(playedPiece.getRight() == tail.getRight() || playedPiece.getLeft()== tail.getRight()){
					System.out.println("Adding piece to the right of the table.");
					if(playedPiece.getLeft() == tail.getRight()){
						DominoServerApp.gameBoard.addToTail(playedPiece);
						System.out.println("Piece has been added to the tail of the gameboard.");
						DominoServerApp.playerLists.get(player).remove(read-1);
					}else{
						playedPiece.rotate();
						DominoServerApp.gameBoard.addToTail(playedPiece);
						System.out.println("Piece has been added to the tail of the gameboard.");
						DominoServerApp.playerLists.get(player).remove(read-1);
					}
				}else{
					// This pice is not playable
					System.out.println("This pice is not playable");
				}
			}else{
				DominoServerApp.gameBoard.addToTail(playedPiece);
				DominoServerApp.playerLists.get(player).remove(read-1);
				System.out.println("Piece has been added to the gameboard.");
				
			}*/
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void putPieceLeft(int player){
		int read;
		Piece playedPiece = new Piece();
		try {
			
			do{
				Output.writeInt(Codes.OK);
				Output.flush();
				
				read = Input.readInt();
				
				playedPiece = DominoServerApp.playerLists.get(player).getPiece(read - 1);
				System.out.println("The player wants to play piece: " + playedPiece.getPiece());
				if(!playedPiece.equals(6, 6)){
					if(!DominoServerApp.gameBoard.isEmpty()){
						Piece head = DominoServerApp.gameBoard.getHead();
	
						if(playedPiece.getRight() == head.getLeft()){
							DominoServerApp.gameBoard.addToHead(playedPiece);
							System.out.println("Piece has been added to head of the gameboard.");
							DominoServerApp.playerLists.get(player).remove(read-1);
							Output.writeInt(Codes.OK);
							Output.flush();
						}else if(playedPiece.getLeft() == head.getLeft()) {
							playedPiece.rotate();
							DominoServerApp.gameBoard.addToHead(playedPiece);
							System.out.println("Piece has been added to head of the gameboard.");
							DominoServerApp.playerLists.get(player).remove(read-1);
							Output.writeInt(Codes.OK);
							Output.flush();
						}
					}else{
						System.out.println("This piece is not playable...");
						Output.writeInt(Codes.NOP);
						Output.flush();
					}
				}else{
					DominoServerApp.gameBoard.addToHead(playedPiece);
					DominoServerApp.playerLists.get(player).remove(read-1);
					System.out.println("Piece has been added to the gameboard.");
					Output.writeInt(Codes.OK);
					Output.flush();
				}

				read = Input.readInt();
				System.out.println("read in put pice left loop; " + read);
			}while(read != Codes.OK);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void putPieceRight(int player){
		int read;
		Piece playedPiece = new Piece();
		try {

			Output.writeInt(Codes.OK);
			Output.flush();
			
			read = Input.readInt();
			
			playedPiece = DominoServerApp.playerLists.get(player).getPiece(read - 1);
			System.out.println("The player wants to play piece: " + playedPiece.getPiece());

			if(!playedPiece.equals(6, 6)){
				Piece tail = DominoServerApp.gameBoard.getTail();
				if(playedPiece.getRight() == tail.getRight()){
					DominoServerApp.gameBoard.addToTail(playedPiece);
					System.out.println("Piece has been added to head of the gameboard.");
					DominoServerApp.playerLists.get(player).remove(read-1);
				}else if (playedPiece.getLeft() == tail.getRight()) {
					playedPiece.rotate();
					DominoServerApp.gameBoard.addToTail(playedPiece);
					System.out.println("Piece has been added to head of the gameboard.");
					DominoServerApp.playerLists.get(player).remove(read-1);
				}
			}else{
				DominoServerApp.gameBoard.addToTail(playedPiece);
				DominoServerApp.playerLists.get(player).remove(read-1);
				System.out.println("Piece has been added to the gameboard.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void pass(int player){
		int read;
		Piece playedPiece = new Piece();
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//This function will send the player pieces.
	public void SENDPICECommand(int player){
		byte[] buffer = new byte[Codes.BUFFER_SIZE];
		int read;
		try {

			Output.writeInt(Codes.SENDPIECE);
			Output.flush();

			//Wait for ok
			read = Input.readInt();

			if(read == Codes.OK){
				//Send player pieces
				System.out.println("Player " + (player+1) + " list: " + DominoServerApp.playerLists.get(player).getList());
				Output.write(DominoServerApp.playerLists.get(player).getList().getBytes());
				Output.flush();
			}
			//System.out.println("Exit sendpiece on dominoesServer");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void exitCommand(int player)
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
