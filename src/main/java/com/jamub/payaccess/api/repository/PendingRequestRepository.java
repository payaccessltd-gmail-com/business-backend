package com.jamub.payaccess.api.repository;

import com.jamub.payaccess.api.models.PendingRequest;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.response.PendingRequestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PendingRequestRepository extends JpaRepository<PendingRequest, Long> {


}