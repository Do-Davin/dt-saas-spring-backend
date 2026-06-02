package com.dtsaas.backend.customerrequests.dto;

import com.dtsaas.backend.customerrequests.entity.RequestStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateCustomerRequestStatusRequest(@NotNull RequestStatus status) {
}
