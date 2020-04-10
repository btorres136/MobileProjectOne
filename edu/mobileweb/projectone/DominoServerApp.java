package edu.mobileweb.projectone;
import edu.mobileweb.projectone.dominoesServer.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
    public static ArrayList<PieceList> playerLists;
    // first player (i.e., she has the (6,6))
    private int firstPlayer;

    private DominoServerApp(){
        this.allPieces = new PieceList();
        DominoServerApp.gameBoard = new PieceList();
        DominoServerApp.playerLists = new ArrayList<PieceList>(4);
    }

    private PieceList getplayerList(int i){
        return DominoServerApp.playerLists.get(i);

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
			DominoServerApp.playerLists.add(new PieceList());
			//Assign 7 pieces to each player
			for(int j = 1; j < 8; j++)
			{		
				//Select a piece from the list
				randomNum = rand.nextInt((totalCount));
				//Add the selected piece to player i
				( DominoServerApp.playerLists.get(i)).addToTail(this.allPieces.getPiece(randomNum));
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
        int turn;
        //Update the players of the current table and who's turn it is.
        do{
            turn = (this.firstPlayer+i)%4;
            System.out.println("The current player is: " + (turn + 1));
            for(int x = 0; x<4; x++){
                if(!DominoServerApp.gameBoard.isEmpty()){
                    players.get(x).update(turn, DominoServerApp.gameBoard.getList());
                }
            }
            players.get(turn).play(turn);
            i++;
        }while(win(players, turn) | stuck(players, turn));
    }

    private boolean win(ArrayList<DominoesServer> players, int turn){
        System.out.println("CHECKING WIN...");
        boolean win = true;
        for(int x = 0; x<4; x++){
            if(DominoServerApp.playerLists.get(x).size() == 0){
                System.out.println("The winer of the game is player: " + (x+1) + ", out of pieces.");
                win = false;
                for(int i = 0; i<players.size(); i++){
                    players.get(i).endgame(turn, " out of pieces.");
                }
            }
        }
        
        return win;
    }

    private boolean stuck(ArrayList<DominoesServer> players, int turn){
        System.out.println("CHECKING STUCK...");
        boolean stuck = true;
        ArrayList<Integer> points = new ArrayList<Integer>(4);
        if(DominoServerApp.gameBoard.getHead().getLeft() == DominoServerApp.gameBoard.getTail().getRight()){
            System.out.println("head left == tail right.");
            int count = 0;
            for(int i = 0; i<DominoServerApp.gameBoard.size(); i++){
                if(DominoServerApp.gameBoard.getPiece(i).getLeft() == DominoServerApp.gameBoard.getHead().getLeft()
                || DominoServerApp.gameBoard.getPiece(i).getRight() == DominoServerApp.gameBoard.getHead().getLeft()){
                    count++;
                    System.out.println("Count of similar pieces is: " + count);
                    if(count > 6){
                        stuck = false;
                        break;
                    }
                }
            }
            if(!stuck){
                System.out.println("THE GAME IS STUCK... \n THE WINNER IS...");
                for(int i = 0; i<4; i++){
                    int sum = 0;
                    for(int x =0; x<DominoServerApp.playerLists.get(i).size(); x++){
                        int temp = DominoServerApp.playerLists.get(i).getPiece(x).points();
                        sum = sum + temp;
                    }
                    points.add(sum);
                }
                int winner = points.indexOf(Collections.min(points));
                System.out.println("PLAYER " + (winner+1) + " WITH " + points.get(winner) + " POINTS!!!");
                for(int i = 0; i<players.size(); i++){
                    players.get(i).endgame(turn, " having less points: " + points.get(winner));
                }
            }    
        } 
        return stuck;
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
                //players.get(i).setPlayerPieceList(serverApp.getplayerList(i));
                System.out.println("DominoesServerApp: " + (i+1) + " pieces are: " + DominoServerApp.playerLists.get(i).getList());
                //System.out.println("PLayer: " + (i+1) + " pieces are: " + players.get(i).getPlayerPieceList().getList());
            }

            System.out.println("Game Starts...");
            serverApp.play(players);

        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}