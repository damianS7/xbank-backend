package com.damian.xBank.shared.exception;

public class Exceptions {

    public static class USER {

        public static class ACCOUNT {
            public static final String BAD_CREDENTIALS = "Invalid email or password.";
            public static final String NOT_FOUND = "Account not found.";
            public static final String INVALID_PASSWORD = "Password does not match.";
            public static final String SUSPENDED = "Account has been suspended.";
            public static final String NOT_VERIFIED = "Account email is not verified.";
            public static final String EMAIL_TAKEN = "This email is already registered.";


            public static class VERIFICATION {
                public static final String NOT_ELIGIBLE = "Account is not eligible for activation.";

                public static class TOKEN {
                    public static final String NOT_FOUND = "Token not found.";
                    public static final String USED = "This token has already been used.";
                    public static final String EXPIRED = "This token has expired.";
                }
            }
        }

    }

    public static class CUSTOMER {
        public static final String EMAIL_TAKEN = "Email is already taken.";
        public static final String NOT_FOUND = "Customer not found.";
        public static final String NOT_OWNER = "Customer ownership violation.";
        public static final String UPDATE_FAILED = "Customer update failed.";

        public static class IMAGE {
            public static final String NOT_FOUND = "Profile photo not found.";
            public static final String FILE_SIZE_LIMIT = "Profile photo is too large.";
            public static final String ONLY_IMAGES_ALLOWED = "Profile photo must be an image.";
            public static final String EMPTY_FILE = "File is empty.";
            public static final String UPLOAD_FAILED = "Profile photo upload failed.";
        }

        public static class SETTINGS {
            public static final String NOT_FOUND = "Setting not found.";
            public static final String NOT_OWNER = "You are not the owner of this setting.";
        }

        public static class NOTIFICATION {
            public static final String SELF_NOTIFICATION = "You cannot notify yourself.";
        }
    }


    public static class IMAGE {
        public static final String NOT_FOUND = "Image not found.";
        public static final String INVALID_PATH = "Image path is invalid.";
        public static final String TOO_LARGE = "Image is too large.";
        public static final String TYPE_NOT_SUPPORTED = "Image type not supported.";
        public static final String EMPTY = "Image file is empty.";
        public static final String INVALID = "Image is not valid.";
        public static final String UPLOAD_FAILED = "Image upload failed.";
        public static final String TYPE_NOT_DETECTED = "Image type could not be detected.";
    }

    public static class JWT {
        public static class TOKEN {
            public static final String EXPIRED = "JWT token has expired.";
            public static final String INVALID = "Invalid JWT token.";
        }
    }

    public static class COMMON {
        public static final String NOT_OWNER = "You are not allowed to access this resource.";
        public static final String NOT_FOUND = "Resource not found.";
        public static final String VALIDATION_FAILED = "Validation failed.";
    }

    public static class STORAGE {
        public static final String NOT_FOUND = "File not found.";
        public static final String INVALID_PATH = "Path is invalid.";
        public static final String FAILED = "File storage failed.";
    }


    public static class BANKING {
        public static class ACCOUNT {
            public static final String SUSPENDED = "This account is suspended.";
            public static final String CLOSED = "This account is disabled.";
            public static final String SAME_DESTINATION = "You cannot transfer to the same account.";
            public static final String NOT_FOUND = "Account not found.";
            public static final String CARD_LIMIT = "The account has reached the maximum number of cards allowed.";
            public static final String LOCKED = "Account is locked.";
            public static final String DISABLED = "Account is disabled.";
            public static final String ACCESS_FORBIDDEN = "You are not the owner of this account.";
            public static final String OWNERSHIP = "You are not the owner of this account.";
            public static final String INSUFFICIENT_FUNDS = "Insufficient funds.";
            public static final String INVALID_TRANSITION_STATUS = "Account cannot transition from %s to %s";
        }

        public static class CARD {
            public static final String OWNERSHIP = "You are not the owner of this card.";
            public static final String INVALID_TRANSITION_STATUS = "Card cannot transition from %s to %s";
            public static final String NOT_FOUND = "Card not found.";
            public static final String LOCKED = "Card is locked.";
            public static final String INVALID_PIN = "Incorrect card pin.";
            public static final String DISABLED = "Card is disabled.";
            public static final String ACCESS_FORBIDDEN = "You are not the owner of this card.";
            public static final String INSUFFICIENT_FUNDS = "Insufficient funds.";
        }

        public static class TRANSACTION {
            public static final String NOT_FOUND = "Transaction not found.";
            public static final String ACCESS_FORBIDDEN = "You are not the owner of this transaction.";
            public static final String INVALID_TYPE = "Invalid transaction type";
            public static final String DIFFERENT_CURRENCY = "Transactions must be in the same currency.";
            public static final String FAILED_DEPOSIT = "Failed to deposit.";
            public static final String OWNERSHIP = "You are not the owner of this transaction.";
            public static final String
                    INVALID_STATUS_CHANGE
                    = "BankingTransactionStatus cannot transition from %s to %s";
        }

        public static class OPERATION {
            public static final String FAILED_DEPOSIT = "Transaction not found.";
        }
    }
}


