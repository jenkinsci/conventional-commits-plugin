package io.jenkins.plugins.conventionalcommits.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO to represent an helm chart's file.
 */
public class HelmChart implements Serializable {

    // Version attribute
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object anotherObject) {
        if (this == anotherObject) return true;
        if (anotherObject == null || getClass() != anotherObject.getClass()) return false;
        HelmChart helmChart = (HelmChart) anotherObject;
        return Objects.equals(version, helmChart.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version);
    }


}
