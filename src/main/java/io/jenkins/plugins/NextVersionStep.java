package io.jenkins.plugins;

import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.TaskListener;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import com.github.zafarkhaja.semver.Version;

public class NextVersionStep extends Step {

    private String outputFormat;

    @DataBoundConstructor
    public NextVersionStep() {
        // empty constructor, for now...
    }

    @DataBoundSetter
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new Execution(outputFormat, stepContext);
    }

    public static class Execution extends SynchronousStepExecution<String> {

        private static final long serialVersionUID = 1L;

        @SuppressFBWarnings(value="SE_TRANSIENT_FIELD_NOT_RESTORED", justification="Only used when starting.")
        private transient final String outputFormat;

        protected Execution(String outputFormat, @Nonnull StepContext context) {
            super(context);
            this.outputFormat = outputFormat;
        }

        @Override
        protected String run() throws Exception {
            // FIXME this needs correctly implementing
            // getContext().get(FilePath.class).

            Gitter git = new GitterImpl();

            String latestTag = git.latestTag();
            getContext().get(TaskListener.class).getLogger().println("Current Tag is: " + latestTag);

            // TODO get a list of commits between 'this' and the tag
            List<String> commits = git.commits(latestTag);
            for (String commit: commits) {
                getContext().get(TaskListener.class).getLogger().println("Commit: " + commit);
            }

            Version currentVersion = Version.valueOf(latestTag);

            // based on the commit list, determine how to bump the version
            Version nextVersion = new ConventionalCommits().nextVersion(currentVersion, commits);

            // TODO write the version using the output template

            getContext().get(TaskListener.class).getLogger().println(nextVersion);

            return nextVersion.toString();
        }

    }

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
