package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;
import java.io.File;
import java.io.IOException;

public class CurrentVersion {

    private Version getCurrentVersionTag(String latestTag){
        return Version.valueOf(latestTag.isEmpty() ? "0.0.0" : latestTag);
    }

    public Version getCurrentVersion(File directory, String latestTag) throws IOException, InterruptedException {

        Version currentVersion;
        ProjectType projectType = ProjectTypeFactory.getProjectType(directory);

        if (projectType != null)
            currentVersion = projectType.getCurrentVersion(directory);
        else
            currentVersion = getCurrentVersionTag(latestTag);

        return  currentVersion;
    }

}
