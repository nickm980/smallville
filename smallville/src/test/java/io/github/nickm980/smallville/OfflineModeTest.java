package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.config.GeneralConfig;
import io.github.nickm980.smallville.runtime.RuntimeSettingsService;

/**
 * Tests for offline mode and loopback-only isolation policy.
 */
public class OfflineModeTest {

    @Test
    public void loopback_only_allows_127_0_0_1_api_path() {
	GeneralConfig config = new GeneralConfig();
	config.setApiPath("http://127.0.0.1:8080/v1/chat/completions");
	config.setLoopbackOnly(true);
	config.setOfflineMode(false);

	RuntimeSettingsService rts = new RuntimeSettingsService(config);
	assertTrue(rts.isLoopbackApiPath("http://127.0.0.1:8080/v1/chat/completions"));
    }

    @Test
    public void loopback_only_allows_localhost_api_path() {
	GeneralConfig config = new GeneralConfig();
	config.setApiPath("http://localhost:8080/v1/chat/completions");
	config.setLoopbackOnly(true);
	config.setOfflineMode(false);

	RuntimeSettingsService rts = new RuntimeSettingsService(config);
	assertTrue(rts.isLoopbackApiPath("http://localhost:8080/v1/chat/completions"));
    }

    @Test
    public void loopback_only_rejects_non_loopback_api_path() {
	GeneralConfig config = new GeneralConfig();
	config.setApiPath("https://api.openai.com/v1/chat/completions");
	config.setLoopbackOnly(true);

	assertThrows(IllegalStateException.class, () -> {
	    new RuntimeSettingsService(config);
	});
    }

    @Test
    public void offline_mode_flag_is_respected() {
	GeneralConfig config = new GeneralConfig();
	config.setApiPath("http://127.0.0.1:8080/v1/chat/completions");
	config.setOfflineMode(true);
	config.setLoopbackOnly(false);

	// Should construct without error — offline mode just disables cloud support
	RuntimeSettingsService rts = new RuntimeSettingsService(config);
	assertTrue(config.isOfflineMode());
    }

    @Test
    public void non_loopback_allowed_when_loopback_only_is_false() {
	GeneralConfig config = new GeneralConfig();
	config.setApiPath("https://api.openai.com/v1/chat/completions");
	config.setLoopbackOnly(false);
	config.setOfflineMode(false);

	// Should construct without error — external endpoints allowed without loopbackOnly
	RuntimeSettingsService rts = new RuntimeSettingsService(config);
	assertEquals("https://api.openai.com/v1/chat/completions", config.getApiPath());
    }
}
