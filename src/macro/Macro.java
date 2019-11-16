package macro;

/**
 * @author Panu
 *
 */
public class Macro {
	
	/**
	 * Game board dimensions: Rows 
	 */
	public static final int DIM_ROWS = 8;
	
	/**
	 * Game board dimensions: Columns 
	 */
	public static final int DIM_COLUMNS = 8;
	
	
	/**
	 * Value: 0
	 * if anonymous board: unknown
	 * if known board: empty
	 */
	public static final int EMPTY_OR_UNKNOWN = 0;
	
	
	/**
	 * Value: 1
	 * known board: fort
	 */
	public static final int FORT = 1;
	
	
	/**
	 * Value: 2
	 * anonymous / known board: missed hit
	 */
	public static final int EMPTY_HIT = 2;
	
	
	/**
	 * Value: 3
	 * anonymous / known board: fort hit
	 */
	public static final int FORT_HIT = 3;
	
	public static final int[] FORT_CROSS = {0,1,  1,0,  1,1,  1,2,  2,1};
	public static final int[] FORT_SQUARE = {0,0,  0,1,  1,0,  1,1};
	public static final int[] FORT_CORNER = {0,0,  1,0,  1,1};
	public static final int[] FORT_TWO_VERT = {0,0,  1,0};
	public static final int[] FORT_TWO_HOR = {0,0,  0,1};
	
	

	/*
	 * These are for  pinpoint algorithm
	 */
	public static final int ACTUAL = 1;
	public static final int POTENTIAL = 1;
	public static final int NOT_APPLICABLE_TARGET = -1;
	public static final int UNKNOWN = 0;
	
	
	/**
	 * Create hashamp for the PinpointAlgorithm
	 * @return HashMap
	 */
	public static int[] initFortMap() {
		
		int[] list = {1, 1, 1, 1, 1};
		return list;
	}
	
	
	/**
	 * @param i
	 * @return int[]
	 * Translates a fort index to a corresponding coordinates list
	 */
	public static int[] getFortByIndex(int i) {
		if(i==0) { return FORT_CROSS; }
		else if(i==1) { return FORT_SQUARE; }
		else if(i==2) { return FORT_CORNER; }
		else if(i==3) { return FORT_TWO_VERT; }
		else if(i==4) { return FORT_TWO_HOR; }
		else {
			throw new IndexOutOfBoundsException();
		}
	}
	
	/**
	 * @param i
	 * @return int
	 * Translates a fort index to size of fort
	 */
	public static int getFortSizeByIndex(int i) {
		if(i==0) { return 5; }
		else if(i==1) { return 4; }
		else if(i==2) { return 3;}
		else if(i==3) { return 2;}
	//	else if(i==4) { return  2;}
		else {
			throw new IndexOutOfBoundsException();
		}
	}
	
	/**
	 * @param i
	 * @return int
	 * Translates a fort size to fort index
	 */
	public static int getFortBySize(int i) {
		if(i==2) { return 3; }
		else if(i==3) { return 2; }
		else if(i==4) { return 1; }
		else if(i==5) { return 0; }
		else {
			throw new IndexOutOfBoundsException();
		}
	}
}
