package uk.nhs.digital.nhsconnect.nhais.mesh;

import uk.nhs.digital.nhsconnect.nhais.mesh.http.MeshConfig;

public class FakeMeshConfig extends MeshConfig {
    public FakeMeshConfig() {
        super("mailboxId",
            "password",
            "SharedKey",
            System.getProperty("NHAIS_MESH_HOST"),
            "false",
            System.getProperty("NHAIS_MESH_ENDPOINT_CERT"),
            System.getProperty("NHAIS_MESH_ENDPOINT_KEY"),
            System.getProperty("NHAIS_MESH_SUB_CA"));
    }
}
