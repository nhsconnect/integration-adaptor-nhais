package uk.nhs.digital.nhsconnect.nhais.outbound.mapper;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PatientIdentificationType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.NhsIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.util.stream.Collectors;

@Component
public class PersonNameMapper implements FromFhirToEdifactMapper<PersonName> {

    public PersonName map(Parameters parameters) {

        Patient patient = ParametersExtension.extractPatient(parameters);

        HumanName nameFirstRep = patient.getNameFirstRep();

        return PersonName.builder()
            .nhsNumber(getNhsNumber(patient))
            .patientIdentificationType(PatientIdentificationType.OFFICIAL_PATIENT_IDENTIFICATION)
            .surname(nameFirstRep.getFamily())
            .firstForename(nameFirstRep.getGiven().stream()
                .findFirst()
                .map(StringType::toString)
                .orElse(null))
            .title(StringUtils.stripToNull(nameFirstRep.getPrefixAsSingleString()))
            .secondForename(nameFirstRep.getGiven().stream()
                .skip(1)
                .findFirst()
                .map(StringType::toString)
                .orElse(null))
            .otherForenames(StringUtils.stripToNull(
                nameFirstRep.getGiven().stream()
                    .skip(2)
                    .map(StringType::toString)
                    .collect(Collectors.joining(" "))
                )
            )
            .build();
    }

    private String getNhsNumber(Patient patient) {
        return patient.getIdentifier().stream()
            .filter(identifier -> identifier.getSystem().equals(NhsIdentifier.SYSTEM))
            .findFirst()
            .map(Identifier::getValue)
            .orElse(null);
    }

}
