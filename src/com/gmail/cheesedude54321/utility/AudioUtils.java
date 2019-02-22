/*
 * Date: 11/18/2018
 * Dev: Sam Haskins
 * Version: v1.0
 */
package com.gmail.cheesedude54321.utility;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/*
 * Name: Sam Haskins
 * Date: 11/18/2018
 * Inputs: The name of audio resources
 * Outputs: The audio resources
 * Description:
 *     Utilities for playing audio clips stored in the application's resources
 */
public final class AudioUtils {
    
    /*
     * Name: Sam Haskins
     * Date: 11/18/2018
     * Inputs: The name of the resource to play
     * Outputs: Plays the audio resource
     * Description:
     *     Plays the audio resource with the specified resource name
     *     Does nothing if errors are encountered
     */
    public static void play(String name) {
        // First, find the resource
        URL path = new AudioUtils().getClass().getClassLoader().getResource(name);
        
        try {
            // Open the input stream
            AudioInputStream stream = AudioSystem.getAudioInputStream(path);
            
            // Create and play the clip
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            clip.start();
            
            // Clip will release resources as required
        } catch (Exception e) {
            // Oh well, we tried. This shouldn't happen for resources *in* the jar, anyways
            return;
        }
    }
}
