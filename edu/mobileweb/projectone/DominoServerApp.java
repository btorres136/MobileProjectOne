package edu.mobileweb.projectone;
import edu.mobileweb.projectone.dominoesServer.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import edu.mobileweb.projectone.dominoesServer.DominoesServer;

/**
 * <h3>DominoServerApp</h3>
 * 
 * @author Brian D. Torres Alvarado
 * @author Joel E. Martinez
 */
public class DominoServerApp {
    // All pieces
	private PieceList allPieces;
	// Pieces on the table
    public static PieceList gameBoard;
    // pieces assigned to each player
    private ArrayList<PieceList> playerLists;
    // first player (i.e., she has the (6,6))
    private int firstPlayer;

    private DominoServerApp(){
        this.allPieces = new PieceList();
        this.gameBoard = new PieceList();
        this.playerLists = new ArrayList<PieceList>(4);
    }

    private PieceList getplayerList(int i){
        return this.playerLists.get(i);
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
			//playerLists.get(i).printList();
		}
	}
    
    
    public void play(ArrayList<DominoesServer> players) throws IOException {
        int i = 0;
        //Update the players of the current table and who's turn it is.
        do{
            int turn = (this.firstPlayer+i)%4;
            System.out.println("The current player is: " + (turn + 1));
            for(int x = 0; x<4; x++){
                if(!DominoServerApp.gameBoard.isEmpty()){
                    players.get(x).update(turn, DominoServerApp.gameBoard.getList() );
                }else{
                    players.get(x).update(turn, "");
                }
            }
            players.get(turn).play(turn);
            i++;
        }while(true);

    }

    public static void main(String args[]) {
        try {
            ServerSocket server = new ServerSocket(1234);
            System.out.println("Welcome to B and J Dominoes Game...");
            ArrayList<DominoesServer> players = new ArrayList<DominoesServer>(4);
            DominoServerApp serverApp = new DominoServerApp();
            serverApp.assignPieces();
            for(int i = 0; i<4; i++){
                System.out.println(i + " are waiting...");
                Socket newclient = server.accept();
                players.add(new DominoesServer(newclient, i));
                System.out.println("New Connection...");
                players.get(i).setPlayerPieceList(serverApp.getplayerList(i));
                System.out.println("PLayer: " + (i+1) + " pieces are: " + players.get(i).getPlayerPieceList().getList());
            }

            System.out.println("Game Starts...");
            serverApp.play(players);

        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}