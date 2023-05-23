package io.github.nickm980.smallville.entities;

import java.time.LocalDateTime;

public class AccessTime {
    public static final LocalDateTime START = LocalDateTime.now();
    
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
