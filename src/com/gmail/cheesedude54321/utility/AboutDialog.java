/*
 * Date: 11/18/2018
 * Dev: Sam Haskins
 * Version: v1.01-BATTLESHIP
 */
package com.gmail.cheesedude54321.utility;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/*
 * Name: Sam Haskins
 * Date: 11/18/2018
 * Inputs: Information about an application
 * Outputs: A dialog box for that application
 * Description:
 *     A generic about dialog for Swing programs. Displays the program's icon,
 *     information about the program, and information about the environment.
 */
public final class AboutDialog extends JDialog {
    
    /*
     * Name: Sam Haskins
     * Date: 11/18/2018
     * Inputs: A Frame to modal for, the program's Icon, the name, description, author, version, and extra info to display
     * Outputs: Creates an AboutDialog with the specified information
     * Description:
     *     Builds an about dialog displaying the provided information and icon
     */
    public AboutDialog(Frame frame, Icon icon, String name, String description,
                       String author, String version, String extraInfo) {
        // Modal for the frame
        super(frame, Dialog.ModalityType.APPLICATION_MODAL);
        
        // The basic properties
        setTitle(String.format("About %s", name));
        setSize(600, 400);
        setResizable(false);
        
        // Create and add the root panel, using a horizontal layout
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.X_AXIS));
        getContentPane().add(root);
        
        // Create the left panel, with the icon and close button
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        
        SmoothLabel iconDisplay = new SmoothLabel(icon);
        iconDisplay.setMaximumSize(new Dimension(256, 256));
        iconDisplay.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        left.add(iconDisplay);
        
        // We wrap the close button in another JPanel to add a margin
        JPanel closeHolder = new JPanel();
        closeHolder.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        
        JButton close = ActionUtils.button("Close", this::close);
        close.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        
        closeHolder.add(close);
        left.add(closeHolder);
        root.add(left);
        
        // Build the dialog text. This is a process
        String dialogText = String.format("<center><h1>About %s</h1></center>" +
                "<p>%s</p>" +
                "<table>" +
                "<tr><td>Version</td><td>%s</td></tr>" +
                "<tr><td>Author</td><td>%s</td></tr>" +
                "<tr><td>Java RE Version</td><td>%s</td></tr>" +
                "<tr><td>Environment</td><td>%s v%s on the %s platform</td></tr>" +
                "</table><br/><hr/>" +
                "<p>%s</p>",
                name, description, version, author,
                System.getProperty("java.version"), System.getProperty("os.name"),
                System.getProperty("os.version"), System.getProperty("os.arch"),
                extraInfo);
        
        // Use HTML for the about message
        JEditorPane pane = new JEditorPane();
        pane.setEditable(false);
        
        pane.setContentType("text/html");
        pane.setText(dialogText);
        
        root.add(new JScrollPane(pane));
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/13/2018
     * Inputs: None
     * Outputs: Closes the dialog
     * Description:
     *     Posts a close event to the dialog's message queue, acting as if
     *     the user pressed the X button
     */
    private void close() {
        // Post a message to the queue to close the window
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
}