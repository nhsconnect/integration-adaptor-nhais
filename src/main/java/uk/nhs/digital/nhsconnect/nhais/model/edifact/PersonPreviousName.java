package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Example PNA+PER++++SU:KENNEDY+FO:SARAH+TI:MISS+MI:ANGELA'
 */

@Getter
@Builder
@EqualsAndHashCode(callSuper = false)
@ToString
public class PersonPreviousName extends Segment {

    public static final String KEY = "PNA";
    public static final String QUALIFIER = "PER";
    public static final String KEY_QUALIFIER = KEY + PLUS_SEPARATOR + QUALIFIER;

    //all properties are optional
    private final String nhsNumber;
    private final PatientIdentificationType patientIdentificationType;
    private final String familyName;
    private final String forename;
    private final String title;
    private final String middleName;
    private final String thirdForename;

    public static PersonPreviousName fromString(String edifactString) {
        if (!edifactString.startsWith(PersonPreviousName.KEY_QUALIFIER)) {
            throw new IllegalArgumentException("Can't create " + PersonPreviousName.class.getSimpleName() + " from " + edifactString);
        }
        return PersonPreviousName.builder()
            .nhsNumber(extractNhsNumber(edifactString))
            .patientIdentificationType(getPatientIdentificationType(edifactString))
            .familyName(extractNamePart("SU", edifactString))
            .forename(extractNamePart("FO", edifactString))
            .title(extractNamePart("TI", edifactString))
            .middleName(extractNamePart("MI", edifactString))
            .thirdForename(extractNamePart("FS", edifactString))
            .build();
    }

    private static String extractNhsNumber(String edifactString) {
        String[] components = Split.byPlus(edifactString);
        if (components.length > 2 && StringUtils.isNotEmpty(components[2])) {
            return Split.byColon(components[2])[0];
        }
        return null;
    }

    private static PatientIdentificationType getPatientIdentificationType(String edifactString) {
        String[] components = Split.byPlus(edifactString);
        if (StringUtils.isNotEmpty(extractNhsNumber(edifactString)) && components.length > 1) {
            return PatientIdentificationType.fromCode(Split.byColon(components[2])[1]);
        }
        return null;
    }

    private static String extractNamePart(String qualifier, String text) {
        return Arrays.stream(Split.byPlus(text))
            .filter(value -> value.startsWith(qualifier))
            .map(value -> Split.byColon(value)[1])
            .findFirst()
            .orElse(null);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        List<String> values = new ArrayList<>();
        values.add(QUALIFIER);

        values.add(Optional.ofNullable(this.nhsNumber)
            .map(value -> value + ":" + this.patientIdentificationType.getCode())
            .orElse(StringUtils.EMPTY));
        values.add(StringUtils.EMPTY);
        values.add(StringUtils.EMPTY);
        values.add(Optional.ofNullable(this.familyName)
            .map(value -> "SU:" + value)
            .orElse(StringUtils.EMPTY));
        values.add(Optional.ofNullable(this.forename)
            .map(value -> "FO:" + value)
            .orElse(StringUtils.EMPTY));
        values.add(Optional.ofNullable(this.title)
            .map(value -> "TI:" + value)
            .orElse(StringUtils.EMPTY));
        values.add(Optional.ofNullable(this.middleName)
            .map(value -> "MI:" + value)
            .orElse(StringUtils.EMPTY));
        values.add(Optional.ofNullable(this.thirdForename)
            .map(value -> "FS:" + value)
            .orElse(StringUtils.EMPTY));

        values = removeEmptyTrailingFields(values, StringUtils::isNotBlank);

        return String.join(PLUS_SEPARATOR, values);
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        //NOP
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }

}
