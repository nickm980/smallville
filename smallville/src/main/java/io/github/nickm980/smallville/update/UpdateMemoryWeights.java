package io.github.nickm980.smallville.update;

import java.util.List;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.models.Agent;
import io.github.nickm980.smallville.models.memory.Memory;

public class UpdateMemoryWeights extends AgentUpdate {

    @Override
    public boolean update(ChatService converter, World world, Agent agent) {
	LOG.info("[Memory] Updating memory weights");

	List<Memory> memories = agent.getMemoryStream().getUnweightedMemories();

	if (!memories.isEmpty()) { // avoids extra API calls for getWeights()
	    int[] weights = converter.getWeights(agent);

	    for (int i = 0; i < weights.length && i < memories.size(); i++) {
		memories.get(i).setImportance(weights[i]);
	    }
	}

	LOG.info("[Memory] Memory weights updated");

	return next(converter, world, agent);
    }
}
