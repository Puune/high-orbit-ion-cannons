package model;

import java.util.ArrayList;

import macro.Macro;
import model.containers.BoardCell;
import model.containers.GameBoard;


public class AiLogic {
	
	/**
	 * myBoard has robots targets and opponents hits on it
	 */
	private GameBoard aiTargetBoard = new GameBoard();


	/**
	 *  anonPlayerBoard is an empty board that the robot uses to store it's observations
	 */
	private GameBoard anonPlayerBoard = new GameBoard();


	/**
	 * 	Parameter that tells firing solutions-state machine what routine it is doing 
	 * 0 - Smart random firing 
	 * 1 - Ai has found a fort and tries to destroy it
	 */
	private int firingParameter = 0;

	/**
	 * reference to pinpoint algorithm
	 */
	private PinpointAlgorithm pin;


	/**
	 * Store last place this robot tried to hit. References straight to a cell in anonPlayerBoard
	 */
	private BoardCell lastHit;

	private int[] availableForts = Macro.initFortMap();

	private boolean printing = true; // DEBUG
	
	/**
	 * DEBUGGING & TESTING
	 * Give this logic engine an anonymous board, that the robot will project its
	 * view of the opponents board. Will probably be used only in debugging
	 * 
	 * @param board
	 */
	public void setAnonPlayerBoard(GameBoard board) {
		anonPlayerBoard = board;
	}

	/**
	 * DEBUGGING & TESTING
	 * Give this logic engine a board that holds robots targets
	 * @param board
	 */
	public void setAiBoard(GameBoard board) {
		aiTargetBoard = board;
	}

	/**
	 * It is player's turn. Players new hit will be given to ai. Ai will respond,
	 * @param row    - row numbers start from 0
	 * @param column - column numbers start from 0
	 * @return boolean - True if hit hit a fort
	 */
	public boolean setNewHit(int row, int column) {
		return aiTargetBoard.hit(row, column);
	}

	/**
	 * Player will inform whether robot has hit player's target
	 * 
	 * @param hit - hit is true when target has been hit, false when not.
	 */
	public void giveFeedback(boolean hit) {
		if (hit) {
			lastHit.setType(Macro.FORT_HIT);
			if (firingParameter == 0) {
				// first time hitting a new fort boots up a new targeting algorithm
				pin = new PinpointAlgorithm(lastHit.getRow(), lastHit.getColumn(), anonPlayerBoard.getHitsAsArray());
				pin.setAvailableForts(availableForts);

//				if(!printing) { pin.suppressOutPrint(); System.out.println("___NEW PINPOINT ALGORITHM___");}	//DEBUG
			}
			firingParameter = 1;

		} else {
			lastHit.setType(Macro.EMPTY_HIT);
		}
	}
	
	/**
	 * DEBUGGING & TESTING
	 * @param row
	 * @param column
	 */
	public void selectLastHit(int row, int column) {
		lastHit = anonPlayerBoard.getCell(row, column);
	}

	/**
	 * It is robot's turn. Order the robot to play it's turn.
	 * 
	 * @return int[] - An array with (Row, column) integers. The return represents
	 *         the location the robot has decided to hit.
	 */
	public int[] playRobotsTurn() {
		if (firingParameter == 0) {
			lastHit = randFiringSolution();
			int[] n = { lastHit.getRow(), lastHit.getColumn() };
//			if (printing) {	System.out.println("selected: " + lastHit.getRow() + ", " + lastHit.getColumn()); } // DEBUG
			return n;

		} else if (firingParameter == 1) {

			lastHit = pinpointFiringSolution();
			int[] n = { lastHit.getRow(), lastHit.getColumn() };
			return n;

		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	/**
	 * DEBUGGING & TESTING
	 * Let gameboard be manipulated or observed
	 * 
	 * @return GameBoard - board with computer's targets.
	 */
	public GameBoard getGameBoard() {
		return aiTargetBoard;
	}

	/**
	 * DEBUGGING & TESTING
	 * @return anonPlayerBoard
	 */
	public GameBoard getAnonPlayerboard() {
		return anonPlayerBoard;
	}
	
	/**
	 * Generate a smart random firing solution
	 * 
	 * @return BoardCell - return reference to a cell in anonPlayerBoard
	 */
	private BoardCell randFiringSolution() {
		// Get all possible targets and set them in a linear list
		ArrayList<BoardCell> availableTargets = new ArrayList<>();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (anonPlayerBoard.getCell(i, j).getType() < Macro.EMPTY_HIT) {
					availableTargets.add(anonPlayerBoard.getCell(i, j));
				}
			}
		}

//		Elimination 1
//		Don't fire in a surrounded square, there is no 1-piece fort
		for (int i = 0; i < availableTargets.size(); i++) {
			int index = 0;
			BoardCell temp = availableTargets.get(i);

			try {
				index += anonPlayerBoard.getCell(temp.getRow() - 1, temp.getColumn()).getType() > 1 ? 1 : 0;
			} catch (IndexOutOfBoundsException ie) {
				index++;
			}
			try {
				index += anonPlayerBoard.getCell(temp.getRow() + 1, temp.getColumn()).getType() > 1 ? 1 : 0;
			} catch (IndexOutOfBoundsException ie) {
				index++;
			}
			try {
				index += anonPlayerBoard.getCell(temp.getRow(), temp.getColumn() - 1).getType() > 1 ? 1 : 0;
			} catch (IndexOutOfBoundsException ie) {
				index++;
			}
			try {
				index += anonPlayerBoard.getCell(temp.getRow(), temp.getColumn() + 1).getType() > 1 ? 1 : 0;
			} catch (IndexOutOfBoundsException ie) {
				index++;
			}

			if (index == 4) {
//				System.out.println("ELIMINATION 1");			//DEBUG
				availableTargets.remove(i);
			}
		}

//		Elimination 2
//		Don't fire next to a fort, forts cannot be placed touching each other
		for (int i = 0; i < availableTargets.size(); i++) {
			BoardCell temp = availableTargets.get(i);
			boolean remove = false;

			try {
				if (anonPlayerBoard.getCell(temp.getRow() - 1, temp.getColumn()).getType() == Macro.FORT_HIT) {
					remove = true;
				}
			} catch (IndexOutOfBoundsException ie) {
			}
			try {
				if (anonPlayerBoard.getCell(temp.getRow() + 1, temp.getColumn()).getType() == Macro.FORT_HIT) {
					remove = true;
				}
			} catch (IndexOutOfBoundsException ie) {
			}
			try {
				if (anonPlayerBoard.getCell(temp.getRow(), temp.getColumn() - 1).getType() == Macro.FORT_HIT) {
					remove = true;
				}
			} catch (IndexOutOfBoundsException ie) {
			}
			try {
				if (anonPlayerBoard.getCell(temp.getRow(), temp.getColumn() + 1).getType() == Macro.FORT_HIT) {
					remove = true;
				}
			} catch (IndexOutOfBoundsException ie) {
			}

			if (remove) {
//				if(printing) {System.out.println("ELIMINATION 2");}		//DEBUG
				availableTargets.remove(i);
			}
		}
//		if(printing) {System.out.println("AVAILABLE --: " + availableTargets.size());} 		//DEBUG
		return availableTargets.get(getRandomIndexOf(availableTargets));

	}

	/**
	 * Calculate a firing solution based on last hits When algorithm decides that it
	 * should not be executed anymore, it return null which is catched.
	 * 
	 * @return BoardCell - Return reference to a cell in anonPlayerBoard
	 */
	private BoardCell pinpointFiringSolution() {
//		if(printing) {System.out.println("Called PinPoint From GameLogic");	}	//DEBUG

		try {
			BoardCell temp = pin.getSolution(lastHit);
			return anonPlayerBoard.getCell(temp.getRow(), temp.getColumn());

		} catch (Exception e) {
//			if(printing) {System.out.println("Fort found !!!");	}		//DEBUG
			availableForts = pin.getAvailableForts();

			firingParameter = 0; // switch to random firing solutions
			return randFiringSolution();

		}
	}

	/**
	 * GIve list, get random member index. Helper class
	 * 
	 * @param a - ArrayList
	 * @return int - a index of random member
	 */
	private int getRandomIndexOf(ArrayList a) {
		return (int) (Math.ceil(Math.random() * a.size()) - 1);
	}
	
	
	/**
	 * Manually override firing parameter
	 * @param p
	 */
	public void setFiringParameter(int p) {
		firingParameter = p;
	}

	/**
	 * Suppress debug printing
	 */
	public void suppressOutPrint() {
		printing = false;
		aiTargetBoard.suppressoutPrint();
		anonPlayerBoard.suppressoutPrint();
	}
}
