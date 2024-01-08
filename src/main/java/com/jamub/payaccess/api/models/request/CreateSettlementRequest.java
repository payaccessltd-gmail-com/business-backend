package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.PayAccessCurrency;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class CreateSettlementRequest extends BaseRequest{

    private Long createdByUserId;
    private BigDecimal settlementAmount;
    private LocalDate settlementDate;
    private PayAccessCurrency payAccessCurrency;
}
