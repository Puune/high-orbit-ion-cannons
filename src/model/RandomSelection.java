package model;

import model.containers.BoardCell;
import model.containers.GameBoard;
import java.util.ArrayList;

import macro.Macro;

/**
 * @author Panu
 *This class generates smart-random firing solutions for the ai
 */
public class RandomSelection {
	
	GameBoard anonPlayerBoard;
	
	public BoardCell getNextSolution(GameBoard anonPlayerBoard) {
		this.anonPlayerBoard = anonPlayerBoard;
		
		ArrayList<BoardCell> feasibleTargets = new ArrayList<>();
		for(int i = 0; i < Macro.DIM_ROWS; i++) {
			for(int j = 0; j < Macro.DIM_COLUMNS; j++) {
				if(anonPlayerBoard.getCell(i, j).getType() == (Macro.EMPTY_OR_UNKNOWN)) {
					feasibleTargets.add(anonPlayerBoard.getCell(i, j));
				}
			}
		}
		
		//Elimination round 1
		//If cell has no empty cells surrounding itself, it is eliminated
		for (int i = 0; i < feasibleTargets.size(); i++) {
			int index = 0;
			BoardCell temp = feasibleTargets.get(i);
			BoardCell toBeTested;
			
			try {
				toBeTested = anonPlayerBoard.getCell(temp.getRow() - 1, temp.getColumn());
				index += toBeTested.getType() > Macro.EMPTY_OR_UNKNOWN ? 1 : 0;
			} catch (IndexOutOfBoundsException ie) {
				index++;
			}
			try {
				toBeTested = anonPlayerBoard.getCell(temp.getRow() + 1, temp.getColumn());
				index += toBeTested.getType() > Macro.EMPTY_OR_UNKNOWN ? 1 : 0;
			} catch (IndexOutOfBoundsException ie) {
				index++;
			}
			try {
				toBeTested = anonPlayerBoard.getCell(temp.getRow(), temp.getColumn() - 1);
				index += toBeTested.getType() > Macro.EMPTY_OR_UNKNOWN ? 1 : 0;
			} catch (IndexOutOfBoundsException ie) {
				index++;
			}
			try {
				toBeTested = anonPlayerBoard.getCell(temp.getRow(), temp.getColumn() + 1);
				index += toBeTested.getType() > Macro.EMPTY_OR_UNKNOWN ? 1 : 0;
			} catch (IndexOutOfBoundsException ie) {
				index++;
			}

			if (index == 4) {
				feasibleTargets.remove(i);
			}
		}

		//Elimination round 2
		//As forts cannot touch each other, remove cells touching forts
		for (int i = 0; i < feasibleTargets.size(); i++) {
			BoardCell temp = feasibleTargets.get(i);
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
				feasibleTargets.remove(i);
			}
		}
				
		int selection =  (int) (Math.ceil(Math.random() * feasibleTargets.size()) - 1);
		return feasibleTargets.get(selection);	
	}

}
