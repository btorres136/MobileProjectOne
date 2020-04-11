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
//import jdk.nashorn.internal.ir.IfNode;

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
	// Input and Output for data transfer.
	private DataInputStream Input;
	private DataOutputStream Output;
	// Socke tfor connection to client
	private Socket socket;

	private boolean turn = false;
	private int id;

	public void setPlayerPieceList(PieceList list){
		this.playerPieceList = list;
	}

	public PieceList getPlayerPieceList(){
		return this.playerPieceList;
	}

	
	/**
	 * <h3>DominoesServer</h3>
	 * <p>Recives the socket and id of the client. 
	 * Initializes socket connections, client id, Input and Output data streams for data transfer. </p>
	 * @param clientSocket
	 * @param id
	 */
	public DominoesServer(Socket clientSocket, int id) {
		try {
			this.socket = clientSocket;
			this.Input = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
			this.Output = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
			this.id = id;
			//this.playerPieceList = DominoServerApp.playerLists.get(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.playerPieceList = new PieceList();

	}

	/**
	 * <h3>update</h3>
	 * <p>Recives the player wo is playing and the string gameboard
	 * Indecates who's player turn is to the client and sends the table.</p>
	 * @param x
	 * @param gameBoard
	 */
	public void update(int x, String gameBoard){
		int read;
		try {
			//System.out.println("Sending update code");
			Output.writeInt(Codes.UPDATE);
			Output.flush();
			//System.out.println("Sent update code");
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
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/***
	 * <h3>play</h3>
	 * <p>Recives the player turn.
	 * Sends the player its pieces and indicates the turn for the player. 
	 * Waits for comands of the client, will keep waiting until a valid play is made.</p>
	 * @param player
	 * @throws IOException
	 */
	public void play(int player) throws IOException
	{
		//System.out.println("Entered play for player; " + (player+1));
		int readCommand;
		// Send the pieces to each player
			
		this.SENDPICECommand(player);
			
		this.indicateTurn(player);
			
		do {
			System.out.println("Turn for player: " + (player+1) + " is: " + turn);
			turn = false;
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
				// Exit command
				case Codes.CLOSECONNECTION:
				this.exitCommand(player);
				Input.close();
				Output.close();
			    socket.close();
				break;
			}
		} while (!turn);	
	}

	/**
	 * <h3>endgame</h3>
	 * <p>Recives the player and the method in which the player won.
	 * indicates this to the client.</p>
	 * @param player
	 * @param method
	 */
	public void endgame(int player, String method){
		int read;
		try {
			Output.writeInt(Codes.ENDGAME);
			Output.flush();

			read = Input.readInt();

			if(read == Codes.OK){
				System.out.println("Indication end game to player: " + (this.id+1));
				if(player != this.id){
					Output.write(new String("PLAYER: " + (player+1) + " HAS WON THE GAME BY: " + method).getBytes());
					Output.flush();   
				}else if(player == this.id){
					Output.write(new String("YOU HAVE WON THE GAME!!! " + method).getBytes());
					Output.flush();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * <h3>indicateTurn</h3>
	 * <p>Indicates the turn to play to the player that it recived.</p>
	 * @param player
	 */
	public void indicateTurn(int player){
		int read;
		System.out.println("Entered indicate turn on dominoeServer");
		try {
			Output.writeInt(Codes.TURN);
			Output.flush();

			//wait for ok
			read = Input.readInt();
	
			if(read == Codes.OK){
				System.out.println("Its player: " + (this.id+1)+ " turn");
				Output.write(new String("Its  youre turn player: " + (this.id+1)).getBytes());
				Output.flush();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * <h3>SEETABLECommand</h3>
	 * <p>Recives the gameboard as a srtring and sends it to the client.</p>
	 * @param gameBoard
	 */
	public void SEETABLECommand(String gameBoard){
		int read;
		try{
			Output.writeInt(Codes.SEETABLE);
			Output.flush();

			read = Input.readInt();

			if(read == Codes.OK){
				Output.write(gameBoard.getBytes());
				Output.flush();
				System.out.println("Table has been sent to the player");
			}

		}catch(IOException e){
			e.printStackTrace();
		}
		//System.out.println("Exit seetable in domonesServer");
	}
	
	/**
	 * <h3>putPieceLeft</h3>
	 * <p>Recives player, its used to idndicate which player played.
	 * Recives the piece that the client whants to play verify if its playable on the Left side of the board.,
	 * if it is playable the piece is added to the table and removed from the player piece and will send the cleint a comfirmed.
	 * If the piece is not plyabel the client will be sent and invalid command.
	 * If the played is valid turn = true if its not turn = false</p>
	 * @param player
	 */
	public void putPieceLeft(int player){
		int read;
		Piece playedPiece = new Piece();
		try {
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
							turn = true;
						}else if(playedPiece.getLeft() == head.getLeft()) {
							playedPiece.rotate();
							DominoServerApp.gameBoard.addToHead(playedPiece);
							System.out.println("Piece has been added to head of the gameboard.");
							DominoServerApp.playerLists.get(player).remove(read-1);
							Output.writeInt(Codes.OK);
							Output.flush();
							turn = true;
						}else{
							System.out.println("This piece is not playable...");
							Output.writeInt(Codes.NOP);
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
					turn = true;
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <h3>putPieceLeft</h3>
	 * <p>Recives player, its used to idndicate which player played.
	 * Recives the piece that the client whants to play verify if its playable on the Right side of the board.,
	 * if it is playable the piece is added to the table and removed from the player piece and will send the cleint a comfirmed.
	 * If the piece is not plyabel the client will be sent and invalid command. If the played is valid turn = true if its not turn = false</p>
	 * @param player
	 */
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
					if(!DominoServerApp.gameBoard.isEmpty()){
						Piece tail = DominoServerApp.gameBoard.getTail();
	
						if(playedPiece.getLeft() == tail.getRight()){
							DominoServerApp.gameBoard.addToTail(playedPiece);
							System.out.println("Piece has been added to head of the gameboard.");
							DominoServerApp.playerLists.get(player).remove(read-1);
							Output.writeInt(Codes.OK);
							Output.flush();
							turn = true;
						}else if(playedPiece.getRight() == tail.getRight()) {
							playedPiece.rotate();
							DominoServerApp.gameBoard.addToTail(playedPiece);
							System.out.println("Piece has been added to head of the gameboard.");
							DominoServerApp.playerLists.get(player).remove(read-1);
							Output.writeInt(Codes.OK);
							Output.flush();
							turn = true;
						}else{
							System.out.println("This piece is not playable...");
							Output.writeInt(Codes.NOP);
							Output.flush();
						}
					}else{
						System.out.println("This piece is not playable...");
						Output.writeInt(Codes.NOP);
						Output.flush();
					}
				}else{
					DominoServerApp.gameBoard.addToTail(playedPiece);
					DominoServerApp.playerLists.get(player).remove(read-1);
					System.out.println("Piece has been added to the gameboard.");
					Output.writeInt(Codes.OK);
					Output.flush();
					turn = true;
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <h3>pass</h3>
	 * <p>Recives player, its used to idndicate which player passed.
	 * Verifies that the player that wnts to pass has a playabel piece or not.
	 * If the player has a playable piece the client will recive a invalid command. 
	 * if the player dosent have a playable piece the client will recive a valid command.
	 * if the pass is valid turn = true if its not turn = false</p>
	 * @param player
	 */
	public void pass(int player){
		int read;
		try {
			Output.writeInt(Codes.OK);
			Output.flush();

			if(!DominoServerApp.gameBoard.isEmpty()){
				Piece head = DominoServerApp.gameBoard.getHead();
				Piece tail = DominoServerApp.gameBoard.getTail();
				for(int i = 0; i < DominoServerApp.playerLists.get(player).size(); i++){
					Piece piece = DominoServerApp.playerLists.get(player).getPiece(i);
					
					if((piece.getRight() == head.getLeft() || piece.getLeft() == head.getLeft()) || 
					(piece.getRight() == tail.getRight() ||  piece.getLeft() == tail.getRight())){
						System.out.println("Player has a playable piece.");
						Output.writeInt(Codes.NOP);
						Output.flush();
						turn = false;
						break;
					}else{
						System.out.println("Player dosent have a playable piece.");
						//Output.writeInt(Codes.OK);
						//Output.flush();
						turn = true;
					}
				}

				if(turn){
					Output.writeInt(Codes.OK);
					Output.flush();
				}
			}else{
				System.out.println("Player has a playable piece.");
				Output.writeInt(Codes.NOP);
				Output.flush();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * <h3>SENDPICECommand</h3>
	 * <p>Recives player, its used to idndicate which player will recive its pieces.
	 * Sends the pieces of the player</p>
	 * @param player
	 */
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
	/**
	 * <h3>exitCommand</h3>
	 * <p>Sends the comfirmation that the player will disconnect to the cleint.<p>
	 * @param player
	 */
	private void exitCommand(int player)
	{
		try{
			Output.writeInt(Codes.OK);
			Output.flush();
			System.out.println("The connection has been closed for player: " + player);
			turn = true;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
