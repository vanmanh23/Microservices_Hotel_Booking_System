package com.manhnguyen.payment.controller;

import com.manhnguyen.payment.dto.PaymentDTO;
import com.manhnguyen.payment.dto.PaymentRequest;
import com.manhnguyen.payment.dto.RefundRequest;
import com.manhnguyen.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentDTO> processPayment(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.processPayment(request));
    }

    @PostMapping("/refund")
    public ResponseEntity<PaymentDTO> refund(@Valid @RequestBody RefundRequest request) {
        return ResponseEntity.ok(paymentService.refund(request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<PaymentDTO>> getHistory(@RequestParam Long userId) {
        return ResponseEntity.ok(paymentService.getPaymentHistory(userId));
    }
}
