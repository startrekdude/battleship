/*
 * Date: 11/15/2018
 * Dev: Sam Haskins
 * Version: v1.0
 */
package com.gmail.cheesedude54321.battleship.game;

/*
 * Name: Sam Haskins
 * Date: 11/15/2018
 * Inputs: Battleship ship drawing information
 * Outputs: Draws battleship ships onto the board
 * Description:
 *     This is a utility class used to draw values into arrays, but only if the
 *     existing value is BattleshipCell->EMPTY. It is used to draw the ships in Battleship,
 *     and it fully supports rotation. All of its methods return this, allowing chaining.
 *     Ex:
 *         new Marker(board[turn], rotation, row, column, nextShip).draw(2).rotate(1).draw(2);
 *     This throws errors explicitly, if the cell being drawn over is not the empty cell.
 *     This throws errors implicitly, if something it tries to draw is out-of-bounds.
 */
final class Marker {
    private int rotation;
    private BattleshipCell ship;
    private BattleshipCell[][] board;
    
    private int currentRow;
    private int currentColumn;
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: The player's board to draw on, the initial rotation, the starting row, the starting column, and the ship to draw
     * Outputs: A Marker object, ready to draw
     * Description:
     *     Initializes the Marker object with the provided properties
     */
    Marker(BattleshipCell[][] board, int rotation, int row, int column,
           BattleshipCell ship) {
        // Set all the fields
        this.rotation = rotation;
        this.ship = ship;
        this.board = board;
        currentRow = row;
        currentColumn = column;
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: The amount to rotate the marker
     * Outputs: Returns the same Marker with the rotation updated
     * Description:
     *     Rotates the marker using rotation += rotate; rotation %= 4
     *     Returns this, like all other Marker public methods
     */
    Marker rotate(int rotate) {
        rotation += rotate;
        rotation %= 4;
        return this;
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: The number of times to draw
     * Outputs: Returns the marker object; updates the board
     * Description:
     *     Draws ship onto board using rotation starting from currentRow, currentColumn
     *     the provided number of times
     *     Returns this, to allow chaining
     */
    Marker draw(int n) {
        // Drawn n times
        while (n > 0) {
            // Draw the ship
            if (board[currentRow][currentColumn] == BattleshipCell.EMPTY) {
                board[currentRow][currentColumn] = ship;
            } else {
                throw new IllegalArgumentException("cannot overwrite ship");
            }
            move();
            n--;
        }
        return this;
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: Uses the rotation
     * Outputs: Moves one space, using the rotation
     * Description:
     *     Using the rotation, moves one space forward
     *     r:0 = go up
     *     r:1 = go right
     *     r:2 = go down
     *     r:3 = go left
     */
    private void move() {
        // Fix the rotation if it is invalid. If it's negative, throw an IllegalStateException
        if (rotation > 3) {
            rotation %= 4;
        } else if (rotation < 0) {
            throw new IllegalStateException("rotation > 0");
        }
        
        switch(rotation) {
        case 0:
            currentRow++;
            break;
        case 1:
            currentColumn++;
            break;
        case 2:
            currentRow--;
            break;
        case 3:
            currentColumn--;
            break;
        }
    }
}
