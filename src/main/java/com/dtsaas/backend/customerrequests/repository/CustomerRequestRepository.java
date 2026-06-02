package com.dtsaas.backend.customerrequests.repository;

import com.dtsaas.backend.customerrequests.entity.CustomerRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRequestRepository
        extends JpaRepository<CustomerRequest, UUID>, JpaSpecificationExecutor<CustomerRequest> {

    Optional<CustomerRequest> findByIdAndBusinessId(UUID id, UUID businessId);
}
