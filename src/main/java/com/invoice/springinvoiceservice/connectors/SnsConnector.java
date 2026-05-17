package com.invoice.springinvoiceservice.connectors;

import io.awspring.cloud.sns.core.SnsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class SnsConnector {

    private final SnsTemplate snsTemplate;

    private final ObjectMapper objectMapper;

    @Autowired
    public SnsConnector(SnsTemplate snsTemplate, ObjectMapper objectMapper) {
        this.snsTemplate = snsTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishMessage(String topicName, Object message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            snsTemplate.sendNotification(topicName, messageJson);
        } catch (Exception e) {
        }
    }
}
