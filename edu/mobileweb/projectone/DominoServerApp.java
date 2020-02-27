package edu.mobileweb.projectone;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import edu.mobileweb.projectone.dominoesServer.DominoesServer;

/**
 * <h3>DominoServerApp</h3>
 * 
 * @author Brian D. Torres Alvarado
 * @author Joel E. Martinez
 */
public class DominoServerApp {
    
    public static void main(String args[]) {
        try {
            ServerSocket server = new ServerSocket(1234);
            ArrayList<Socket> clients = new ArrayList<Socket>();
            System.out.println("Welcome to B and J Dominoes Game...");
            DominoesServer domino = new DominoesServer();
		    domino.assignPieces();
            while(clients.size() < 4){
                System.out.println(clients.size() + " are waiting...");
                Socket newclient = server.accept();
                System.out.println("New Connection...");
                clients.add(newclient);
            }
            System.out.println("Game Starts...");
            domino.play(clients);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}