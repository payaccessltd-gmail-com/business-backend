package com.jamub.payaccess.api.enums;

public enum Permission {
    CREATE_NEW_ACQUIRER("Create New Acquirers"),
    VIEW_ACQUIRERS("View Acquirers"),
    VIEW_AUDIT_TRAILS("View Audit Trails"),
    CREATE_NEW_BANK("Create New Bank"),
    VIEW_BANKS("View Banks"),
    AUTHENTICATE_WITH_OTP("Authenticate with OTP"),
    GENERATE_MERCHANT_KEYS("Generate Merchant Keys"),
    VIEW_MERCHANT_KEYS("View Merchant Keys"),
    UPDATE_MERCHANT_CALLBACK_WEBHOOK("Update Merchant Callback Webhook"),
    CREATE_INVOICE("Create Invoices"),
    RESEND_INVOICE_EMAIL("Resend Email Reminders on Invoice Payment"),
    MARK_INVOICE_AS_PAID("Mark Invoices As Paid"),
    DELETE_INVOICE("Delete Invoices"),
    VIEW_INVOICES("View Invoices"),
    CREATE_MAKER_CHECKER("Create Maker-Checker"),
    VIEW_MAKER_CHECKER("View Maker-Checker"),
    ADD_NEW_MERCHANT("Add New Merchant"),
    UPDATE_MERCHANT("Update Merchant"),
    VIEW_MERCHANT("View Merchants"),
    APPROVE_MERCHANT("Approve Merchant"),
    DISAPPROVE_MERCHANT("Disapprove Merchant"),
    UPDATE_MERCHANT_STATUS("Update Merchant Status"),
    REVIEW_MERCHANT_STATUS("Review Merchant Status"),
    VIEW_MERCHANT_REVIEW("View Merchant Reviews"),
    SWITCH_API_MODE("Switch API Mode of Merchant"),
    VIEW_PAYMENT_REQUEST("View Payment Requests"),
    CREATE_ROLE_PERMISSION("Create Role Permissions"),
    VIEW_ROLE_PERMISSION("View Role Permissions"),
    VIEW_PERMISSION("View Permissions"),
    VIEW_ROLE("View Roles"),
    CREATE_CONTACT_US_MESSAGE("Create Contact Us Messages"),
    CREATE_FEEDBACK_MESSAGE("Create Feedback Messages"),

    CREATE_TICKET("Create Transaction Ticket"),

    ASSIGN_TICKET("Assign Transaction Ticket"),

    VIEW_TICKET("View Transaction Tickets"),

    CLOSE_TICKET("Close Transaction Tickets"),

    VIEW_SETTLEMENT("View Settlement"),

    CREATE_TERMINAL_REQUEST("Create Terminal Request"),
    VIEW_TERMINAL_REQUEST("View Terminal Requests"),
    APPROVE_TERMINAL_REQUEST("Approve Terminal Request"),
    DISAPPROVE_TERMINAL_REQUEST("Disapprove Terminal Request"),
    DELETE_TERMINAL_REQUEST("Delete Terminal Request"),
    VIEW_TERMINAL("View Terminals"),

    VIEW_TRANSACTION("View Transactions"),

    DEBIT_CARD("Debit Payment Card"),

    AUTHORIZE_CARD_PAYMENT("Authorize Card Payment"),

    CREATE_ADMIN_USER("Create Administrator"),

    UPDATE_ADMIN_USER("Update Administrator"),

    VIEW_USER("View details of a registered user"),

    UPDATE_USER("Update details of a registered user"),

    GENERATE_OTP_FOR_USER("Generate OTP for registered users"),

    VALIDATE_OTP_FOR_USER("Validate OTP for registered users"),

    UPDATE_USER_STATUS("Update status of registered users"),

    RUN_SETTLEMENT("Run settlement");

    public final String value;

    private Permission(String value) {
        this.value = value;
    }

    public Permission valueOfLabel(String label) {
        for (Permission e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }

    public static String getValue(Permission p)
    {
        return p.value;
    }


}
