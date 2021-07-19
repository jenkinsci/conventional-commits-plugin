package io.jenkins.plugins.conventionalcommits;

import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.utils.LogUtils;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

/** Class to process conventional commit messages to get next version. */
public class ConventionalCommits {

  LogUtils logger = new LogUtils();

  private List<String> filterMergeCommits(List<String> commits) {
    return commits.stream().filter(s -> !s.startsWith("Merge")).collect(Collectors.toList());
  }

  /**
   * Return the next semantic version.
   *
   * @param in The current semantic version.
   * @param commits List of commit messages from the last tag.
   * @return The next calculated version (based on Semver).
   */
  public Version nextVersion(Version in, List<String> commits) {
    List<String> filtered = filterMergeCommits(commits);
    List<String> breaking =
        filtered.stream()
            .filter(s -> s.contains("!:") || breakingChangeFooter(s))
            .collect(Collectors.toList());
    List<String> features =
        filtered.stream().filter(s -> s.startsWith("feat")).collect(Collectors.toList());

    if (!breaking.isEmpty()) {
      return in.incrementMajorVersion();
    }

    if (!features.isEmpty()) {
      return in.incrementMinorVersion();
    }

    return in.incrementPatchVersion();
  }

  private boolean breakingChangeFooter(String commit) {

    boolean result = false;
    String[] lines = commit.split("[\\r\\n]+");

    for (String line : lines) {
      if (line.startsWith("BREAKING CHANGE:") || line.startsWith("BREAKING-CHANGE:")) {
        result = true;
        break;
      } else if (line.toLowerCase().startsWith("breaking change:")
          || line.toLowerCase().startsWith("breaking-change:")) {
        String keyword = line.substring(0, 16);
        logger.log(
            Level.INFO,
            Level.INFO,
            Level.FINE,
            Level.FINE,
            true,
            "'"
                + keyword
                + "' detected which is not compliant with Conventional Commits Guidelines "
                + "(https://www.conventionalcommits.org/en/v1.0.0/#summary)");
      }
    }

    return result;
  }
}
