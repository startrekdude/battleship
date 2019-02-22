/*
 * Date: 11/16/2018
 * Dev: Sam Haskins
 * Version: v1.0
 */
package com.gmail.cheesedude54321.battleship.ai;

import java.util.ArrayList;
import java.util.List;

import com.gmail.cheesedude54321.battleship.game.Battleship;
import com.gmail.cheesedude54321.battleship.game.BattleshipMove;
import com.gmail.cheesedude54321.battleship.game.HitmapCell;
import com.gmail.cheesedude54321.utility.RandomUtils;

/*
 * Name: Sam Haskins
 * Date: 11/18/2018
 * Inputs: The Battleship to play on
 * Outputs: Moves on that Battleship
 * Description:
 *     A simple yet robust AI to simulate a Battleship player
 *     Developers are strongly encouraged to read AINOTE.txt for many more details
 */
public final class BattleshipAI {
    // State of the search algorithm
    private int currentQuadrant = 0;
    
    // State of the fire algorithm
    // -1 if not currently sinking a ship
    private int shipX = -1;
    private int shipY = -1;
    private int currentShipX = -1;
    private int currentShipY = -1;
    private int originalDeltaX = -999;
    private int originalDeltaY = -999;
    private int deltaX = -999;
    private int deltaY = -999;
    private Boolean hasAttemptedInvertDelta = false;
    
    // Private state of establishDelta. Stops it from searching up-down-up-down infinitely
    private int lastDirection = -1;
    
    /*
     * Name: Sam Haskins
     * Date: 11/18/2018
     * Inputs: The game to place a ship on, a Battleship
     * Outputs: Places a ship on the game's board
     * Description:
     *     Randomly places a ship, using a random rotation, on
     *     the Battleship board. See AINOTE for rationale
     */
    public void placeShip(Battleship game) {
        // Randomly place a ship
        int x, y, r;
        do {
            x = RandomUtils.range(0, 9);
            y = RandomUtils.range(0, 9);
            // The rotation
            r = RandomUtils.range(0, 3);
        } while (!game.placeShip(x, y, r));
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/18/2018
     * Inputs: The game to fire using
     * Outputs: The result of the fire operation
     * Description:
     *     Determines whether the AI is currently sinking a ship,
     *     and calls search or sink appropriately (see AINOTE)
     */
    public BattleshipMove fire(Battleship game) {
        if (shipX == -1) {
            // Find a new target
            return search(game);
        } else {
            // Sink the target we've found
            return sink(game);
        }
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/18/2018
     * Inputs: The game and the AI's state
     * Outputs: Attempts to proceed the other direction in the same axis from the origin of the ship
     * Description:
     *     Called when the AI, after finding the direction of a ship and proceeding along it,
     *     cannot finish the ship. Returns to the origin and goes in the other direction
     */
    private BattleshipMove invertDelta(Battleship game) {
        hasAttemptedInvertDelta = true;
        
        // Try to "invert the delta," i.e. proceed in the opposite direction on the same axis
        int newDeltaX = -originalDeltaX;
        int newDeltaY = -originalDeltaY;
        
        // Start from the origin, not the current position
        int newShipX = shipX + newDeltaX;
        int newShipY = shipY + newDeltaY;
        
        // If this puts us off the screen, try something different
        if (newShipX < 0 || newShipX > 9 || newShipY < 0 || newShipY > 9)
            return sink(game);
        
        // Possible we fired here when searching for a delta; if so, don't do it again
        if (game.hitmaps[0][newShipX][newShipY] != HitmapCell.EMPTY)
            return sink(game);
        
        BattleshipMove result = game.fire(newShipX, newShipY);
        if (game.hitmaps[0][newShipX][newShipY] == HitmapCell.HIT_SHIP) {
            // The gambit succeeded; record the state
            currentShipX = newShipX;
            currentShipY = newShipY;
            deltaX = newDeltaX;
            deltaY = newDeltaY;
        }
        return result;
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/18/2018
     * Inputs: The game to try and establish a delta for, the x and y coordinates to start from
     * Outputs: Updates deltaX and deltaY and possibly originalDeltaX and originalDeltaY. Returns the move result
     * Description:
     *     Attempts to figure out what direction a ship is going in. Checks, in order
     *     above, to the left, below, to the right
     *     (please see AINOTE)
     */
    private BattleshipMove establishDelta(Battleship game, int x, int y) {
        // The co-ordinate difference to try
        int xDelta = 0;
        int yDelta = 0;
        
        // Find an empty space to shoot at
        if (y != 9 && game.hitmaps[0][x][y + 1] == HitmapCell.EMPTY) {
            // Above
            yDelta = 1;
        } else if (x != 0 && game.hitmaps[0][x - 1][y] == HitmapCell.EMPTY) {
            // Left
            xDelta = -1;
        } else if (y != 0 && game.hitmaps[0][x][y - 1] == HitmapCell.EMPTY) {
            // Below
            yDelta = -1;
        } else if (x != 9 && game.hitmaps[0][x + 1][y] == HitmapCell.EMPTY) {
            // Right
            xDelta = 1;
        }
        
        BattleshipMove result = null;
        
        // If we haven't found a delta, find a delta from an adjacent hit cell
        if (xDelta == 0 && yDelta == 0) {
            for (int i = 0; i < 4; i++) {
                // Guard against searching up-down-up-down-up-down infinitely
                if (lastDirection != -1 &&(lastDirection + 2) % 4 == i)
                    continue;
                
                if (i == 0 && y != 9 &&
                    game.hitmaps[0][x][y + 1] == HitmapCell.HIT_SHIP) {
                    // Try the above cell
                    lastDirection = i;
                    result = establishDelta(game, x, y + 1);
                } else if (i == 1 && x != 0 &&
                           game.hitmaps[0][x - 1][y] == HitmapCell.HIT_SHIP) {
                    // Try the cell to the left
                    lastDirection = i;
                    result = establishDelta(game, x - 1, y);
                } else if (i == 2 && y != 0 &&
                           game.hitmaps[0][x][y - 1] == HitmapCell.HIT_SHIP) {
                    // Try the below cell
                    lastDirection = i;
                    result = establishDelta(game, x, y - 1);
                } else if (i == 3 && x != 9 &&
                           game.hitmaps[0][x + 1][y] == HitmapCell.HIT_SHIP) {
                    // Try the cell to the right
                    lastDirection = i;
                    result = establishDelta(game, x + 1, y);
                }
                // If, at any point, we found a free cell and made a delta, return the result
                if (result != null) {
                    return result;
                }
            }
            // That didn't succeed; return null to allow a method higher in the stack to look in another
            // direction. If this can't find a new delta, sink will escape and try search instead
            return null;
        }
        
        // Make the move and see if it hits
        result = game.fire(x + xDelta, y + yDelta);
        // If it hit, update the state
        if (game.hitmaps[0][x + xDelta][y + yDelta] == HitmapCell.HIT_SHIP) {
            deltaX = xDelta;
            deltaY = yDelta;
            currentShipX = x + xDelta;
            currentShipY = y + yDelta;
            lastDirection = -1;
            // If the ship's original delta is unset, set it
            if (originalDeltaX == -999) {
                originalDeltaX = xDelta;
                originalDeltaY = yDelta;
            }
        }
        return result;
    }

    
    /*
     * Name: Sam Haskins
     * Date: 11/18/2018
     * Inputs: The game, and also uses the AI's state
     * Outputs: Attempts to sink a discovered ship; returns the move's result
     * Description:
     *     This algorithm is rather complex and is explained best in AINOTE
     *     In a nutshell, it attempts to find the axis the ship is on and sink it
     */
    private BattleshipMove sink(Battleship game) {
        BattleshipMove result;
        // When we start finding a new ship (no ship in progress), set currentShipX and Y
        if (currentShipX == -1) {
            currentShipX = shipX;
            currentShipY = shipY;
        }
        
        // Try the invert delta gambit
        if (!hasAttemptedInvertDelta && originalDeltaX != -999 &&
                   deltaX == -999) {
            result = invertDelta(game);
        // Find a new delta; it's our main strategy
        } else if (deltaX == -999) {
            try {
                result = establishDelta(game, shipX, shipY);
            } catch (StackOverflowError e) {
                // It's stuck somewhere and can't find any new squares;
                // a human would be able to deal with this, but the AI can't
                // Return to search mode and find a new ship to sink
                shipX = shipY = -1;
                originalDeltaX = originalDeltaY = deltaX = deltaY = -999;
                hasAttemptedInvertDelta = false;
                return search(game);
            }
        // We have a delta and there's no special strategies to try; follow it
        } else {
            currentShipX += deltaX;
            currentShipY += deltaY;
            // If this puts us off the board, try something else
            if (currentShipX < 0 || currentShipX > 9 ||
                currentShipY < 0 || currentShipY > 9) {
                deltaX = deltaY = -999;
                return sink(game);
            }
            
            // If we already know that the next square isn't a ship hit, try again
            if (game.hitmaps[0][currentShipX][currentShipY] != HitmapCell.EMPTY) {
                deltaX = deltaY = -999;
                return sink(game);
            }
            
            result = game.fire(currentShipX, currentShipY);
            if (game.hitmaps[0][currentShipX][currentShipY] != HitmapCell.HIT_SHIP) {
                // This didn't work; try something new next time
                deltaX = deltaY = -999;
            }
        }
        
        // We've made a move; check the result
        if (result == BattleshipMove.SUNK_SHIP || result == null) {
            // Reset state and return to search pattern
            shipX = shipY = -1;
            originalDeltaX = originalDeltaY = deltaX = deltaY = -999;
            hasAttemptedInvertDelta = false;
            if (result == null) {
                // We didn't find a square to check; do search
                result = search(game);
            }
        }
        
        return result;
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/18/2018
     * Inputs: The game and the quadrant to find the fire options for
     * Outputs: A list of eligible positions to fire in the quadrant
     * Description:
     *     Used when searching; eligible squares would be white on a checkerboard
     *     and have not been fired upon previously
     */
    private List<int[]> findFireOptions(Battleship game, int quadrantIndex) {
        // The offset of coordinates in the quadrant
        int offsetX = 0;
        int offsetY = 0;
        
        // This is a private method; the caller guarantees the quadrantIndex is 0 - 3
        // and we don't need to deal with 0 because offsetX and offsetY are zero by default
        switch (quadrantIndex) {
        case 1:
            offsetX = 5;
            break;
        case 2:
            offsetY = 5;
            break;
        case 3:
            offsetX = offsetY = 5;
            break;
        }
        
        // Store eligible coordinates
        List<int[]> options = new ArrayList<int[]>();
        
        // Loop over every coordinate in a 5-by-5 grid
        for (int i = offsetX; i < offsetX + 5; i++) {
            for (int j = offsetY; j < offsetY + 5; j++) {
                // Make sure it's white on a checkerboard
                // And that is hasn't been fired on previously
                if ((i % 2) == (j % 2) &&
                    game.hitmaps[0][i][j] == HitmapCell.EMPTY) {
                    options.add(new int[] { i, j });
                }
            }
        }
        // Return options
        return options;
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/18/2018
     * Inputs: The game to search for a ship on
     * Outputs: The result of the search move the AI decides on
     * Description:
     *     Searches for a ship in the current quadrant, then advances to the next
     *     If there's no place to fire in the current quadrant, uses the next
     *     If there's no place to fire anywhere, fire randomly
     *     (seriously read AINOTE)
     */
    private BattleshipMove search(Battleship game) {
        // The decided move x and y coordinates
        int x = -1;
        int y = -1;
        
        // Start by trying each quadrant, starting at the current quadrant
        for (int i = currentQuadrant; i < currentQuadrant + 4; i++) {
            // Find the fire options for the quadrant currently being operated on
            // (We use modulo four because if we started at 3 it loops back around easily)
            List<int[]> fireOptions = findFireOptions(game, i % 4);
            if (fireOptions.size() != 0) {
                // Fire on the quadrant
                int[] pos = RandomUtils.choice(fireOptions);
                x = pos[0];
                y = pos[1];
                break;
            }
        }
        
        // Advance to the next quadrant
        currentQuadrant += 1;
        currentQuadrant %= 4;
        
        // If there were no available positions in the checkerboard, pick one at random
        if (x == -1) {
            do {
                x = RandomUtils.range(0, 9);
                y = RandomUtils.range(0, 9);
            } while (game.hitmaps[0][x][y] != HitmapCell.EMPTY);
        }
        
        // Fire. If we hit a ship, mark it
        BattleshipMove result = game.fire(x, y);
        if (game.hitmaps[0][x][y] == HitmapCell.HIT_SHIP) {
            shipX = x;
            shipY = y;
        }
        
        return result;
    }
}