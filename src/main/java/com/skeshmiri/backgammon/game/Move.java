package com.skeshmiri.backgammon.game;

/**
 * 
 * @author 130017964
 * @author Sam Keshmiri
 * @version 5.00
 * 
 *  Class stores move for Backgammon
 * 
 *  stores start position, amount moved, end position
 * 	in special case for bear off move stores amount from dice needed and
 *  actual amount needed to reach bear off.
 * 
 *  Start field is white home at 1 
 * 
 *	Getter and setters for undo move
 * 
 */

public class Move {
	private MoveType moveType;
	private int startField;
	private int endField;
	private int moveAmount;
	private int specialBearOff;

	/**
	 * constructor for Move object
	 */
	public Move(MoveType mt, int start, int moveAmount) {
		moveType = mt;
		startField = start;
		this.moveAmount = moveAmount;
		endField = start + moveAmount;
	}

	// for bear off move
	public Move(MoveType mt, int start, int moveAmount, int endPos,
			int actualMoveAmount) {
		moveType = mt;
		startField = start;
		this.moveAmount = moveAmount;
		endField = endPos;
		specialBearOff = actualMoveAmount;
	}

	public MoveType getMoveType() {
		return moveType;
	}

	public void setMoveTypeUndo() {
		moveType = MoveType.UNDO;
	}

	public int getStartField() {
		return startField;
	}

	public void setStartField(int endPos) {
		startField = endPos;
	}

	public int getEndField() {
		return endField;
	}

	public void setEndField(int startPos) {
		endField = startPos;
	}

	public int getMoveAmount() {
		return moveAmount;
	}

	public int getBEAROFFMoveAmount() {
		return specialBearOff;
	}
}
