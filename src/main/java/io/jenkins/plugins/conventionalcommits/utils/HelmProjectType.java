package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Represent an Helm project type (i.e with a Chart.yaml file).
 */
public class HelmProjectType extends ProjectType {
    /**
     * TO know il the project is an Helm project type.
     *
     * @param directory The directory to check. <b>Mandatory</b>
     * @return true if a Chart.yaml file is found.
     */
    @Override
    public boolean check(File directory) {
        Objects.requireNonNull(directory);
        return new File(directory.getAbsoluteFile() + File.separator + "Chart.yaml").exists();
    }

    @Override
    public Version getCurrentVersion(File directory, ProcessHelper processHelper) throws IOException, InterruptedException {
        return null;
    }
}
