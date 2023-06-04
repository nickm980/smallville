package io.github.nickm980.smallville;

import java.util.List;

public interface AgentHandlerCallback {
    void handle(List<SmallvilleAgent> agents, List<SmallvilleLocation> locations);
}
