package uk.nhs.digital.nhsconnect.nhais.model.edifact;


import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InterchangeTrailerTest {

    private final ZonedDateTime translationDateTime = ZonedDateTime.of(2019, 4, 23, 9, 0, 0, 0, ZoneOffset.UTC);

    @Test
    public void testValidInterchangeTrailer() throws EdifactValidationException {
        InterchangeTrailer interchangeTrailer = new InterchangeTrailer(1);
        interchangeTrailer.setSequenceNumber(1);

        String edifact = interchangeTrailer.toEdifact();

        assertEquals("UNZ+1+00000001'", edifact);
    }

    @Test
    public void testPreValidationNumberOfMessagesZero() {
        InterchangeTrailer interchangeTrailer = new InterchangeTrailer(0);
        interchangeTrailer.setSequenceNumber(1);

        Exception exception = assertThrows(EdifactValidationException.class, interchangeTrailer::preValidate);

        String expectedMessage = "UNZ: Attribute numberOfMessages is required";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testPreValidationRecipientEmptyString() {
        InterchangeTrailer interchangeTrailer = new InterchangeTrailer(1);

        Exception exception = assertThrows(EdifactValidationException.class, interchangeTrailer::validateStateful);

        String expectedMessage = "UNZ: Attribute sequenceNumber is required";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
