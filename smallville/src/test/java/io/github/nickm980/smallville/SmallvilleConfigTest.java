package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.config.GeneralConfig;
import io.github.nickm980.smallville.config.SmallvilleConfig;
import io.github.nickm980.smallville.config.prompts.Prompts;
import io.github.nickm980.smallville.config.simulation.SimulationFile;

public class SmallvilleConfigTest {

    @Test
    public void test_default_scenario_path_gate_only_applies_to_config_and_simulation() throws Exception {
	Method method = SmallvilleConfig.class.getDeclaredMethod("isDefaultScenarioPath", String.class);
	method.setAccessible(true);

	assertTrue((boolean) method.invoke(null, "config.yaml"));
	assertTrue((boolean) method.invoke(null, "simulation.yaml"));
	assertFalse((boolean) method.invoke(null, "prompts.yaml"));
    }

    @Test
    public void test_repo_default_config_and_simulation_prefer_two_house_scenario_files() {
	GeneralConfig config = SmallvilleConfig.loadYamlFile("config.yaml", GeneralConfig.class);
	SimulationFile simulation = SmallvilleConfig.loadYamlFile("simulation.yaml", SimulationFile.class);

	assertNotNull(config);
	assertNotNull(simulation);
	assertTrue(config.isSimulationFile());
	assertTrue(simulation.getWorld().getLocations().stream().anyMatch(location -> location.getName().equals("Blue House")));
	assertFalse(simulation.getWorld().getLocations().stream().anyMatch(location -> location.getName().equals("Forest")));
	assertTrue(simulation.getAgents().stream().anyMatch(agent -> agent.getName().equals("Jamie")));
	assertTrue(simulation.getAgents().stream().anyMatch(agent -> agent.getName().equals("Alex")));
	assertTrue(simulation.getWorldBuilding().getSummary().contains("two-house horticultural microcosm"));
	assertTrue(simulation.getDailyRhythm().getBreakfast().equals("06:00-09:30"));
    }

    @Test
    public void test_prompts_loading_remains_outside_default_scenario_redirect() {
	Prompts prompts = SmallvilleConfig.loadYamlFile("prompts.yaml", Prompts.class);

	assertNotNull(prompts);
	assertNotNull(prompts.getMisc());
	assertTrue(prompts.getMisc().getDebug().contains("ping: {{ping}}"));
	assertTrue(prompts.getMisc().getDebug().contains("agent.name: {{agent.name}}"));
    }
}