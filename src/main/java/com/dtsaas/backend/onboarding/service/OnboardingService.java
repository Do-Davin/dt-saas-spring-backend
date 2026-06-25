package com.dtsaas.backend.onboarding.service;

import com.dtsaas.backend.auth.dto.AuthOwnerResponse;
import com.dtsaas.backend.auth.entity.Owner;
import com.dtsaas.backend.auth.repository.OwnerRepository;
import com.dtsaas.backend.businesses.entity.Business;
import com.dtsaas.backend.businesses.entity.BusinessType;
import com.dtsaas.backend.businesses.entity.CatalogMode;
import com.dtsaas.backend.businesses.repository.BusinessRepository;
import com.dtsaas.backend.categories.entity.Category;
import com.dtsaas.backend.categories.repository.CategoryRepository;
import com.dtsaas.backend.common.exception.ApiException;
import com.dtsaas.backend.common.security.JwtService;
import com.dtsaas.backend.common.security.PasswordService;
import com.dtsaas.backend.onboarding.dto.OnboardingRequest;
import com.dtsaas.backend.onboarding.dto.OnboardingRequest.CategorySeedItem;
import com.dtsaas.backend.onboarding.dto.OnboardingResponse;
import com.dtsaas.backend.products.entity.PricingType;
import com.dtsaas.backend.products.entity.Product;
import com.dtsaas.backend.products.entity.UnitOfMeasure;
import com.dtsaas.backend.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final OwnerRepository ownerRepository;
    private final BusinessRepository businessRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    @Transactional
    public OnboardingResponse register(OnboardingRequest request) {
        // 1. Reject duplicate username
        if (ownerRepository.existsByUsername(request.username())) {
            throw ApiException.conflict("Username already taken");
        }

        // 2. Create owner — email is a derived placeholder; username is the login identity
        String placeholderEmail = request.username() + "@users.dtsaas.local";
        Owner owner = new Owner(
                placeholderEmail,
                request.username(),
                passwordService.hash(request.password()),
                request.fullName());
        owner = ownerRepository.save(owner);

        // 3. Build subscription data
        Instant now = Instant.now();
        double basePrice = baseMonthlyPrice(request.plan());
        double addonPrice = request.extraItemPacks() * 9.0 + request.extraUserPacks() * 15.0;
        double totalMonthly = basePrice + addonPrice;
        int itemLimit = 50 + request.extraItemPacks() * 100;
        int userLimit = 1 + request.extraUserPacks() * 5;

        // 4. Generate unique slug
        String slug = toSlug(request.businessName());

        // 5. Create business
        BusinessType businessType = mapBusinessType(request.businessType());
        Business business = new Business(
                owner,
                request.businessName(),
                nullIfBlank(request.businessNameKm()),
                slug,
                businessType,
                CatalogMode.MENU);
        business.setSubscriptionStatus("ACTIVE");
        business.setSubscriptionStartDate(now);
        business.setSubscriptionExpiresAt(now.plus(365, ChronoUnit.DAYS));
        business.setSubscriptionPlan(normalizePlan(request.plan()));
        business.setUserLimit(userLimit);
        business.setMenuItemLimit(itemLimit);
        if (totalMonthly > 0) {
            business.setMonthlyPrice(BigDecimal.valueOf(totalMonthly));
        }
        business = businessRepository.save(business);

        // 6. Seed categories and products
        List<CategorySeedItem> categories =
                request.categories() != null ? request.categories() : List.of();

        for (int i = 0; i < categories.size(); i++) {
            CategorySeedItem item = categories.get(i);
            if (item.name() == null || item.name().isBlank()) continue;

            Category category = new Category(business, null, item.name(), null, i, true);
            category = categoryRepository.save(category);

            List<String> products = item.products() != null ? item.products() : List.of();
            for (String productName : products) {
                if (productName == null || productName.isBlank()) continue;
                Product product = new Product(
                        business, null, category,
                        productName, null, null, null,
                        null, BigDecimal.valueOf(5.00), null,
                        PricingType.FIXED, null, UnitOfMeasure.UNIT,
                        true, true);
                productRepository.save(product);
            }
        }

        // 7. Generate JWT and return
        String token = jwtService.generate(
                owner.getId(), owner.getEmail(), owner.getUsername(), owner.getName(), owner.getRole());

        return new OnboardingResponse(
                token,
                AuthOwnerResponse.from(owner),
                business.getId(),
                business.getSlug());
    }

    private String toSlug(String businessName) {
        String base = businessName.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        if (base.isEmpty()) base = "business";
        if (base.length() > 60) base = base.substring(0, 60).replaceAll("-+$", "");
        if (!businessRepository.existsBySlug(base)) return base;
        for (int i = 2; i <= 999; i++) {
            String candidate = base + "-" + i;
            if (!businessRepository.existsBySlug(candidate)) return candidate;
        }
        return base + "-" + System.currentTimeMillis();
    }

    private double baseMonthlyPrice(String plan) {
        if (plan == null) return 24.99;
        return switch (plan.toUpperCase()) {
            case "MONTHLY" -> 24.99;
            case "ANNUALLY" -> 19.99;
            default -> 0.0; // CUSTOM
        };
    }

    private String normalizePlan(String plan) {
        if (plan == null) return "MONTHLY";
        return switch (plan.toUpperCase()) {
            case "ANNUALLY" -> "ANNUALLY";
            case "CUSTOM"   -> "CUSTOM";
            default         -> "MONTHLY";
        };
    }

    private BusinessType mapBusinessType(String input) {
        if (input == null) return BusinessType.RESTAURANT;
        return switch (input.toUpperCase()) {
            case "CAFE_SHOP", "COFFEE_SHOP" -> BusinessType.COFFEE_SHOP;
            case "CLOTHING_STORE"           -> BusinessType.RETAIL_STORE;
            case "RESTAURANT"               -> BusinessType.RESTAURANT;
            case "BAKERY"                   -> BusinessType.BAKERY;
            case "BUFFET"                   -> BusinessType.BUFFET;
            case "CAR_WASH"                 -> BusinessType.CAR_WASH;
            case "GARAGE"                   -> BusinessType.GARAGE;
            case "ONLINE_SELLER"            -> BusinessType.ONLINE_SELLER;
            case "RETAIL_STORE"             -> BusinessType.RETAIL_STORE;
            case "SERVICE_BUSINESS"         -> BusinessType.SERVICE_BUSINESS;
            default                         -> BusinessType.RESTAURANT;
        };
    }

    private static String nullIfBlank(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
