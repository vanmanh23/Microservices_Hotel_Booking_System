package com.manhnguyen.payment.service;

import com.manhnguyen.common.exception.ApiException;
import com.manhnguyen.payment.client.BookingServiceClient;
import com.manhnguyen.payment.dto.PaymentRequest;
import com.manhnguyen.payment.event.PaymentEvent;
import com.manhnguyen.payment.model.Payment;
import com.manhnguyen.payment.model.PaymentStatus;
import com.manhnguyen.payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
//import org.springframework.util.concurrent.ListenableFuture;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingServiceClient bookingServiceClient;

    @Mock
    private KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void shouldProcessPayment_whenRequestValid() {
        PaymentRequest request = new PaymentRequest(10L, 1L, new BigDecimal("150.00"), null);
        SendResult<String, PaymentEvent> sendResult =
                mock(SendResult.class);

        CompletableFuture<SendResult<String, PaymentEvent>> future =
                CompletableFuture.completedFuture(sendResult);
        when(paymentRepository.findByBookingId(10L)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(kafkaTemplate.send(eq("payment-events"), any(String.class), any(PaymentEvent.class)))
                .thenReturn(future);
        doNothing().when(bookingServiceClient).confirmBooking(anyLong());

        var actual = paymentService.processPayment(request);

        assertThat(actual.bookingId()).isEqualTo(10L);
        assertThat(actual.status()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(actual.paymentMethod()).isEqualTo("CARD");
        assertThat(actual.transactionId()).startsWith("TXN-");
    }

    @Test
    void shouldThrowApiException_whenPaymentAlreadyExists() {
        Payment existing = Payment.builder().id(99L).bookingId(11L).build();
        when(paymentRepository.findByBookingId(11L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> paymentService.processPayment(new PaymentRequest(11L, 2L, new BigDecimal("50.00"), "CARD")))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Payment already exists");
    }

    @Test
    void shouldRefundPayment_whenAmountIsAvailable() {
        Payment payment = Payment.builder()
                .id(1L)
                .bookingId(20L)
                .userId(2L)
                .amount(new BigDecimal("100.00"))
                .refundAmount(BigDecimal.ZERO)
                .status(PaymentStatus.COMPLETED)
                .build();
        SendResult<String, PaymentEvent> sendResult =
                mock(SendResult.class);

        CompletableFuture<SendResult<String, PaymentEvent>> future =
                CompletableFuture.completedFuture(sendResult);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(kafkaTemplate.send(eq("payment-events"), any(String.class), any(PaymentEvent.class)))
                .thenReturn(future);

        var actual = paymentService.refund(new com.manhnguyen.payment.dto.RefundRequest(1L, new BigDecimal("40.00")));

        assertThat(actual.status()).isEqualTo(PaymentStatus.PARTIALLY_REFUNDED);
        assertThat(actual.refundAmount()).isEqualTo(new BigDecimal("40.00"));
    }

    @Test
    void shouldThrowApiException_whenRefundAmountExceedsRemainingBalance() {
        Payment payment = Payment.builder()
                .id(2L)
                .bookingId(21L)
                .userId(3L)
                .amount(new BigDecimal("100.00"))
                .refundAmount(new BigDecimal("90.00"))
                .status(PaymentStatus.COMPLETED)
                .build();
        when(paymentRepository.findById(2L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.refund(new com.manhnguyen.payment.dto.RefundRequest(2L, new BigDecimal("20.00"))))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Refund amount exceeds remaining balance");
    }

    @Test
    void shouldThrowApiException_whenPaymentNotFoundForRefund() {
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.refund(new com.manhnguyen.payment.dto.RefundRequest(999L, new BigDecimal("10.00"))))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Payment not found");
    }

    @Test
    void shouldThrowApiException_whenPaymentCannotBeRefunded() {
        Payment payment = Payment.builder()
                .id(3L)
                .bookingId(22L)
                .userId(4L)
                .amount(new BigDecimal("100.00"))
                .status(PaymentStatus.PENDING)
                .build();
        when(paymentRepository.findById(3L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.refund(new com.manhnguyen.payment.dto.RefundRequest(3L, new BigDecimal("10.00"))))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Payment cannot be refunded");
    }

    @Test
    void shouldReturnPaymentHistory_whenPaymentsExist() {
        Payment payment = Payment.builder().id(5L).userId(7L).build();
        when(paymentRepository.findByUserIdOrderByCreatedAtDesc(7L)).thenReturn(List.of(payment));

        var actual = paymentService.getPaymentHistory(7L);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).id()).isEqualTo(5L);
    }
}
