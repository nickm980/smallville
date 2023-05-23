package io.github.nickm980.smallville.models;

import java.time.LocalDateTime;
import io.github.nickm980.smallville.Smallville;

public class AccessTime {
    public static final LocalDateTime START = Smallville.getServer().getSimulationService().getTimekeeper().getSimulationTime();
    
    private LocalDateTime lastAccessed;
    private LocalDateTime createdAt;
    
    public AccessTime() {
	this.lastAccessed = LocalDateTime.now();
	this.createdAt = LocalDateTime.now();
    }
    
    public LocalDateTime createdAt() {
	return createdAt;
    }
    
    public void update() {
	this.lastAccessed = LocalDateTime.now();
    }
    
    public LocalDateTime getLastAccessed() {
	return lastAccessed;
    }
}
