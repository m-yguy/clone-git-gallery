package cs1302.game;

import cs1302.gameutil.GamePhase;
import cs1302.gameutil.Token;
import cs1302.gameutil.TokenGrid;

/**
 * {@code ConnectFour} represents a two-player connection game involving a two-dimensional grid of
 * {@linkplain cs1302.gameutil.Token tokens}. When a {@code ConnectFour} game object is
 * constructed, several instance variables representing the game's state are initialized and
 * subsequently accessible, either directly or indirectly, via "getter" methods. Over time, the
 * values assigned to these instance variables should change so that they always reflect the
 * latest information about the state of the game. Most of these changes are described in the
 * project's <a href="https://github.com/cs1302uga/cs1302-c4-alpha#functional-requirements">
 * functional requirements</a>.
 */
public class ConnectFour {

    //----------------------------------------------------------------------------------------------
    // INSTANCE VARIABLES: You should NOT modify the instance variable declarations below.
    // You should also NOT add any additional instance variables. Static variables should
    // also NOT be added.
    //----------------------------------------------------------------------------------------------

    private int rows;        // number of grid rows
    private int cols;        // number of grid columns
    private Token[][] grid;  // 2D array of tokens in the grid
    private Token[] player;  // 1D array of player tokens (length 2)
    private int numDropped;  // number of tokens dropped so far
    private int lastDropRow; // row index of the most recent drop
    private int lastDropCol; // column index of the most recent drop
    private GamePhase phase; // current game phase

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    /**
     * Constructs a {@link cs1302.game.ConnectFour} game with a grid that has {@code rows}-many
     * rows and {@code cols}-many columns. All of the game's instance variables are expected to
     * be initialized by this constructor as described in the project's
     * <a href="https://github.com/cs1302uga/cs1302-c4-alpha#functional-requirements">functional
     * requirements</a>.
     *
     * @param rows the number of grid rows
     * @param cols the number of grid columns
     * @throws IllegalArgumentException if the value supplied for {@code rows} or {@code cols} is
     *     not supported. The following values are supported: {@code 6 <= rows <= 9} and
     *     {@code 7 <= cols <= 9}.
     */
    public ConnectFour(int rows, int cols)  {

        if (rows < 6 || rows > 9 || cols < 7 || cols > 9) {
            throw new IllegalArgumentException("An invalid size for the board was inputted");
        }

        this.rows = rows;
        this.cols = cols;
        this.grid = new Token[rows][cols];
        this.player = new Token[2];
        this.numDropped = 0;
        this.lastDropRow = -1;
        this.lastDropCol = -1;
        this.phase = GamePhase.NEW;

        //
        //throw new UnsupportedOperationException("constructor: not yet implemented.");
    } // ConnectFour

    //----------------------------------------------------------------------------------------------
    // INSTANCE METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Return the number of rows in the game's grid.
     *
     * @return the number of rows
     */
    public int getRows() {
        return this.rows;
    } // getRows

    /**
     * Return the number of columns in the game's grid.
     *
     * @return the number of columns
     */
    public int getCols() {
        return this.cols;
    } // getCols

    /**
     * Return whether {@code row} and {@code col} specify a location inside this game's grid.
     *
     * @param row the position's row index
     * @param col the positions's column index
     * @return {@code true} if {@code row} and {@code col} specify a location inside this game's
     *     grid and {@code false} otherwise
     */
    public boolean isInBounds(int row, int col) {
        boolean inBounds = true;
        if (row > rows || col > cols) {
            inBounds = false;
        }

        if (row < 0 || col < 0) {
            inBounds = false;
        }
        return inBounds;
    } // isInBounds

    /**
     * Return the grid {@linkplain cs1302.gameutil.Token token} located at the specified position
     * or {@code null} if no token has been dropped into that position.
     *
     * @param row the token's row index
     * @param col the token's column index
     * @return the grid token located in row {@code row} and column {@code col}, if it exists;
     *     otherwise, the value {@code null}
     * @throws IndexOutOfBoundsException if {@code row} and {@code col} specify a position that is
     *     not inside this game's grid.
     */
    public Token getTokenAt(int row, int col) {
        if (row > rows || col > cols || col < 0 || row < 0) {
            throw new IndexOutOfBoundsException("The token position is not in this game's grid.");
        }
        return grid[row][col];
    } // getTokenAt

    /**
     * Set the first player token and second player token to {@code token0} and {@code token1},
     * respectively. If the current game phase is {@link cs1302.gameutil.GamePhase#NEW}, then
     * this method changes the game phase to {@link cs1302.gameutil.GamePhase#READY}, but only
     * if no exceptions are thrown.
     *.
     * @param token0 token for first player
     * @param token1 token for second player
     * @throws NullPointerException if {@code token0} or {@code token1} is {@code null}.
     * @throws IllegalArgumentException if {@code token0 == token1}.
     * @throws IllegalStateException if {@link #getPhase getPhase()} returns
     *     {@link cs1302.gameutil.GamePhase#PLAYABLE} or {@link cs1302.gameutil.GamePhase#OVER}.
     */
    public void setPlayerTokens(Token token0, Token token1) {
        if (token0 == token1) {
            throw new IllegalArgumentException("Both players cannot have the same tokens.");
        }

        if (token0 == null || token1 == null) {
            throw new NullPointerException("Both tokens must be of the values provided.");
        }
        if (phase == GamePhase.PLAYABLE || phase == GamePhase.OVER) {
            throw new IllegalStateException("To set player token, phase must be in NEW or READY");
        }

        player[0] = token0;
        player[1] = token1;

        if (phase == GamePhase.NEW) {
            phase = GamePhase.READY;
        }
    } // setPlayerTokens

    /**
     * Return a player's token.
     *
     * @param player the player ({@code 0} for first player and {@code 1} for second player)
     * @return the token for the specified player
     * @throws IllegalArgumentException if {@code player} is neither {@code 0} nor {@code 1}
     * @throws IllegalStateException if {@link #getPhase getPhase()} returns
     *     {@link cs1302.gameutil.GamePhase#NEW}.
     */
    public Token getPlayerToken(int player) {
        if (player != 0 && player != 1) {
            throw new IllegalArgumentException("That player does not exist.");
        }

        if (phase == GamePhase.NEW) {
            throw new IllegalStateException("Cannot get player token in the NEW phase");
        }
        return this.player[player];
    } // getPlayerToken

    /**
     * Return the number of tokens that have been dropped into this game's grid so far.
     *
     * @return the number of dropped tokens
     * @throws IllegalStateException if {@link #getPhase getPhase()} returns
     *     {@link cs1302.gameutil.GamePhase#NEW} or {@link cs1302.gameutil.GamePhase#READY}.
     */
    public int getNumDropped() {
        return numDropped;
    } // getNumDropped

    /**
     * Return the row index of the last (i.e., the most recent) token dropped into this
     * game's grid.
     *
     * @return the row index of the last drop
     * @throws IllegalStateException if {@link #getPhase getPhase()} returns
     *     {@link cs1302.gameutil.GamePhase#NEW} or {@link cs1302.gameutil.GamePhase#READY}.
     */
    public int getLastDropRow() {
        return lastDropRow;
    } // getLastDropRow

    /**
     * Return the col index of the last (i.e., the most recent) token dropped into this
     * game's grid.
     *
     * @return the column index of the last drop
     * @throws IllegalStateException if {@link #getPhase getPhase()} returns
     *     {@link cs1302.gameutil.GamePhase#NEW} or {@link cs1302.gameutil.GamePhase#READY}.
     */
    public int getLastDropCol() {
        return lastDropCol;
    } // getLastDropCol

    /**
     * Return the current game phase.
     *
     * @return current game phase
     */
    public GamePhase getPhase() {
        return phase;
    } // getPhase

    /**
     * Drop a player's token into a specific column in the grid. This method should not enforce turn
     * order -- that is the players' responsibility should they desire an polite and honest game.
     *
     * @param player the player ({@code 0} for first player and {@code 1} for second player)
     * @param col the grid column where the token will be dropped
     * @throws IndexOutOfBoundsException if {@code col} is not a valid column index
     * @throws IllegalArgumentException if {@code player} is neither {@code 0} nor {@code 1}
     * @throws IllegalStateException if {@link #getPhase getPhase()} does not return
     *    {@link cs1302.gameutil.GamePhase#READY} or {@link cs1302.gameutil.GamePhase#PLAYABLE}
     * @throws IllegalStateException if the specified column in the grid is full
     */
    public void dropToken(int player, int col) {
        if (col >= cols || col < 0) {
            throw new IndexOutOfBoundsException("Not a valid column index.");
        }
        if (player != 0 && player != 1) {
            throw new IllegalArgumentException("The inputted player does not exist.");
        }
        if (getPhase() != GamePhase.READY && getPhase() != GamePhase.PLAYABLE) {
            throw new IllegalStateException("Only dropToken when game phase is READY or PLAYABLE.");
        }
        if (grid[0][col] != null) {
            throw new IllegalStateException("Cannot drop token because the column is full.");
        }

        int lowestRow = rows - 1;
        while (lowestRow >= 0 && grid[lowestRow][col] != null) {
            lowestRow = lowestRow - 1;
        }

        grid[lowestRow][col] = this.player[player];
        numDropped++;
        lastDropRow = lowestRow;
        lastDropCol = col;
        phase = GamePhase.PLAYABLE;
    } // dropToken

    /**
     * Return {@code true} if the last token dropped via {@link #dropToken} created a
     * <em>connect four</em>. A <em>connect four</em> is a sequence of four equal tokens (i.e., they
     * have the same color) -- this sequence can occur horizontally, vertically, or diagonally.
     * If the grid is full or the last drop created a <em>connect four</em>, then this method
     * changes the game's phase to {@link cs1302.gameutil.GamePhase#OVER}.
     *
     * <p>
     * <strong>NOTE:</strong> The only instance variable that this method might change, if
     * applicable, is ``phase``.
     *
     * <p>
     * <strong>NOTE:</strong> If you want to use this method to determine a winner, then you must
     * call it after each call to {@link #dropToken}.
     *
     * @return {@code true} if the last token dropped created a <em>connect four</em>, else
     *     {@code false}
     */
    public boolean isLastDropConnectFour() {
        boolean isLastDropConnectFour = false;

        if (numDropped == rows * cols) {
            phase = GamePhase.OVER;
        }
        // Checks horizontal connect four! If the count of same tokens
        // are in the same row and they add up to 4, then phase changes to over!


        Token token = grid[lastDropRow][lastDropCol];
        int counter = 0;

        if ( horizontal() || vertical() || positiveDiag()) {
            return true;
        }

        // Negative diagnally - from bottom right to top left
        token = grid[lastDropRow][lastDropCol];
        int startingRow = lastDropRow;
        int startingCol = lastDropCol;
        while (startingRow < rows - 1 && startingCol < cols - 1) {
            startingRow++;
            startingCol++;
        }

        counter = 0;
        while (startingRow >= 0 && startingCol >= 0) {
            if (grid[startingRow][startingCol] == token) {
                counter++;
                if (counter == 4) {
                    phase = GamePhase.OVER;
                    isLastDropConnectFour = true;
                }
            } else {
                counter = 0;
            }
            startingRow--;
            startingCol--;
        }
        return isLastDropConnectFour;
    } // isLastDropConnectFour

    //----------------------------------------------------------------------------------------------
    // ADDITIONAL METHODS: If you create any additional methods, then they should be placed in the
    // space provided below.
    //----------------------------------------------------------------------------------------------


    /**
     * This method checks the connect four board for 4 tokens in a row diagnally
     * in a positive slope.
     *
     * @return a boolean, either true or false, that a Connect Four happened
     */
    public boolean positiveDiag() {
        Token token = grid[lastDropRow][lastDropCol];
        boolean isLastDropConnectFour = false;
        int startingRow = lastDropRow;
        int startingCol = lastDropCol;
        while (startingRow < rows - 1 && startingCol > 0) {
            startingRow++;
            startingCol--;
        }

        int counter = 0;
        while (startingRow >= 0 && startingCol < cols) {
            if (grid[startingRow][startingCol] == token) {
                counter++;
                if (counter == 4) {
                    phase = GamePhase.OVER;
                    isLastDropConnectFour = true;
                }
            } else {
                counter = 0;
            }
            startingRow--;
            startingCol++;
        }

        return isLastDropConnectFour;
    }


    /**
     * This method checks the Connect Four grid for 4 tokens in a row horizontally.
     * If there are 4 consecutive tokens in a row horizontally, then it is a
     * horizontal win!
     *
     * @return a boolean, either true or false, that a Connect Four occured
     */


    public boolean horizontal() {
        boolean isLastDropConnectFour = false;
        Token token = grid[lastDropRow][lastDropCol];
        int counter = 0;
        for (int i = 0; i < cols; i++) {
            if (grid[lastDropRow][i] == token) {
                counter++;
                if (counter == 4) {
                    phase = GamePhase.OVER;
                    isLastDropConnectFour = true;
                }
            } else {
                counter = 0;
            }
        }
        return isLastDropConnectFour;
    }


    /**
     * This method check the Connect Four grid for 4 consecutive tokens
     * in a column. If there are 4 consecutive tokens in a column, then
     * that is a vertical win!
     *
     * @return a boolean indicating whether a connect 4 happened, true or false
     */

    public boolean vertical() {
        boolean isLastDropConnectFour = false;
        Token token = grid[lastDropRow][lastDropCol];
        int counter = 0;
        for (int i = 0; i < rows; i++) {
            if (grid[i][lastDropCol] == token) {
                counter++;
                if (counter >= 4) {
                    phase = GamePhase.OVER;
                    isLastDropConnectFour = true;
                }
            } else {
                counter = 0;
            }
        }
        return isLastDropConnectFour;
    } // Vertical win

    //----------------------------------------------------------------------------------------------
    // DO NOT MODIFY THE METHODS BELOW!
    //----------------------------------------------------------------------------------------------

    /**
     * <strong>DO NOT MODIFY:</strong>
     * Print the game grid to standard output. This method assumes that the constructor
     * is implemented correctly.
     *
     * <p>
     * <strong>NOTE:</strong> This method should not be modified!
     */
    public void printGrid() {
        TokenGrid.println(this.grid);
    } // printGrid

} // ConnectFour
