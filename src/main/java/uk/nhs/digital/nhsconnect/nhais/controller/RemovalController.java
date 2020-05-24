package uk.nhs.digital.nhsconnect.nhais.controller;

import org.hl7.fhir.r4.model.OperationOutcome;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class RemovalController {

    @PostMapping(path="/fhir/Patient/{id}/$nhais.removal", consumes="application/json", produces="application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OperationOutcome removal(@PathVariable(name="id") String id, @RequestBody String parameters) {
        return null;
    }

}