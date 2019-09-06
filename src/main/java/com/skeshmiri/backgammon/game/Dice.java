package com.skeshmiri.backgammon.game;

import java.util.Random;

/**
 * 
 * @author 130017964
 * @author Sam Keshmiri
 * @version 5.0
 * 
 *          Used to generate pair of random dice in range (1-6 inclusive) 
 */
public class Dice {
	private Random rn;
	private int diceX;
	private int diceY;

	public Dice() {
		rn = new Random();
	}

	/**
	 * nextInt(6) not (5).
	 * nextInt(5) RNG means a 5 is incredibly unlikely so 5+1 never occured
	 */
	public void throwDices() {	
		diceX = 1 + rn.nextInt(6);
		diceY = 1 + rn.nextInt(6);
	}

	/**
	 * @return array of 2 dices
	 */
	public int[] getDices() {
		return new int[] { diceX, diceY };
	}
}
