package io.github.nickm980.smallville.api.v1.dto;

public class ImportAgentsRequest {
    private String yaml;
    private boolean preview;

    public String getYaml() {
	return yaml;
    }

    public void setYaml(String yaml) {
	this.yaml = yaml;
    }

    public boolean isPreview() {
	return preview;
    }

    public void setPreview(boolean preview) {
	this.preview = preview;
    }
}
