/*
 * Date: 11/15/2018
 * Dev: Sam Haskins
 * Version: v1.0
 */
package com.gmail.cheesedude54321.battleship.game;

/*
 * Name: Sam Haskins
 * Date: 11/15/2018
 * Inputs: None
 * Outputs: A set of symbols to store the result of a battleship move
 * Description:
 *     The result of a Battleship move. This is NO_RESULT, WIN, or SUNK_SHIP
 *     This was added so the AI can know when a move sinks a ship and stop looking
 */
public enum BattleshipMove {
    NO_RESULT, WIN, SUNK_SHIP
}
