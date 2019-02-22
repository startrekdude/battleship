/*
 * Date: 11/2/2018
 * Dev: Sam Haskins
 * Version: v1.0-CONTACTMANAGERV4
 */
package com.gmail.cheesedude54321.utility;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JMenuItem;

/*
 * Name: Sam Haskins
 * Date: 11/2/2018
 * Inputs: Various text and Runnables to create components for
 * Outputs: Various components with specific text that run Runnables when clicked
 * Description:
 *     A set of utilities that help to create Swing GUI elements
 *     associated with specific Runnable's as their corresponding actions
 *     Ex:
 *         ActionUtils->menu ("Save", CustomWindow::Save) returns a JMenuItem
 *         ActionUtils->button ("Save, CustomWindow::Save) returns a JButton
 *     These items also print the name of the action invoked to the console, though
 *     this behavior is configurable using printActions
 */
public final class ActionUtils {
    public static Boolean printActions = true;
    
    /*
     * Name: Sam Haskins
     * Date: 11/2/2018
     * Inputs: The desired text and the action to be run
     * Outputs: A JMenuItem with the desired text that runs the action when selected
     * Description:
     *     An easy and quick way to create menu items that invoke functions, no
     *     dispatcher intermediate needed! Additionally, if printActions is true,
     *     prints the action's name when invoked
     */
    public static JMenuItem menu (String text, Runnable action) {
        // Check the arguments and throw if invalid
        if (text == null || action == null)
            throw new IllegalArgumentException("text and action must not be null.");
        
        // Create the JMenuItem
        JMenuItem item = new JMenuItem(text);
        item.addActionListener((e) -> action.run());
        if (printActions)
            item.addActionListener(ActionUtils::printAction);
        return item;
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/2/2018
     * Inputs: The desired text and the action to be run
     * Outputs: A JButton with the desired text that runs the action when selected
     * Description:
     *     Creates a JButton that runs a specific Runnable when invoked. Also,
     *     if printActions is true, prints the action's name when invoked
     */
    public static JButton button (String text, Runnable action) {
        // Check the argument and throw if invalid
        if (text == null || action == null)
            throw new IllegalArgumentException("text and action must not be null.");
        
        // Create the JButton
        JButton button = new JButton(text);
        button.addActionListener((e) -> action.run());
        if (printActions)
            button.addActionListener(ActionUtils::printAction);
        return button;
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/2/2018
     * Inputs: An ActionEvent detailing the action to be printed
     * Outputs: Prints the action to the console
     * Description:
     *     Receives events from components created by ActionUtils
     *     Prints the action that occurred to the console
     */
    private static void printAction (ActionEvent e) {
        System.out.println(e.getActionCommand());
    }
}
