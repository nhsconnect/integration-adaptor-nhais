package uk.nhs.digital.nhsconnect.nhais.inbound.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.inbound.RecepConsumerService;
import uk.nhs.digital.nhsconnect.nhais.inbound.RegistrationConsumerService;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.InboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.utils.ConversationIdService;
import uk.nhs.digital.nhsconnect.nhais.utils.JmsHeaders;
import uk.nhs.digital.nhsconnect.nhais.utils.JmsReader;
import uk.nhs.digital.nhsconnect.nhais.utils.TimestampService;

import javax.jms.JMSException;
import javax.jms.Message;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InboundQueueService {

    private final RegistrationConsumerService registrationConsumerService;

    private final RecepConsumerService recepConsumerService;

    private final ObjectMapper objectMapper;

    private final TimestampService timestampService;

    private final JmsTemplate jmsTemplate;

    private final ConversationIdService conversationIdService;

    @Value("${nhais.amqp.meshInboundQueueName}")
    private String meshInboundQueueName;

    @JmsListener(destination = "${nhais.amqp.meshInboundQueueName}")
    public void receive(Message message) throws IOException, JMSException {
        try {
            setLoggingConversationId(message);
            String body = JmsReader.readMessage(message);
            LOGGER.debug("Received message body: {}", body);
            InboundMeshMessage meshMessage = objectMapper.readValue(body, InboundMeshMessage.class);
            LOGGER.info("Processing MeshMessageId={} with MeshWorkflowId={}",
                meshMessage.getMeshMessageId(), meshMessage.getWorkflowId());

            if (WorkflowId.REGISTRATION.equals(meshMessage.getWorkflowId())) {
                registrationConsumerService.handleRegistration(meshMessage);
            } else if (WorkflowId.RECEP.equals(meshMessage.getWorkflowId())) {
                recepConsumerService.handleRecep(meshMessage);
            } else {
                throw new UnknownWorkflowException(meshMessage.getWorkflowId());
            }

            message.acknowledge();
            LOGGER.info("Completed processing MeshMessageId={}", meshMessage.getMeshMessageId());
        } catch (Exception e) {
            LOGGER.error("Error while processing mesh inbound queue message", e);
            throw e; //message will be sent to DLQ after few unsuccessful redeliveries
        } finally {
            conversationIdService.resetConversationId();
        }
    }

    @SneakyThrows
    public void publish(InboundMeshMessage messageContent) {
        messageContent.setMessageSentTimestamp(timestampService.formatInISO(timestampService.getCurrentTimestamp()));
        jmsTemplate.send(meshInboundQueueName, session -> {
            var message = session.createTextMessage(serializeMeshMessage(messageContent));
            message.setStringProperty(JmsHeaders.CONVERSATION_ID, conversationIdService.getCurrentConversationId());
            return message;
        });
    }

    @SneakyThrows
    private String serializeMeshMessage(InboundMeshMessage meshMessage) {
        return objectMapper.writeValueAsString(meshMessage);
    }

    private void setLoggingConversationId(Message message) {
        try {
            conversationIdService.applyConversationId(message.getStringProperty(JmsHeaders.CONVERSATION_ID));
        } catch (JMSException e) {
            LOGGER.error("Unable to read header " + JmsHeaders.CONVERSATION_ID + " from message", e);
        }
    }
}
