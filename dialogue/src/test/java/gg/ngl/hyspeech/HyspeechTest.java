package gg.ngl.hyspeech;

import org.junit.Test;
import static org.junit.Assert.*;

public class HyspeechTest {
    @Test
    public void pluginJsonExists() {
        // Verify manifest.json exists in resources
        assertNotNull(getClass().getClassLoader().getResource("manifest.json"));
    }
}
