package com.jamub.payaccess.api.models.request;


import com.jamub.payaccess.api.enums.TransactionStatus;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Getter
@Setter
public class TransactionFilterRequest {

//    @NotNull(message = "Incomplete request parameters. Transaction status not provided")
    private String transactionStatus;

//    @NotNull(message = "Incomplete request parameters. Merchant identification not provided")
    private String merchantCode;

//    @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$", flags = Pattern.Flag.UNICODE_CASE, message = "Invalid format of the start date. Must be represented in the format yyyy-MM-dd")
    private String startDate;

//    @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$", flags = Pattern.Flag.UNICODE_CASE, message = "Invalid format of the end date. Must be represented in the format yyyy-MM-dd")
    private String endDate;

//    @NotNull(message = "Incomplete request parameters. Order reference key not provided")
    private String orderRef;

//    @NotNull(message = "Incomplete request parameters. Switch Transaction Ref not provided")
    private String switchTransactionRef;

//    @NotNull(message = "Incomplete request parameters. Terminal code key not provided")
    private String terminalCode;

//    @NotNull(message = "Incomplete request parameters. Minimum amount key not provided")
//    @DecimalMin(value = "0.10", message = "Please Enter a valid Amount. Minimum amount acceptable is 0.10")
    private Double minAmount;

//    @NotNull(message = "Incomplete request parameters. Maximum amount key not provided")
//    @DecimalMin(value = "0.10", message = "Please Enter a valid maximum Amount.")
    private Double maxAmount;
}
