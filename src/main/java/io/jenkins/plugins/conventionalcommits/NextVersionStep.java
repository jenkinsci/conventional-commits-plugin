package io.jenkins.plugins.conventionalcommits;

import static io.jenkins.plugins.conventionalcommits.process.ProcessUtil.execute;
import static io.jenkins.plugins.conventionalcommits.utils.TagsHelper.getLatestTag;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import com.github.zafarkhaja.semver.Version;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.conventionalcommits.utils.CurrentVersion;
import io.jenkins.plugins.conventionalcommits.utils.WriteVersion;

/**
 * Base class of the plugin.
 */
public class NextVersionStep extends Step {

  private String outputFormat;
  private String startTag;
  // Pre release information (optional)
  private String preRelease;
  // True to preserve in the next version the prerelease information (if set)
  private boolean preservePreRelease;
  // True to increment prerelease information instead of the version itself
  private boolean incrementPreRelease;
  private String buildMetadata;
  private boolean writeVersion;
  // True if non annotated tag are supported
  private boolean nonAnnotatedTag;

  @DataBoundConstructor
  public NextVersionStep() {
    // empty constructor, for now...
  }

  public String getOutputFormat() {
    return outputFormat;
  }

  @DataBoundSetter
  public void setOutputFormat(String outputFormat) {
    this.outputFormat = outputFormat;
  }

  public String getStartTag() {
    return startTag;
  }

  @DataBoundSetter
  public void setStartTag(String startTag) {
    this.startTag = startTag;
  }

  public String getBuildMetadata() {
    return buildMetadata;
  }

  @DataBoundSetter
  public void setBuildMetadata(String buildMetadata) {
    this.buildMetadata = buildMetadata;
  }

  public boolean isWriteVersion() {
    return writeVersion;
  }

  @DataBoundSetter
  public void setWriteVersion(boolean writeVersion) {
    this.writeVersion = writeVersion;
  }

  public String getPreRelease() {
    return preRelease;
  }

  @DataBoundSetter
  public void setPreRelease(String preRelease) {
    this.preRelease = preRelease;
  }

  public boolean isPreservePreRelease() {
    return preservePreRelease;
  }

  @DataBoundSetter
  public void setPreservePreRelease(boolean preservePreRelease) {
    this.preservePreRelease = preservePreRelease;
  }

  public boolean isIncrementPreRelease() {
    return incrementPreRelease;
  }

  @DataBoundSetter
  public void setIncrementPreRelease(boolean incrementPreRelease) {
    this.incrementPreRelease = incrementPreRelease;
  }

  public boolean isNonAnnotatedTag() {
    return nonAnnotatedTag;
  }

  @DataBoundSetter
  public void setNonAnnotatedTag(boolean nonAnnotatedTag) {
    this.nonAnnotatedTag = nonAnnotatedTag;
  }

  @Override
  public StepExecution start(StepContext stepContext) throws Exception {
    return new Execution(outputFormat, startTag, buildMetadata, writeVersion, preRelease,
        preservePreRelease, incrementPreRelease, nonAnnotatedTag, stepContext);
  }

  /**
   * This class extends Step Execution class, contains the run method.
   */
  public static class Execution extends SynchronousStepExecution<String> {

    private static final long serialVersionUID = 1L;

    @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED",
        justification = "Only used when starting.")
    private final transient String outputFormat;

    @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED",
        justification = "Only used when starting.")
    private final transient String startTag;

    @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED",
        justification = "Only used when starting.")
    private final transient String buildMetadata;

    @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED",
        justification = "Only used when starting.")
    private final transient boolean writeVersion;

    @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED",
        justification = "Only used when starting.")
    // Pre release information to add to the next version
    private final transient String preRelease;

    @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED",
        justification = "Only used when starting.")
    // True to preserve in the next version the prerelease information (if set)
    private final transient boolean preservePreRelease;

    @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED",
        justification = "Only used when starting.")
    // True to increment prerelease information instead of the version itself
    private final transient boolean incrementPreRelease;

    // True if annotated tags are supported
    @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED",
        justification = "Only used when starting.")
    private final transient boolean nonAnnotatedTag;


    /**
     * Constructor with fields initialisation.
     *
     * @param outputFormat Output format for the next version
     * @param startTag Git tag
     * @param buildMetadata Add meta date to the version.
     * @param writeVersion Should write the new version in the file.
     * @param preRelease Pre release information to add
     * @param preservePreRelease Keep existing prerelease information or not
     * @param incrementPreRelease Increment prerelease information or not
     * @param nonAnnotatedTag Should use or non annotated tags
     * @param context Jenkins context
     */
    protected Execution(String outputFormat, String startTag, String buildMetadata,
        boolean writeVersion, String preRelease, boolean preservePreRelease,
        boolean incrementPreRelease, boolean nonAnnotatedTag, @Nonnull StepContext context) {
      super(context);
      this.outputFormat = outputFormat;
      this.startTag = startTag;
      this.buildMetadata = buildMetadata;
      this.writeVersion = writeVersion;
      this.preRelease = preRelease;
      this.preservePreRelease = preservePreRelease;
      this.incrementPreRelease = incrementPreRelease;
      this.nonAnnotatedTag = nonAnnotatedTag;
    }

    @Override
    protected String run() throws Exception {
      FilePath workspace = getContext().get(FilePath.class);
      if (workspace == null) {
        throw new IOException("no workspace");
      }

      // if the workspace is remote then lets make a local copy
      File dir = new File(workspace.getRemote());
      String latestTag = getLatestTag(getContext(), dir, nonAnnotatedTag);

      Version currentVersion = new CurrentVersion().getCurrentVersion(dir, latestTag,
          getContext().get(TaskListener.class).getLogger());

      String commitMessagesString = null;
      if (latestTag.isEmpty()) {
        commitMessagesString = execute(dir, "git", "log", "--pretty=format:%s").trim();
      } else {
        // FIXME get a list of commits between 'this' and the tag
        // git log --pretty=format:%s tag..HEAD
        commitMessagesString =
            execute(dir, "git", "log", "--pretty=format:%s", latestTag + "..HEAD").trim();
      }

      List<String> commitHistory = Arrays.asList(commitMessagesString.split("\n"));

      Version nextVersion;
      if (!incrementPreRelease || StringUtils.isEmpty(currentVersion.getPreReleaseVersion())) {
        // based on the commit list, determine how to bump the version
        nextVersion = new ConventionalCommits().nextVersion(currentVersion, commitHistory);
      } else {
        nextVersion = currentVersion.incrementPreReleaseVersion();
      }

      if (StringUtils.isNotBlank(buildMetadata)) {
        nextVersion = nextVersion.setBuildMetadata(buildMetadata);
      }

      // Keep (or not) the pre-release information only if incrementPreRelease is not set
      if (!incrementPreRelease && StringUtils.isNotBlank(currentVersion.getPreReleaseVersion())) {
        if (preservePreRelease) {
          nextVersion = nextVersion.setPreReleaseVersion(currentVersion.getPreReleaseVersion());
        } else {
          if (!StringUtils.isNotBlank(preRelease)) {
            nextVersion = Version.valueOf(currentVersion.getNormalVersion());
          }
        }
      }

      // If pre-release information, add it
      if (StringUtils.isNotBlank(preRelease)) {
        nextVersion = nextVersion.setPreReleaseVersion(preRelease);
      }

      getContext().get(TaskListener.class).getLogger().println(nextVersion);

      if (writeVersion) {
        WriteVersion writer = new WriteVersion();
        String writeLog = writer.write(nextVersion, dir);
        getContext().get(TaskListener.class).getLogger().println(writeLog);
      }

      return nextVersion.toString();
    }
  }

  /**
   * This Class implements the abstract class StepDescriptor.
   */
  @Extension
  public static class DescriptorImpl extends StepDescriptor {

    @Override
    public String getDisplayName() {
      return "Next Version: determine the next version from the conventional commit history";
    }

    @Override
    public Set<? extends Class<?>> getRequiredContext() {
      return ImmutableSet.of(TaskListener.class, FilePath.class);
    }

    @Override
    public String getFunctionName() {
      return "nextVersion";
    }
  }
}
