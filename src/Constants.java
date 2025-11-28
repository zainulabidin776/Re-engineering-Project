/**
 * Constants class to replace magic numbers and strings throughout the codebase
 * This improves maintainability and makes configuration changes easier
 */
public class Constants {
    
    // Tax rates
    public static final double DEFAULT_TAX_RATE = 1.06;
    public static final double NJ_TAX_RATE = 1.07;
    public static final double NY_TAX_RATE = 1.04;
    
    // Discount rates
    public static final float COUPON_DISCOUNT = 0.90f;
    
    // Validation constants
    public static final int CREDIT_CARD_LENGTH = 16;
    
    // File paths - Database directory
    public static final String DATABASE_DIR = "Database/";
    
    // Database file names
    public static final String EMPLOYEE_DATABASE = DATABASE_DIR + "employeeDatabase.txt";
    public static final String ITEM_DATABASE = DATABASE_DIR + "itemDatabase.txt";
    public static final String RENTAL_DATABASE = DATABASE_DIR + "rentalDatabase.txt";
    public static final String USER_DATABASE = DATABASE_DIR + "userDatabase.txt";
    public static final String COUPON_FILE = DATABASE_DIR + "couponNumber.txt";
    public static final String TEMP_FILE = DATABASE_DIR + "temp.txt";
    public static final String NEW_TEMP_FILE = DATABASE_DIR + "newTemp.txt";
    public static final String SALE_INVOICE_RECORD = DATABASE_DIR + "saleInvoiceRecord.txt";
    public static final String RETURN_SALE_RECORD = DATABASE_DIR + "returnSale.txt";
    public static final String EMPLOYEE_LOG_FILE = DATABASE_DIR + "employeeLogfile.txt";
    
    // Employee positions
    public static final String POSITION_ADMIN = "Admin";
    public static final String POSITION_CASHIER = "Cashier";
    
    // Login return codes
    public static final int LOGIN_INVALID = 0;
    public static final int LOGIN_CASHIER = 1;
    public static final int LOGIN_ADMIN = 2;
    
    // Date formats
    public static final String DATE_FORMAT_SHORT = "MM/dd/yy";
    public static final String DATE_FORMAT_LONG = "yyyy-MM-dd HH:mm:ss.SSS";
    
    // Private constructor to prevent instantiation
    private Constants() {
        throw new AssertionError("Constants class should not be instantiated");
    }
}

