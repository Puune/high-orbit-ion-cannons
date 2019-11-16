package model;

import java.util.ArrayList;

import macro.Macro;
import model.containers.BoardCell;
import model.containers.GameBoard;


/**
 * @author Panu
 *This class contains all operation-methods that allow ai to play against an opponent. Logic should flow as follows:
 * 1: Players turn setNewHit()  
 * 2: Robots turn: playAiTurn()  
 * 3: Robots turn: giveAiFeedback()  
 * : repeat
 */
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
	 * reference to pinpoint algorithm class
	 */
	private PinpointAlgorithm pin;
	
	/**
	 * reference to Random Selection class
	 */
	private RandomSelection randSel = new RandomSelection();


	/**
	 * Store last place this robot tried to hit. References straight to a cell in anonPlayerBoard
	 */
	private BoardCell lastHit;

	private int[] availableForts = Macro.initFortMap();


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
	 * It is robot's turn. Order the robot to play it's turn.
	 * 
	 * @return int[] - An array with (Row, column) integers. The return represents
	 *         the location the robot has decided to hit.
	 */
	public int[] playAiTurn() {
		if (firingParameter == 0) {
			lastHit = randSel.getNextSolution(anonPlayerBoard);
			int[] n = { lastHit.getRow(), lastHit.getColumn() };
			return n;

		} else {
			lastHit = pinpointFiringSolution();
			int[] n = { lastHit.getRow(), lastHit.getColumn() };
			return n;
		}
	}

	/**
	 * Player will inform whether robot has hit player's target
	 * 
	 * @param hit - hit is true when target has been hit, false when not.
	 */
	public void giveAiFeedback(boolean hit) {
		if (hit) {
			lastHit.setType(Macro.FORT_HIT);
			if (firingParameter == 0) {
				// first time hitting a new fort boots up a new targeting algorithm
				pin = new PinpointAlgorithm(lastHit.getRow(), lastHit.getColumn(), anonPlayerBoard.getHitsAsArray());
				pin.setAvailableForts(availableForts);
			}
			firingParameter = 1;

		} else {
			lastHit.setType(Macro.EMPTY_HIT);
		}
	}

	/**
	 * Calculate a firing solution based on last hits When algorithm decides that it
	 * should not be executed anymore, it return null which is catched.
	 * 
	 * @return BoardCell - Return reference to a cell in anonPlayerBoard
	 */
	private BoardCell pinpointFiringSolution() {

		try {
			BoardCell temp = pin.getSolution(lastHit);
			return anonPlayerBoard.getCell(temp.getRow(), temp.getColumn());

		} catch (Exception e) {
			availableForts = pin.getAvailableForts();

			firingParameter = 0; // switch to random firing solutions
			return randSel.getNextSolution(anonPlayerBoard);
		}
	}

	
	/**
	 * DEBUGGING & TESTING
	 * Manually override firing parameter
	 * @param p
	 * @throws Exception 
	 */
	public void setFiringParameter(int p) throws Exception {
		if(p == 0 || p == 1) {
			firingParameter = p;
		} else {
			throw new Exception("Invali firing parameter");
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

}
