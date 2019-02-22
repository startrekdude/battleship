/*
 * Date: 11/15/2018
 * Dev: Sam Haskins
 * Version: v1.0
 */
package com.gmail.cheesedude54321.utility;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/*
 * Name: Sam Haskins
 * Date: 11/15/2018
 * Inputs: The name of images to load
 * Outputs: Image objects from the jar
 * Description:
 *     The code for loading an image resource in Swing is really, really long.
 *     This class makes it easy! Get awt.Image or swing.Icon from an image resource
 *     file name
 */
public final class ImageUtils {
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: The name of the Image to load
     * Outputs: The Image, loaded from the jar or Eclipse rsrc folder
     * Description:
     *     Loads an image asset and returns it to the consumer
     */
    public static Image load(String name) {
        URL path = new ImageUtils().getClass().getClassLoader().getResource(name);
        return Toolkit.getDefaultToolkit().getImage(path);
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/15/2018
     * Inputs: The name of the Icon to load
     * Outputs: A Swing Icon, loaded from the jar or Eclipse rsrc folder
     * Description:
     *     Some libraries accept Image objects and some accept Icon objects.
     *     This wraps the Image in an ImageIcon that implements Icon and
     *     returns it
     */
    public static Icon loadIcon(String name) {
        return new ImageIcon(load(name));
    }
}
