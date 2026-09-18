package com.srinaka.common.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    SUCCESS("0000", "SUCCESS", "Success", HttpStatus.OK),

    VALIDATION_ERROR("ERR_VALIDATION", "Request validation failed", "Please check your input and try again.", HttpStatus.BAD_REQUEST),

    INVALID_CREDENTIALS("ERR_INVALID_CREDENTIALS", "Username or password is incorrect", "Username or password is incorrect.", HttpStatus.UNAUTHORIZED),
    ACCOUNT_DISABLED("ERR_ACCOUNT_DISABLED", "Account is disabled", "This account has been disabled.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID("ERR_REFRESH_TOKEN_INVALID", "Refresh token is invalid or revoked", "Your session has expired. Please login again.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("ERR_REFRESH_TOKEN_EXPIRED", "Refresh token has expired", "Your session has expired. Please login again.", HttpStatus.UNAUTHORIZED),
    OLD_PASSWORD_INCORRECT("ERR_OLD_PASSWORD_INCORRECT", "Old password is incorrect", "Old password is incorrect.", HttpStatus.BAD_REQUEST),
    ACCOUNT_TEMPORARILY_LOCKED("ERR_ACCOUNT_LOCKED", "Account temporarily locked due to too many failed login attempts", "Too many failed login attempts. Please try again in a few minutes.", HttpStatus.TOO_MANY_REQUESTS),

    USERNAME_DUPLICATE("ERR_USERNAME_DUPLICATE", "Username already exists", "This username is already taken.", HttpStatus.CONFLICT),
    PHONE_DUPLICATE("ERR_PHONE_DUPLICATE", "Phone number already in use", "This phone number is already in use.", HttpStatus.CONFLICT),
    MENU_KEY_DUPLICATE("ERR_MENU_KEY_DUPLICATE", "Menu key already exists", "This menu key is already in use.", HttpStatus.CONFLICT),

    USER_NOT_FOUND("ERR_USER_NOT_FOUND", "User not found", "The requested user was not found.", HttpStatus.NOT_FOUND),
    MENU_ITEM_NOT_FOUND("ERR_MENU_ITEM_NOT_FOUND", "Menu item not found", "The requested menu item was not found.", HttpStatus.NOT_FOUND),

    LINE_AUTH_FAILED("ERR_LINE_AUTH_FAILED", "LINE authentication failed", "Could not sign in with LINE. Please try again.", HttpStatus.UNAUTHORIZED),
    LINE_ACCOUNT_ALREADY_LINKED("ERR_LINE_ACCOUNT_ALREADY_LINKED", "This LINE account is already linked to another user", "This LINE account is already linked to another account.", HttpStatus.CONFLICT),

    SERVICE_CATEGORY_NOT_FOUND("ERR_SERVICE_CATEGORY_NOT_FOUND", "Service category not found", "The requested category was not found.", HttpStatus.NOT_FOUND),
    SERVICE_NOT_FOUND("ERR_SERVICE_NOT_FOUND", "Service or product not found", "The requested service or product was not found.", HttpStatus.NOT_FOUND),
    PROMOTION_NOT_FOUND("ERR_PROMOTION_NOT_FOUND", "Promotion not found", "The requested promotion was not found.", HttpStatus.NOT_FOUND),
    PROMOTION_NOT_APPLICABLE("ERR_PROMOTION_NOT_APPLICABLE", "Promotion is not applicable to this service", "This promotion cannot be applied to the selected service.", HttpStatus.BAD_REQUEST),

    BOOKING_NOT_FOUND("ERR_BOOKING_NOT_FOUND", "Booking not found", "The requested booking was not found.", HttpStatus.NOT_FOUND),
    BOOKING_NOT_OWNED("ERR_BOOKING_NOT_OWNED", "Booking does not belong to this user", "You do not have permission to access this booking.", HttpStatus.FORBIDDEN),
    CUSTOMER_NOT_VERIFIED("ERR_CUSTOMER_NOT_VERIFIED", "Customer must be verified before booking", "Please verify your account (link LINE) before booking.", HttpStatus.FORBIDDEN),
    INVALID_BOOKING_STATUS("ERR_INVALID_BOOKING_STATUS", "Booking is not in a valid status for this action", "This action cannot be performed on the booking in its current status.", HttpStatus.BAD_REQUEST),

    SHOP_INFO_NOT_FOUND("ERR_SHOP_INFO_NOT_FOUND", "Shop info not found", "Shop information has not been initialized.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String desc;
    private final String displayMessage;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String desc, String displayMessage, HttpStatus httpStatus) {
        this.code = code;
        this.desc = desc;
        this.displayMessage = displayMessage;
        this.httpStatus = httpStatus;
    }
}
