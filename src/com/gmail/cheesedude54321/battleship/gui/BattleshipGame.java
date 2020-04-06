/*
 * Date: 11/27/2018
 * Dev: Sam Haskins
 * Version: v1.0.1
 */
package com.gmail.cheesedude54321.battleship.gui;

import java.awt.Desktop;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URI;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.gmail.cheesedude54321.battleship.ai.BattleshipAI;
import com.gmail.cheesedude54321.battleship.game.Battleship;
import com.gmail.cheesedude54321.battleship.game.BattleshipMove;
import com.gmail.cheesedude54321.battleship.game.HitmapCell;
import com.gmail.cheesedude54321.utility.AboutDialog;
import com.gmail.cheesedude54321.utility.ActionUtils;
import com.gmail.cheesedude54321.utility.AudioUtils;
import com.gmail.cheesedude54321.utility.ImageUtils;
import com.gmail.cheesedude54321.utility.SmoothLabel;

/*
 * Name: Sam Haskins
 * Date: 11/15/2018
 * Inputs: User input to the program
 * Outputs: The Battleship game state; also opens dialogs when required
 * Description:
 *     The Battleship Game window. Accepts user input and advances the game when appropriate.
 *     The main window of the Battleship application; uses a Battleship class to store game state
 */
public final class BattleshipGame extends JFrame implements KeyEventDispatcher {
    // The underlying game state
    private Battleship game;
    private BattleshipAI ai;
    private Boolean isPlayingAI;
    
    // The rotation used when placing ships; valid values are 0, 1, 2, and 3.
    // See battleship.game.Marker for details
    private int rotation;
    
    private JPanel root;
    
    // The two board displays
    private BattleshipBoardDisplay[] displays;
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: None
     * Outputs: Builds a BattleshipGame with all required user interface elements
     * Description:
     *     Builds a BattleshipGame with a menu bar and two BattleshipBoardDisplay's, one for each player
     */
    public BattleshipGame() {
        // Set basic properties
        setTitle("Battleship");
        setSize(900, 675);
        
        // Set the icon (see THIRD-PARTY.txt)
        setIconImage(ImageUtils.load("icon.png"));
        
        // When shown, invoke selectGameType
        // Make sure the ComponentAdapter gets this in its closure
        BattleshipGame _this = this;
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                _this.selectGameType();
            }
        });
        
        // Create the menu
        JMenuBar menu = new JMenuBar();
        
        JMenu battleship = new JMenu("Battleship");
        battleship.add(ActionUtils.menu("Select game type", this::selectGameType));
        battleship.add(ActionUtils.menu("Start new game", this::startNewGame));
        
        // The mute menu option
        JCheckBoxMenuItem muteMenu = new JCheckBoxMenuItem("Mute");
        muteMenu.addItemListener(e -> AudioUtils.mute = muteMenu.isSelected());
        battleship.add(muteMenu);
        
        battleship.add(ActionUtils.menu("Exit", this::exit));
        menu.add(battleship);
        
        // Set the default rotation to 0. Valid values are 0, 1, 2 and 3
        rotation = 0;
        
        JMenu rotate = new JMenu("Rotate");
        rotate.add(ActionUtils.menu("Forward", () -> rotation = (rotation + 1) % 4));
        rotate.add(ActionUtils.menu("Backward", () -> rotation = (rotation + 3) % 4));
        menu.add(rotate);
        
        JMenu help = new JMenu("Help");
        help.add(ActionUtils.menu("User Guide", this::help));
        help.add(ActionUtils.menu("About", this::about));
        menu.add(help);
        
        setJMenuBar(menu);
        
        // Add the key events for rotating. As the main frame, we add a KeyEventDispatcher to get
        // key events no matter what is in focus
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(this);
        
        // Create and add the root panel, using no layout (custom positioning)
        root = new JPanel();
        root.setLayout(null);
        getContentPane().add(root);
        game = new Battleship();
        
        // Make the two BattleshipBoardDisplays, one for each player
        displays = new BattleshipBoardDisplay[2];
        
        // Player 1
        displays[0] = new BattleshipBoardDisplay(game, 0);
        displays[0].onHover = this::previewPlaceShip;
        displays[0].onFire = this::placeShip;
        root.add(displays[0]);
        
        // Player 2
        displays[1] = new BattleshipBoardDisplay(game, 1);
        displays[1].onHover = this::previewPlaceShip;
        displays[1].onFire = this::placeShip;
        root.add(displays[1]);
        
        // Register the custom layout manager we use for the board displays
        // For more details, see doBoardLayout
        addComponentListener(new ComponentAdapter() {
           public void componentResized(ComponentEvent e) {
               _this.doBoardLayout();
           }
        });
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/16/2018
     * Inputs: None
     * Outputs: Starts a new Battleship game
     * Description:
     *     Starts a new game, gives it to the displays, hooks
     *     up the event handler used for placing ships, and repaints
     */
    private void startNewGame() {
        // Create the game
        game = new Battleship();
        
        // Reset the displays
        displays[0].game = displays[1].game = game;
        displays[0].onHover = displays[1].onHover = this::previewPlaceShip;
        displays[0].onFire = displays[1].onFire = this::placeShip;
        displays[0].allShipsPlaced = displays[1].allShipsPlaced = false;
        
        // Repaint
        displays[0].repaint();
        displays[1].repaint();
        
        // Make a new AI if we're using AI
        // (this lets the AI reset its internal state)
        if (isPlayingAI)
            ai = new BattleshipAI();
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/16/2018
     * Inputs: The x and y coordinates the user is firing on
     * Outputs: Fires, shows a message if the user sinks a ship, and invokes win if neccessary
     * Description:
     *     Fires at the indicated coordinates
     *     If the result is NoResult, continues
     *     If the result is SunkShip, show a message
     *     If the result is Win, invoke win
     *     Simulates AI if required
     */
    private void fire(int x, int y) {
        // Make the move
        BattleshipMove result = game.fire(x, y);
        
        // Play the sound if it hit
        if (game.hitmaps[game.turn][x][y] == HitmapCell.HIT_SHIP)
            AudioUtils.play("fire.wav");
        
        if (result == BattleshipMove.SUNK_SHIP) {
            // They sunk a ship
            JOptionPane.showMessageDialog(this, "You sunk a ship!");
        } else if (result == BattleshipMove.WIN) {
            // They won!
            win();
            return;
        }
        
        // Make a move as the AI if required
        if (isPlayingAI) {
            if (ai.fire(game) == BattleshipMove.WIN) {
                win();
                return;
            }
        }
        
        // Repaint the board
        displays[0].repaint();
        displays[1].repaint();
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: The KeyEvent to process
     * Outputs: Uses 'a' to rotate backward and 'd' to rotate forward
     * Description:
     *     Modifies the BattleshipGame's rotation on 'a' and 'd' keypress
     *     events. Returns false to allow further processing
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        // Only process key press events, not up or down events
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            if (e.getKeyCode() == KeyEvent.VK_A) {
                // Move forward three to move back one
                rotation += 3;
                rotation %= 4;
            } else if (e.getKeyCode() == KeyEvent.VK_D) {
                rotation += 1;
                rotation %= 4;
            }
        }
        // Return false to allow the keyboard event to continue processing
        return false;
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: The x and y coordinates the user would like to place the next ship at
     * Outputs: Undoes any previews in progress, then places the ship
     * Description:
     *     First, sets displays[game.turn].game to game to undo previews
     *     Then, places the ship
     *     If all ships are placed, hooks up fire to onFire
     */
    private void placeShip(int x, int y) {
        // Undo any preview operations (see previewPlaceShip)
        displays[game.turn].game = game;
        
        // Place the ship
        game.placeShip(x, y, rotation);
        // Errors in placeShip are handled well implicitly; it just doesn't do anything
        
        // If playing as an AI AND the user's placement succeeded
        if (isPlayingAI && game.turn == 1) {
            ai.placeShip(game);
        }
        
        displays[0].repaint();
        displays[1].repaint();
        
        
        // If all ships are placed, start the game
        if (game.allShipsPlaced()) {
            displays[0].onHover = displays[1].onHover = null;
            displays[0].onFire = displays[1].onFire = this::fire;
            displays[0].allShipsPlaced = displays[1].allShipsPlaced = true;
        }
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: The x and y co-ordinates the user is hovering over
     * Outputs: "Previews" the ship placement by cloning the board, placing the ship, and displaying the new board
     * Description:
     *     Used to let the user preview their ship's placement
     *     It clones the board, places the ship, and sets the BattleshipBoardDisplay to display the clone
     */
    private void previewPlaceShip(int x, int y) {
        // Clone the game's state and display it with the changes
        Battleship clone = game.cloneState();
        clone.placeShip(x, y, rotation);
        // Fix the clone's turn; otherwise the board won't display the ships
        clone.turn = game.turn;
        displays[game.turn].game = clone;
        displays[game.turn].repaint();
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: None
     * Outputs: Redoes the board layout calculations
     * Description:
     *     Invoked on resize; places the BattleshipBoardDisplays using setBounds
     *     It tries to allocate 1/3 of the horizontal space to each board
     */
    private void doBoardLayout() {
        // First, figure out the base "unit." This should be 1/3 of the board size,
        // which will be 1/3 of the vertical size or 1/9 of the horizontal size, whichever is smaller
        int unit = Math.min(root.getHeight() / 3, root.getWidth() / 9) ;
        int boardSz = unit * 3;
        
        // Calculate the vertical position
        int vpos = (root.getHeight() - boardSz) / 2;
        
        if (vpos < 2) {
            // The board has been resized oddly. Keep using the boardSz derived from the height,
            // but center using the width. This works pretty well, but not perfectly, for
            // window sizes with very little vertical room
            unit = root.getWidth() / 9;
        }
        
        // Now, position the boards. Each will be unit*3 by unit*3 size
        // Padding will be distributed unit - board - unit - board - unit
        displays[0].setBounds(unit, vpos, boardSz, boardSz);
        displays[1].setBounds(unit*5, vpos, boardSz, boardSz);
        
        // That was easy. 7 LOC of layout code. Absolutely the thing to write a custom
        // layout function for. I don't even know what this would look like using Swing
        // layouts
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: Opens a dialog to let the user choose
     * Outputs: When complete, user's response goes to gameTypeSelected
     * Description:
     *     Opens a SelectGameType wired to gameTypeSelected to
     *     allow the user to select the type of game they want to play
     */
    private void selectGameType() {
        // Create and show the dialog
        SelectGameType dialog = new SelectGameType(this);
        dialog.onComplete = this::gameTypeSelected;
        dialog.setVisible(true);
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: Receives the user's choice as a Boolean
     * Outputs: Records the user's choice in isPlayingAI
     * Description:
     *     Receives whether or not the user chose to play against the AI
     *     from the SelectGameType; records this in isPlayingAI
     */
    private void gameTypeSelected(Boolean choice) {
        isPlayingAI = choice;
        if (isPlayingAI && ai == null)
            ai = new BattleshipAI();
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: Uses the game's state
     * Outputs: Shows an appropriate win or loss dialog based on the game state
     * Description:
     *     If playing AI and game's turn is 1 -> you lose
     *     Otherwise, show player X wins
     */
    private void win() {
        // Unhook the fire event
        displays[0].onFire = displays[1].onFire = null;
        
        // Repaint the boards
        displays[0].repaint();
        displays[1].repaint();
        
        // The image and message to display
        JLabel image;
        String message;
        
        if (isPlayingAI && game.turn == 1) {
            // The AI won
            image = new SmoothLabel(ImageUtils.loadIcon("lose.png"));
            message = "You Lose!!!";
            
            // Play the lose sound
            AudioUtils.play("lose.wav");
        } else if (game.turn == 1) {
            // Player 2 won
            image = new SmoothLabel(ImageUtils.loadIcon("player2win.png"));
            message = "Player 2 Wins!!!";
            
            // Play the win sound
            AudioUtils.play("flourish.wav");
        } else {
            // Player 1 won
            image = new SmoothLabel(ImageUtils.loadIcon("player1win.png"));
            message = "Player 1 Wins!!!";
            
            // Play the win sound
            AudioUtils.play("flourish.wav");
        }
        
        // Display the dialog using JOptionPane
        JOptionPane.showMessageDialog(this, image, message,
                                      JOptionPane.PLAIN_MESSAGE);
        
        /*
         * The resources are antialiased, yet the JLabel class doesn't support alpha blending
         * properly. This results in somewhat fuzzy borders on the image. Regrettably, there's
         * nothing I can do about it.
         */
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/28/2018
     * Inputs: None
     * Outputs: Displays the user guide PDF; if this fails, allows the user to download Adobe Reader
     * Description:
     *     Uses Desktop->open to open "Battleship-Guide.pdf" from the same directory
     *     If this fails, asks the user if they'd like to download Adobe Reader, and, if so,
     *     opens Adobe's web site
     */
    private void help() {
        Desktop desktop = Desktop.getDesktop();
        
        try {
            // Make sure the user guide exists
            File guide = new File("Battleship-Guide.pdf");
            if (!guide.isFile()) {
                JOptionPane.showMessageDialog(this, "User Guide not found;" +
                                              " check your Battleship install.",
                                              "File not found",
                                              JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Open the user guide
            desktop.open(guide);
        } catch (Exception e) {
            // The user must not have an application to open PDFs - offer to download adobe reader
            int choice = JOptionPane.showConfirmDialog(this, "Battleship's" + 
                    " User Guide is delivered in the industry-standard" +
                    " Portable Document Format (PDF) by Adobe.\n\nWould" +
                    " you like to download Adobe Reader for PDF documents?",
                    "Download PDF reader?", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                try {
                    /*
                     * Note the interesting, non-standard, choice of the Adobe Reader download URL
                     * This is because Adobe offers 3 download URLs:
                     *     1) The standard, consumer Adobe Reader URL.
                     *        This offers to install McAfee antivirus.
                     *        Yeah, not even once :/
                     *     2) The "enterprise," Adobe Reader URL.
                     *        No "anti"-malware bundled, but a lot of confusing, scary language.
                     *        Not great for a user facing application.
                     *     3) The direct download URL (generated from 2) )
                     *        Does a direct download, but it's the best of
                     *        three bad choices.
                     * 
                     * I chose 3) for the reasons outlined above.
                     */
                    desktop.browse(new URI("https://get.adobe.com/reader/com" +
                            "pletion/?installer=Reader_DC_2019.008.20081_Eng" +
                            "lish_for_Windows&stype=7765&direct=true&standalone=1"));
                } catch (Exception e2) {
                    // A platform without a browser. Interesting. Tell the user
                    JOptionPane.showMessageDialog(this, "No browser found.",
                                                  "An error occurred",
                                                  JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/18/2018
     * Inputs: None
     * Outputs: Shows an about dialog
     * Description:
     *     Invoked by Help -> About. Shows an AboutDialog for Battleship
     */
    private void about() {
        // Create the dialog
        AboutDialog dialog = new AboutDialog(this, ImageUtils.loadIcon("icon.png"),
                "Battleship", "Battleship is a fun Java Swing game", "Sam Haskins",
                "1.1.2", "The Battleship icon is licensed from Icons8. For more" +
                " details, please see THIRD-PARTY.txt, included in your" +
                " distribution of Battleship.");
        // Show the dialog
        dialog.setVisible(true);
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: None
     * Outputs: Exits BattleshipGame
     * Description:
     *     Posts a window closing message to the queue to exit BattleshipGame
     */
    private void exit() {
       // Post an exit message to the queue
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
}