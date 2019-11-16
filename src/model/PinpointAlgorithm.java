package model;
import java.util.ArrayList;

import macro.Macro;
import model.containers.BoardCell;

/**
 * @author Panu
 *This class generates algorithmically firing solutions to destroy whole forts.
 *A new instance is created for each fort and the algorithm operates locally around root cell on 5x5 area,
 *which is based on the fact that a fort can only expand 2 cells from the root to any direction.
 */
public class PinpointAlgorithm {
	
	private int[] availableForts;

	/**
	 * Actual fort cells, 1=fort / 0=unknown / -1=notApplicable
	 */
	private int[][] actuals;

	/**
	 * Potential fort locations, 1=potential / 0=Unknown potential /
	 * -1=notApplicable
	 */
	private int[][] potentials;

	boolean printing = true; // DEBUG
	int timeSpent = 0; // DEBUG

	/**
	 * Constructor - Start a new targeting algorithm. The setup will mark the first
	 * cell as the first actual, and create potentials around
	 * 
	 * @param row
	 * @param column
	 * @param old_hits - int[][] matrix, 1=hit , 0=empty
	 */
	public PinpointAlgorithm(int row, int column, int[][] old_hits) {

		actuals = new int[Macro.DIM_ROWS][Macro.DIM_COLUMNS];
		potentials = new int[Macro.DIM_ROWS][Macro.DIM_COLUMNS];

		// setup the lists
		for (int i = 0; i < Macro.DIM_ROWS; i++) {
			for (int j = 0; j < Macro.DIM_COLUMNS; j++) {
				actuals[i][j] = Macro.UNKNOWN;
				potentials[i][j] = Macro.UNKNOWN;
			}
		}

		// set all old hits as not applicable targets
		if (old_hits != null) {
			for (int i = 0; i < Macro.DIM_ROWS; i++) {
				for (int j = 0; j < Macro.DIM_COLUMNS; j++) {
					actuals[i][j] = old_hits[i][j] == 1 ? Macro.NOT_APPLICABLE_TARGET : Macro.UNKNOWN;
					potentials[i][j] = old_hits[i][j] == 1 ? Macro.NOT_APPLICABLE_TARGET : Macro.UNKNOWN;
				}
			}
		}

		// set latest hit as actual and create new potentials
		actuals[row][column] = Macro.ACTUAL;

		potentials = newSetOfPotentials(row, column, potentials);
	}

	/**
	 * Main access method for gamelogic to call. Handles all pre-work that test()
	 * will need. This algorithm will run for multiple calls and when it should not
	 * be excecuted anymore it will return null. The null return must be handled by
	 * the higher level class.
	 * 
	 * @param lastHit - BoardCell that was hit prior
	 * @return new BoardCell that will be hit next OR null if algorithm is done
	 */
	public BoardCell getSolution(BoardCell lastHit) {
		timeSpent++; // DEBUG

		if (lastHit.getType() == Macro.FORT_HIT) {
			actuals[lastHit.getRow()][lastHit.getColumn()] = Macro.ACTUAL;
			potentials = newSetOfPotentials(lastHit.getRow(), lastHit.getColumn(), potentials);

		} else if (lastHit.getType() == Macro.EMPTY_HIT) {
			actuals[lastHit.getRow()][lastHit.getColumn()] = Macro.NOT_APPLICABLE_TARGET;
			potentials[lastHit.getRow()][lastHit.getColumn()] = Macro.NOT_APPLICABLE_TARGET;

		} else {
			if (printing) {
				System.out.println("SNAFU");			//DEBUG
			} // DEBUG
		}

//		if(printing) {System.out.println("Pot: " + amountOf(Macro.POTENTIAL, potentials) + "    Act: " + amountOf(Macro.ACTUAL, actuals));	}	//DEBUG
//		if(printing) {System.out.println("POTENTIALS-notapplicable: " + amountOf(-1, potentials));}																	//DEBUG
		return test();
	}

	/**
	 * This method orchestrates testing and selection of potential target locations.
	 * 
	 * @return BoardCell - a carrier for the coordinates, the value of this cell is
	 *         null.
	 */
	private BoardCell test() {
		if (availableForts == null) {
			throw new NullPointerException();
		}

		// check largest fort
		int largestFort = 5;
		for (int i = 0; i < 5; i++) {
			if (availableForts[i] == 1) {
				largestFort = Macro.getFortSizeByIndex(i);
				break;
			}
		}

		int fort_index;

		// major selection combines all forts possibles.
		ArrayList<BoardCell> majorSelection = new ArrayList<>();

		for (int test = 0; test < availableForts.length; test++) {

			// quick hack to avoid overtesting.
			if (amountOf(Macro.ACTUAL, actuals) >= largestFort) {
				break;
			}

			// only test forts that aren't destroyed already
			if (availableForts[test] == 1) {

				fort_index = test;

				// create list of possibles
				// minor selection consists of one fort's possibles.
				ArrayList<BoardCell> minorSelection = new ArrayList<>();
				for (int i = 0; i < Macro.DIM_ROWS; i++) {
					for (int j = 0; j < Macro.DIM_COLUMNS; j++) {
						if (potentials[i][j] == Macro.POTENTIAL) {
							minorSelection.add(new BoardCell(i, j));
						}
					}
				}

				// test each potential whether they could fill the fort that is being tested
				for (int i = 0; i < minorSelection.size(); i++) {

					// create quick copy of actuals. We don't want to write on the original actuals.
					int[][] testMap = new int[Macro.DIM_ROWS][Macro.DIM_COLUMNS];
					for (int s = 0; s < Macro.DIM_ROWS; s++) {
						for (int t = 0; t < Macro.DIM_COLUMNS; t++) {
							int n = actuals[s][t];
							testMap[s][t] = n;
						}
					}

					BoardCell bc = minorSelection.get(i);
					testMap[bc.getRow()][bc.getColumn()] = Macro.ACTUAL;

					// inspect, can we remove impossible potentials
					int inspected = inspect(fort_index, testMap);
					if (inspected > amountOf(Macro.ACTUAL, actuals)) {
						// do nothing
					} else {
						minorSelection.remove(bc);
					}

					// TODO [PINPOINT] remove from selection members that are too far to create fort
					// with actuals
				}
				for (BoardCell bc : minorSelection) {
					if (!majorSelection.contains(bc)) {
						majorSelection.add(bc);
					}
				}
			}

			if (!majorSelection.isEmpty()) {
				BoardCell target = majorSelection.get((int) Math.ceil((Math.random() * majorSelection.size()) - 1));
//				if(printing) {System.out.println("Selected!: " + target.getRow() + ", " + target.getColumn() + "\nFrom selection sized: " + majorSelection.size());	}		//DEBUG
				return target;
			}
		}

		int size = amountOf(Macro.ACTUAL, actuals);
		int fort = Macro.getFortBySize(size);
//		if(printing) {System.out.println("DELETING THIS FORT: "  + fort);}		//DEBUG
		// if(printing) {System.out.println("EXIT pinpointalgorith");} //DEBUG
//		if(printing) {System.out.println("Time spent: " + timeSpent);}		//DEBUG

		// When there's no point in executing algorithm anymore, returns null
		return null;
	}

	/**
	 * This method will try to match the fort it's given to the map its given. It
	 * will go through all possible configurations, the fort might fit the map.,
	 * 
	 * @param fort_index   - index of fort being inspected. See Macro forts
	 * @param referenceMap - Import a int[][] that will be searched
	 * @return int amount of matches between fort and reference map
	 */
	public int inspect(int fort_index, int[][] referenceMap) {

		// base the working area.
		// get the first row and the first column the detected fort habits.
		int minRow = 7;
		int minCol = 7;
		for (int i = 0; i < Macro.DIM_ROWS; i++) {
			for (int j = 0; j < Macro.DIM_COLUMNS; j++) {
				if (referenceMap[i][j] == Macro.ACTUAL) {
					minRow = i < minRow ? i : minRow;
					minCol = j < minCol ? j : minCol;
				}
			}
		}

		// test fort in all configurations against a map of actuals
		int matches = 0;
		int[] fort = Macro.getFortByIndex(fort_index);

		// test from multiple locations
		for (int ref_i = 0; ref_i < 2; ref_i++) {
			for (int ref_j = 0; ref_j < 2; ref_j++) {

				int n = 0;
				int k = 1;
				int temp_matches = 0;

				// test from reference location
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						try {
							int temp = referenceMap[minRow - ref_i + i][minCol - ref_j + j];

							if (temp == Macro.ACTUAL && i == fort[n] && j == fort[k]) {
								n += 2;
								k += 2; // match
								temp_matches++;
							} else if (temp == Macro.UNKNOWN && i == fort[n] && j == fort[k]) {
								n += 2;
								k += 2; // unknown, but possible
							} else if (temp == Macro.ACTUAL && (i != fort[n] || j != fort[k])) {
								throw new Exception(); // fail, fort not possible
							} else if (temp == Macro.UNKNOWN || temp == Macro.NOT_APPLICABLE_TARGET) {
								// do nothing
							} else {
								// do nothing
							}
						} catch (IndexOutOfBoundsException io) {

						} catch (Exception e) {
							i = 10; // quit loop
							j = 10; // quit loop
							temp_matches = 0;
						}
					}
				}
				matches = temp_matches > matches ? temp_matches : matches;
			}
		}
		return matches;
	}

	/**
	 * Helper method to chart new potentials around an actual cell. 1. If tested
	 * cell is unknown -> mark as potential 2. If tested cell is wasAFort|notAFort
	 * -> leave
	 * 
	 * @param row
	 * @param column
	 * @param in - int[][] matrix
	 */
	private int[][] newSetOfPotentials(int row, int column, int[][] in) {

		int[][] temp = new int[8][8];

		for (int i = 0; i < Macro.DIM_ROWS; i++) {
			for (int j = 0; j < Macro.DIM_COLUMNS; j++) {
				if (in[i][j] == Macro.POTENTIAL) {
					temp[i][j] = Macro.POTENTIAL;
				}
				if (actuals[i][j] == Macro.ACTUAL || actuals[i][j] == Macro.NOT_APPLICABLE_TARGET) {
					temp[i][j] = Macro.NOT_APPLICABLE_TARGET;
				}
			}
		}

		try {
			if (temp[row - 1][column] == Macro.UNKNOWN) {
				temp[row - 1][column] = Macro.POTENTIAL;
			}
		} catch (IndexOutOfBoundsException ie) {
		}
		try {
			if (temp[row + 1][column] == Macro.UNKNOWN) {
				temp[row + 1][column] = Macro.POTENTIAL;
			}
		} catch (IndexOutOfBoundsException ie) {
		}
		try {
			if (temp[row][column - 1] == Macro.UNKNOWN) {
				temp[row][column - 1] = Macro.POTENTIAL;
			}
		} catch (IndexOutOfBoundsException ie) {
		}
		try {
			if (temp[row][column + 1] == Macro.UNKNOWN) {
				temp[row][column + 1] = Macro.POTENTIAL;
			}
		} catch (IndexOutOfBoundsException ie) {
		}

		temp[row][column] = Macro.NOT_APPLICABLE_TARGET;

		return temp;
	}

	/**
	 * Helper method to count amount of specific integer in 2d array
	 * 
	 * @param i - chosen integer
	 * @param a - array
	 * @return int - amount of i integers
	 */
	private int amountOf(int i, int[][] a) {
		int output = 0;
		String pout = "";
		for (int n = 0; n < Macro.DIM_ROWS; n++) {
			for (int k = 0; k < Macro.DIM_COLUMNS; k++) {
				if (a[n][k] == i) {
					pout += "||" + n + ", " + k;
					output++;
				}
			}
		}
//		if(printing) {System.out.println(pout + " ||");}			//DEBUG
		return output;
	}

	/**
	 * Give this algorithm available forts.
	 * 
	 * @param a - int[] of available forts. Index defines what fort, and value if
	 *          it's available
	 */
	public void setAvailableForts(int[] a) {
		availableForts = a;
	}

	/**
	 * Get updated list of available forts from this algorithm
	 * 
	 * @return availableForts - int[] list
	 */
	public int[] getAvailableForts() {
		return availableForts;
	}

	/**
	 * Suppress debug printing
	 */
	public void suppressOutPrint() {
		printing = false;
	}
}