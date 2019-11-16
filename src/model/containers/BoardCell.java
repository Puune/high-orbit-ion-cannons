package model.containers;

public class BoardCell {
	private int type;
	private int row;
	private int column;
	
	public BoardCell() {
		this.type = 0;
		this.row = 0;
		this.column = 0;
	}
	
	public BoardCell(int type, int row, int column) {
		this.type = type;
		this.row = row;
		this.column = column;
	}
	
	public BoardCell(int row, int column) {
		this.row = row;
		this.column = column;
	}

	public void addToType(int a) {
		type += a;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}
	
	@Override
	public String toString() {
		return "type: " + type + "  -  pos: " + row + ", " + column;
	}
}
