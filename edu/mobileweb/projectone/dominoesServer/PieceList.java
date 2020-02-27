package edu.mobileweb.projectone.dominoesServer;

import java.util.ArrayList;


/**
 * <h3>PieceList Class</h3>
 * @author Brian D. Torres Alvarado
 * @author Joel E. Martinez
 * @since 1.0
 */
public class PieceList {

	// actual pieces list
	private ArrayList<Piece> actualList;
	// list size
	private int count;
	
	/**
	 *  <h3>Constructor</h3>
	 *  <p>Creates a empty list and set the list size to 0<p>
	 */
	public PieceList() {
		this.actualList = new ArrayList();
		this.count=0;
	}
	
	/**
	 * <h3>getHead Function</h3>
	 * <p>Get the first piece of the list</p>
	 * @return The first piece in the list
	 */
	public Piece getHead() {
		return actualList.get(0);
	}
	
	/**
	 * <h3>getTail Function</h3>
	 * <p>Get the last piece of the  list</p>
	 * @return The last piece in the list
	 */
	public Piece getTail() {
		return actualList.get(count-1);
	}
	
	/**
	 * <h3>addToHead</h3>
	 * <p>Add a piece to the list</p>
	 * @param element actual piece
	 */
	public void addToHead(Piece element)
	{
		actualList.add(0, element);
		count++;
	}
	
	/**
	 * <h3>addToTail</h3>
	 * Add a piece at the end of the list
	 * @param element actual piece
	 */
	public void addToTail(Piece element)
	{
		actualList.add(count, element);
		count++;
	}
	
	/** 
	 * <h3>remove Function</h3>
	 * <p>remove the piece in position post</p>
	 * @param pos position
	 */
	public void remove(int pos)
	{
		actualList.remove(pos);
		count--;
	}
	/**
	 * <h3>getPiece</h3>
	 * <p>Get the piece in the provided position<p>
	 * @param pos: position of the piece
	 * @return The piece in the provided position
	 */
	public Piece getPiece(int pos)
	{
		return actualList.get(pos);
	}
	
	public String getList(){
		ArrayList<String> Pieces = new ArrayList<String>();
		for(int i = 0; i < count; i++){
			Pieces.add("(" + ((Piece)actualList.get(i)).getLeft() + "|" + ((Piece)actualList.get(i)).getRight() + ")" );
		}
		return Pieces.toString();
	}
	/***
	 * Print all elements in the list 
	 */
	public void printList() {
		for (int i=0; i<count; i++)
			System.out.print("(" + ((Piece)actualList.get(i)).getLeft() + "|" + ((Piece)actualList.get(i)).getRight() + ")" );
		System.out.println();
	}


	
	/***
	 *  print all elements but one per line
	 */
	public void printlnList()
	{
		for (int i=0; i<count; i++)
			System.out.println(i + " ("+ ((Piece)actualList.get(i)).getLeft() + "|" + ((Piece)actualList.get(i)).getRight() + ")" );
		System.out.println();
	}
}
