/*
 * Date: 11/15/2018
 * Dev: Sam Haskins
 * Version: v1.0
 */
package com.gmail.cheesedude54321.battleship;

import java.awt.Frame;
import java.awt.Window;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.gmail.cheesedude54321.battleship.gui.BattleshipGame;
import com.gmail.cheesedude54321.utility.ThrowableUtils;

/*
 * Name: Sam Haskins
 * Date: 11/17/2018
 * Inputs: No direct inputs
 * Outputs: Initializes the environment and starts a BattleshipGame
 * Description:
 *     A class that contains main. Responsible for setting up the Swing environment,
 *     registering the Master Error Handler, and starting a new BattleshipGame
 */
public final class BattleshipGameProgram {
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: No direct inputs
     * Outputs: Starts a new BattleshipGame after registering the Master Error Handler
     * Description:
     *     The entry point for the program. Sets the Master Error Handler, loads
     *     the Swing theme, and creates and shows a new BattleshipGame
     */
    public static void main(String[] args) {
        // Register the Master Error Handler
        Thread.setDefaultUncaughtExceptionHandler(BattleshipGameProgram::masterErrorHandler);
        
        // Try to set the look and feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            /*
             * This really shouldn't fail; NimbusLookAndFeel is supported on every platform
             * This isn't a fatal error, of course; the program can continue.
             * Tell the user and keep going
             */
            String message = String.format("Error setting look and feel:\n%s\n" +
                                           "Application will run without theming",
                                           ThrowableUtils.getStackTrace(e));
            JOptionPane.showMessageDialog(null, message, "An error occurred",
                                          JOptionPane.ERROR_MESSAGE);
        }
        
        // Start BattleshipGame
        BattleshipGame window = new BattleshipGame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Maximize by default
        window.setExtendedState(window.getExtendedState() | Frame.MAXIMIZED_BOTH);
        window.setVisible(true);
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/2/2018
     * Inputs: Accepts a Thread, t, and a Throwable, e
     * Outputs: Informs the user of the error
     * Description:
     *     The master error handler of the program
     *     Swing creates a separate thread to handle UI activities on; as such,
     *     exceptions may not reach main. Here we create an uncaught exception handler
     *     to handle all the program's exceptions in a more graceful way than Java
     *     would by default
     */
    private static void masterErrorHandler(Thread t, Throwable e) {
        /*
         * The Master Error Handler for the program
         * 
         * If an error has occurred and the control flow has returned here,
         * rather than a more specific catch block, then we can't recover.
         * 
         * This is a bug; I've most likely made a mistake in an algorithm or
         * forgot to handle an Exception in something user-facing.
         * 
         * More specific exception and edge-case handlers exist where required, including in
         * the input-handling routines, among other locations.
         * 
         * If control flow is here, I've made a mistake, it's a bug, the program can't
         * recover.
         * 
         * Ergo, we:
         *     i) Cleanup: Close all open windows
         *    ii) Inform the user that a bug has occurred
         *   iii) Instruct them to inform the developer, providing:
         *    iv) The complete stacktrace that we print
         *     v) Cleanup: Exit the program
         * 
         * Additionally, since this is a graphical program, we inform the user
         * graphically using a JOptionPane.
         * 
         * This is harder than it sounds, as no-one
         * saw fit to include in the standard library a method to get a Throwable's stack
         * trace as a String; the stdlib only has a method to print said stack trace
         * to the console.
         */
        // Close all windows. We use dispose to make sure that the windows clean up before
        // we forcibly exit the program
        Arrays.stream(Window.getWindows())
                     .forEach((w) -> w.dispose());
        
        // Print to console
        String msg = "An error has occurred. This indicates that ContactManager"
                + " has a serious bug.\nPlease report this bug to the"
                + " developer at your earliest convenience.\nInclude the"
                + " following details in your bug report: \n";
        System.err.print(msg);
        e.printStackTrace();
        
        // Show a GUI
        JOptionPane.showMessageDialog(null,
                                      msg + ThrowableUtils.getStackTrace(e),
                                      "An error occurred",
                                      JOptionPane.ERROR_MESSAGE);
        
        // Exit the program
        System.exit(-1);
    }
}
