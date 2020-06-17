package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonSex;

public class PersonSexMapper implements FromFhirToEdifactMapper<PersonSex> {


    public PersonSex map(Parameters parameters) {
        return PersonSex.builder()
            .sexCode(getPersonSex(parameters))
            .build();
    }

    private String getPersonSex(Parameters parameters) {
        Patient patient = getPatient(parameters);
        return PersonSex.getGenderCode(patient);
    }
}