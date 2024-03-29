package edu.mobileweb.projectone.dominoesServer;


/**
 * <h3>Piece Class</h3>
 * <p> This class Represents a Domino Piece </p>
 * @author Brian D. Torres Alvarado
 * @author Joel E. Martinez
 * @since 1.0
 */
public class Piece {
	//Each piece has a left and a right side
	private int left;
	private int right;
	private String piece;
	private int points;

	/**
	 * <h3> Constructor </h3>
	 * <p>Receive and Set the Left and the Right side of the Dominoes</p>
	 * @param left Left Value of the Domino Piece
	 * @param right Right Value of the Domino Piece
	 */
	public Piece(int left, int right)
	{
		this.left=left;
		this.right=right;
		this.points = left + right;
	}

	public Piece(){}
	/**
	 * <h3>points</h3>
	 * <p>Gets the point of a piece</p>
	 * @return sum of left side and the right side of the piece.
	 */
	public int points(){
		return this.points;
	}
	
	/**
	 * <h3>getLeft Function</h3>
	 * <p>Get the Left side of the Domino Piece</p>
	 * @return Left value of the Domino Piece
	 */
	public int getLeft()
	{
		return left;
	}

	/**
	 * <h3>getRight Function</h3>
	 * <p>Get the Right side of the Domino Piece</p>
	 * @return Right Value of the Domino Piece
	 */
	public int getRight()
	{
		return right;
	}


	/**
	 * <h3>Rotate function</h3>
	 * <p>The left side of the Domino Piece will be the Right side </p>
	 */
	public void rotate()
	{
		int temp;
		temp=left;
		left=right;
		right=temp;
	}
	
	/**
	 * <h3>printPiece Function</h3>
	 * <p>Print the left side followed by the right side</p>
	 */
	public void printPiece()
	{
		System.out.print("(" + left + "|" + right+")");
	}
	/**
	 * Get the piece in string
	 * @return pieceStr
	 */
	public String getPiece(){
		this.piece = new String("(" + left + "|" + right+")");
		return this.piece;
	}
	
	/**
	 * <h3>Equals Functions</h3>
	 * <p>Check if the Right side of the Domino is equal to the Left side</p>
	 * @param left Right side of the Domino Piece
	 * @param right Left side of the Domino Piece
	 * @return True if the
	 *  right side and the left side are the same, if not return false
	 */
	public boolean equals(int left, int right)
	{
		return ((this.left==left) && (this.right==right)); 
	}

	/**
	 * Turns piece string to Piece obj.
	 * @param strPiece
	 * @return Piece
	 */
	public Piece StrToPiece(String strPiece){

		String str = strPiece.replaceAll("[^0-9]", "");
		int left = 0;
		int right = 0;
		for(int i = 0; i<str.length(); i++){
			int num = Character.getNumericValue(str.charAt(i));
			if(i%2 == 0){
				left = num;
			}else{
				right = num; 
			}
		}

		Piece piece = new Piece(left, right);
		return piece;
	}
}
