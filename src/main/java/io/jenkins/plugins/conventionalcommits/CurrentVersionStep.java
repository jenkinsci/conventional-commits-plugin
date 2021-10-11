package io.jenkins.plugins.conventionalcommits;

import static io.jenkins.plugins.conventionalcommits.NextVersionStep.stdout;
import static io.jenkins.plugins.conventionalcommits.process.ProcessUtil.execute;

import com.github.zafarkhaja.semver.Version;
import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.conventionalcommits.utils.CurrentVersion;
import io.jenkins.plugins.conventionalcommits.utils.TagsHelper;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * Step to get the current version of the project.
 * Example :
 * <code>def CURRENT_VERSION = currentVersion()</code>
 */
public class CurrentVersionStep extends Step {

  @DataBoundConstructor
  public CurrentVersionStep() {
    // empty constructor, for now...
  }

  @Override
  public StepExecution start(StepContext stepContext) throws Exception {
    return new Execution(stepContext);
  }

  /**
   * This class extends Step Execution class, contains the run method.
   * This is the main entry point of the step.
   */
  public static class Execution extends SynchronousStepExecution<String> {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor with fields initialisation.
     *
     * @param context      Jenkins context
     */
    protected Execution(@Nonnull StepContext context) {
      super(context);
    }

    /**
     * Entry point of the step.
     *
     * @return The current version of the project.
     * @throws Exception If errors occurs ;).
     */
    @Override
    protected String run() throws Exception {
      FilePath workspace = getContext().get(FilePath.class);
      if (workspace == null) {
        throw new IOException("no workspace");
      }

      // if the workspace is remote then lets make a local copy
      if (workspace.isRemote()) {
        throw new IOException("workspace.isRemote(), not entirely sure what to do here...");
      } else {
        File dir = new File(workspace.getRemote());
        String latestTag = TagsHelper.getLatestTag(getContext(), dir, false);

        Version currentVersion =
            new CurrentVersion()
                .getCurrentVersion(
                    dir, latestTag, getContext().get(TaskListener.class).getLogger());

        return currentVersion.toString();
      }
    }
  }

  /**
   * This Class implements the abstract class StepDescriptor.
   */
  @Extension
  public static class DescriptorImpl extends StepDescriptor {

    @Override
    public String getDisplayName() {
      return "Current Version: determine the current version from the conventional commit history";
    }

    @Override
    public Set<? extends Class<?>> getRequiredContext() {
      return ImmutableSet.of(TaskListener.class, FilePath.class);
    }

    @Override
    public String getFunctionName() {
      return "currentVersion";
    }
  }
}