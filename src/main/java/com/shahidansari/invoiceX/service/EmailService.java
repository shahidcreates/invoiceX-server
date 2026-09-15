package com.shahidansari.invoiceX.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

//    private final JavaMailSender mailSender;
//
//    @Value("${spring.mail.properties.mail.smtp.from}")
//    private String mailFrom;
//
//    public void sendInvoiceMail(String toEmail, MultipartFile file) throws MessagingException, IOException{
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message,true);
//
//        helper.setFrom(mailFrom);
//        helper.setTo(toEmail);
//        helper.setSubject("Your Invoice");
//        helper.setText("Dear Customor, \n\nPlease find attached your invoice.\n\nThank You.");
//        String fileName = "invoice_"+System.currentTimeMillis()+".pdf";
//        helper.addAttachment(fileName,new ByteArrayResource(file.getBytes()));
//        mailSender.send(message);
//    }

    private final RestTemplate restTemplate;

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    public void sendInvoiceMail(String toEmail, MultipartFile file)
            throws IOException {

        String url = "https://api.brevo.com/v3/smtp/email";

        // PDF ko Base64 me convert
        String base64File = Base64.getEncoder()
                .encodeToString(file.getBytes());

        // Sender
        Map<String, String> sender = new HashMap<>();
        sender.put("name", senderName);
        sender.put("email", senderEmail);

        // Receiver
        Map<String, String> receiver = new HashMap<>();
        receiver.put("email", toEmail);

        // Attachment
        Map<String, String> attachment = new HashMap<>();
        attachment.put("name", "invoice_" + System.currentTimeMillis() + ".pdf");
        attachment.put("content", base64File);

        // Request body
        Map<String, Object> body = new HashMap<>();

        body.put("sender", sender);
        body.put("to", new Map[]{receiver});
        body.put("subject", "Your Invoice");
        body.put(
                "textContent",
                "Dear Customer,\n\nPlease find attached your invoice.\n\nThank You."
        );
        body.put("attachment", new Map[]{attachment});

        // Headers
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        // API call
        ResponseEntity<String> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        request,
                        String.class
                );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException(
                    "Email sending failed: " + response.getBody()
            );
        }
    }

    public void sendHtmlEmail(
            String to,
            String subject,
            String htmlContent) {

        String url = "https://api.brevo.com/v3/smtp/email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        Map<String, Object> requestBody = Map.of(
                "sender", Map.of(
                        "name", "InvoiceX",
                        "email", senderEmail
                ),
                "to", List.of(
                        Map.of("email", to)
                ),
                "subject", subject,
                "htmlContent", htmlContent
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        request,
                        String.class
                );

        log.info("Brevo Response: {}", response.getBody());
    }
}