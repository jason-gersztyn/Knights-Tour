import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * The Knight's Tour using backtracking and an intelligent look-ahead approach which adds weight to all given moves. 
 * By choosing moves which have the lowest weight, the tour may be solved nearly in linear time to the number of valid 
 * spaces on the board, in the best case. This approach is known as Warnsdorf's Rule.
 *
 * @author Jason Gersztyn
 * @version 2.0.1
 * 
 */
public class KnightsTour {

	private int boardLength; //the length of the board
	private int board[][]; //the simulated board
	private int maxMoves; //it should take no more than this many moves to solve the puzzle
	boolean solved = false; //tells whether or not the board is solved

	//list of possible moves for the knight
	private final Point[] MOVES = new Point[]{new Point(-2, -1),
			new Point(-2, 1), new Point(2, -1), new Point(2, 1), new Point(-1, -2),
			new Point(-1, 2), new Point(1, -2), new Point(1, 2)};

	/**
	 * Constructor for this class
	 * @param size the x*y grid of the board
	 */
	public KnightsTour(int size) {
		boardLength = size + 4;
		maxMoves = size * size;
	}

	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args) {
		//board of size 8x8
		KnightsTour knightsBoard = new KnightsTour(8);
		//start at a random point along the board
		knightsBoard.startLocations();
	}
	
	/**
	 * Designates the starting space for the knight.
	 */
	private void startLocations() {
		int row = 2 + (int) (Math.random() * (getValidLength()));
		int col = 2 + (int) (Math.random() * (getValidLength()));
		solveTour(row,col); //begin the tour at this point
	}

	/**
	 * The space within the two-dimensional array that actually represents the board.
	 * Any square with negative two as a value can be considered padding.
	 * @return the valid length and height of the board
	 */
	private int getValidLength() {
		return boardLength - 4;
	}

	/**
	 * Helper method to determine if a square is safe for the knight.
	 * @param row the row the knight is on
	 * @param col the column the knight is on
	 * @param grid the current state of the board which the move is beign performed on
	 * @return true if the square is safe for the knight
	 */
	private boolean legalMove(int row, int col, ArrayList<ArrayList<Integer>> grid) {
		return (grid.get(row).get(col) == -1);
	}

	/**
	 * Prints the solution to the tour and the order in which the moves were made
	 * @param boardToPrint the board to be printed
	 */
	private void printMoves(ArrayList<ArrayList<Integer>> boardToPrint) {

		int maxSize = 0;
		for (ArrayList<Integer> innerList : boardToPrint) {
			if (maxSize < innerList.size()) {
				maxSize = innerList.size();
			}
		}

		for (int i = 0; i < maxSize; i++) {
			for (ArrayList<Integer> innerList : boardToPrint) {
				if(innerList.get(i) != -2) {
					System.out.printf("%-4d", innerList.get(i));
				}
			}
			System.out.printf("%n"); //new line for the next row
		}
	}

	/**
	 * Solves the knight's tour using backtracking.
	 * This method also converts the board into a two dimensional array list.
	 * @param sRow the starting row
	 * @param sCol the starting column
	 * @return true if there is a solution
	 */
	public boolean solveTour(int sRow, int sCol) {
		initializeBoard(); //create a board for the knight to move along

		//simulates the two-dimensional array; our board
		ArrayList<ArrayList<Integer>> rowGrid = new ArrayList<ArrayList<Integer>>();

		for(int[] row : board) { //rows in the two dimensional array list
			ArrayList<Integer> c = new ArrayList<Integer>();
			for(int col : row) { //columns in the two dimensional array list
				c.add(col);
			}
			rowGrid.add(c);
		}

		rowGrid.get(sRow).set(sCol, 1); //the first move; starting position

		return solveRecurs(sRow, sCol, 2, rowGrid); //calls the recursive solve method
	}  

	/**
	 * Recursive helper method which will solve the knight's tour.
	 * @param row the current row
	 * @param col the current column
	 * @param nMove the number of moves made thus far
	 * @param grid the current representation of the board
	 * @return true if there is a solution to the knight's tour
	 */
	private boolean solveRecurs(int row, int col, int nMove, ArrayList<ArrayList<Integer>> grid) {

		//base case
		if (nMove > maxMoves) {
			printMoves(grid); //puzzle has been solved!
			solved = true;
		}

		if(!solved) {
			TreeMap<Integer,Point> moves = optimalMove(row, col, nMove, grid);
			for (Map.Entry<Integer,Point> stuff : moves.entrySet()) {
				Point p = stuff.getValue();
				ArrayList<ArrayList<Integer>> gridCopy = boardCopier(grid);

				int nextRow = row + p.x;
				int nextCol = col + p.y;

				gridCopy.get(nextRow).set(nextCol, nMove);
				
				if(solveRecurs(nextRow, nextCol, nMove + 1, gridCopy)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * A utility method called before each move. It is used to optimize the knight's tour with a 
	 * single look-ahead while also expressing Warnsdorf's Rule.
	 * @param row current row position of knight
	 * @param col current column position of knight
	 * @param move the number of moves made thus far
	 * @param grid the current state of the board
	 * @return a mapping of all valid moves from the current space, ordered by lowest weight first
	 */
	private TreeMap<Integer,Point> optimalMove(int row, int col, 
			int move, ArrayList<ArrayList<Integer>> grid) {

		TreeMap<Integer,Point> moves = new TreeMap<Integer,Point>(); //store the moves which are possible

		for (Point p : MOVES) {

			int nRow = row + p.x; //next row to move to
			int nCol = col + p.y; //next column to move to
			int possibleMoves = 0;

			if (legalMove(nRow, nCol, grid)) {
				//temporary board to simulate the following moves
				ArrayList<ArrayList<Integer>> tempBoard = boardCopier(grid);
				//use max moves since the value is arbitrary and this array is temporary
				tempBoard.get(nRow).set(nCol, move + 1000); //this move will easily stick out in debugging
				for (Point check : MOVES) {
					int r = nRow + check.x;
					int c = nCol + check.y;
					//note that we only check for a legal move and do not actually move
					if (legalMove(r, c, tempBoard)) {
						possibleMoves++; //increase the count if this is a possible move from this given location
					}
					//no need to make further moves. We only wanted to check the next
				}
				//if this move is valid, add it to the mapping of possible moves
				if(possibleMoves > 0) {
					Integer weight = new Integer(possibleMoves);
					moves.put(weight, p); //note that moves are sorted based on the lowest cost, or weight, being first
				}
				//special case which is only applicable to the final move
				if(move == maxMoves) {
					moves.put(0, p);
				}
			}
		}

		return moves;
	}

	/**
	 * Copy the two dimensional array list into an identical new two dimensional array list.
	 * @param grid the array list to be copied
	 * @return the carbon copy of the list
	 */
	private ArrayList<ArrayList<Integer>> boardCopier(ArrayList<ArrayList<Integer>> grid) {
		ArrayList<ArrayList<Integer>> gridCopy = new ArrayList<ArrayList<Integer>>();
		for(ArrayList<Integer> r : grid) {
			ArrayList<Integer> temp = new ArrayList<Integer>();
			temp.addAll(r);
			gridCopy.add(temp);
		}
		return gridCopy;
	}

	/**
	 * Create the board to perform the knight's tour on. The board created is a two dimensional array.
	 * Note that a board is padded on all sides. The valid board does not include the padding.
	 */
	private void initializeBoard() {
		board = new int[boardLength][boardLength];
		//make all of the board -1 because have not visited any square

		for (int r = 0; r < boardLength; r++) {
			for (int c = 0; c < boardLength; c++) {
				if (r < 2 || r > boardLength - 3 || c < 2 || c > boardLength - 3) {
					board[r][c] = -2; //invalid space which is outside the board
				}
				else {
					board[r][c] = -1; //valid space
				}
			}
		}
	}
}