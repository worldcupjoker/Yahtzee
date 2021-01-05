/*
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 * https://www.youtube.com/watch?v=Huwf0TgWrOw&ab_channel=Telusko
 */

import acm.io.*;
import acm.program.*;
import acm.util.*;
import java.util.*;

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {
	
	public static void main(String[] args) {
		new Yahtzee().start(args);
	}
	
	public void init() {
		this.setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
	}
	
	public void run() {
		IODialog dialog = getDialog();
		nPlayers = dialog.readInt("Enter number of players");
		playerNames = new String[nPlayers];
		for (int i = 1; i <= nPlayers; i++) {
			playerNames[i - 1] = dialog.readLine("Enter name for player " + i);
		}
		display = new YahtzeeDisplay(getGCanvas(), playerNames);
		playGame();
	}

	private void playGame() {
		
		/* You fill this in */
		scoreboard();
		
		/* Set up a log for category. 1 means used. 0 means unused. */
		categoryLog = new int[N_CATEGORIES][nPlayers];
		
		/* Start the game loop. */
		while (true) {
			round++;
			if (round > 13) {
				break;
			}
			for (int i = 0; i < nPlayers; i++) {
				
				/* First roll */
				display.printMessage(playerNames[i] + "'s turn! Click \"Roll Dice\" button to roll the dice.");
				display.waitForPlayerToClickRoll(i + 1);
				rollAllTheDice();
				display.displayDice(dice);
				
				/* Second roll */
				display.printMessage("Select the dice you wish to re-roll and click \"Roll again\".");
				display.waitForPlayerToSelectDice();
				rollAgain();
				display.displayDice(dice);
				
				/* Third roll */
				display.printMessage("Select the dice you wish to re-roll and click \"Roll again\".");
				display.waitForPlayerToSelectDice();
				rollAgain();
				display.displayDice(dice);
				int category = selectCategory(i);
				int score = checkTheCategory(category);
				
				/* Update the score for this round. */
				scoreboard[category][i] = score;
				display.updateScorecard(category, i + 1, score);
				
				/* Update the total score. */
				scoreboard[16][i] += score;
				display.updateScorecard(17, i + 1, scoreboard[16][i]);
			}
		}
		finalCalculations();
		declareTheWinner();
	}
	
	/* Method: declareTheWinner */
	private void declareTheWinner() {
		int winner = 0;
		for (int i = 1; i < nPlayers; i++) {
			if (scoreboard[16][i] > scoreboard[16][winner]) {
				winner = i;
			}
		}
		display.printMessage("Congratulations, " + playerNames[winner] + ", you're the winner with a total score of " + scoreboard[16][winner] + "!");
	}
	
	/* Method: finalCalculation */
	/**
	 * Calculate the upper score and the lower score and assign the bonus.
	 */
	private void finalCalculations() {
		upperLowerScores();
		upperBonusAndTotal();
	}
	
	/* Method: upperBonusAndTotal */
	/**
	 * Calculate and update the upper bonuses and the total scores.
	 */
	private void upperBonusAndTotal() {
		for (int i = 0; i < nPlayers; i++) {
			
			/* Calculate bonuses. */
			if (scoreboard[6][i] >= 63) {
				scoreboard[7][i] = 35;
			}
			display.updateScorecard(8, i + 1, scoreboard[7][i]);
			
			/* Calculate total scores. */
			scoreboard[16][i] = scoreboard[6][i] + scoreboard[7][i] + scoreboard[15][i];
			display.updateScorecard(17, i + 1, scoreboard[16][i]);
		}
	}
	
	/* Method: upperLowerScore */
	/**
	 * Calculate the upper scores and the lower scores.
	 */
	private void upperLowerScores() {
		for (int i = 0; i < nPlayers; i++) {
			
			/* Upper scores */
			for (int j = 0; j < 7 - 1; j++) {
				scoreboard[6][i] += scoreboard[j][i];
			}
			display.updateScorecard(7, i + 1, scoreboard[6][i]);
			
			/* Lower scores */
			for (int j = 8; j < 15; j++) {
				scoreboard[15][i] += scoreboard[j][i];
			}
			display.updateScorecard(16, i + 1, scoreboard[15][i]);
		}
	}
	
	/* Method: totalScore */
	/**
	 * Calculate the total score before updating the regional sums like upper scores, etc.
	 * @param player
	 * @return
	 */
	private int totalScore(int player) {
		int sum = 0;
		for (int i = 0; i < scoreboard.length - 1; i++) {
			sum += scoreboard[i][player];
		}
		return sum;
	}
	
	/* Method: checkTheCategory */
	/**
	 * Get the score from the selected category.
	 * @param category
	 * @return
	 */
	private int checkTheCategory(int category) {
		switch (category) {
		case 1: return digits(category);
		case 2: return digits(category);
		case 3: return digits(category);
		case 4: return digits(category);
		case 5: return digits(category);
		case 6: return digits(category);
		case 9: return threeOfAKind();
		case 10: return fourOfAKind();
		case 11: return fullHouse();
		case 12: return smallStraight();
		case 13: return largeStraight();
		case 14: return yahtzee();
		case 15: return chance();
		default: return 0;
		}
	}
	
	/* Method: fullHouse*/
	/**
	 * Calculate the score for Full House category.
	 * @return
	 */
	private int fullHouse() {
		ArrayList<String> diceList = new ArrayList<String>();
		int[] frequency = new int[2];
		
		/* Record the frequency. */
		for (int i = 0; i < dice.length; i++) {
			if (!diceList.contains("" + dice[i])) {
				diceList.add("" + dice[i]);
				if (diceList.size() > frequency.length) {
					return 0;
				}
				int index = diceList.indexOf("" + dice[i]);
				frequency[index]++;
			} else {
				int index = diceList.indexOf("" + dice[i]);
				frequency[index]++;
			}
		}
		
		/* Check the frequency. */
		for (int i = 0; i < frequency.length; i++) {
			if (frequency[i] > 3) {
				return 0;
			}
		}
		return 25;
	}
	
	/* Method: fourOfAKind */
	/**
	 * Calculate the score for Four of a Kind category.
	 * @return
	 */
	private int fourOfAKind() {
		ArrayList<String> diceList = new ArrayList<String>();
		int[] frequency = new int[2];
		
		/* Record the frequency. */
		for (int i = 0; i < dice.length; i++) {
			if (!diceList.contains("" + dice[i])) {
				diceList.add("" + dice[i]);
				if (diceList.size() > frequency.length) {
					return 0;
				}
				int index = diceList.indexOf("" + dice[i]);
				frequency[index]++;
			} else {
				int index = diceList.indexOf("" + dice[i]);
				frequency[index]++;
			}
		}
		
		/* Check the frequency. */
		for (int i = 0; i < frequency.length; i++) {
			if (frequency[i] >= 4) {
				return sumDice();
			}
		}
		return 0;
	}
	
	/* Method: threeOfAKind */
	/**
	 * Calculate the score for Three of a Kind category.
	 * @return
	 */
	private int threeOfAKind() {
		ArrayList<String> diceList = new ArrayList<String>();
		int[] frequency = new int[3];
		
		/* Record the frequency. */
		for (int i = 0; i < dice.length; i++) {
			if (!diceList.contains("" + dice[i])) {
				diceList.add("" + dice[i]);
				if (diceList.size() > frequency.length) {
					return 0;
				}
				int index = diceList.indexOf("" + dice[i]);
				frequency[index]++;
			} else {
				int index = diceList.indexOf("" + dice[i]);
				frequency[index]++;
			}
		}
		
		/* Check the frequency. */
		for (int i = 0; i < frequency.length; i++) {
			if (frequency[i] >= 3) {
				return sumDice();
			}
		}
		return 0;
	}
	
	/* Method: sumDice */
	/**
	 * Add all the values showing on the dice.
	 * @return
	 */
	private int sumDice() {
		int sum = 0;
		for (int i = 0; i < dice.length; i++) {
			sum += dice[i];
		}
		return sum;
	}
	
	/* Method: largeStraight */
	/**
	 * Calculate the score for Large Straight category.
	 * @return
	 */
	private int largeStraight() {
		ArrayList<String> diceList = createDiceList();
		if (diceList.contains("2") && diceList.contains("3") && diceList.contains("4") && diceList.contains("5")) {
			if (diceList.contains("1") || diceList.contains("6")) {
				return 40;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}
	
	/* Method: smallStraight */
	/**
	 * Calculate the score for Small Straight category.
	 * @return
	 */
	private int smallStraight() {
		ArrayList<String> diceList = createDiceList();
		if (diceList.contains("3") && diceList.contains("4")) {
			if (diceList.contains("1") && diceList.contains("2")) {
				return 30;
			} else if (diceList.contains("2") && diceList.contains("5")) {
				return 30;
			} else if (diceList.contains("5") && diceList.contains("6")) {
				return 30;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}
	
	/* Method: createDiceList */
	/**
	 * Turn an Array into an ArrayList for searching purpose.
	 * @return
	 */
	private ArrayList<String> createDiceList() {
		ArrayList<String> diceList = new ArrayList<String>();
		for (int i = 0; i < dice.length; i++) {
			diceList.add("" + dice[i]);
		}
		return diceList;
	}
	
	/* Method: yahtzee */
	/**
	 * Calculate the score for Yahtzee! category.
	 * @return
	 */
	private int yahtzee() {
		int score = 10;
		for (int i = 1; i < dice.length; i++) {
			if (dice[i] == dice[i - 1]) {
				score += 10;
			} else {
				return 0;
			}
		}
		return score;
	}
	
	/* Method: digits */
	/**
	 * Calculate the score for categories from Ones to Sixes.
	 * @param category
	 * @return
	 */
	private int digits(int category) {
		int score = 0;
		for (int i = 0; i < dice.length; i++) {
			if (dice[i] == category) {
				score += category;
			}
		}
		return score;
	}
	
	/* Method: chance */
	/**
	 * Calculate the score for Chance category.
	 * @return
	 */
	private int chance() {
		int score = 0;
		for (int i = 0; i < dice.length; i++) {
			score += dice[i];
		}
		return score;
	}
	
	/* Method: selecteCategory */
	/**
	 * Select a category. Inform the player to choose again if a repeated category is selected.
	 * @param player
	 * @return
	 */
	private int selectCategory(int player) {
		display.printMessage("Select a category for this roll.");
		while (true) {
			int category = display.waitForPlayerToSelectCategory();
			if (categoryLog[category - 1][player] == 0) {
				categoryLog[category - 1][player] = 1;
				return category;
			}
			display.printMessage("This category has been used. Please select other categories.");
		}
	}
	
	/* Method: */
	/**
	 * Set up the socreboard.
	 */
	private void scoreboard() {
		scoreboard = new int[N_CATEGORIES][nPlayers];
	}
	
	/* Method: rollDice */
	/**
	 * Roll all the dice.
	 * @param dice
	 */
	private void rollAllTheDice() {
		for (int i = 0; i < N_DICE; i++) {
			dice[i] = rgen.nextInt(1, 6);
		}
	}
	
	/* Method: rollAgain */
	/**
	 * Roll again the selected dice.
	 */
	private void rollAgain() {
		for (int i = 0; i < N_DICE; i++) {
			if (display.isDieSelected(i)) {
				dice[i] = rgen.nextInt(1, 6);
			}
		}
	}

/* Private instance variables */
	private int nPlayers;
	private String[] playerNames;
	private YahtzeeDisplay display;
	private RandomGenerator rgen = new RandomGenerator();
	private static int[][] scoreboard;
	private static int round = 0;
	private static int[] dice = new int[N_DICE];
	private static int[][] categoryLog;
}
