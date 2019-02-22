/*
 * Date: 12/11/2018
 * Dev: Sam Haskins
 * Version: 1.0
 */
package com.gmail.cheesedude54321.utility;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;
import javax.swing.JLabel;

/*
 * Name: Sam Haskins
 * Date: 12/11/2018
 * Inputs: An Icon image to display
 * Outputs: Displays the image smooth-ly using bilinear interpolation
 * Description:
 *     A very simple subclass of JLabel that enables bilinear
 *     interpolation. Interpolation is like antialiasing, but
 *     it works on raster graphics. This makes image labels
 *     look significantly better and removes the fuzzy edge
 *     effect.
 *     The standard JLabel should be used in preference to this
 *     for texts
 */
public final class SmoothLabel extends JLabel {
    
    /*
     * Name: Sam Haskins
     * Date: 12/11/2018
     * Inputs: An image to make a label for
     * Outputs: A SmoothLabel for that image
     * Description:
     *     Supers with image to construct a JLabel for the image
     */
    public SmoothLabel(Icon image) {
        super(image);
    }
    
    /*
     * Name: Sam Haskins
     * Date: 12/11/2018
     * Inputs: A Graphics object to paint to
     * Outputs: Enables bilinear interpolation, then supers to paint
     * Description:
     *     Enables bilinear interpolation on the Graphics,
     *     then uses the JLabel's implementation to paint
     */
    @Override
    protected void paintComponent(Graphics g) {
        // No more fuzzy edges
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                          RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        super.paintComponent(g);
    }
}
