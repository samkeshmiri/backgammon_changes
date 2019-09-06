package com.skeshmiri.backgammon.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Stack;

/**
 * 
 * @author 130017964
 * @author Sam Keshmiri
 * @version 5.00
 * 
 *          Board for the backgammon is represented by two arrays, both of size
 *          28: 24 playable fields, 2 Bar places for each player and 2 bearoff
 *          places for each player. One array keeps color of pieces currently
 *          stored on it and second keeps the amount of pieces stored on the
 *          field. White player moves from start of array indexes to the end,
 *          positive direction and black player moves from the end of arrays
 *          towards negative indexes in order to achieve winning condition
 * 
 */
public class Board implements Cloneable {
	public static final int WHITEBAR = 0, BLACKBAR = 25, WHITEBEAROFF = 26,
			BLACKBEAROFF = 27, TOTAL_NO_OF_FIELDS = 28,
			NO_OF_PLAYABLE_FIELDS = 26; // 0-25

	/**
	 * 2 arrays to represent amount and color of pieces on each field
	 */

	private Color[] colorArray;
	private int[] amountArray;

	private ArrayList<Move> validMoves;
	private ArrayList<Integer> possibleMoves;
	private int[] dices;

	private Color opponent;
	private Color currPlayer;
	private Stack<Integer> cachedDices;

	/**
	 * constructor to copy current board representation
	 * 
	 */

	@SuppressWarnings("unchecked")
	public Board(Board board) {
		amountArray = board.getAmountArray().clone();
		colorArray = board.getColorArray().clone();
		dices = board.getDices().clone();
		validMoves = (ArrayList<Move>) board.getValidMoves().clone();
		possibleMoves = (ArrayList<Integer>) board.getPossibleMoves().clone();
		currPlayer = board.getCurrentPlayer();
		cachedDices = (Stack<Integer>) board.cachedDices.clone();
	}

	public Board() {
		initialiseBoard();
	}

	/**
	 * Initializes backgammon board according to rules
	 */
	private void initialiseBoard() {
		// creating fields on the board
		// [id] 1-24 playable,
		// 2 for captured pieces (bar),
		// [id] 0/ white/black
		// 2 for taken away from board pieces (bear off),
		// [id] 26/27 white/black
		// total 28
		colorArray = new Color[TOTAL_NO_OF_FIELDS];
		amountArray = new int[TOTAL_NO_OF_FIELDS];
		validMoves = new ArrayList<Move>();
		cachedDices = new Stack<>();

		// create default field
		for (int i = 0; i < TOTAL_NO_OF_FIELDS; i++) {
			switch (i) {
			case 1:
				colorArray[i] = Color.WHITE;
				amountArray[i] = 2;
				break;
			case 6:
			case 13:
				colorArray[i] = Color.BLACK;
				amountArray[i] = 5;
				break;
			case 8:
				colorArray[i] = Color.BLACK;
				amountArray[i] = 3;
				break;
			case 12:
			case 19:
				colorArray[i] = Color.WHITE;
				amountArray[i] = 5;
				break;
			case 17:
				colorArray[i] = Color.WHITE;
				amountArray[i] = 3;
				break;
			case 24:
				colorArray[i] = Color.BLACK;
				amountArray[i] = 2;
				break;
			case WHITEBEAROFF:
				colorArray[i] = Color.WHITE;
				amountArray[i] = 0;
				break;
			case BLACKBEAROFF:
				colorArray[i] = Color.BLACK;
				amountArray[i] = 0;
				break;
			case WHITEBAR:
				colorArray[i] = Color.WHITE;
				amountArray[i] = 0;
				break;
			case BLACKBAR:
				colorArray[i] = Color.BLACK;
				amountArray[i] = 0;
				break;
			default:
				colorArray[i] = Color.NONE;
				amountArray[i] = 0;
				break;
			}
		}
	}

	/**
	 * Counts all the white checkers on all fields.
	 * 
	 * @return the total dice count of field 25 - pip count
	 */

	public int countWhite() {
		// pip count is negative number because it's going anti-clockwise
		// so returns the maximum pip count minus current pip count
		int max = 375; // 15 checkers * 25 fields = max total dice required to bear off
		int currentPipCount = 0;
		int whitePipCount = 0;

		for (int i = 0; i < NO_OF_PLAYABLE_FIELDS; i++) {
			int fieldCount = 0;
			if (colorArray[i] == Color.WHITE) {
				fieldCount += amountArray[i];
				fieldCount *= i;
				currentPipCount += fieldCount;
			}
		}
		whitePipCount = max - currentPipCount;
		
		return whitePipCount;
	}

	/**
	 * counts all the black checkers on all fields to give pip count
	 * 
	 * @return pip count of dice total needed to win
	 */

	public int countBlack() {
		int blackPipCount = 0;
		for (int i = 0; i < NO_OF_PLAYABLE_FIELDS; i++) {
			int fieldCount = 0;
			if (colorArray[i] == Color.BLACK) {
				fieldCount += amountArray[i];
				fieldCount *= i;
				blackPipCount += fieldCount;
			}
		}
		return blackPipCount;
	}

	/**
	 * Method checks if there are dices left to play
	 * 
	 * @return true if there are still not played dices false otherwise
	 */
	public boolean isDicesLeft() {
		if (dices == null)
			return false;
		return true;
	}

	/**
	 * Tells what are the current dices
	 * 
	 * @return an array of type integer of size 2 with integers showing what
	 *         dices represent
	 */
	public int[] getDices() {
		return this.dices;
	}

	/**
	 * clear valid moves list evaluate possible moves from dices If there are
	 * possible moves - find valid moves
	 */
	public void searchForValidMoves() {
		validMoves.clear();
		evalDices();
		if (possibleMoves.size() != 0) {
			findValidMoves();
			refineValidMoves(this, 0);
		}
	}

	/**
	 * does the same as method above: clear valid moves list evaluate possible
	 * moves from dices then find valid moves but doesn't refine moves it is
	 * used for recursive check later on
	 */
	public void searchForValidMovesNoRefining() {
		validMoves.clear();
		evalDices();
		findValidMoves();
	}

	/**
	 * Method implements backgammon game rules as on UKBGF website
	 */
	private void findValidMoves() {
		// used to check if there are pieces on the bar
		boolean barFlag = true;

		// playable area, going through all fields
		for (int i = 0; i < NO_OF_PLAYABLE_FIELDS; i++) {
			// move own piece check
			if (colorArray[i] == currPlayer && amountArray[i] > 0) {
				for (Integer amount : possibleMoves) {
					// but bear off move

					if (isAllAtHome(currPlayer)) {
						// bear off move
						// goes off the grid
						// amount = actual amount used to move the piece
						if (currPlayer == Color.WHITE) {
							if ((i + amount) >= BLACKBAR) {
								validMoves.add(new Move(MoveType.BEAROFF, i,
										BLACKBAR - i, WHITEBEAROFF, amount));
							}
						} else {
							if ((i + amount) <= WHITEBAR) {
								validMoves.add(new Move(MoveType.BEAROFF, i,
										WHITEBAR - i, BLACKBEAROFF, amount));
							}
						}
					}
					if (amount == 5)
					;
					// boundary check
					// has to be playable area only
					// move to: for white up untill BlackBar, for black up until
					// WHITEBAR
					if ((i + amount) < BLACKBAR && (i + amount) > WHITEBAR) {
						// move to own color or empty
						if (colorArray[i + amount] == currPlayer
								|| colorArray[i + amount] == Color.NONE) {
							validMoves
									.add(new Move(MoveType.NORMAL, i, amount));
							// hit move
						} else if (colorArray[i + amount] == opponent
								&& amountArray[i + amount] == 1) {
							validMoves
									.add(new Move(MoveType.CAPTURE, i, amount));
						}
					}
				}
			}
			// only moves from bar are allowed if it is not empty
			// for both players it need s to be checked so location 0 and 24
			// are checked
			if (barNotEmpty(currPlayer) && barFlag) {
				// counter will get increased by 1
				i = BLACKBAR - 1;
				barFlag = false;
			}

		}
	}

	/**
	 * Method to check if a given player has pieces on his bar
	 * 
	 * @param currPlayer
	 *            who is playing now - white/black
	 * @return
	 */
	private boolean barNotEmpty(Color currPlayer) {
		if ((currPlayer == Color.WHITE && amountArray[WHITEBAR] != 0)
				|| (currPlayer == Color.BLACK && amountArray[BLACKBAR] != 0))
			return true;
		return false;
	}

	/**
	 * Method checks if all pieces for given player are in home quadrant
	 * 
	 * @param homePlayer
	 *            current player
	 * @return true if all at home, false otherwise
	 */
	private boolean isAllAtHome(Color homePlayer) {
		// checks that no pieces of player color are outside home area
		// home for white 19-24 inclusive

		switch (homePlayer) {
		case WHITE:
			for (int i = WHITEBAR; i <= 18; i++) {
				if (colorArray[i] == Color.WHITE && amountArray[i] > 0) {
					return false;
				}
			}
			break;
		// home for black 1-6 inclusive
		case BLACK:
			for (int i = BLACKBAR; i >= 7; i--) {
				if (colorArray[i] == Color.BLACK && amountArray[i] > 0) {
					return false;
				}
			}
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * evaluates dices to find all possible move amounts from the dices
	 */
	public void evalDices() {
		possibleMoves = new ArrayList<Integer>();
		// if no more dices left, possible moves = null, no valid moves
		if (dices == null) {
			return;
		}
		if (dices.length == 2) {
			if (dices[0] == dices[1]) {
				possibleMoves.add(dices[0]);
			} else {
				possibleMoves.add(dices[0]);
				possibleMoves.add(dices[1]);
			}
		} else {
			possibleMoves.add(dices[0]);
		}
	}

	/**
	 * Moving, based on a picked move from valid moves
	 * 
	 * @param moveIndex
	 *            move that has been chosen from arrayList of valid moves
	 */
	public void move(int moveIndex) {
		Move chosenMove = validMoves.get(moveIndex);
		move(chosenMove);		
	}

	public void move(Move chosenMove) {
		int start = chosenMove.getStartField();
		int end = chosenMove.getEndField();
		switch (chosenMove.getMoveType()) {
		case NORMAL:
			// make normal move
			amountArray[start] -= 1;
			amountArray[end] += 1;

			// check if field is left empty
			checkIfFieldLeftEmpty(start);

			// check if moved to empty field
			if (colorArray[end] == Color.NONE)
				colorArray[end] = currPlayer;
			useMove(chosenMove.getMoveAmount());
			break;
		case CAPTURE:
			amountArray[start] -= 1;
			// amount at [end] stays same

			// check if field is left empty
			checkIfFieldLeftEmpty(start);

			// change color after capturing field
			colorArray[end] = currPlayer;

			// capture piece
			if (currPlayer == Color.WHITE) {
				amountArray[BLACKBAR] += 1;
			} else {
				amountArray[WHITEBAR] += 1;
			}
			useMove(chosenMove.getMoveAmount());
			break;
		case BEAROFF:
			amountArray[start] -= 1;
			if (currPlayer == Color.WHITE) {
				amountArray[WHITEBEAROFF] += 1;
			} else {
				amountArray[BLACKBEAROFF] += 1;
			}
			// check if field is left empty
			checkIfFieldLeftEmpty(start);
			useMove(chosenMove.getBEAROFFMoveAmount());
			break;
		case UNDO:
			amountArray[start] += 1;
			amountArray[end] -= 1;
			colorArray[start] = currPlayer;

			checkIfFieldLeftEmpty(end);
			break;
		default:
			break;
		}
	}

	/**
	 * Method checks if field was left empty then changes color to none but
	 * excluding blackbar, whitebar, BLACKBEAROFF, WHITEBEAROFF
	 * 
	 * @param fieldID
	 *            field to check
	 */
	private void checkIfFieldLeftEmpty(int fieldID) {
		if (amountArray[fieldID] == 0 && !(colorArray[fieldID] == Color.NONE)
				&& fieldID != BLACKBAR && fieldID != WHITEBAR
				&& fieldID != WHITEBEAROFF && fieldID != BLACKBEAROFF) {
			colorArray[fieldID] = Color.NONE;
		}
	}

	private void cacheDice(int dice) {
		// only cache up to a max of 4 dice
		if (cachedDices.size() > 3)
			cachedDices = new Stack<>();
		
		cachedDices.add(dice);
	}

	public void recoverDiceForUndo() {
		addDice(cachedDices.pop());
	}

	/**
	 * 
	 * @param moveAmount
	 */
	private void useMove(int moveAmount) {
		switch (dices.length) {
		case 4:
			dices = new int[] { dices[0], dices[0], dices[0] };
			break;
		case 3:
			dices = new int[] { dices[0], dices[0] };
			break;
		case 2:
			if (moveAmount == dices[0]) {
				dices = new int[] { dices[1] };
			} else {
				dices = new int[] { dices[0] };
			}
			// check 1st = amount, throw away 1, left 2
			// check 2nd = amount, viceversa
			// no more moves
			break;
		case 1:
			dices = null;
			// no more moves
			break;

		default:
			break;
		}

		cacheDice(moveAmount);
	}

	/**
	 * implements rule, where if with some moves you can use more moves, you
	 * have to use them, and other moves become invalid
	 * 
	 * makes a move and then checks if further moves are possible if so, counts
	 * up their move amount and returns it. Duplicates for valid moves are
	 * removed as the rule states that one of the best possible choices is taken
	 * and if move will have smaller amount than there is it is counted as
	 * invalid at recursion step 0, sums are checked against each other and the
	 * smallest values are removed as they are invalid
	 * 
	 * @param passedState
	 *            passed in state of the board to clone it and perform moves on
	 *            it
	 * @param currentRecStep
	 *            to execute stuff in the end just before sums of initial valid
	 *            moves are added up, to remove ones with least amount
	 * @return a sum of valid move amounts
	 */
	private int refineValidMoves(Board passedState, int currentRecStep) {
		Board MoveAheadBoard = new Board(passedState);
		ArrayList<Board> furtherBoards = new ArrayList<Board>();

		// Initializing array for move amounts
		Integer[] amountUsedPerMoves = new Integer[MoveAheadBoard
				.getValidMoves().size()];
		for (int i = 0; i < amountUsedPerMoves.length; i++) {
			amountUsedPerMoves[i] = 0;
		}

		// A player must use both numbers of a roll if this is legally possible
		// (or all four numbers of a double).
		for (int i = 0; i < MoveAheadBoard.getValidMoves().size(); i++) {
			furtherBoards.add(new Board(MoveAheadBoard));
			// getting last board and making a move for it
			int last = furtherBoards.size() - 1;
			amountUsedPerMoves[i] = furtherBoards.get(last).getValidMoves()
					.get(i).getMoveAmount();
			furtherBoards.get(last).move(i);
			furtherBoards.get(last).searchForValidMovesNoRefining();

			if (furtherBoards.get(last).hasValidMovesLeft()) {
				amountUsedPerMoves[i] += refineValidMoves(
						furtherBoards.get(last), currentRecStep + 1);
			}
		}

		if (currentRecStep == 0) {
			int bestMove = 0;
			// finding best possible choice
			// for black player amounts are less than zero, multiply them by -1
			// check if there were any valid moves and amount used per moves has
			// something inside

			// for black player * -1
			if (currPlayer == Color.BLACK) {
				for (int i = 0; i < amountUsedPerMoves.length; i++) {
					amountUsedPerMoves[i] *= -1;
				}
			}
			// finding best solution
			for (int i = 0; i < amountUsedPerMoves.length; i++) {
				if (amountUsedPerMoves[i] > bestMove) {
					bestMove = amountUsedPerMoves[i];
				}
			}

			// if a move has smaller of dices amount used - remove it
			// create temp array and put all the valid moves in it
			ArrayList<Move> tempMovesList = new ArrayList<Move>();
			for (int i = 0; i < amountUsedPerMoves.length; i++) {
				if (amountUsedPerMoves[i] == bestMove) {
					tempMovesList.add(validMoves.get(i));
				}
			}

			// reinitialise valid moves array with tempArray
			validMoves = new ArrayList<Move>(tempMovesList);

		}

		int returnValue = 0;

		// find what could be best move
		if (currPlayer == Color.BLACK) {
			for (int i = 0; i < amountUsedPerMoves.length; i++) {
				if (returnValue > amountUsedPerMoves[i]) {
					returnValue = amountUsedPerMoves[i];
				}
			}
		} else {
			for (int i = 0; i < amountUsedPerMoves.length; i++) {
				if (returnValue < amountUsedPerMoves[i]) {
					returnValue = amountUsedPerMoves[i];
				}
			}
		}
		
		return returnValue;
		/*
		 * RULES When only one number can be played, the player must play that
		 * number. Or if either number can be played but not both, the player
		 * must play the larger one. When neither number can be used, the player
		 * loses his turn. In the case of doubles, when all four numbers cannot
		 * be played, the player must play as many numbers as he can.
		 */
	}

	public Integer[] removeDuplicates(Integer[] arr) {
		return new HashSet<Integer>(Arrays.asList(arr)).toArray(new Integer[0]);
	}

	public GameState checkWin() {
		if (amountArray[26] == 15) {
			return GameState.WHITE_WON;
		} else if (amountArray[27] == 15) {
			return GameState.BLACK_WON;
		}
		return GameState.STILL_PLAYING;
	}

	/**
	 * Generates random boolean
	 * 
	 * @return randomly returns true for white to start or black for black to
	 *         start
	 */
	public boolean getWhoStarts() {
		return new Random().nextBoolean();
	}

	/**
	 * method to set who is currently playing and who is an opposite player in
	 * terms of colors
	 * 
	 * also sets move amounts to -1 if black player is currently playing
	 * 
	 * @param isWhite
	 *            Boolean, true means white playing, false means black is
	 *            playing
	 */
	public void setPlayers(boolean isWhite) {
		if (isWhite) {
			currPlayer = Color.WHITE;
			opponent = Color.BLACK;
		} else {
			currPlayer = Color.BLACK;
			opponent = Color.WHITE;
			for (int i = 0; i < dices.length; i++) {
				dices[i] *= (-1);
			}
		}
	}

	/**
	 * Method to check if there are valid moves left
	 * 
	 * @return true if valid moves left, false if not
	 */
	public boolean hasValidMovesLeft() {
		// if no possible moves turn changes
		if (validMoves.size() == 0)
			return false;
		return true;
	}

	/**
	 * sets dices in case of a double, creates 4 dices
	 * 
	 * @param dices
	 */
	public void setDices(int[] dices) {
		if (dices[0] == dices[1]) {
			this.dices = new int[] { dices[0], dices[0], dices[0], dices[0] };
		} else {
			this.dices = dices;
		}
	}

	/**
	 * When undoing, adds dice back to dices list
	 * 
	 * @param newDice cached dice popped from stack
	 */
	public void addDice(int newDice) {
		ArrayList<Integer> diceList = new ArrayList<Integer>();
		int dicesLength = 0;
		if (dices != null && dices.length > 0) {
			for (int dice : dices) {
				diceList.add(dice);
			}
			dicesLength = this.dices.length;
		}
		
		diceList.add(newDice);
		this.dices = new int[dicesLength + 1];
		for (int i = 0; i < diceList.size(); i++) {
			this.dices[i] = diceList.get(i);
		}
	}
	

	public ArrayList<Integer> getPossibleMoves() {
		return possibleMoves;
	}

	public Color[] getColorArray() {
		return colorArray;
	}

	public int[] getAmountArray() {
		return amountArray;
	}

	public Color getCurrentPlayer() {
		return this.currPlayer;
	}

	public ArrayList<Move> getValidMoves() {
		return this.validMoves;
	}
}