package com.skeshmiri.backgammon.game;

import static org.junit.Assert.*;
import java.util.ArrayList;

import org.junit.Test;
import com.skeshmiri.backgammon.game.Board;
import com.skeshmiri.backgammon.game.Move;
import com.skeshmiri.backgammon.game.MoveType;
import com.skeshmiri.backgammon.gui.MainWindow;

public class BoardTests {

    @Test
    public void testAddDice(){
        Board board = new Board();
        int[] dices = new int[]{3, 4, 5};
        board.setDices(dices);
        board.addDice(8);
        assertArrayEquals(board.getDices(), new int[]{3, 4, 5, 8});
    }

    @Test
    public void testUndoRecoversDice() {
        int[] dices = new int[] { 4, 8 };
        int[] expectedDices = new int[] { 8, 4 };
        Board board = new Board();
        board.setPlayers(true);
        board.setDices(dices);
        board.searchForValidMoves();
        Move move = board.getValidMoves().get(0);
        board.move(move);
        Move undoMove = new Move(MoveType.UNDO, move.getStartField(), move.getMoveAmount());
        board.move(undoMove);
        board.recoverDiceForUndo();
        int[] dicesAfterUndo = board.getDices();
        assertArrayEquals(expectedDices, dicesAfterUndo);
    }

    @Test
    public void testCountBlackStart() {
        Board board = new Board();
        assertEquals(board.countBlack(), 167);
    }

    @Test
    public void testCountWhiteStart() {
        Board board = new Board();
        assertEquals(board.countWhite(), 167);
    }

    @Test
    public void testCountBlackMoved() {
        int[] dices = new int[] { 4, 6 };
        int expectedCount = 157;
        Board board = new Board();
        board.setDices(dices);
        board.setPlayers(false);
        board.searchForValidMoves();
        Move move = board.getValidMoves().get(0);
        board.move(move);
        board.searchForValidMoves();
        move = board.getValidMoves().get(0);
        board.move(move);
        assertEquals(expectedCount, board.countBlack());
    }

    @Test
    public void testCountWhiteMovedDouble() {
        int[] dices = new int[] { 6, 6, 6, 6 };
        Board board = new Board();
        board.setDices(dices);
        board.setPlayers(false);

        board.searchForValidMoves();
        board.move(board.getValidMoves().get(0));

        board.searchForValidMoves();
        board.move(board.getValidMoves().get(0));

        board.searchForValidMoves();
        board.move(board.getValidMoves().get(0));

        board.searchForValidMoves();
        board.move(board.getValidMoves().get(0));

        assertEquals(143, board.countBlack());
    }   

}