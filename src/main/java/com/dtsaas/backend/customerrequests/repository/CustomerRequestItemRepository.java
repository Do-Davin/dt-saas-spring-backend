package com.dtsaas.backend.customerrequests.repository;

import com.dtsaas.backend.customerrequests.entity.CustomerRequestItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CustomerRequestItemRepository extends JpaRepository<CustomerRequestItem, UUID> {

    List<CustomerRequestItem> findAllByRequestIdOrderByCreatedAtAsc(UUID requestId);
}
