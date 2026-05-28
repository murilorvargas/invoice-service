package com.invoice.invoiceservice.connectors;

import io.awspring.cloud.sns.core.SnsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class SnsConnector {

    private static final Logger log = LoggerFactory.getLogger(SnsConnector.class);

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
