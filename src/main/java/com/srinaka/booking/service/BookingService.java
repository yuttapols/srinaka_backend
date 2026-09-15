package com.srinaka.booking.service;

import com.srinaka.booking.domain.BookingStatus;
import com.srinaka.booking.dto.BookingCreateRequest;
import com.srinaka.booking.dto.BookingResponse;
import com.srinaka.booking.entity.Booking;
import com.srinaka.booking.repository.BookingRepository;
import com.srinaka.catalog.entity.SpaService;
import com.srinaka.catalog.repository.SpaServiceRepository;
import com.srinaka.common.domain.UserRole;
import com.srinaka.common.error.BusinessException;
import com.srinaka.common.error.ErrorCode;
import com.srinaka.promotion.domain.DiscountType;
import com.srinaka.promotion.entity.Promotion;
import com.srinaka.promotion.repository.PromotionRepository;
import com.srinaka.user.entity.User;
import com.srinaka.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SpaServiceRepository spaServiceRepository;
    private final PromotionRepository promotionRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookingResponse create(Long customerId, BookingCreateRequest request) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (customer.getRole() != UserRole.CUSTOMER || customer.getVerifiedAt() == null) {
            throw new BusinessException(ErrorCode.CUSTOMER_NOT_VERIFIED);
        }

        SpaService service = spaServiceRepository.findByIdAndActiveTrue(request.serviceId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SERVICE_NOT_FOUND));

        Promotion promotion = null;
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.promotionId() != null) {
            promotion = resolveApplicablePromotion(request.promotionId(), service.getId());
            discountAmount = calculateDiscount(service.getPrice(), promotion);
        }

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setService(service);
        booking.setPromotion(promotion);
        booking.setScheduledAt(request.scheduledAt());
        booking.setFinalPrice(service.getPrice().subtract(discountAmount));
        booking.setDiscountAmount(discountAmount);
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        booking.setNotes(request.notes());
        bookingRepository.save(booking);

        return toResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> listAll() {
        return bookingRepository.findAllByOrderByScheduledAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> listForEmployee(Long employeeId) {
        return bookingRepository.findByEmployee_IdOrderByScheduledAtDesc(employeeId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> listForCustomer(Long customerId) {
        return bookingRepository.findByCustomer_IdOrderByScheduledAtDesc(customerId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public BookingResponse assignEmployee(Long bookingId, Long employeeId) {
        Booking booking = getOrThrow(bookingId);

        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (employee.getRole() != UserRole.EMPLOYEE) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        booking.setEmployee(employee);
        return toResponse(booking);
    }

    @Transactional
    public BookingResponse markCompleted(Long bookingId, Long employeeId) {
        Booking booking = getOrThrow(bookingId);

        if (booking.getEmployee() == null || !booking.getEmployee().getId().equals(employeeId)) {
            throw new BusinessException(ErrorCode.BOOKING_NOT_OWNED);
        }
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BusinessException(ErrorCode.INVALID_BOOKING_STATUS);
        }

        booking.setStatus(BookingStatus.COMPLETED);
        return toResponse(booking);
    }

    private Promotion resolveApplicablePromotion(Long promotionId, Long serviceId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROMOTION_NOT_FOUND));

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Bangkok"));
        boolean applicable = promotion.isActive()
                && !today.isBefore(promotion.getStartDate())
                && !today.isAfter(promotion.getEndDate())
                && promotionRepository.existsByIdAndServices_Id(promotionId, serviceId);
        if (!applicable) {
            throw new BusinessException(ErrorCode.PROMOTION_NOT_APPLICABLE);
        }
        return promotion;
    }

    private BigDecimal calculateDiscount(BigDecimal price, Promotion promotion) {
        BigDecimal discount = promotion.getDiscountType() == DiscountType.PERCENT
                ? price.multiply(promotion.getDiscountValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                : promotion.getDiscountValue();
        return discount.min(price);
    }

    private Booking getOrThrow(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));
    }

    private BookingResponse toResponse(Booking booking) {
        User customer = booking.getCustomer();
        User employee = booking.getEmployee();
        return new BookingResponse(
                booking.getId(),
                customer != null ? customer.getId() : null,
                customer != null ? customer.getFullName() : null,
                booking.getWalkInCustomerName(),
                booking.getService().getId(),
                booking.getService().getName(),
                employee != null ? employee.getId() : null,
                employee != null ? employee.getFullName() : null,
                booking.getPromotion() != null ? booking.getPromotion().getId() : null,
                booking.getChannel(),
                booking.getScheduledAt(),
                booking.getFinalPrice(),
                booking.getDiscountAmount(),
                booking.getStatus(),
                booking.getNotes(),
                booking.getCreatedAt());
    }
}
