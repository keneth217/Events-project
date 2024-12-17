package com.example.events.registration;

import com.example.events.event.EventResponse;
import com.example.events.event.MyEvent;
import com.example.events.user.MyRegisteredEventsResponse;
import com.example.events.user.User;
import com.example.events.user.UserResponse;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.IOException;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Component
@RequiredArgsConstructor
public class RegisterMapper {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    // Utility method to get the logged-in user's details (shopId, shopCode, username) from token
    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal(); // Assuming your `User` implements `UserDetails`
    }

    ///get  details of the logged-in user
    public User getLoggedInUserDetails() {
        return getLoggedInUser(); // Directly return the logged-in user
    }
    public RegisterResponse toRegister(EventRegistration registerEvent) {
        if (registerEvent == null) {
            throw new IllegalArgumentException("EventRegistration cannot be null");
        }

        return RegisterResponse.builder()
                .id(registerEvent.getRegistrationId())
                .users(UserResponse.builder()
                        .userId(registerEvent.getUser().getId())
                        .firstName(registerEvent.getUser().getFirstName())
                        .lastName(registerEvent.getUser().getLastName())
                        .email(registerEvent.getUser().getEmail())
                        .build())
                .events(EventResponse.builder()
                        .eventId(registerEvent.getEvent().getId())
                        .eventName(registerEvent.getEvent().getEventName())
                        .description(registerEvent.getEvent().getDescription())
                        .location(registerEvent.getEvent().getLocation())
                        .startDate(registerEvent.getEvent().getStartDate())
                        .endDate(registerEvent.getEvent().getEndDate())
                        .startTime(registerEvent.getEvent().getStartTime())
                        .endTime(registerEvent.getEvent().getEndTime())
                        .build())
                .regDate(registerEvent.getRegDate())
                .regTime(registerEvent.getRegTime())
                .status(registerEvent.getStatus())
                .build();
    }
    // Mapping method
    public UserResponse mapToUserResponse(EventRegistration registerEvent) {
        return UserResponse.builder()
                .userId(registerEvent.getUser().getId())
                .firstName(registerEvent.getUser().getFirstName())
                .lastName(registerEvent.getUser().getLastName())
                .email(registerEvent.getUser().getEmail())
                .regDate(registerEvent.getRegDate())
                .regTime(registerEvent.getRegTime())
                .build();
    }

    public UserResponse mapToEventResponse(EventRegistration registerEvent) {
        return UserResponse.builder()
                .userId(registerEvent.getUser().getId())
                .firstName(registerEvent.getUser().getFirstName())
                .lastName(registerEvent.getUser().getLastName())
                .email(registerEvent.getUser().getEmail())
                .regDate(registerEvent.getRegDate())
                .regTime(registerEvent.getRegTime())
                .build();
    }

    public TicketResponse mapToTicketResponse(EventRegistration registerEvent) {
        return TicketResponse.builder()
                .eventName(registerEvent.getEvent().getEventName())
                .userName(registerEvent.getUser().getFirstName())
                .paidAmount(registerEvent.getPaidAmount())
                .scanned(registerEvent.isScanned())
                .ticketQuantity(registerEvent.getTicketQuantity())
                .regDate(registerEvent.getRegDate())
                .transactionId(registerEvent.getTransactionId())
                .remainingAmount(registerEvent.getRemainingAmount())
                .eventCost(registerEvent.getEventCost())
                .regTime(registerEvent.getRegTime())
                .userId(registerEvent.getUser().getId())
                .scannedDate(registerEvent.getScannedDate())
                .scannedTime(registerEvent.getScannedTime())
                .ticketId(registerEvent.getRegistrationId())
                .build();
    }


    // Convert a list of EventRegistration to a list of EventResponse
    public List<EventResponse> toEventResponses(List<EventRegistration> eventRegistrations) {
        return eventRegistrations.stream()
                .map(this::toEventResponse)
                .collect(Collectors.toList());
    }

    // Convert a single EventRegistration to EventResponse
    public EventResponse toEventResponse(EventRegistration eventRegistration) {
        MyEvent event = eventRegistration.getEvent();
        return EventResponse.builder()
                .eventId(event.getId())
                .eventName(event.getEventName())
                .startDate(event.getStartDate())
                .location(event.getLocation())
                .startTime(event.getStartTime())
                .status(event.getStatus())
                .categoryId(event.getCategory().getId())
                .endTime(event.getEndTime())
                .description(event.getDescription())
                .build();
    }


    // New Method for Building MyRegisteredEventsResponse
    public  MyRegisteredEventsResponse toMyRegisteredEventsResponse(List<EventRegistration> eventRegistrations) {
        List<EventResponse> eventResponses = toEventResponses(eventRegistrations);
        return MyRegisteredEventsResponse.builder()
                .myEvents(eventResponses) // Include the list of event responses
                .build();
    }

    public String generateQRCodeAsBase64(String text, int width, int height) {
        try {
            // Generate QR code matrix
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

            // Convert BitMatrix to Base64 encoded image
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            return Base64.encodeBase64String(imageBytes);
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Error generating QR Code", e);
        }
    }
    // Fix: Send email to the correct recipient (user who registered, not the logged-in user)
    public void sendEmailAlertWithAttachmentQRCode(String recipient, MyEvent event, String qrCodeBase64) {
        String recipientEmail = getLoggedInUserDetails().getEmail();
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = getMimeMessageHelper(event, mimeMessage, recipientEmail);

            // Convert Base64 QR code to a byte array
            byte[] qrCodeBytes = Base64.decodeBase64(qrCodeBase64);

            // Create a file for the attachment
            File qrCodeFile = File.createTempFile("qrCode", ".png");
            java.nio.file.Files.write(qrCodeFile.toPath(), qrCodeBytes);

            // Attach the QR code image
            helper.addAttachment("QR_Code_" + event.getEventName() + ".png", qrCodeFile);

            // Send the email
            javaMailSender.send(mimeMessage);

            // Clean up the temporary file
            qrCodeFile.delete();
        } catch (MessagingException | IOException e) {
            throw new RuntimeException("Failed to send email with QR Code attachment", e);
        }
    }

    private MimeMessageHelper getMimeMessageHelper(MyEvent event, MimeMessage mimeMessage, String recipientEmail) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setFrom(senderEmail);
        helper.setTo(recipientEmail);
        helper.setSubject("Your Event QR Code");

        String htmlContent = "<html><body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<div style='background-color: #f4f4f9; padding: 20px; border-radius: 8px;'>" +
                "<p style='font-size: 18px; color: #2c3e50;'>Dear <strong>" + getLoggedInUser().getFirstName() + "</strong>,</p>" +
                "<p style='font-size: 16px;'>Thank you for registering for the event: <strong>" + event.getEventName() + "</strong>.</p>" +
                "<p style='font-size: 16px;'>Attached is your unique QR code for event entry. Please keep it safe!</p>" +
                "<div style='margin-top: 20px;'>" +
                "<p style='font-size: 14px; color: #7f8c8d;'>If you have any questions, feel free to reach out to us.</p>" +
                "<p style='font-size: 14px; color: #7f8c8d;'>Thank you for your participation!</p>" +
                "</div>" +
                "<div style='margin-top: 30px; text-align: center;'>" +
                "<p style='font-size: 12px; color: #bdc3c7;'>Best regards, <br>The Event Team</p>" +
                "</div>" +
                "</div>" +
                "</body></html>";

        helper.setText(htmlContent, true); // true indicates HTML content
        return helper;
    }

}
