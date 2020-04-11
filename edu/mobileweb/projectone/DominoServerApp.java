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
    /**
     * <h3>DominoServeApp</h3>
     * <p>Constructor that initializes two PieceList Objects and a List for the pieces of the player.</p>
     */
    private DominoServerApp(){
        this.allPieces = new PieceList();
        DominoServerApp.gameBoard = new PieceList();
        DominoServerApp.playerLists = new ArrayList<PieceList>(4);
    }


    /**
     * <h3>assignPieces</h3>
     * <p>Generates 28 dominoe pieces and assigns them to each player randomly.</p>
     */
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
    
    /**
     * <h3>Play</h3>
     * <p>Recives a list of DominoeServer objects(players). 
     * Indicate the turn of the player to play and updates all other players of who is playing.
     * Until a winer is found.</p>
     * @param players
     * @throws IOException
     */
    public void play(ArrayList<DominoesServer> players) throws IOException {
        int i = 0;
        int turn;
        //Update the players of the current table and who's turn it is.
        do{
            turn = (this.firstPlayer+i)%4;
            System.out.println("THE CURRENT PLAYER IS: " + (turn + 1));
            for(int x = 0; x<4; x++){
                if(!DominoServerApp.gameBoard.isEmpty()){
                    players.get(x).update(turn, DominoServerApp.gameBoard.getList());
                }
            }
            players.get(turn).play(turn);
            i++;
        }while(win(players, turn));

        
    }

    /**
     * <h3>win</h3>
     * <p>Recives DominoesServer List of 4 players and the one that is playing(int).
     * Verifies the Piece list of all players to see which one is empty, 
     * if it is empty then it will broad cast a message indicating who won the game by having no pieces. 
     * If all players have 1 or more pieces it will check if the game is stuck. The function will return false if there is a winner.</p>
     * @param players
     * @param turn
     * @return
     */
    private boolean win(ArrayList<DominoesServer> players, int turn){
        System.out.println("CHECKING WIN...");
        boolean win = true;
        for(int x = 0; x<4; x++){
            if(DominoServerApp.playerLists.get(x).size() == 0){
                System.out.println("The winer of the game is player: " + (x+1) + ", out of pieces.");
                win = false;
                for(int i = 0; i<players.size(); i++){
                    players.get(i).endgame(turn, " OUT OF PIECES.");
                }
            }
        }

        win = stuck(players, turn);
        
        return win;
    }

    /**
     * <h3>stuck</h3>
     * <p>Recives DominoesServer List of 4 players and the one that is playing(int).
     * Verifies that the left side of the left piece equals the right side of the right piece,
     * if this is true it will check if there are 7 pieces with the same number as the sides of the table and set the boollean stuck to false,
     * if stuck is set to false, the function will add all the pieces of eeach individual player and store them in a List,
     * which is checked by searching the minimum points of the all the players indicating the winner.
     * Will brodcast the winner to all players with the method have less points and the point total.</p>
     * @param players
     * @param turn
     * @return
     */
    private boolean stuck(ArrayList<DominoesServer> players, int turn){
        System.out.println("CHECKING STUCK...");
        boolean stuck = true;
        ArrayList<Integer> points = new ArrayList<Integer>(4);
        if(DominoServerApp.gameBoard.getHead().getLeft() == DominoServerApp.gameBoard.getTail().getRight()){
            int count = 0;
            for(int i = 0; i<DominoServerApp.gameBoard.size(); i++){
                if(DominoServerApp.gameBoard.getPiece(i).getLeft() == DominoServerApp.gameBoard.getHead().getLeft()
                || DominoServerApp.gameBoard.getPiece(i).getRight() == DominoServerApp.gameBoard.getHead().getLeft()){
                    count++;
                    if(count > 6){
                        stuck = false;
                        break;
                    }
                }
            }
            if(!stuck){
                System.out.println("THE GAME IS STUCK... \nTHE WINNER IS...");
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
                    players.get(i).endgame(turn, " HAVING LESS POINTS: " + points.get(winner));
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
                System.out.println("DominoesServerApp: " + (i+1) + " pieces are: " + DominoServerApp.playerLists.get(i).getList());
            }

            System.out.println("Game Starts...");
            serverApp.play(players);

        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}