package model.containers;

import macro.Macro;

/**
 * @author Panu
 *
 */
public class GameBoard {
	
	private BoardCell[][] board;
	
	private boolean printing = true;
	
	
	public GameBoard() {
		board = new BoardCell[Macro.DIM_ROWS][Macro.DIM_COLUMNS];
		hardReset();
	}
	
	
	/**
	 * Reset all cells to 0.
	 */
	public void hardReset() {
		for(int i=0; i<Macro.DIM_ROWS; i++) {
			for(int j=0; j<Macro.DIM_COLUMNS; j++) {
				board[i][j] = new BoardCell(Macro.EMPTY_OR_UNKNOWN, i, j);
			}
		}
	}
	
	
	/**
	 * Remove all hits from the board leaving the forts.
	 */
	public void softReset() {
		for(int i=0; i<Macro.DIM_ROWS; i++) {
			for(int j=0; j<Macro.DIM_COLUMNS; j++) {
				if(board[i][j].getType()>=Macro.EMPTY_HIT) {
					board[i][j].addToType(-2);
				}
			}
		}
	}
	
	
	
	/**
	 * Set value for a cell on the board. Value other than one below will throw an Exception.
	 *  0 - Unhit empty cell
	 * 	1 - Unhit fortification cell
	 * 	2 - Hit empty cell
	 * 	3 - Hit fortification cell
	 * @param value
	 * @param row
	 * @param column
	 * @throws Exception 
	 */
	private void setCell(int value, int row, int column) throws Exception {
		if(value<0 || value>3) {
			throw new Exception("Type ot of bounds");
		}
		board[row][column].setType(value);
	}
	
	
	/**
	 * Try to hit a cell on a gameboard
	 * @param row
	 * @param column
	 * @return boolean - True if fortification has been hit. False if repeat hit on same spot or empty
	 * space.
	 */
	public boolean hit(int row, int column) {
		
		//Catch a hit on hit cell
		if(board[row][column].getType()>=2) {
			if(printing) {System.out.println("This cell has already been hit!");}	//DEBUG
			return false;
		}
		
		board[row][column].addToType(2);
		
		if(board[row][column].getType()==Macro.EMPTY_HIT) {
			return false;
		} else if(board[row][column].getType()==Macro.FORT_HIT) {
			return true;
		} else {
			if(printing) {System.out.println("Issue on a 'GameBoard'");}		//DEBUG
			return false;
		}
	}
	
	
	/**
	 * Automatically fill this board 
	 */
	public void selfCreateGameBoard() {

		/*
		BoardCell[][] test = quickCopy(board);
		
		for(int i=0; i<4; i++) {
			boolean success = false;
			int exitIndex = 0;
			while(!success) {
				
				if(exitIndex>50) { System.exit(-1);}
				exitIndex++;
					
				int row = (int) Math.ceil((Math.random()*8)-1);
				int col = (int) Math.ceil((Math.random()*8)-1);
				
				try {
					buildFort(i, row, col, test);
					success = true;
					buildFort(i, row, col);
					if(printing) { System.out.println("Fort: " + i + ", in " + row + "," + col); }
					
				} catch (Exception e) {
					if(printing) { System.out.println("Bad fort location: " + row + "," + col); }
					test = quickCopy(board);
				}
			}
		}
		*/
		
		//TODO[GAMEBOARD] Fix the fort building section
		//if this is here, I guess time ran out.
		try {
			buildFort(0, 3, 5);
			buildFort(1, 0, 0);
			buildFort(2, 4, 0);
			buildFort(3, 2, 5);
		} catch (Exception e) {
		}
	}
	
	private BoardCell[][] quickCopy(BoardCell[][] in){
		BoardCell[][] out = new BoardCell[Macro.DIM_ROWS][Macro.DIM_COLUMNS];
		for(int n=0; n<Macro.DIM_ROWS; n++) {
			for(int k=0; k<Macro.DIM_COLUMNS; k++) {
				out[n][k] = new BoardCell();
				out[n][k].setType(in[n][k].getType());
			}
		}
		return out;
	}
	
	/**
	 * Build a fort onto the board, when placing imagine the fort is in a square that just surrounds
	 * the fort. When you place down a fort, the coordinates you give decide the top-left corner of
	 * that square.
	 * In example a cross:
	 * +  o  x
	 * o  o  o
	 * x  o  x
	 * The '+' mark is the coordinate spot, o:s are the fort pieces.
	 * @param index - 0:cross, 1:4square, 2:corner, 3:2horizontal, 4:2vertical
	 * @param row
	 * @param column
	 * @throws Exception 
	 */
	public void buildFort(int index, int row, int column) throws Exception, IndexOutOfBoundsException {
		int[] temp;
		switch(index) {
		case 0:
			temp = Macro.FORT_CROSS;
			break;
		case 1:
			temp = Macro.FORT_SQUARE;
			break;
		case 2:
			temp = Macro.FORT_CORNER;
			break;
		case 3:
			temp = Macro.FORT_TWO_HOR;
			break;
		case 4:
			temp = Macro.FORT_TWO_VERT;
			break;	
		default:
			return;
		}
			
		int i=0;
		int j=1;
		while(j<temp.length) {
			if(board[temp[i]][temp[j]].getType()>=Macro.EMPTY_HIT) {
				throw new Exception("Fort overlaps another");
			}
			setCell(Macro.FORT, row+temp[i], column+temp[j]);
			i+=2;
			j+=2;
		}
	}
	
	
	
	/**
	 * Build a fort onto *A* board. when placing imagine the fort is in a square that just surrounds
	 * the fort. When you place down a fort, the coordinates you give decide the top-left corner of
	 * that square.
	 * In example a cross:
	 * This is a override of buildFort()
	 * +  o  x
	 * o  o  o
	 * x  o  x
	 * The '+' mark is the coordinate spot, o:s are the fort pieces.
	 * @param index - 0:cross, 1:4square, 2:corner, 3:2horizontal, 4:2vertical
	 * @param row
	 * @param column
	 * @throws Exception 
	 */
/*	public void buildFort(int index, int row, int column, BoardCell[][] in) throws Exception, IndexOutOfBoundsException {
		int[] temp;
		switch(index) {
		case 0:
			temp = Macro.FORT_CROSS;
			break;
		case 1:
			temp = Macro.FORT_SQUARE;
			break;
		case 2:
			temp = Macro.FORT_CORNER;
			break;
		case 3:
			temp = Macro.FORT_TWO_HOR;
			break;
		case 4:
			temp = Macro.FORT_TWO_VERT;
			break;	
		default:
			return;
		}
			
		int i=0;
		int j=1;
		while(j<temp.length) {
			if(in[temp[i]][temp[j]].getType()==Macro.FORT) {
				throw new Exception("Fort overlaps another");
			}
			in[row+temp[i]][column+temp[j]].setType(Macro.FORT);
			//setCell(Macro.FORT, row+temp[i], column+temp[j]);
			i+=2;
			j+=2;
		}
	}
	*/
	
	/**
	 * Get simplified form of gameboard
	 * @return int[][] 
	 */
	public int[][] getBoardAs2dArray(){
		int[][] temp = new int[Macro.DIM_ROWS][Macro.DIM_COLUMNS];
		for(int i=0; i<Macro.DIM_ROWS;i++) {
			for(int j=0;j<Macro.DIM_COLUMNS;j++) {
				temp[i][j] = board[i][j].getType();
			}
		}
		return temp;
	}
	
	
	/**
	 * Set board from int[][] matrix
	 * @param in - int[][]
	 */
	public void setBoardFrom2dArray(int[][] in) {
		for(int i=0; i<Macro.DIM_ROWS; i++) {
			for(int j=0; j<Macro.DIM_COLUMNS; j++) {
				board[i][j].setType(in[i][j]);
			}
		}
	}
	
	
	/**
	 * Returns reference to a cell on the board
	 * @param row 
	 * @param column
	 * @return BoardCell - reference to the real boardcell
	 */
	public BoardCell getCell(int row, int column) {
		return board[row][column];
	}
	
	/**
	 * Form all hits, (empty || fort) as an integer 2d array
	 * @return int[][] - 1=hit, 0=unknown
	 */
	public int[][] getHitsAsArray(){
		int[][] temp = new int[Macro.DIM_ROWS][Macro.DIM_COLUMNS];
		for(int i=0; i<Macro.DIM_ROWS; i++) {
			for(int j=0; j<Macro.DIM_COLUMNS; j++) {
				temp[i][j] = board[i][j].getType()>1 ? 1 : 0;
			}
		}		
		return temp;
	}
	
	/**
	 * Suppress debug printing
	 */
	public void suppressoutPrint() {
		printing = false;
	}
}
