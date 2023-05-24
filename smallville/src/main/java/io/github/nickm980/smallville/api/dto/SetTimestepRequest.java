package io.github.nickm980.smallville.api.dto;

import java.util.logging.Logger;

public class SetTimestepRequest {
    private int numOfMinutes;

    public int getNumOfMinutes() {
        return numOfMinutes;
    }

    public void setNumOfMinutes(int numOfMinutes) {
        this.numOfMinutes = numOfMinutes;
        System.out.println("Timestep numOfMinutes set: " + numOfMinutes);
    }


}
