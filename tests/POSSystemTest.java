import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Characterization tests for POSSystem (authentication and routing)
 */
public class POSSystemTest {
    
    private POSSystem posSystem;
    
    @Before
    public void setUp() {
        posSystem = new POSSystem();
    }
    
    @Test
    public void testLoginWithInvalidUsername() {
        int result = posSystem.logIn("invalid", "password");
        assertEquals("Should return 0 for invalid username", 0, result);
    }
    
    @Test
    public void testLoginWithValidUsernameWrongPassword() {
        // Using known employee from employeeDatabase.txt
        int result = posSystem.logIn("110001", "wrongpassword");
        assertEquals("Should return 0 for wrong password", 0, result);
    }
    
    @Test
    public void testLoginWithValidCashier() {
        // Using known cashier: 110002 Cashier Debra Cooper lehigh2016
        int result = posSystem.logIn("110002", "lehigh2016");
        assertEquals("Should return 1 for cashier", 1, result);
    }
    
    @Test
    public void testLoginWithValidAdmin() {
        // Using known admin: 110001 Admin Harry Larry 1
        int result = posSystem.logIn("110001", "1");
        assertEquals("Should return 2 for admin", 2, result);
    }
    
    @Test
    public void testCheckTempFileExists() {
        // This test depends on whether temp.txt exists
        boolean result = posSystem.checkTemp();
        // Just verify method doesn't throw exception
        assertNotNull("checkTemp should return boolean", Boolean.valueOf(result));
    }
    
    @Test
    public void testContinueFromTempWhenFileDoesNotExist() {
        // When temp file doesn't exist, should return empty string
        String result = posSystem.continueFromTemp(1234567890L);
        assertEquals("Should return empty string when temp file doesn't exist", "", result);
    }
}

