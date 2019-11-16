package controller;

import java.util.Scanner;

import model.AiLogic;
import model.containers.BoardCell;

public class ProtoView {
	
	static int SCORE = 13;
	
	
	/**
	 * 0: PLAY AGAINST AI
	 * 1: AI AGAINST AI
	 */
	static int SELECT = 1;
	static Scanner scan = new Scanner(System.in);

	
	
	/**
	 * Main
	 * @param args
	 */
	public static void main(String[] args) {
		if(SELECT==0) {
			AiLogic gl1 = new AiLogic();
			AiLogic gl2 = new AiLogic();
			
			try {
				gl1.getGameBoard().buildFort(0, 5, 5);
				gl1.getGameBoard().buildFort(1, 0, 0);
				gl1.getGameBoard().buildFort(2, 4, 0);
				gl1.getGameBoard().buildFort(3, 0, 5);
				
				gl2.getGameBoard().buildFort(0, 5, 3);
				gl2.getGameBoard().buildFort(1, 1, 2);
				gl2.getGameBoard().buildFort(2, 4, 0);
				gl2.getGameBoard().buildFort(3, 0, 5);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
			int score1 = 0;
			int score2 = 0;
			
			System.out.println("GUIDE \n"
					+ "0=empty   1=fort    2=empty hit    3=fort hit\n\n");
			
			System.out.println("This is your forts                      this is your target board");
			graphicalDemo(gl1, gl2);
			
			for(int i=0; i<65; i++) {
				//pl1 turn
				System.out.println("Select row");
				int r = scan.nextInt();
				
				System.out.println("Select col");
				int c = scan.nextInt();
				
				
				BoardCell temp = new BoardCell(r, c);
				gl1.selectLastHit(r, c);
				boolean hit = gl2.setNewHit(temp.getRow(), temp.getColumn());
				
				if(hit) { System.out.println("You hit!"); }
				gl1.giveFeedback(hit);
				
				System.out.print("player1 hit: " + temp.getRow() + " " + temp.getColumn() + "     ");
				
				if(hit==true) {
					score1++;
				}
				//turn done
				
				//pl2 turn
				int[] n = gl2.playRobotsTurn();
				temp = new BoardCell(n[0], n[1]);
				hit = gl1.setNewHit(temp.getRow(), temp.getColumn());
				System.out.print("player2 hit: " + temp.getRow() + " " + temp.getColumn());
		
				gl2.giveFeedback(hit);
				
				if(hit==true) {
					score2++;
				}
				//turn done
				System.out.println("\n");
				graphicalDemo(gl1, gl2);
				
				try {
					Thread.sleep( 2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				//checks
				if(score1==SCORE || score2 == SCORE) {
					System.out.println("fin");
					System.out.println("turns: " + (i / 2));
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) { }
					System.exit(0);
				} else if(i==64) {
					System.exit(-1);
				}
			}
		} 
		
		
		else {
			AiLogic gl1 = new AiLogic();
			AiLogic gl2 = new AiLogic();
			
			try {
				gl1.getGameBoard().buildFort(0, 5, 5);
				gl1.getGameBoard().buildFort(1, 0, 0);
				gl1.getGameBoard().buildFort(2, 4, 0);
				gl1.getGameBoard().buildFort(3, 0, 5);
				
				gl2.getGameBoard().buildFort(0, 5, 3);
				gl2.getGameBoard().buildFort(1, 1, 2);
				gl2.getGameBoard().buildFort(2, 4, 0);
				gl2.getGameBoard().buildFort(3, 0, 5);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
			int score1 = 0;
			int score2 = 0;
			
			System.out.println("GUIDE \n"
					+ "0=empty   1=fort    2=empty hit    3=fort hit\n\n");
			
			System.out.println("This is your forts                      this is your target board");
			graphicalDemo(gl1, gl2);
			
			for(int i=0; i<65; i++) {
				//pl1 turn
				int[] n = gl1.playRobotsTurn();
						
				BoardCell temp = new BoardCell(n[0], n[1]);
				gl1.selectLastHit(n[0], n[1]);
				boolean hit = gl2.setNewHit(temp.getRow(), temp.getColumn());
				
				if(hit) { System.out.println("You hit!"); }
				gl1.giveFeedback(hit);
				
				System.out.print("player1 hit: " + temp.getRow() + " " + temp.getColumn() + "     ");
				
				if(hit==true) {
					score1++;
				}
				//turn done
				
				//pl2 turn
				n = gl2.playRobotsTurn();
				temp = new BoardCell(n[0], n[1]);
				hit = gl1.setNewHit(temp.getRow(), temp.getColumn());
				System.out.print("player2 hit: " + temp.getRow() + " " + temp.getColumn());
		
				gl2.giveFeedback(hit);
				
				if(hit==true) {
					score2++;
				}
				//turn done
				System.out.println("\n");
				graphicalDemo(gl1, gl2);
				
				try {
					Thread.sleep( 2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				//checks
				if(score1==SCORE || score2 == SCORE) {
					System.out.println("fin");
					System.out.println("turns; " + i / 2);
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {}
					System.exit(0);
				} else if(i==64) {
					System.exit(-1);
				}
			}			
		}
	}

	
	public static void graphicalDemo(AiLogic g1, AiLogic g2) {
		
		int[][] board1 = g1.getGameBoard().getBoardAs2dArray();
		int[][] board2 = g2.getGameBoard().getBoardAs2dArray();
		
		String combine ="";
		String a = "";
		String b ="";
		System.out.println("     0   1   2   3   4   5   6   7                 0   1   2   3   4   5   6   7\n"
				+ "______________________________________________________________________________________");
	
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++) {
				if(j==0) {
					a += i + " | ";
					b += i + " | ";
				}
				a += String.valueOf(board1[i][j]) + "   ";
				b += String.valueOf(board2[i][j]) + "   ";
			}
			combine += a + "         " + b + "\n";
			a = "";
			b = "";
		}
		System.out.println(combine);
	}
}
