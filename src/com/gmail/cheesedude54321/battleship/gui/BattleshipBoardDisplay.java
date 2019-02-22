/*
 * Date: 11/27/2018
 * Dev: Sam Haskins
 * Version: v1.0.1
 */
package com.gmail.cheesedude54321.battleship.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.event.MouseInputAdapter;

import com.gmail.cheesedude54321.battleship.game.Battleship;
import com.gmail.cheesedude54321.battleship.game.BattleshipCell;
import com.gmail.cheesedude54321.battleship.game.HitmapCell;

/*
 * Name: Sam Haskins
 * Date: 11/16/2018
 * Inputs: The Battleship game state
 * Outputs: Draws the board to the screen, accepts mouse events to fire and place ships
 * Description:
 *     A component that displays a player's Battleship board. It conceptually contains
 *     an 11-by-11 grid within its bounds, and draws in three stages:
 *         - The grid lines and 1-10, A-J labels
 *         - The hitmap (grey for hit, red for hit ship)
 *         - Only if it's the player's turn: the board
 *     When the  player clicks on a sqaure that is part of the board, dispatches onFire
 */
public final class BattleshipBoardDisplay extends JComponent {    
    private int player;
    // Uses instead of game->allShipsPlaced because of the clone during preview
    Boolean allShipsPlaced;
    Battleship game;
    
    // Use to preview ship places
    public BiConsumer<Integer, Integer> onHover;
    // Used to place ships and fire
    public BiConsumer<Integer, Integer> onFire;
    
    /*
     * Name: Sam Haskins
     * Date: 11/16/2018
     * Inputs: The player and game to display
     * Outputs: A BattleshipBoardDisplay ready to display a player's boards
     * Description:
     *     First, ensures player is 0 or 1 and throws IllegalArgumentException if it isn't
     *     Then, creates a BattleshipBoardDisplay with the requested data
     */
    public BattleshipBoardDisplay(Battleship game, int player) {
        // Validate the player
        if (player != 0 && player != 1)
            throw new IllegalArgumentException("0 == player == 1");
        // Validate the game
        if (game == null)
            throw new NullPointerException("game != null");
            
        this.player = player;
        this.game = game;
        allShipsPlaced = false;
        
        // Register a mouse adapter; hover is used to preview placing ships, and
        // click is used to place ships and fire
        // Make sure this is in the MouseInputAdapter's closure
        BattleshipBoardDisplay _this = this;
        MouseInputAdapter adapter = new MouseInputAdapter() {
            public void mouseMoved(MouseEvent e) {
                _this.onMouseMove(e);
            }
            public void mouseClicked(MouseEvent e) {
                _this.onMouseClick(e);
            }
        };
        addMouseMotionListener(adapter);
        addMouseListener(adapter);
    }
    
    private void onMouseClick(MouseEvent e) {
        // Check to see if the consumer cares about the click event
        if (onFire != null) {
            // When placing ships, player will click own board
            if (game.turn == player && !allShipsPlaced) {
                fireMouseEvent(e, onFire);
            // When firing, you must click on the opponent's board
            } else if (game.turn != player && allShipsPlaced) {
                fireMouseEvent(e, onFire);
            } else if (allShipsPlaced) {
                 // Indicate that you must click on the opponent's board to fire
                JOptionPane.showMessageDialog(this, "To fire, click the" +
                                              " opponent's board");
            }
        }
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/16/2018
     * Inputs: The mouse move event
     * Outputs: Gives the current mouse coordinates to the consumer via onHover
     * Description:
     *     Invokes fireMouseEvent (e, onHover) to calculate the grid square the mouse is on,
     *     and give it to the consumer
     */
    private void onMouseMove(MouseEvent e) {
        // Check to see if the consumer cares about the hover event
        // The player can only preview-place ships on their turn
        if (onHover != null && game.turn == player) {
            fireMouseEvent(e, onHover);
        }
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/16/2018
     * Inputs: The mouse move event and a function to give the co-ordinates to
     * Outputs: Calculates the co-ordinates referred to by the mouse event and, if they are on the board, gives them to callback
     * Description:
     *     Calculates the board co-ordinates referred to by the mouse event
     *     If they are on the battleship board (i.e. not the labels, etc), return them
     */
    private void fireMouseEvent(MouseEvent e,
                                BiConsumer<Integer, Integer> callback) {
        // Ensure callback is not null
        if (callback == null)
            throw new NullPointerException("callback != null");
        
        // Find the grid size
        int sz = getWidth() / 11;
        int x = e.getX() / sz;
        int y = e.getY() / sz;
        // Ensure the coordinates are on the battleship board
        x--;
        y--;
        if (x >= 0 && y >= 0 && x <= 9 && y <= 9)
            callback.accept(x, y);
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/16/2018
     * Inputs: The Graphics object to paint to
     * Outputs: Paints, using other methods, the complete Battleship board
     * Description:
     *     Calculates the cell size, then paints:
     *         1) The lines and 0-9 A-J labels
     *         2) The hitmap
     *         3) (only if it's player's turn) The board
     */
    @Override
    protected void paintComponent(Graphics g) {
        // The cell size
        int sz = getWidth() / 11;
        
        // Turn on antialiasing
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                          RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw a simple white background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // Draw the hitmap first - show the grid lines
        drawHitmap(g, sz);
        // Draw grid lines
        drawBaseGrid(g, sz);
        
        // Only draw the ships if it's the players turn
        if (game.turn == player)
            drawShips(g, sz);
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/16/2018
     * Inputs: The Graphics to draw to and the grid cell size
     * Outputs: Draws the player's ships to the component
     * Description:
     *     Draws the position of the player's ships to the Graphics objects
     *     Represents ships cell with a letter
     */
    private void drawShips(Graphics g, int sz) {
        // Use black
        g.setColor(Color.BLACK);
        // Retrieve the board
        BattleshipCell[][] board = game.boards[player].board;
        
        // Go over every cell
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                BattleshipCell cell = board[i][j];
                if (cell != BattleshipCell.EMPTY) {
                    // Use the first letter of the ship name as a symbol
                    String symbol = Character.toString(cell.toString().charAt(0));
                    // Draw the symbol
                    gridDrawString(g, sz, i + 1, j + 1, symbol);
                }
            }
        }
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/16/2018
     * Inputs: The Graphics object to paint to, the cell size, the x and y positions of the cell, the String to draw
     * Outputs: Draws the String, centered and at an appropriate size, to the grid cell
     * Description:
     *     First, calculates an appropriate font size for the String
     *     Then, draws the String in the cell, centered vertically and horizontally
     */
    private void gridDrawString(Graphics g, int sz, int x, int y, String s) {
        // Create a font of appropriate size
        int fontSize = sz * Toolkit.getDefaultToolkit().getScreenResolution() / 96;
        // Fix the font size
        fontSize /= s.length();
        
        Font font = new Font("Sans", Font.PLAIN, fontSize);
        
        // Calculate the centered position
        FontMetrics metrics = g.getFontMetrics(font);
        
        int newX = sz * x + (sz - metrics.stringWidth(s)) / 2;
        int newY = sz * y + ((sz - metrics.getHeight()) / 2)
                   + metrics.getAscent();
        
        g.setFont(font);
        g.drawString(s, newX, newY);
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/16/2018
     * Inputs: The Graphics to draw to and the grid cell size
     * Outputs: Draws the player's hitmap to the component
     * Description:
     *     Draws the player's hitmap to the component. Uses
     *     grey for hit and red for hit ship
     */
    private void drawHitmap(Graphics g, int sz) {
        HitmapCell[][] hitmap = game.hitmaps[player];
        // Iterate over every hitmap cell
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                HitmapCell cell = hitmap[i][j];
                // Pick a color
                switch(cell) {
                case EMPTY:
                    g.setColor(Color.WHITE);
                    break;
                case HIT:
                    g.setColor(Color.LIGHT_GRAY);
                    break;
                case HIT_SHIP:
                    g.setColor(Color.RED);
                    break;
                }
                // Draw the grid square
                // Don't draw over the grid lines
                g.fillRect((i+1)*sz, (j+1)*sz, sz, sz);
            }
        }
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/16/2018
     * Inputs: The Graphics object to paint to, the cell size
     * Outputs: Paints the grid lines and 0-10 A-J labels onto the component
     * Description:
     *     Paints the grid lines and the coordinate labels onto the
     *     Graphics object. Called from paintComponent
     */
    private void drawBaseGrid(Graphics g, int sz) {
        // Set the color to black
        g.setColor(Color.BLACK);
        // Use really thin lines
        ((Graphics2D) g).setStroke(new BasicStroke(0.2f));
        
        int currentNum = 1;
        char currentChar = 'A';
        for (int i = 1; i < 12; i++) {
            // Draw from (sz, sz*i) to (sz*11, sz*i)
            // and (sz*i, sz) to (sz*i, sz*11)
            g.drawLine(sz, sz*i, sz*11, sz*i);
            g.drawLine(sz*i, sz, sz*i, sz*11);
            
            // Draw the characters
            if (i != 11) {
                gridDrawString(g, sz, 0, i, Integer.toString(currentNum));
                gridDrawString(g, sz, i, 0, Character.toString(currentChar));
            }
            
            currentNum++;
            currentChar++;
        }
    }
}