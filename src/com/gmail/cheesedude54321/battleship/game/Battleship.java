/*
 * Date: 11/15/2018
 * Dev: Sam Haskins
 * Version: v1.0
 */
package com.gmail.cheesedude54321.battleship.game;

import java.util.Arrays;

/*
 * Name: Sam Haskins
 * Date: 11/15/2018
 * Inputs: Moves in a Battleship simulation
 * Outputs: State and move results from a Battleship simulation
 * Description:
 *     A class that implements the game logic of Battleship.
 *     Uses a 10-by-10 board with 5 ship types
 *     Consumers can use placeShip and fire, and it also
 *     exposes its data structures for display
 */
public final class Battleship {
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: Starts with default board state and tracks changes
     * Outputs: Stores a Battleship board for a player
     * Description:
     *     Stores the Battleship board for one player;
     *     this includes the 10-by-10 BattleshipCell board
     *     and the health of all the ships
     */
    public final class Board {
        // The actual grid
        public BattleshipCell[][] board = new BattleshipCell[10][10];
        // The ship's health
        private int aircraftCarrierHealth = 5;
        private int battleshipHealth = 4;
        private int cruiserHealth = 3;
        private int submarineHealth = 3;
        private int destroyerHealth = 2;
    }
    
    // The public data; the board, hitmaps, and current turn
    public Board[] boards;
    public HitmapCell[][][] hitmaps = new HitmapCell[2][10][10];
    public int turn = 0;
    
    // The next ship that will be placed
    private BattleshipCell nextShip = BattleshipCell.AIRCRAFT_CARRIER;
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: None
     * Outputs: Creates a Battleship with empty hitmaps and boards
     * Description:
     *     Initialize all Battleship fields with default values,
     *     and fills in the hitmaps with HitmapCell->Empty and the boards
     *     with BattleshipCell->Empty
     */
    public Battleship() {
        boards = new Board[] { new Board(), new Board() };
        
        // Fill in all the cells with empty values
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 10; j++) {
                for (int k = 0; k < 10; k++) {
                    boards[i].board[j][k] = BattleshipCell.EMPTY;
                    hitmaps[i][j][k] = HitmapCell.EMPTY;
                }
            }
        }
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: The row and column to fire at
     * Outputs: Performs the move, updating the hitmap and board. Returns NoResult, Win, or SunkShip
     * Description:
     *     First, makes sure the row and column parameters are in the required range
     *     Then, fires! Updates the opponent's hitmap and the health values in the board
     *     Returns whether a ship was sunk and whether the player won
     *     Advances to the next turn, as well
     */
    public BattleshipMove fire(int row, int column) {
        // Check the co-ordinates range
        if (row < 0 || row > 9 || column < 0 || column > 9) {
            throw new IllegalArgumentException("Coordinates out of range");
        }
        
        // Make sure all ships are placed
        if (!allShipsPlaced()) {
            throw new IllegalStateException("Ships not yet placed");
        }
        
        int opponent = (turn + 1) % 2;
        Board board = boards[opponent];
        BattleshipCell mapCell = board.board[row][column];
        HitmapCell hitCell = hitmaps[opponent][row][column];
        
        // Unless we hit a ship, do not return SunkShip
        int shipHealth = 999;
        
        // Only take further action if the cell hasn't already been hit
        // ...otherwise a player could hit the same ship's cell multiple times and sink it
        if (hitCell == HitmapCell.EMPTY) {
            if (mapCell != BattleshipCell.EMPTY) {
                // We hit a ship; handle this
                hitmaps[opponent][row][column] = HitmapCell.HIT_SHIP;
                // Take away health
                switch (mapCell) {
                case AIRCRAFT_CARRIER:
                    // Interesting that this syntax works. Neat and elegant
                    shipHealth = board.aircraftCarrierHealth -= 1;
                    break;
                case BATTLESHIP:
                    shipHealth = board.battleshipHealth -= 1;
                    break;
                case CRUISER:
                    shipHealth = board.cruiserHealth -= 1;
                    break;
                case SUBMARINE:
                    shipHealth = board.submarineHealth -= 1;
                    break;
                case DESTROYER:
                    shipHealth = board.destroyerHealth -= 1;
                    break;
                }
            } else {
                // Didn't hit a ship; indicate this
                hitmaps[opponent][row][column] = HitmapCell.HIT;
            }
        } // end if (hitCell == HitmapCell.EMPTY)
        
        // Check for a win state
        // If the player won, do not advance the turn
        if (board.aircraftCarrierHealth == 0 && board.battleshipHealth == 0 &&
            board.cruiserHealth == 0 && board.submarineHealth == 0 &&
            board.destroyerHealth == 0) {
            return BattleshipMove.WIN;
        }
        
        // Rollover the turn
        turn = opponent;
        
        // If we sunk a ship, return that. Otherwise NoResult
        if (shipHealth == 0) {
            return BattleshipMove.SUNK_SHIP;
        } else {
            return BattleshipMove.NO_RESULT;
        }
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: Uses the Battleship's fields
     * Outputs: A clone of the Battleship object
     * Description:
     *     Makes an entirely new, yet identical, Battleship object. Used to
     *     preview ship placements
     */
    public Battleship cloneState() {
        // Make the new Battleship
        Battleship battleship = new Battleship();
        // Clone the fields
        battleship.boards = cloneBoards();
        battleship.turn = turn;
        battleship.nextShip = nextShip;
        // A three-level deep clone, this'll be fun
        battleship.hitmaps = Arrays.stream(hitmaps)
                             .map((hitmap) -> Arrays.stream(hitmap)
                                              .map((row) -> row.clone())
                                              .toArray((l) -> new HitmapCell[l][]))
                             .toArray((l) -> new HitmapCell[l][][]);
       return battleship;
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: Uses the boards field
     * Outputs: A clone of the boards field, suitable for experimentation
     * Description:
     *     Deep-clones the boards field. Used to undo partial ship placements, and in cloneState
     */
    private Board[] cloneBoards() {
        // Create an array to store the cloned boards
        Board[] newBoards = new Board[2];
        for (int i = 0; i < 2; i++) {
            // Clone the health fields
            Board board = new Board();
            board.aircraftCarrierHealth = boards[i].aircraftCarrierHealth;
            board.battleshipHealth = boards[i].battleshipHealth;
            board.cruiserHealth = boards[i].cruiserHealth;
            board.submarineHealth = boards[i].submarineHealth;
            board.destroyerHealth = boards[i].destroyerHealth;
            // Deep clone the grid
            board.board = Arrays.stream(boards[i].board)
                                .map((r) -> r.clone())
                                .toArray((l) -> new BattleshipCell[l][]);
            newBoards[i] = board;
        }
        return newBoards;
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: The row, column, and rotation to draw a ship at
     * Outputs: Draws the ship or throws an exception
     * Description:
     *     Contains the code to draw each type of ship. Only invoked
     *     from placeShip, which does parameter validation
     */
    private void drawShip(int row, int column, int rotation) {
        // Don't let partial draws effect state
        Board[] newBoards = cloneBoards();
        Marker marker = new Marker(newBoards[turn].board, rotation, row,
                                   column, nextShip);
        switch (nextShip) {
        case AIRCRAFT_CARRIER:
            marker.draw(1).rotate(1).draw(4);
            break;
        case BATTLESHIP:
            marker.draw(4);
            break;
        case CRUISER:
        case SUBMARINE:
            marker.draw(3);
            break;
        case DESTROYER:
            marker.draw(2);
            break;
        }
        // The previous completed without error; good to go
        this.boards = newBoards;
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: The row, column, and rotation to place a ship at
     * Outputs: A Boolean indicating whether the ship was placed; if true, advances to the next turn
     * Description:
     *     If allShipsPlaced, returns false
     *     First, ensures the rotation is valid (0-3) and throws IllegalArgumentException
     *     Next, tries to place the ship. If it fails (not entirely on the board), returns false
     *     (Also fails if it intersects with another ship)
     *     If it succeeds, advances to the next turn and returns true
     *     (If turn == 1, also advances to the next ship)
     */
    public Boolean placeShip (int row, int column, int rotation) {
        // If all ships have been placed, don't place more
        if (allShipsPlaced())
            return false;
        
        // Make sure the rotation is valid
        if (rotation < 0 || rotation > 3)
            throw new IllegalArgumentException("0 <= rotation <= 3");
        
        // Try to use a Marker to draw the ship
        // If it fails, it throws an exception (either out-of-bounds or illegal argument)
        try {
            drawShip(row, column, rotation);
        } catch (Exception e) {
            // It failed; this is an invalid place for a ship
            return false;
        }
        
        // Advance to the next turn
        turn += 1;
        turn %= 2;
        
        // If turn is 0, go to the next type of ship
        if (turn == 0) {
            switch (nextShip) {
            case AIRCRAFT_CARRIER:
                nextShip = BattleshipCell.BATTLESHIP;
                break;
            case BATTLESHIP:
                nextShip = BattleshipCell.CRUISER;
                break;
            case CRUISER:
                nextShip = BattleshipCell.SUBMARINE;
                break;
            case SUBMARINE:
                nextShip = BattleshipCell.DESTROYER;
                break;
            case DESTROYER:
                // We're done here. No more ships to place.
                nextShip = BattleshipCell.EMPTY;
                break;
            }
        }
        
        // Success!
        return true;
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: None
     * Outputs: Whether all ships in the Battleship have been placed
     * Description:
     *     Returns whether all the game's ships have already been placed
     *     (i.e. nextShip is equal to BattleshipCell->Empty)
     */
    public Boolean allShipsPlaced () {
        return nextShip == BattleshipCell.EMPTY;
    }
}