/*
 * Date: 11/1/2018
 * Dev: Sam Haskins
 * Version: v1.01-CONTACTMANAGERV4
 */
package com.gmail.cheesedude54321.utility;

import java.io.PrintWriter;
import java.io.StringWriter;

/*
 * Name: Sam Haskins
 * Date: 11/1/2018
 * Inputs: Throwables
 * Outputs: Various data related to Throwables
 * Description
 *     So, the Throwable class in the Java standard library has a method
 *     to print the stack trace to standard out, but no method to just get
 *     the stack trace as a String, for some reason. This becomes a problem
 *     in, for example, graphical programs where a user needs to be informed
 *     of an error by a message box
 */
public final class ThrowableUtils {
    
    /*
     * Name: Sam Haskins
     * Date: 11/1/2018
     * Inputs: A Throwable, e, to get the stack trace of
     * Outputs: The Throwable's stack trace as a String
     * Description:
     *     Returns the Throwable's stack trace, as printed by printStackTrace,
     *     as a String
     */
    public static String getStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
        // Huh looks like Eclipse is smart enough to know that StringWriters don't have to be closed :)
    }
}