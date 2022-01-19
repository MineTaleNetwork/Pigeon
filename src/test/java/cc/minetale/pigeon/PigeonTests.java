package cc.minetale.pigeon;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PigeonTests {

    static Pigeon pigeon;

    @BeforeAll
    static void setup() {
        final var host = "abc";
        final var port = 123;
        final var networkId = "testId";
        final var unitId = "testId2";

        //Initialize Pigeon

        pigeon = new Pigeon();
        pigeon.initialize(host, port, networkId, unitId, new ObjectMapper());

        var postOffice = pigeon.getPostOffice();
        assertNotNull(postOffice);

        assertEquals(postOffice.getHost(), host);
        assertEquals(postOffice.getPort(), port);
        assertEquals(postOffice.getNetworkId(), networkId);

        var unit = postOffice.getUnit();
        assertNotNull(unit);

        assertEquals(unit.id(), unitId);

        //Register the test payloads
        final var payloadsRegistry = pigeon.getPayloadsRegistry();
        assertNotNull(payloadsRegistry);

        payloadsRegistry.registerPayload(TestPayload.class);
    }

    @Test
    void testPayload() throws JsonProcessingException {
        var testPayload = new TestPayload();

        //Serialize

        testPayload.setOrigin(pigeon.getPostOffice().getUnit());

        var serialized = testPayload.toJson();
        assertNotNull(serialized);
        assertFalse(serialized.isEmpty());

        System.out.println(serialized);

        //Deserialize

        var deserialized = testPayload.fromJson(serialized);
        assertNotNull(deserialized);
        assertNotNull(deserialized.getOrigin());

        var payloadId = deserialized.getPayloadId();
        assertNotNull(payloadId);
        assertFalse(payloadId.isEmpty());
    }

    @Payload
    static class TestPayload extends BasePayload {
        private final byte byteVal = 100;
        private final short shortVal = 200;
        private final int intVal = 300;
        private final long longVal = 400;

        private final double doubleVal = 2.192;
        private final float floatVal = 4.291f;

        private String stringVal = "abc";

        private Color colorVal = new Color(255, 0, 128);
        private UUID uuidVal = UUID.randomUUID();

        private List<Color> listVal = List.of(
                new Color(255, 0, 0),
                new Color(0, 255, 0),
                new Color(0, 0, 255)
        );

        private Map<UUID, Color> mapVal = Map.ofEntries(
                Map.entry(UUID.randomUUID(), new Color(255, 0, 0)),
                Map.entry(UUID.randomUUID(), new Color(0, 255, 0)),
                Map.entry(UUID.randomUUID(), new Color(0, 0, 255))
        );

        public TestPayload() {
            payloadId = "test";
        }

        @Override public void receive() { }
    }

}
