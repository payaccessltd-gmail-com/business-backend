package com.jamub.payaccess.api.services;

import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.enums.PendingRequestModule;
import com.jamub.payaccess.api.enums.PendingRequestStatus;
import com.jamub.payaccess.api.enums.PendingRequestType;
import com.jamub.payaccess.api.models.PendingRequest;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.PortalUserDetail;
import com.jamub.payaccess.api.models.response.PendingRequestResponse;
import com.jamub.payaccess.api.repository.PendingRequestRepository;
import lombok.Builder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PendingRequestServiceTest {

    @Autowired
    private PendingRequestService pendingRequestService;

    @MockBean
    private PendingRequestRepository pendingRequestRepository;

    @BeforeEach
    void setUp() {
        PendingRequest pr = new PendingRequest();
        pr.setModule(PendingRequestModule.USER);
        Mockito.when(pendingRequestRepository.save(pr)).thenReturn(pr);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void saveRequest() {
       PendingRequestResponse expResp = new PendingRequestResponse(PayAccessStatusCode.SUCCESS.label,PayAccessStatusCode.SUCCESS.name());
        User user= new User("Phoenix");
        PortalUserDetail param = new PortalUserDetail().builder()
                        .requestType(PendingRequestType.CREATE_USER)
                        .requestDate(new Date())
                        .status(PendingRequestStatus.PENDING)
                        .description("create user")
                        .user(user)
                        .build();
        PendingRequestResponse response = pendingRequestService.saveRequest(user, param, PendingRequestModule.USER);
        assertEquals(expResp.getStatusCode(), response.getStatusCode());
    }
}