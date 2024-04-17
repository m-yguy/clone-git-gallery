package cs1302.game;

import cs1302.gameutil.Token;
import cs1302.gameutil.GamePhase;

/**
 * A program to test the ConnectFour implementation against the specification
 * that is mainly described through JavaDoc comments.
 */
public class ConnectFourTester {

    public static void main(String[] args) {
        // TODO: Students should add additional test methods to test individual functions
        // they implemented in ConnectFour class and call those test methods from here.
        ConnectFour game = new ConnectFour(7,7);
        GamePhase phase = game.getPhase();
        System.out.println("Num of Rows: " + game.getRows());
        System.out.println("Num of Cols: " + game.getCols());
        System.out.println("Game Phase: " + phase);
        System.out.println("Are 6 & 6 parameters in bound? " + game.isInBounds(6, 6));
        System.out.println("Are 7 and 9 paramters in bound ? " + game.isInBounds(7, 9));
        game.setPlayerTokens(Token.RED, Token.GREEN);
        System.out.println("Set Player tokens method was called.");
        Token token = game.getPlayerToken(0);
        System.out.println("Player 0: " + token);
        token = game.getPlayerToken(1);
        System.out.println("Player 1: " + token);
        System.out.println("Game Phase: " + game.getPhase());
        game.dropToken(0, 3);
        game.printGrid();
        System.out.println(game.getLastDropRow());
        System.out.println(game.getLastDropCol());
        game.dropToken(0, 5);
        game.dropToken(0, 6);
        game.printGrid();
        System.out.println(game.getLastDropRow());
        System.out.println(game.getLastDropCol());
        System.out.println(game.isLastDropConnectFour());
        game.dropToken(0, 4);
        game.printGrid();
        System.out.println(game.isLastDropConnectFour());
        testGamePlayCase01();
        testGamePlayCase02();
        // Example test cases to test gameplay cases. You can uncomment the two lines
        // below and run these tests once you have completed the ConnectFour class.
        // testGamePlayCase01();
        // testGamePlayCase02();

        // TODO: Students should add additional test methods to test different gameplay
        // cases and call those test methods from here. Be sure to include methods
        // to check various win scenarios from different locations on the board.

        // TODO: Identify duplicate code in test code and move them into separate
        // functions and reuse them to minimize redundancy.
    } // main

    /**
     * This method simply prints out the rows and columns.
     *
     */
    public static void printRowsAndColumns() {
        ConnectFour game = new ConnectFour(7,7);
        System.out.println(game.getRows());
        System.out.println(game.getCols());
    }

    /**
     * This method checks different grids to see if a certain inputted grid is inbounds
     * of the grid of the game.
     */
    public static void gameInBounds() {
        ConnectFour game = new ConnectFour(7,7);
        System.out.println(game.isInBounds(7, 7));
    }
    /**
     * This method allows me to see .
     */

    public static void seeToken() {
        ConnectFour game = new ConnectFour(7,7);
        game.setPlayerTokens(Token.RED, Token.GREEN);
        System.out.println(game.getPhase());
    }
    /**
     * When a player makes a connect four from their last dropped token,
     * isLastDropConnectFour should return true.
     */

    public static void testGamePlayCase01() {
        ConnectFour game = new ConnectFour(6, 7);
        game.setPlayerTokens(Token.RED, Token.BLUE);
        game.dropToken(0, 0); // player 0 drops at col 0.
        game.dropToken(1, 1); // player 1 drops at col 1.
        game.dropToken(0, 0); // player 0 drops at col 0.
        game.dropToken(1, 1); // player 1 drops at col 1.
        game.dropToken(0, 0); // player 0 drops at col 0.
        game.dropToken(1, 1); // player 1 drops at col 1.
        game.dropToken(0, 0); // player 0 drops at col 0.

        // Player 0 made a connect four with the last drop.
        if (!game.isLastDropConnectFour()) {
            throw new AssertionError("Fail: isLastDropConnectFour should return true"
                    + " since player 0 made c4 in last drop.");
        } // if
        if (game.getPhase() != GamePhase.OVER) {
            throw new AssertionError("Fail: isLastDropConnectFour should set the game"
                    + " phase to OVER.");
        } // if
        System.out.println("Pass: isLastDropConnectFour returned true and game phase"
                    + " is set to OVER since player 0 made c4 in the last drop.");
    } // testGamePlayCase01

    /**
     * When a player makes a connect four from their last dropped token,
     * isLastDropConnectFour should return true.
     */
    public static void testGamePlayCase02() {
        ConnectFour game = new ConnectFour(6, 7);
        game.setPlayerTokens(Token.RED, Token.BLUE);
        game.dropToken(0, 0); // player 0 drops at col 0.
        game.dropToken(1, 1); // player 1 drops at col 1.
        game.dropToken(0, 1); // player 0 drops at col 1.
        game.dropToken(1, 2); // player 1 drops at col 2.
        game.dropToken(0, 2); // player 0 drops at col 2.
        game.dropToken(1, 3); // player 1 drops at col 3.
        game.dropToken(0, 2); // player 0 drops at col 2.
        game.dropToken(1, 3); // player 1 drops at col 3.
        game.dropToken(0, 3); // player 0 drops at col 3.
        game.dropToken(1, 6); // player 1 drops at col 6.
        game.dropToken(0, 3); // player 0 drops at col 3.

        // Player 0 made a connect four with the last drop.
        if (!game.isLastDropConnectFour()) {
            throw new AssertionError("Fail: isLastDropConnectFour should return true"
                    + " since player 0 made c4 in last drop.");
        } // if
        if (game.getPhase() != GamePhase.OVER) {
            throw new AssertionError("Fail: isLastDropConnectFour should set the game"
                    + " phase to OVER.");
        } // if
        System.out.println("Pass: isLastDropConnectFour returned true and game phase"
                    + " is set to OVER since player 0 made c4 in the last drop.");
    } // testGamePlayCase02

} // ConnectFourTester
