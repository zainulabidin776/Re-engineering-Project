/**
 * Utility class for system-related operations
 * Consolidates OS detection logic that was scattered across multiple classes
 */
public class SystemUtils {
    
    /**
     * Checks if the current operating system is Windows
     * @return true if Windows, false otherwise
     */
    public static boolean isWindows() {
        String osName = System.getProperty("os.name");
        return osName != null && (osName.startsWith("W") || osName.startsWith("w"));
    }
    
    /**
     * Gets the line separator for the current operating system
     * @return line separator string
     */
    public static String getLineSeparator() {
        return System.getProperty("line.separator");
    }
    
    // Private constructor to prevent instantiation
    private SystemUtils() {
        throw new AssertionError("SystemUtils class should not be instantiated");
    }
}

