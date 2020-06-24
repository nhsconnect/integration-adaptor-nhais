package uk.nhs.digital.nhsconnect.nhais.repository;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Recep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.v2.TransactionV2;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.utils.OperationId;

import java.time.Instant;

@CompoundIndexes({
    @CompoundIndex(
        name = "unique_message",
        def = "{'receiveInterchangeSequence' : 1, 'receiveMessageSequence': 1, 'sender': 1, 'recipient': 1}",
        unique = true)
})
@Data
@Document
public class InboundState {
    @Id
    @Setter(AccessLevel.NONE)
    private String id;
    private WorkflowId workflowId;
    private String operationId;
    private Long receiveInterchangeSequence;
    private Long receiveMessageSequence;
    private String sender;
    private String recipient;
    private Long transactionNumber;
    private Instant translationTimestamp;
    private ReferenceTransactionType.TransactionType transactionType;

    public static InboundState fromTransaction(TransactionV2 transaction) {
        //TODO initial assumption that interchange can have a single message only

        var interchangeHeader = transaction.getMessage().getInterchange().getInterchangeHeader();
        var translationDateTime = transaction.getMessage().getTranslationDateTime();
        var messageHeader = transaction.getMessage().getMessageHeader();
        var referenceTransactionNumber = transaction.getReferenceTransactionNumber();
        var referenceTransactionType = transaction.getMessage().getReferenceTransactionType();

        var recipient = interchangeHeader.getRecipient();
        var transactionNumber = referenceTransactionNumber.getTransactionNumber();

        return new InboundState()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setOperationId(OperationId.buildOperationId(recipient, transactionNumber))
            .setSender(interchangeHeader.getSender())
            .setRecipient(recipient)
            .setReceiveInterchangeSequence(interchangeHeader.getSequenceNumber())
            .setReceiveMessageSequence(messageHeader.getSequenceNumber())
            .setTransactionNumber(transactionNumber)
            .setTransactionType(referenceTransactionType.getTransactionType())
            .setTranslationTimestamp(translationDateTime.getTimestamp());
    }

    public static InboundState fromRecep(Recep recep) {
        var interchangeHeader = recep.getInterchangeHeader();
        var messageHeader = recep.getMessageHeader();
        var dateTimePeriod = recep.getDateTimePeriod();

        return new InboundState()
            .setWorkflowId(WorkflowId.RECEP)
            .setReceiveInterchangeSequence(interchangeHeader.getSequenceNumber())
            .setReceiveMessageSequence(messageHeader.getSequenceNumber())
            .setSender(interchangeHeader.getSender())
            .setRecipient(interchangeHeader.getRecipient())
            .setTranslationTimestamp(dateTimePeriod.getTimestamp());
    }
}
