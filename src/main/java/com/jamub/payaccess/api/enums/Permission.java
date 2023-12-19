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
    CREATE_CONTACT_US_MESSAGE("Create Contact Us Messages"),
    CREATE_FEEDBACK_MESSAGE("Create Feedback Messages"),

    CREATE_TICKET("Create Transaction Ticket"),

    VIEW_TICKETS("View Transaction Tickets");

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
