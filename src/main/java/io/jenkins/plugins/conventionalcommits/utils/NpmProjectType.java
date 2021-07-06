package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;

import java.io.File;
import java.io.IOException;

/**
 * Class to interact with a NPM project type.
 */
public class NpmProjectType extends ProjectType {

    /**
     * Check if the project is a NPM project (i.e a package.json is found).
     *
     * @param directory The directory of the project.
     * @return True if a package.json is found.
     */
    @Override
    public boolean check(File directory) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Return the next version of the version attribute.
     *
     * @param directory The project's directory.
     * @return The next calculated version (based on Semver).
     * @throws IOException          If an error occur reading files.
     * @throws InterruptedException If an error occur on the current thread;
     */
    @Override
    public Version getCurrentVersion(File directory) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
