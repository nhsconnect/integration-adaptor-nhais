package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DeductionDate;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DeductionReasonCode;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.NewHealthAuthorityName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.NhsIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class DeductionTransactionMapperTest {

    private static final String NHS_NUMBER = "1234567890";
    private static final String DEDUCTION_REASON_CODE = "1";
    private static final String DATE = "2015-01-15";
    private static final LocalDate DEDUCTION_DATE = LocalDate.parse(DATE);
    private static final String NEW_HA_CIPHER = "COV";
    @Mock
    private Transaction transaction;
    @Mock
    private PersonName personName;
    @Mock
    private DeductionReasonCode deductionReasonCode;
    @Mock
    private DeductionDate deductionDate;
    @Mock
    private NewHealthAuthorityName newHealthAuthorityName;

    @Test
    void when_allFieldsInTransaction_Then_mapAllFields(SoftAssertions softly) {
        when(transaction.getPersonName()).thenReturn(Optional.of(personName));
        when(transaction.getDeductionReasonCode()).thenReturn(Optional.of(deductionReasonCode));
        when(transaction.getDeductionDate()).thenReturn(Optional.of(deductionDate));
        when(transaction.getNewHealthAuthorityName()).thenReturn(Optional.of(newHealthAuthorityName));

        when(personName.getNhsNumber()).thenReturn(NHS_NUMBER);
        when(deductionReasonCode.getCode()).thenReturn(DEDUCTION_REASON_CODE);
        when(deductionDate.getDate()).thenReturn(DEDUCTION_DATE);
        when(newHealthAuthorityName.getHaName()).thenReturn(NEW_HA_CIPHER);

        var parameters = new Parameters()
            .addParameter(new PatientParameter(new Patient()));
        new DeductionTransactionMapper().map(parameters, transaction);

        ParametersExtension parametersExt = new ParametersExtension(parameters);

        softly.assertThat(parametersExt.size()).isEqualTo(4);
        Patient patient = parametersExt.extractPatient();
        softly.assertThat(patient.getIdentifierFirstRep().getValue()).isEqualTo(NHS_NUMBER);
        softly.assertThat(patient.getIdentifierFirstRep().getSystem()).isEqualTo(NhsIdentifier.SYSTEM);
        softly.assertThat(parametersExt.extractValue(ParameterNames.DEDUCTION_REASON_CODE)).isEqualTo(DEDUCTION_REASON_CODE);
        softly.assertThat(parametersExt.extractValue(ParameterNames.DATE_OF_DEDUCTION)).isEqualTo(DATE);
        softly.assertThat(parametersExt.extractValue(ParameterNames.NEW_HA_CIPHER)).isEqualTo(NEW_HA_CIPHER);
    }

    @Test
    void when_allMandatoryFieldsInTransaction_Then_mapOnlyMandatoryFields(SoftAssertions softly) {
        when(transaction.getPersonName()).thenReturn(Optional.of(personName));
        when(transaction.getDeductionReasonCode()).thenReturn(Optional.of(deductionReasonCode));
        when(transaction.getDeductionDate()).thenReturn(Optional.of(deductionDate));
        when(transaction.getNewHealthAuthorityName()).thenReturn(Optional.empty());

        when(personName.getNhsNumber()).thenReturn(NHS_NUMBER);
        when(deductionReasonCode.getCode()).thenReturn(DEDUCTION_REASON_CODE);
        when(deductionDate.getDate()).thenReturn(DEDUCTION_DATE);

        var parameters = new Parameters()
            .addParameter(new PatientParameter(new Patient()));
        new DeductionTransactionMapper().map(parameters, transaction);

        ParametersExtension parametersExt = new ParametersExtension(parameters);

        softly.assertThat(parametersExt.size()).isEqualTo(3);
        Patient patient = parametersExt.extractPatient();
        softly.assertThat(patient.getIdentifierFirstRep().getValue()).isEqualTo(NHS_NUMBER);
        softly.assertThat(patient.getIdentifierFirstRep().getSystem()).isEqualTo(NhsIdentifier.SYSTEM);
        softly.assertThat(parametersExt.extractValue(ParameterNames.DEDUCTION_REASON_CODE)).isEqualTo(DEDUCTION_REASON_CODE);
        softly.assertThat(parametersExt.extractValue(ParameterNames.DATE_OF_DEDUCTION)).isEqualTo(DATE);
    }

    @Test
    void when_NhsNumberIsMissing_Then_ThrowException(SoftAssertions softly) {
        when(transaction.getPersonName()).thenReturn(Optional.empty());

        var parameters = new Parameters()
            .addParameter(new PatientParameter(new Patient()));
        assertThatThrownBy(() -> new DeductionTransactionMapper().map(parameters, transaction))
            .isExactlyInstanceOf(EdifactValidationException.class);
    }

    @Test
    void when_DeductionReasonCodeIsMissing_Then_ThrowException(SoftAssertions softly) {
        when(transaction.getPersonName()).thenReturn(Optional.of(personName));
        when(transaction.getDeductionReasonCode()).thenReturn(Optional.empty());

        when(personName.getNhsNumber()).thenReturn(NHS_NUMBER);

        var parameters = new Parameters()
            .addParameter(new PatientParameter(new Patient()));
        assertThatThrownBy(() -> new DeductionTransactionMapper().map(parameters, transaction))
            .isExactlyInstanceOf(EdifactValidationException.class);
    }

    @Test
    void when_DeductionDateIsMissing_Then_ThrowException(SoftAssertions softly) {
        when(transaction.getPersonName()).thenReturn(Optional.of(personName));
        when(transaction.getDeductionReasonCode()).thenReturn(Optional.of(deductionReasonCode));
        when(transaction.getDeductionDate()).thenReturn(Optional.empty());

        when(personName.getNhsNumber()).thenReturn(NHS_NUMBER);
        when(deductionReasonCode.getCode()).thenReturn(DEDUCTION_REASON_CODE);

        var parameters = new Parameters()
            .addParameter(new PatientParameter(new Patient()));
        assertThatThrownBy(() -> new DeductionTransactionMapper().map(parameters, transaction))
            .isExactlyInstanceOf(EdifactValidationException.class);
    }

    @Test
    void testGetTransactionType() {
        assertThat(new DeductionTransactionMapper().getTransactionType())
            .isEqualTo(ReferenceTransactionType.Inbound.DEDUCTION);
    }

}