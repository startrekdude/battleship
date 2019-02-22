/*
 * Date: 11/15/2018
 * Dev: Sam Haskins
 * Version: v1.0
 */
package com.gmail.cheesedude54321.battleship.gui;

import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.gmail.cheesedude54321.utility.ActionUtils;

/*
 * Name: Sam Haskins
 * Date: 11/15/2018
 * Inputs: User input of the game to select
 * Outputs: Returns to consumer via onComplete
 * Description:
 *     A dialog to allow the user to select whether they'd like to
 *     play against the AI or another human. Uses RadioButtons, and
 *     returns the result via a Consumer<Boolean> onComplete
 */
public final class SelectGameType extends JDialog {
    public Consumer<Boolean> onComplete;
    
    private ButtonGroup selection;
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: The Frame that this will modal for
     * Outputs: Creates a new SelectGameType with a standard layout
     * Description:
     *     Creates a SelectGameType with radio buttons for human and AI
     */
    public SelectGameType(Frame parent) {
        // Make this a modal dialog
        super(parent, Dialog.ModalityType.APPLICATION_MODAL);
        
        // Set basic properties
        setTitle("Select Type of Game");
        setSize(300, 200);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        // Don't let the user use the X button
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        // Create the layout
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        getContentPane().add(root);
        
        // Add a label with a title size, bolded
        JLabel title = new JLabel("Select Type of Game");
        title.setFont(new Font("Sans", Font.BOLD, 18));
        title.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        root.add(title);
        
        // Create radio buttons for AI and human
        selection = new ButtonGroup();
        
        JRadioButton ai = new JRadioButton("Game against the AI");
        ai.setActionCommand("AI");
        ai.setSelected(true);
        ai.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        selection.add(ai);
        root.add(ai);
        
        JRadioButton human = new JRadioButton("Two-player game");
        human.setActionCommand("Human");
        human.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        selection.add(human);
        root.add(human);
        
        // Add the button
        JButton button = ActionUtils.button("Select", this::complete);
        button.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        root.add(button);
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: The user's selection
     * Outputs: Returns whether the user is playing AI to onComplete
     * Description:
     *     Evaluates whether the user would like to play against the AI, and
     *     returns the result via onComplete
     */
    private void complete() {
        // Make sure onComplete isn't null
        if (onComplete != null) {
            if (selection.getSelection().getActionCommand().equals("AI")) {
                onComplete.accept(true);
            } else {
                onComplete.accept(false);
            }
        }
        
        // Close the dialog
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
}
