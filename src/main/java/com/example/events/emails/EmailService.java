package com.example.events.emails;


import java.io.File;

public interface EmailService {
    void sendEmailToCustomer(EmailDetails emailDetails);

    void sendEmailAlertWithAttachment(EmailDetails emailDetails, File attachmentFile);

    void sendBulkEmailToCustomers(EmailDetails emailDetails);

    void receiveEmailFromCustomer(EmailDetails emailDetails);
}

