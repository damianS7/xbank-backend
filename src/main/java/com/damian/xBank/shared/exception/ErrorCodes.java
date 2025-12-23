package com.damian.xBank.shared.exception;

public class ErrorCodes {

    // Generics
    public static final String VALIDATION_FAILED = "generic.validation_failed";

    // Login/Security
    public static final String AUTH_LOGIN_BAD_CREDENTIALS = "auth.login.bad_credentials";
    public static final String AUTH_JWT_TOKEN_EXPIRED = "auth.jwt.token.expired";
    public static final String AUTH_JWT_TOKEN_INVALID = "auth.jwt.token.invalid";

    // User account
    public static final String USER_ACCOUNT_NOT_FOUND = "user.account.not_found";
    public static final String USER_ACCOUNT_SUSPENDED = "user.account.suspended";
    public static final String USER_ACCOUNT_EMAIL_TAKEN = "user.account.email_taken";
    public static final String USER_ACCOUNT_INVALID_PASSWORD = "user.account.invalid_password";
    public static final String USER_ACCOUNT_NOT_VERIFIED = "user.account.not_verified";

    // User account verification
    public static final String USER_ACCOUNT_VERIFICATION_NOT_PENDING = "user.account.verification.not_pending";
    public static final String USER_ACCOUNT_VERIFICATION_TOKEN_NOT_FOUND = "user.account.verification.token.not_found";
    public static final String USER_ACCOUNT_VERIFICATION_TOKEN_USED = "user.account.verification.token.used";
    public static final String USER_ACCOUNT_VERIFICATION_TOKEN_EXPIRED = "user.account.verification.token.expired";

    // Customer
    public static final String CUSTOMER_NOT_OWNER = "customer.not_owner";
    public static final String CUSTOMER_NOT_FOUND = "customer.not_found";
    public static final String CUSTOMER_UPDATE_FAILED = "customer.update_failed";
    public static final String CUSTOMER_IMAGE_NOT_FOUND = "customer.image.not_found";

    // Banking account
    public static final String BANKING_ACCOUNT_NOT_OWNER = "banking.account.not_owner";
    public static final String BANKING_ACCOUNT_NOT_FOUND = "banking.account.not_found";
    public static final String BANKING_ACCOUNT_SUSPENDED = "banking.account.suspended";
    public static final String BANKING_ACCOUNT_CLOSED = "banking.account.closed";
    public static final String BANKING_ACCOUNT_DISABLED = "banking.account.disabled";
    public static final String BANKING_ACCOUNT_TRANSFER_SAME_DESTINATION = "banking.account.same_destination";
    public static final String BANKING_ACCOUNT_TRANSFER_DIFFERENT_CURRENCY = "banking.account.different_currency";
    public static final String BANKING_ACCOUNT_CARD_LIMIT = "banking.account.card_limit";
    public static final String BANKING_ACCOUNT_INSUFFICIENT_FUNDS = "banking.account.insufficient_funds";
    public static final String BANKING_ACCOUNT_INVALID_TRANSITION_STATUS = "banking.account.invalid_transition_status";
    public static final String BANKING_ACCOUNT_FAILED_DEPOSIT = "banking.account.failed_deposit";

    // Banking card
    public static final String BANKING_CARD_NOT_FOUND = "banking.card.not_found";
    public static final String BANKING_CARD_NOT_OWNER = "banking.card.not_owner";
    public static final String BANKING_CARD_INVALID_TRANSITION_STATUS = "banking.card.invalid_transition_status";
    public static final String BANKING_CARD_LOCKED = "banking.card.locked";
    public static final String BANKING_CARD_INVALID_PIN = "banking.card.invalid_pin";
    public static final String BANKING_CARD_DISABLED = "banking.card.disabled";
    public static final String BANKING_CARD_INSUFFICIENT_FUNDS = "banking.card.insufficient_funds";

    // Transactions
    public static final String BANKING_TRANSACTION_NOT_FOUND = "banking.transaction.not_found";
    public static final String BANKING_TRANSACTION_NOT_OWNER = "banking.transaction.not_owner";
    public static final String
            BANKING_TRANSACTION_INVALID_TRANSITION_STATUS
            = "banking.transaction.invalid_transition_status";

    // Notification
    public static final String NOTIFICATION_NOT_FOUND = "notification.not_found";
    public static final String NOTIFICATION_NOT_OWNER = "notification.not_owner";

    // Setting
    public static final String SETTING_NOT_OWNER = "setting.not_owner";
    public static final String SETTING_NOT_FOUND = "setting.not_found";

    // Storage
    public static final String STORAGE_FILE_NOT_FOUND = "storage.file.not_found";
    public static final String STORAGE_FILE_INVALID_PATH = "storage.invalid_path";
    public static final String STORAGE_FILE_FAILED = "storage.failed";
    public static final String STORAGE_FILE_DELETE_FAILED = "storage.delete_failed";
    public static final String STORAGE_UPLOAD_FILE_TOO_LARGE = "storage.upload.file.too_large";

    // Image Storage
    public static final String STORAGE_IMAGE_NOT_FOUND = "storage.image.not_found";
    public static final String STORAGE_IMAGE_UPLOAD_FAILED = "storage.image.upload_failed";
    public static final String STORAGE_IMAGE_UPLOAD_TOO_LARGE = "storage.image.upload.file_too_large";
    public static final String STORAGE_IMAGE_INVALID_PATH = "storage.image.invalid_path";
    public static final String STORAGE_IMAGE_INVALID_TYPE = "storage.image.invalid_type";
    public static final String STORAGE_IMAGE_FAILED_RESIZE = "storage.image.failed_resize";
    public static final String STORAGE_IMAGE_FAILED_COMPRESS = "storage.image.failed_compression";
    public static final String STORAGE_IMAGE_EMPTY = "storage.image.empty_file";
}