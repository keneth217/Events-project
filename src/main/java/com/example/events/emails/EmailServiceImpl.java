package com.example.events.emails;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Objects;

@Service
public class EmailServiceImpl implements EmailService{

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;


    @Override
    public void sendEmailToCustomer(EmailDetails emailDetails) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(senderEmail);//send to customer from sender
            mailMessage.setTo(emailDetails.getRecipient());
            mailMessage.setText(emailDetails.getMessageBody());
            mailMessage.setSubject(emailDetails.getSubject());

            javaMailSender.send(mailMessage);
            System.out.println("Mail sent successfully to customer of email:"+ emailDetails.getRecipient() );
        } catch (MailException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void receiveEmailFromCustomer(EmailDetails emailDetails) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            // Set the 'from' and 'to' as the senderEmail to send the email to itself
            mailMessage.setFrom(emailDetails.getRecipient());
            mailMessage.setTo(senderEmail);  // Sending to the sender's email from customer
            mailMessage.setText(emailDetails.getMessageBody());
            mailMessage.setSubject(emailDetails.getSubject());

            javaMailSender.send(mailMessage);
            System.out.println("Mail sent successfully to sender email from:"+ emailDetails.getRecipient());
        } catch (MailException e) {
            throw new RuntimeException("Error sending email to sender", e);
        }
    }


    @Override
    public void sendBulkEmailToCustomers(EmailDetails emailDetails) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(senderEmail); // Set the sender email
            mailMessage.setTo(emailDetails.getCustomerEmails().toArray(new String[0])); // Set multiple recipients
            mailMessage.setText(emailDetails.getMessageBody());
            mailMessage.setSubject(emailDetails.getSubject());

            javaMailSender.send(mailMessage);  // Send email to all recipients
            System.out.println("Mail sent successfully to customers: " + String.join(", ", emailDetails.getCustomerEmails()));
        } catch (MailException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendEmailAlertWithAttachment(EmailDetails emailDetails, File attachmentFile) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(senderEmail);
            mimeMessageHelper.setTo(emailDetails.getRecipient());
            mimeMessageHelper.setSubject(emailDetails.getSubject());
            mimeMessageHelper.setText(emailDetails.getMessageBody(), false);

            if (attachmentFile != null && attachmentFile.exists()) {
                FileSystemResource fileResource = new FileSystemResource(attachmentFile);
                mimeMessageHelper.addAttachment(fileResource.getFilename(), fileResource);
            }

            javaMailSender.send(mimeMessage);
            System.out.println("Email sent successfully to " + emailDetails.getRecipient());
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while sending email", e);
        }
    }




}