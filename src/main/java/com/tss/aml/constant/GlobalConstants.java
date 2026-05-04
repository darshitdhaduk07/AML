package com.tss.aml.constant;

public class GlobalConstants {
    public static final String CSV_DELIMITER = ",";

    public static final String[] CUSTOMER_EXPECTED_HEADERS = {
            "customer_number", "first_name", "middle_name", "last_name",
            "family_code", "dob", "occupation", "nationality_country",
            "country_of_birth", "income", "net_worth"
    };

    public static final String[] TRANSACTION_EXPECTED_HEADERS = {
            "transaction_number", "account_number", "customer_number",
            "txn_time", "amount", "txn_type", "direction", "country","account_type", "IFSC"
    };

    public static final String UPLOAD_DIR = "uploads/";

    public static final String REPORT_DIR = "src/main/resources/reports/";

    public static final String DB_NAME = "aml";
}
