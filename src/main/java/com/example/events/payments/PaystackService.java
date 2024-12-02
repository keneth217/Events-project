package com.example.events.payments;

import com.example.events.exceptions.EventNotFoundException;
import com.example.events.exceptions.ExcessivePaymentException;
import com.example.events.exceptions.PayStackException;
import com.example.events.registration.EventRegistration;
import com.example.events.registration.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaystackService {

    private static final Logger logger = LoggerFactory.getLogger(PaystackService.class);

    @Value("${spring.paystack.secret.key}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final PaymentRepository paymentRepository;
    private final RegistrationRepository registrationRepository;

    // Initialize Transaction
    public Map<String, Object> initializeTransaction(PaymentRequest request, UUID ticketId) throws PayStackException {
        // Fetch the event registration details
        EventRegistration registration = registrationRepository.findById(ticketId)
                .orElseThrow(() -> new PayStackException("Ticket not found for the payment you want to make"));
        System.out.println("TRANSACTION ID:::::::::::::::::::::::::::::::::::::"+ticketId);
        // Paystack API URL and callback
        String url = "https://api.paystack.co/transaction/initialize";
        String callBackUrl = "https://payment-sm-front.vercel.app/payment/callback";

        // Prepare the payload for the Paystack request
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", request.getEmail());
        payload.put("amount", request.getAmount().multiply(new BigDecimal(100))); // Paystack expects the amount in kobo (1 = 100 kobo)
        payload.put("callback_url", callBackUrl);

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + secretKey);
        headers.add("Content-Type", "application/json");

        // Create HttpEntity with the payload and headers
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        // Call the Paystack API
        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null && responseBody.containsKey("data")) {
                    Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                    String reference = (String) data.get("reference");

                    logger.info("Transaction initialized successfully with reference: {}", reference);

                    // Save the payment information to the database
                    Payment payment = new Payment();
                    payment.setReference(reference);
                    payment.setTicket(registration);
                    payment.setEmail(request.getEmail());
                    payment.setAmount(request.getAmount());
                    payment.setStatus("pending"); // Set status as pending initially
                    paymentRepository.save(payment);

                    // Update registration with transaction details
                    registration.setTransactionId(reference);
                    registration.setPaymentStatus(PaymentStatus.NOT_PAID);
                    registration.setPaidAmount(registration.getPaidAmount());

                    // Save updated registration
                    registrationRepository.save(registration);

                    return responseBody;
                } else {
                    logger.error("Error: Response data missing from Paystack initialization.");
                    throw new PayStackException("Error: Response data missing from Paystack initialization.");
                }
            } else {
                logger.error("Error initializing transaction: Paystack API responded with error.");
                throw new PayStackException("Error initializing transaction");
            }
        } catch (Exception e) {
            logger.error("Error initializing Paystack transaction: {}", e.getMessage(), e);
            throw new PayStackException("Error initializing Paystack transaction");
        }
    }

    // Verify Transaction
    public Map<String, Object> verifyTransaction(String reference) throws PayStackException {
        String url = "https://api.paystack.co/transaction/verify/" + reference;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + secretKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null) {
                    logger.info("Transaction verification successful for reference: {}", reference);
                    return responseBody;
                } else {
                    logger.error("Error: Empty response body from Paystack on verification.");
                    throw new PayStackException("Error: Empty response body from Paystack on verification.");
                }
            } else {
                logger.error("Error verifying transaction: Paystack API responded with error.");
                throw new PayStackException("Error verifying transaction");
            }
        } catch (Exception e) {
            logger.error("Error verifying Paystack transaction for reference {}: {}", reference, e.getMessage(), e);
            throw new PayStackException("Error verifying Paystack transaction");
        }
    }

    // Update Payment Status
    @Transactional
    public void updatePayment(String reference, String status, String paidAt,
                              String channel, String createdAt, String currency,
                              String ipAddress) throws PayStackException {
        try {
            // Fetch payment and registration using Optional to avoid null checks
            Payment payment = Optional.ofNullable(paymentRepository.findByReference(reference))
                    .orElseThrow(() -> new PayStackException("Payment not found for reference: " + reference));

            EventRegistration registration = Optional.ofNullable(registrationRepository.findByTransactionId(reference))
                    .orElseThrow(() -> new PayStackException("Registration not found for transaction: " + reference));

            // Update payment details
            payment.setStatus(status);
            payment.setPaidAt(paidAt);
            payment.setCreatedAt(createdAt);
            payment.setCurrency(currency);
            payment.setChannel(channel);
            payment.setIpAddress(ipAddress);
            payment.setTicket(registration);

            // Save updated payment
            paymentRepository.save(payment);

            // If the payment status is successful, update the total paid amount
            if ("success".equalsIgnoreCase(status)) {
                BigDecimal totalPaidAmount = paymentRepository.findByTicket_RegistrationIdAndStatus(registration.getRegistrationId(), "success")
                        .stream()
                        .map(Payment::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Ensure that the paid amount does not exceed the event cost
                if (totalPaidAmount.compareTo(registration.getEventCost()) > 0) {
                    throw new ExcessivePaymentException("Paid amount cannot exceed the event cost.");
                }

                // Update registration with the new total and status
                registration.setPaidAmount(totalPaidAmount);

                if (totalPaidAmount.compareTo(registration.getEventCost()) == 0) {
                    registration.setPaymentStatus(PaymentStatus.FULLY_PAID);
                } else if (totalPaidAmount.compareTo(registration.getEventCost()) < 0) {
                    registration.setPaymentStatus(PaymentStatus.PARTIALLY_PAID);
                }
            }

            // Save updated registration
            registrationRepository.save(registration);

            // Log success
            logger.info("Payment status updated successfully for reference: {}", reference);
        } catch (PayStackException e) {
            logger.error("Error updating payment status for reference {}: {}", reference, e.getMessage(), e);
            throw e;  // Rethrow custom exception
        } catch (Exception e) {
            logger.error("Error updating payment status for reference {}: {}", reference, e.getMessage(), e);
            throw new PayStackException("Error updating payment status for reference " + reference);
        }
    }

}
