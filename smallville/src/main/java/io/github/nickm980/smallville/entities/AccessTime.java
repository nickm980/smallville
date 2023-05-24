package io.github.nickm980.smallville.entities;

import java.time.LocalDateTime;
import io.github.nickm980.smallville.Smallville;
import io.github.nickm980.smallville.entities.Timekeeper;

public class AccessTime {
    public static LocalDateTime START = Timekeeper.getSimulationTime();
    
    private LocalDateTime lastAccessed;
    private LocalDateTime createdAt;
    
    public AccessTime() {
	    this.lastAccessed = Timekeeper.getSimulationTime();
	    this.createdAt = Timekeeper.getSimulationTime();
    }
    
    public LocalDateTime createdAt() {
	return createdAt;
    }
    
    public void update() {
	this.lastAccessed = Timekeeper.getSimulationTime();
    }
    
    public LocalDateTime getLastAccessed() {
	return lastAccessed;
    }
}
