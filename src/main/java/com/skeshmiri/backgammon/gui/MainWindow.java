package com.skeshmiri.backgammon.gui;

import com.skeshmiri.backgammon.game.Board;
import com.skeshmiri.backgammon.game.Dice;
import com.skeshmiri.backgammon.game.GameState;
import com.skeshmiri.backgammon.game.Move;

import java.awt.Font; 
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import com.skeshmiri.backgammon.utils.ResourceLoader;

/**
 * @author 130017964
 * @author Sam Keshmiri
 * @version 5.00 
 * 
 *          GUI Backgammon 5.00
 * 
 *          Bundles game logic and user input together
 * 
 */
public class MainWindow extends JFrame implements ActionListener,
	MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;

	// gui positioning constants
	private static final int LEFT_BOUNDARY = 50;
	private static final int RIGHT_BOUNDARY = 710;
	private static final int TOP_BOUNDARY = 25;
	private static final int BOTTOM_BOUNDARY = 570;
	private static final int PADDING = 10;
	private static final int HORIZONTAL_HALFWAY = 300;
	private static final int NUMBER_OF_STONES = 30;
	private static final int STONE_SIZE = 40;
	private static final int MAX_NO_PER_FIELD_NO_STACK = 6;

	// coords for player label
	// x coord, y coord, width, height
	private static final int[] INFO_LABEL_BOUNDS = new int[] { 350, 560, 100,
			20 };

	// co-ords for white pip count label
	private static final int[] PIP_WHITE_LABEL_BOUNDS = new int[] { 120, 15, 200,
			20 };

	// co-ords for black pip count label
	private static final int[] PIP_BLACK_LABEL_BOUNDS = new int[] { 475, 15, 200,
		20 };



	/**
	 * Configuring dices
	 */
	private static final int DICE_SIZE = 50;
	private static final int DICE_X_COORD = 90;
	private static final int DICE_Y_COORD = 270;
	private static final int DICES_HORIZ_SPACE = 60;

	/**
	 * GUI objects
	 */
	private JPanel mainContentPane;
	private JLayeredPane gamePanel;
	private JPanel endTurnButtonPanel;
	private JPanel rollDiceButtonPanel;
	private JButton rollDiceButton;
	private JButton endTurnButton;
	private JLabel background;
	private JMenuBar menuBar;
	private JMenu menuGameTitle, aboutGameTitle;
	private JMenuItem newGame, exit, help, undoButton;
	private JLabel[] stones;
	private Dimension preferredSize;

	// highlight locations
	private JLabel[] highlights;
	// prints whos turn it is
	private JLabel whosTurn;

	// prints pip counts
	private JLabel pipCountWhite;
	private JLabel pipCountBlack;

	/**
	 * Importing images for Stones
	 */
	private final ImageIcon blackStoneImg = new ImageIcon(ResourceLoader.getPathOf("img/blackChecker.png"));
	private final ImageIcon whiteStoneImg = new ImageIcon(ResourceLoader.getPathOf("img/whiteChecker.png"));

	/**
	 * Creating Array of ImageIcon to store dice faces icons, 6 for each dice
	 * face and 0 is empty for easier use of pics in a system
	 */

	private final ImageIcon[] diceFaces = new ImageIcon[] {
			new ImageIcon("empty"), new ImageIcon(ResourceLoader.getPathOf("img/1.gif")),
			new ImageIcon(ResourceLoader.getPathOf("img/2.gif")), new ImageIcon(ResourceLoader.getPathOf("img/3.gif")),
			new ImageIcon(ResourceLoader.getPathOf("img/4.gif")), new ImageIcon(ResourceLoader.getPathOf("img/5.gif")),
			new ImageIcon(ResourceLoader.getPathOf("img/6.gif")) };



	private final ImageIcon highlightImg = new ImageIcon(ResourceLoader.getPathOf("img/highlight.png"));
	private final ImageIcon highlightFlipped = new ImageIcon(ResourceLoader.getPathOf("img/highlightFlipped.png"));
	private final ImageIcon highlightBearOffIcon = new ImageIcon(ResourceLoader.getPathOf("img/highlightBearOff.png"));

	/**
	 * array to store the two dices
	 */
	private JLabel dices[];
	private Stack<Move> undoStack;
	private ArrayList<Integer> undoDice;

	public volatile boolean finished;

	// vars needed for stones
	private int stoneDragged = NUMBER_OF_STONES;
	private int clickX = 0;
	private int clickY = 0;
	private int initialX;
	private int initialY;
	private HashMap<com.skeshmiri.backgammon.game.Color, ImageIcon> stoneImage;
	private HashMap<Integer, Zone> zones;
	private HashMap<Integer, int[]> locations;
	private int stackProportion;

	private Dice dice;
	private volatile Board board;
	private boolean isMovesLeft;
	private boolean isDicesLeft;
	private boolean isWhite;

	private int currStone;
	private int chosenMove;

	/**
	 * constructor create gui initialise everything
	 */
	public MainWindow() {
		// creates main interface of a program
		createGUI();
		// set pictures for stones
		initStoneColors();
		// create default place for each stone on each field
		initLocs();
		// create zones for each field for dropping stones
		initFieldZones();
		// creates highlights for valid locations
		initHighLights();
		// no stone selected
		currStone = NUMBER_OF_STONES;
	}

	private void createGUI() {
		preferredSize = new Dimension(800, 600);
		mainContentPane = new JPanel();
		mainContentPane.setOpaque(true);
		mainContentPane.setLayout(null);
		mainContentPane.setBackground(new Color(0, 0, 0));
		mainContentPane.setPreferredSize(preferredSize);
		gamePanel = new JLayeredPane();
		gamePanel.setBackground(new Color(200, 200, 200));
		gamePanel.setBounds(0, 0, 800, 600);
		gamePanel.setOpaque(true);
		setSize(preferredSize);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		setTitle("Backgammon");
		background = new JLabel(new ImageIcon(ResourceLoader.getPathOf("img/board.png")));
		background.setBounds(0, 0, 800, 600);
		createMenu();

		// initialising stones;
		initStones();
		// adding stones to game panel
		for (int i = 0; i < stones.length; i++) {
			gamePanel.add(stones[i], 0);
		}
		// setting background to 2 Z order
		gamePanel.add(background, 2);
		// set Z level to 0
		for (int i = 0; i < stones.length; i++) {
			gamePanel.setComponentZOrder(stones[i], 0);
		}

		/**
		 * creating dices representation
		 */

		dices = new JLabel[4];

		for (int i = 0; i < dices.length; i++) {
			dices[i] = new JLabel();
			/**
			 * 0 first time + horizontal space 2nd time through the loop
			 */
			dices[i].setBounds(DICE_X_COORD + (i * DICES_HORIZ_SPACE),
					DICE_Y_COORD, DICE_SIZE, DICE_SIZE);
			;
			dices[i].setVisible(true);
			gamePanel.add(dices[i]);
			gamePanel.setComponentZOrder(dices[i], 0);
		}

		// Creating information label
		whosTurn = new JLabel();
		whosTurn.setBounds(INFO_LABEL_BOUNDS[0], INFO_LABEL_BOUNDS[1],
				INFO_LABEL_BOUNDS[2], INFO_LABEL_BOUNDS[3]);
		whosTurn.setVisible(true);

		// whosTurn.;
		whosTurn.setForeground(Color.WHITE);
		whosTurn.setFont(new Font("Courier New", Font.PLAIN, 14));
		gamePanel.add(whosTurn);
		gamePanel.setComponentZOrder(whosTurn, 1);
		

		// creating white pip count label
		pipCountWhite = new JLabel();
		pipCountWhite.setBounds(PIP_WHITE_LABEL_BOUNDS[0], PIP_WHITE_LABEL_BOUNDS[1],
		PIP_WHITE_LABEL_BOUNDS[2], PIP_WHITE_LABEL_BOUNDS[3]);
		pipCountWhite.setVisible(true);
		pipCountWhite.setForeground(Color.WHITE);
		pipCountWhite.setFont(new Font("Arial", Font.PLAIN, 16));
		gamePanel.add(pipCountWhite);
		gamePanel.setComponentZOrder(pipCountWhite, 1);

		// creating black pip count label
		pipCountBlack = new JLabel();
		pipCountBlack.setBounds(PIP_BLACK_LABEL_BOUNDS[0], PIP_BLACK_LABEL_BOUNDS[1],
		PIP_BLACK_LABEL_BOUNDS[2], PIP_BLACK_LABEL_BOUNDS[3]);
		pipCountBlack.setVisible(true);
		pipCountBlack.setForeground(Color.WHITE);
		pipCountBlack.setFont(new Font("Arial", Font.PLAIN, 16));
		gamePanel.add(pipCountBlack);
		gamePanel.setComponentZOrder(pipCountBlack, 1);

		
		mainContentPane.add(gamePanel);
		setContentPane(mainContentPane);
		pack();

	}

	private void createMenu() {
		menuBar = new JMenuBar();

		menuGameTitle = new JMenu("Game");

		newGame = new JMenuItem("New Game");
		newGame.addActionListener(this);
		newGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		menuGameTitle.add(newGame);

		exit = new JMenuItem("Exit");
		exit.addActionListener(this);
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
		menuGameTitle.add(exit);

		undoButton = new JMenuItem("Undo");
		undoButton.addActionListener(this);
		undoButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		menuGameTitle.add(undoButton);


		aboutGameTitle = new JMenu("About");

		help = new JMenuItem("Help");
		help.addActionListener(this);
		help.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
		aboutGameTitle.add(help);

		// adding to menu bar
		menuBar.add(menuGameTitle);
		menuBar.add(aboutGameTitle);
		menuBar.add(undoButton);
		setJMenuBar(menuBar);

	}

	private void initStones() {
		stones = new JLabel[NUMBER_OF_STONES];
		for (int i = 0; i < stones.length; i++) {
			stones[i] = new JLabel();
			stones[i].setName(i + "");
			stones[i].setSize(STONE_SIZE, STONE_SIZE);
			stones[i].addMouseListener(this);
			stones[i].addMouseMotionListener(this);
		}
	}

	private void initBackgammon() {
		// create undo stack
		undoStack = new Stack<Move>();
		// creating new dice object
		dice = new Dice();
		// creating new backgammon board, that implements backgammon rules
		board = new Board();
		// get who Starts
		isWhite = board.getWhoStarts();
		// clear dice images
		clearDices();
		// nothing done
		finished = false;
		// draw stones
		placeStones(board.getAmountArray(), board.getColorArray());
		// create undo dice list
		undoDice = new ArrayList<Integer>();
		// set pip count start text to 167
		pipCountWhite.setText("White pip count: 167");
		pipCountBlack.setText("Black pip count: 167");
		
	}

	/**
	 * 1: throw dices 2: change picture to represent them 3: show whose turn is
	 * it 4: set their result to the board 5: reset stones on the board 6:
	 * generate valid moves
	 */

	public void setUpMove() {
		// throw and set dices for first move
		dice.throwDices();
		// change pictures on dices
		makeDices();
		// setting dices to board
		board.setDices(dice.getDices());
		// set current player
		board.setPlayers(isWhite);
		// Updating info label		
		whosTurn.setText(board.getCurrentPlayer().toString() + "'s TURN");
		changeMoveCondition();
	}

	/**
	 * draws 4 dice on screen when a double is rolled
	 * instead of only 2 with 4 moves
	 *  
	 */
	private void makeDices() {
		dices[0].setIcon(diceFaces[dice.getDices()[0]]);
		dices[1].setIcon(diceFaces[dice.getDices()[1]]);
		if (dice.getDices()[0] == dice.getDices()[1]) {
			dices[2].setIcon(diceFaces[dice.getDices()[0]]);
			dices[3].setIcon(diceFaces[dice.getDices()[0]]);
		}
	}
	
	/**
	 * redraws the dice to the screen 
	 * if undo is clicked after last move
	 */

	private void makeUndoDice() {
		try {
			if (!isDicesLeft) {
				dices[0].setIcon(diceFaces[Math.abs(undoDice.get(0))]);
				dices[1].setIcon(diceFaces[Math.abs(undoDice.get(1))]);
				if (undoDice.get(Math.abs(0)) == undoDice.get(Math.abs(1))) {
					dices[2].setIcon(diceFaces[Math.abs(undoDice.get(0))]);
					dices[3].setIcon(diceFaces[Math.abs(undoDice.get(0))]);
				}
			}
		} catch (IndexOutOfBoundsException e) {
			// do nothing because it is duplicating pop ups
			// in undoMove catch
		}
	}

	/** 
	 * Clear dice images from screen
	 * Used for change moves and new game
	 */
	private void clearDices(){
		dices[0].setIcon(diceFaces[0]);
		dices[1].setIcon(diceFaces[0]);
		dices[2].setIcon(diceFaces[0]);
		dices[3].setIcon(diceFaces[0]);
	}

	private void endTurnButton() {
		endTurnButtonPanel = new JPanel();
		endTurnButtonPanel.setBounds(570,280,80,27);
		endTurnButton = new JButton("End Turn");
		endTurnButtonPanel.setBackground(new Color(0,0,0,0));
		endTurnButtonPanel.add(endTurnButton);
		mainContentPane.add(endTurnButtonPanel);
	}

	private void rollDiceButton() {
		rollDiceButtonPanel = new JPanel();
		rollDiceButtonPanel.setBounds(570,280,80,27);
		rollDiceButton = new JButton("Roll Dice");
		rollDiceButtonPanel.setBackground(new Color(0,0,0,0));
		rollDiceButtonPanel.add(rollDiceButton);
		mainContentPane.add(rollDiceButtonPanel);
	}

	/**
	 * Draws End Turn button once two checkers have been moved
	 * Draws Roll Dice button once End Turn button is clicked
	 * 	 */
	private void changeMoveCondition() {
		// drawing stones to start locations
		placeStones(board.getAmountArray(), board.getColorArray());
		// evaluating first set of valid moves
		board.searchForValidMoves();
		isMovesLeft = board.hasValidMovesLeft();
		isDicesLeft = board.isDicesLeft();
		// if no possible moves found change the player
		if (!isMovesLeft && !isDicesLeft || !isMovesLeft) {
			endTurnButton();
			endTurnButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					// clears dice images on screen once moves have been made
					clearDices();
					undoButton.setEnabled(false); 
					endTurnButtonPanel.setVisible(false);
					rollDiceButton();
					rollDiceButtonPanel.setVisible(true);
					undoDice.clear();
					changePlayer();
					// update bear off count once turn has ended
					pipCountWhite.setText("White pip count: " + String.valueOf(board.countWhite()));
					pipCountBlack.setText("Black pip count: " + String.valueOf(board.countBlack()));
					rollDiceButton.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent ev) {
							setUpMove();
							undoButton.setEnabled(true);
							rollDiceButtonPanel.setVisible(false);
						}
					});

					EventQueue.invokeLater(new Runnable(){
						@Override
						public void run() {
							rollDiceButtonPanel.repaint();
						}
					});
				}
			});
		}
	}

	private void undoMove(){
		try {
			makeUndoDice();
			Move undoChecker = undoStack.pop();
			undoChecker.setMoveTypeUndo();
			board.move(undoChecker);
			endTurnButtonPanel.setVisible(false);
			board.recoverDiceForUndo();
			changeMoveCondition();
		} catch (EmptyStackException s) {
			JOptionPane.showMessageDialog(this, "No moves to undo!");
		} catch (NullPointerException n) {
			// do nothing because End Turn button hasn't been initialised in first turn
		}
	}

	/**
	 * Move made win check board state is changed redraw board, update moves in
	 * case no moves left change player
	 */
	private void moveMade() {
		// move the piece
		board.move(chosenMove);

		
		// drawing stones to start locations
		placeStones(board.getAmountArray(), board.getColorArray());
		// evaluating first set of valid moves
		board.searchForValidMoves();
		isMovesLeft = board.hasValidMovesLeft();

		// check if game won
		if (board.checkWin() != GameState.STILL_PLAYING) {
			if (board.checkWin() == GameState.WHITE_WON) {
				winMessage(com.skeshmiri.backgammon.game.Color.WHITE.toString());
				initBackgammon();
			} else {
				winMessage(com.skeshmiri.backgammon.game.Color.BLACK.toString());
				initBackgammon();
			}
		}
		changeMoveCondition();
	}

	/**
	 * Highlights valid moves with a triangle
	 * The triangle is flipped if it is the top row
	 * Rectangle highlight for bear off point
	 */
	private void initHighLights() {
		highlights = new JLabel[Board.TOTAL_NO_OF_FIELDS];
		Zone highLightZone;
		for (int i = 0; i < Board.TOTAL_NO_OF_FIELDS; i++) {
			highlights[i] = new JLabel();
			highLightZone = zones.get(i);
			highlights[i].setBounds(highLightZone.LEFT, highLightZone.TOP,
			highLightZone.RIGHT - highLightZone.LEFT,
			highLightZone.BOTTOM - highLightZone.TOP);
			if (i >= 13 && i < 26) {
				highlights[i].setIcon(highlightFlipped);
				highlights[i].setVisible(false);
				gamePanel.add(highlights[i]);
			} else if (i == 26 || i == 27 ) {
				highlights[i].setIcon(highlightBearOffIcon);
				highlights[i].setVisible(false);
				gamePanel.add(highlights[i]);
			} else {
				highlights[i].setIcon(highlightImg);
				highlights[i].setVisible(false);
				gamePanel.add(highlights[i]);
			}
		}
	}

	/**
	 * Action listeners for menus
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == exit) {
			System.exit(0);
		}

		if (e.getSource() == help) {
			JOptionPane
					.showMessageDialog(this, "Ver. 5.00 made by Sam Keshmiri\n"
						+ "Features:\n"
						+ "+ Highlight available moves\n"
						+ "+ Pip count\n"
						+ "+ Show available die\n"
						+ "+ End turn\n"
						+ "+ Roll dice\n"
						+ "+ Undo moves during turn", "INFO",
						JOptionPane.INFORMATION_MESSAGE, new ImageIcon(
								ResourceLoader.getPathOf("img/rules.gif")));
		}

		/**
		 * Actual place to start a new game 
		 * Different options given
		 */

		if (e.getSource() == newGame) {
			// Initializing the actual game, game logic
			initBackgammon();
			setUpMove();
		}

		if (e.getSource() == undoButton) {
			// Undo last move
			undoMove();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		stoneDragged = Integer.parseInt(e.getComponent().getName());
		clickX = e.getX();
		clickY = e.getY();
		initialX = e.getComponent().getX();
		initialY = e.getComponent().getY();
		// highlights valid places to drop
		highLightValidMoveToLocs();
	}

	/**
	 * Method makes sure when stone is dropped it is dropped on valid location
	 * and doesn't allow the piece to be placed off the screen
	 * 
	 * Also it makes higlight fields invisible
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if (stoneDragged >= 0 && stoneDragged < NUMBER_OF_STONES) {
			if (stones[stoneDragged].getX() < LEFT_BOUNDARY)
				stones[stoneDragged].setLocation(initialX, initialY);
			if (stones[stoneDragged].getY() < TOP_BOUNDARY)
				stones[stoneDragged].setLocation(initialX, initialY);
			if (stones[stoneDragged].getX() > RIGHT_BOUNDARY)
				stones[stoneDragged].setLocation(initialX, initialY);
			if (stones[stoneDragged].getY() > BOTTOM_BOUNDARY)
				stones[stoneDragged].setLocation(initialX, initialY);
			if (stonePlacedValidly(stones[stoneDragged].getX(),
					stones[stoneDragged].getY())) {
				moveMade();
			} else {
				stones[stoneDragged].setLocation(initialX, initialY);
			}

		}

		// no stone is dragged
		stoneDragged = NUMBER_OF_STONES;

		// dehighlight highligted valid drop places
		for (int i = 0; i < highlights.length; i++) {
			highlights[i].setVisible(false);
		}
	}

	/**
	 * Method checks if User moved Stone from and to a valid location
	 * 
	 * @param currLeftX
	 *            top left corner of an object at the moment, X coordinate
	 * @param currLeftY
	 *            top left corner of an object at the moment, Y coordinate
	 * @return true for valid and false for an invalid move
	 */
	private boolean stonePlacedValidly(int currLeftX, int currLeftY) {
		// getting valid moves for current game state
		ArrayList<Move> moves = board.getValidMoves();
		
		Zone startLoc;
		Zone endLoc;
		
		for (int i = 0; i < moves.size(); i++) {
			startLoc = zones.get(moves.get(i).getStartField());
			// if piece was taken from valid start zone
			if (startLoc.isInZone(initialX, initialY)) {
				endLoc = zones.get(moves.get(i).getEndField());
				// if piece was placed at valid location for given start
				// location, place it
				if (endLoc.isInZone(currLeftX, currLeftY)) {
					chosenMove = i;
					undoStack.push(moves.get(i));
					undoDice.add(moves.get(i).getMoveAmount());
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Method Drags selected piece
	 * 
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		// off screen check
		int yLoc;
		int xLoc;

		// if a stone is being dragged move it around

		if (stoneDragged >= 0 && stoneDragged < NUMBER_OF_STONES) {
			xLoc = e.getComponent().getX() + e.getX() - clickX;
			yLoc = e.getComponent().getY() + e.getY() - clickY;
			stones[stoneDragged].setLocation(xLoc, yLoc);
		}

	}

	private void highLightValidMoveToLocs() {
		ArrayList<Move> moves = board.getValidMoves();
		Zone startLoc;
		for (int i = 0; i < moves.size(); i++) {
			// finds where piece was taken from
			startLoc = zones.get(moves.get(i).getStartField());
			// if piece was taken from valid start zone
			if (startLoc.isInZone(initialX, initialY)) {
				// highlight valid drop places for curr move
				highlights[moves.get(i).getEndField()].setVisible(true);
				background.setComponentZOrder(highlights[moves.get(i)
						.getEndField()], 0);
			}
		}
	}

	/**
	 * Method places all stones round the board with given amount and color
	 * 
	 * @param amountArray
	 *            array that holds how many pieces at each field
	 * @param colorArray
	 *            array that holds color of the pieces at each field
	 */
	private void placeStones(int[] amountArray, com.skeshmiri.backgammon.game.Color[] colorArray) {
		currStone = 0;
		for (int i = 0; i < Board.TOTAL_NO_OF_FIELDS; i++) {
			if (amountArray[i] != 0) {
				placeStonesOnAField(i, amountArray[i],
						stoneImage.get(colorArray[i]));
			}
		}
	}

	/**
	 * Method nicely places stones from one field into their locations
	 * 
	 * @param fieldID
	 *            representation of field in game arrays, its position 0-27
	 * @param amount
	 *            amount of stones on that field
	 * @param picture
	 *            picture of stone, depends on color
	 */
	private void placeStonesOnAField(int fieldID, int amount, ImageIcon picture) {
		// TODO: check if top stone problem is something here?
		// if more than MAX_NO_PER_FIELD_NO_STACK, then stack stones
		if (amount > MAX_NO_PER_FIELD_NO_STACK) {
			stackProportion = MAX_NO_PER_FIELD_NO_STACK * STONE_SIZE / amount;
		} else {
			stackProportion = STONE_SIZE;
		}
		for (int i = 0; i < amount; i++) {
			stones[currStone].setIcon(picture);
			if (fieldID < 13 || fieldID == 27) {
				stones[currStone].setLocation(locations.get(fieldID)[0],
				locations.get(fieldID)[1] - i * stackProportion);
			} else {
				stones[currStone].setLocation(locations.get(fieldID)[0],
				locations.get(fieldID)[1] + i * stackProportion);
			}
			currStone++;
		}
	}

	// simply changes current player
	private void changePlayer() {
		isWhite = !isWhite;
		undoStack.clear(); // clears undo stack
	}

	// sets images for stones
	private void initStoneColors() {
		stoneImage = new HashMap<com.skeshmiri.backgammon.game.Color, ImageIcon>();
		stoneImage.put(com.skeshmiri.backgammon.game.Color.BLACK, blackStoneImg);
		stoneImage.put(com.skeshmiri.backgammon.game.Color.WHITE, whiteStoneImg);
	}

	// Creating zones for fields on a board for dropping stones
	private void initFieldZones() {
		zones = new HashMap<Integer, Zone>();
		// bottom of board
		zones.put(0, new Zone(350, 399, HORIZONTAL_HALFWAY + PADDING,
				BOTTOM_BOUNDARY));
		zones.put(1, new Zone(650, 699, HORIZONTAL_HALFWAY + PADDING,
				BOTTOM_BOUNDARY));
		zones.put(2, new Zone(600, 649, HORIZONTAL_HALFWAY + PADDING,
				BOTTOM_BOUNDARY));
		zones.put(3, new Zone(550, 599, HORIZONTAL_HALFWAY + PADDING,
				BOTTOM_BOUNDARY));
		zones.put(4, new Zone(500, 549, HORIZONTAL_HALFWAY + PADDING,
				BOTTOM_BOUNDARY));
		zones.put(5, new Zone(450, 499, HORIZONTAL_HALFWAY + PADDING,
				BOTTOM_BOUNDARY));
		zones.put(6, new Zone(400, 449, HORIZONTAL_HALFWAY + PADDING,
				BOTTOM_BOUNDARY));
		zones.put(7, new Zone(300, 349, HORIZONTAL_HALFWAY + PADDING,
				BOTTOM_BOUNDARY));
		zones.put(8, new Zone(250, 299, HORIZONTAL_HALFWAY + PADDING,
				BOTTOM_BOUNDARY));
		zones.put(9, new Zone(200, 249, HORIZONTAL_HALFWAY + PADDING,
				BOTTOM_BOUNDARY));
		zones.put(10, new Zone(150, 199, HORIZONTAL_HALFWAY + PADDING,
				BOTTOM_BOUNDARY));
		zones.put(11, new Zone(100, 149, HORIZONTAL_HALFWAY + PADDING,
				BOTTOM_BOUNDARY));
		zones.put(12, new Zone(50, 99, HORIZONTAL_HALFWAY + PADDING,
				BOTTOM_BOUNDARY));
		// top of the board
		zones.put(13, new Zone(50, 99, TOP_BOUNDARY, (HORIZONTAL_HALFWAY - 1)));
		zones.put(14, new Zone(100, 149, TOP_BOUNDARY,
				(HORIZONTAL_HALFWAY - PADDING)));
		zones.put(15, new Zone(150, 199, TOP_BOUNDARY,
				(HORIZONTAL_HALFWAY - PADDING)));
		zones.put(16, new Zone(200, 249, TOP_BOUNDARY,
				(HORIZONTAL_HALFWAY - PADDING)));
		zones.put(17, new Zone(250, 299, TOP_BOUNDARY,
				(HORIZONTAL_HALFWAY - PADDING)));
		zones.put(18, new Zone(300, 349, TOP_BOUNDARY,
				(HORIZONTAL_HALFWAY - PADDING)));
		zones.put(19, new Zone(400, 449, TOP_BOUNDARY,
				(HORIZONTAL_HALFWAY - PADDING)));
		zones.put(20, new Zone(450, 499, TOP_BOUNDARY,
				(HORIZONTAL_HALFWAY - PADDING)));
		zones.put(21, new Zone(500, 549, TOP_BOUNDARY,
				(HORIZONTAL_HALFWAY - PADDING)));
		zones.put(22, new Zone(550, 599, TOP_BOUNDARY,
				(HORIZONTAL_HALFWAY - PADDING)));
		zones.put(23, new Zone(600, 649, TOP_BOUNDARY,
				(HORIZONTAL_HALFWAY - PADDING)));
		zones.put(24, new Zone(650, 699, TOP_BOUNDARY,
				(HORIZONTAL_HALFWAY - PADDING)));
		zones.put(25, new Zone(350, 399, TOP_BOUNDARY,
				(HORIZONTAL_HALFWAY - PADDING)));
		// bear offs
		// white top
		zones.put(26, new Zone(700, 749, TOP_BOUNDARY,
				(HORIZONTAL_HALFWAY - PADDING)));
		// black bottom
		zones.put(27, new Zone(700, 749, HORIZONTAL_HALFWAY + PADDING,
				BOTTOM_BOUNDARY));
	}

	// Creating location for each field to place stones nicely
	private void initLocs() {
		locations = new HashMap<Integer, int[]>();
		locations.put(0, new int[] { 355, 509 });
		locations.put(1, new int[] { 655, 509 });
		locations.put(2, new int[] { 605, 509 });
		locations.put(3, new int[] { 555, 509 });
		locations.put(4, new int[] { 505, 509 });
		locations.put(5, new int[] { 455, 509 });
		locations.put(6, new int[] { 405, 509 });
		locations.put(7, new int[] { 305, 509 });
		locations.put(8, new int[] { 255, 509 });
		locations.put(9, new int[] { 205, 509 });
		locations.put(10, new int[] { 155, 509 });
		locations.put(11, new int[] { 105, 509 });
		locations.put(12, new int[] { 55, 509 });
		locations.put(13, new int[] { 55, 51 });
		locations.put(14, new int[] { 105, 51 });
		locations.put(15, new int[] { 155, 51 });
		locations.put(16, new int[] { 205, 51 });
		locations.put(17, new int[] { 255, 51 });
		locations.put(18, new int[] { 305, 51 });
		locations.put(19, new int[] { 405, 51 });
		locations.put(20, new int[] { 455, 51 });
		locations.put(21, new int[] { 505, 51 });
		locations.put(22, new int[] { 555, 51 });
		locations.put(23, new int[] { 605, 51 });
		locations.put(24, new int[] { 655, 51 });
		locations.put(25, new int[] { 355, 51 });
		// white top
		locations.put(26, new int[] { 705, 51 });
		// black bottom
		locations.put(27, new int[] { 705, 509 });
	}

	/**
	 * Creates pop-up window when someone wins
	 * 
	 * @param player player that won
	 */
	private void winMessage(String player) {
		JOptionPane.showMessageDialog(this, player + " player Won",
				"End Of Game Message", JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	public Board getBoard() {
		return board;
	}

}
