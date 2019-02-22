/*
 * Date: 10/17/2018
 * Dev: Sam Haskins
 * Version: v1.0
 */
package com.gmail.cheesedude54321.utility;

import java.util.List;

/*
 * Name: Sam Haskins
 * Date: 10/17/2018
 * Inputs: Various bounds, etc, for random numbers
 * Outputs: Various types of random data
 * Description:
 *     A utility class for doing common operations related to random numbers
 *     Partially inspired by Python's random module
 *     Currently has methods that:
 *         - Return whether a "1 in X" chance happened
 *         - Return a random Boolean
 *         - Return a random number in range
 *         - Pick a random entry from a list
 */
public final class RandomUtils {
    
    /*
     * Name: Sam Haskins
     * Date: 11/18/2018
     * Inputs: Minimum and maximum bounds defining a range
     * Outputs: A random number in that range, inclusive on both bounds
     * Description:
     *     Inspired by Python's randint()
     *     Returns a random number within a specified range
     *     Throws IllegalArgumentException is min > max
     */
    public static int range(int min, int max) {
        // Check the bounds
        if (min > max) {
            throw new IllegalArgumentException("min must be less than max");
        }
        
        // Return a random number in the desired range
        return min + (int)(Math.random() * ((max - min) + 1));
    }
    
    /*
     * Name: Sam Haskins
     * Date: 10/17/2018
     * Inputs: Minimum and maximum bounds defining a range
     * Outputs: A random number in that range, inclusive on both bounds
     * Description:
     *     Inspired by Python's randint()
     *     Returns a random number within a specified range
     *     Throws IllegalArgumentException is min >= max
     */
    public static <T> T choice(List<T> list) {
        // Pick a random index, and return the corresponding item
        return list.get(RandomUtils.range(0, list.size() - 1));
    }
    
    /*
     * Name: Sam Haskins
     * Date: 10/17/2018
     * Inputs: The X in "1-in-X chance"
     * Outputs: Whether that chance succeeds
     * Description:
     *     Simulates a 1-in-X chance, returns a Boolean if it passes
     */
    public static Boolean chance(int chance) {
        return range(1, chance) == chance;
    }
    
    /*
     * Name: Sam Haskins
     * Date: 10/17/2018
     * Inputs: None
     * Outputs: Returns a random true or false value
     * Description:
     *     Returns a random Boolean using RandomUtils->chance with 2
     */
    public static Boolean bool() {
        return chance(2);
    }
}
