package uk.nhs.digital.nhsconnect.nhais.sequence;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SequenceService {
    private final static String TRANSACTION_KEY_FORMAT = "TN-%s";
    private final static String INTERCHANGE_FORMAT = "SIS-%s-%s";
    private final static String INTERCHANGE_MESSAGE_FORMAT = "SMS-%s-%s";

    @Autowired
    private SequenceRepository sequenceRepository;

    public Long generateTransactionNumber(String generalPractitioner) {
        validateSender(generalPractitioner);
        return sequenceRepository.getNextForTransaction(String.format(TRANSACTION_KEY_FORMAT, generalPractitioner));
    }

    public Long generateInterchangeSequence(String sender, String recipient) {
        validateSenderAndRecipient(sender, recipient);
        return getNextSequence(String.format(INTERCHANGE_FORMAT, sender, recipient));
    }

    public Long generateMessageSequence(String sender, String recipient) {
        validateSenderAndRecipient(sender, recipient);
        return getNextSequence(String.format(INTERCHANGE_MESSAGE_FORMAT, sender, recipient));
    }

    private void validateSenderAndRecipient(String sender, String recipient) {
        if (StringUtils.isBlank(sender) || StringUtils.isBlank(recipient)) {
            throw new SequenceException(
                    String.format("Sender or recipient not valid. Sender: %s, recipient: %s", sender, recipient)
            );
        }
    }

    private void validateSender(String sender) {
        if (sender == null) {
            throw new SequenceException("Sender cannot be null");
        }
        if (sender.isEmpty()) {
            throw new SequenceException("Sender cannot be empty");
        }
    }

    private Long getNextSequence(String key) {
        return sequenceRepository.getNext(key);
    }
}
