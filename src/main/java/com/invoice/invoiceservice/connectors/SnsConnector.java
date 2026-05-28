package com.invoice.invoiceservice.connectors;

import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
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
            log.error("SnsConnector.publishMessage - failed to publish message to topic {}", topicName, e);
        }
    }
}
