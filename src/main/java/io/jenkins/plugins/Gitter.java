package io.jenkins.plugins;

import java.util.List;

public interface Gitter {

    /**
     * Lists all tags on a repository
     * @return a list of all tags on a repository
     */
    List<String> tags();

    /**
     * Gets the latest tag for a repository
     * @return the latest tag on a repository
     */
    String latestTag();

    /**
     * Lists all commits on a repository
     * @param startTag the tag to start from, or null
     * @return a list of all commits on a repository
     */
    List<String> commits(String startTag);
}
