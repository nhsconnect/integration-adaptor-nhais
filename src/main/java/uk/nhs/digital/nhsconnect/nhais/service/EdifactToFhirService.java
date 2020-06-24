package uk.nhs.digital.nhsconnect.nhais.service;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.v2.TransactionV2;
import uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir.GpTradingPartnerCode;
import uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir.NotSupportedTransactionMapper;
import uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir.PatientParameter;
import uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir.TransactionMapper;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EdifactToFhirService {

    public final Map<ReferenceTransactionType.TransactionType, TransactionMapper> transactionMappers;

    public Parameters convertToFhir(TransactionV2 transaction) {
        var parameters = new Parameters()
            .addParameter(new GpTradingPartnerCode(transaction.getMessage().getInterchange()))
            .addParameter(new PatientParameter(transaction));

        var transactionType = transaction.getMessage().getReferenceTransactionType().getTransactionType();
        transactionMappers
            .getOrDefault(transactionType, new NotSupportedTransactionMapper(transactionType))
            .map(parameters, transaction);

        return parameters;
    }
}
