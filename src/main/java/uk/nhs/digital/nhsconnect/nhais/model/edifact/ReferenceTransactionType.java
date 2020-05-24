package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 *class declaration:
 */
@Getter @Setter
public class ReferenceTransactionType extends Reference {

    public ReferenceTransactionType(@NonNull TransactionType transactionType) {
        super("950", transactionType.getCode());
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        // no stateful properties to validate
    }

    public enum TransactionType {
        ACCEPTANCE("G1"),
        AMENDMENT("G2"),
        REMOVAL("G3"),
        DEDUCTION("G4");

        TransactionType(String code) {
            this.code = code;
        }

        private String code;

        public String getCode() {
            return this.code;
        }
    }

}
