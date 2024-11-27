package com.example.events.emails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetails {
    private String recipient;
    private String messageBody;
    private String subject;
    private String attachment;
    private List<String> customerEmails;
}
