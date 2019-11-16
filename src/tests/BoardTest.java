package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import macro.Macro;
import model.containers.GameBoard;

class BoardTest {
	
	static GameBoard board;
	
	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		board = new GameBoard();
	}
	
	@BeforeEach
	public void setBeforeEach() {
		board.hardReset();
	}

	@Test
	@DisplayName("Test hitting the board")
	public void testHit() {
		board.hit(2, 2);
		assertEquals(2, board.getCell(2, 2).getType());
	}
	
	@Test
	@DisplayName("Test hard reset")
	public void testReset() {
		board.hit(2, 2);
		board.hardReset();
		assertEquals(Macro.EMPTY_OR_UNKNOWN, board.getCell(2, 2).getType(), "Fail: Board not hard reset");
	}
	
	@Test
	@DisplayName("Test building a fort")
	public void testBuildForts() {
		try {
			board.buildFort(1, 0, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(Macro.FORT, board.getCell(0,0).getType(), "Fail: Fort was not built");
		assertEquals(Macro.FORT, board.getCell(1, 0).getType(), "Fail: fort was not built");
		assertEquals(Macro.EMPTY_OR_UNKNOWN, board.getCell(7, 7).getType(), "Fail: there are forts in wrong places");
	}
	
	
	@Test
	@DisplayName("Test hard reset")
	public void testHardReset() {
		try {
			board.buildFort(0, 0, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		board.hit(0, 1); board.hit(1, 0);
		board.softReset();
		assertEquals(Macro.FORT, board.getCell(1, 0).getType(), "Fail: Should be fort but is..");
		assertEquals(Macro.EMPTY_OR_UNKNOWN, board.getCell(5, 5).getType(), "Fail: Should be empty but is..");
	}
	
	@Test
	@RepeatedTest(20)
	@DisplayName("Test self building forts")
	public void testSelfBuild() {
		board = new GameBoard();
		board.selfCreateGameBoard();
		
		int[][] asArray = board.getBoardAs2dArray();
		int amount = 0;
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++) {
				if(asArray[i][j]==Macro.FORT) {
					amount++;
				}
			}
		}
		System.out.println("__________________________________________");
		assertEquals(14, amount, "Wrong amount of forts");
	}
}
