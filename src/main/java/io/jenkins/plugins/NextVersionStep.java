package io.jenkins.plugins;

import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.TaskListener;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.jenkinsci.plugins.gitclient.JGitAPIImpl;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

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

            // TODO get the nearest tag
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Repository repository = builder
                    .readEnvironment() // scan environment GIT_* variables
                    .findGitDir() // scan up the file system tree
                    .build();

            try (Git git = new Git(repository)) {
                List<Ref> call = git.tagList().call();
                for (Ref ref : call) {
                    getContext().get(TaskListener.class).getLogger().println("Tag: " + ref + " " + ref.getName() + " " + ref.getObjectId().getName());

                    // fetch all commits for this tag
                    LogCommand log = git.log();

                    Iterable<RevCommit> logs = log.call();
                    for (RevCommit rev : logs) {
                        getContext().get(TaskListener.class).getLogger().println("Commit: " + rev /* + ", name: " + rev.getName() + ", id: " + rev.getId().getName() */);
                        getContext().get(TaskListener.class).getLogger().println("Full Message: " + rev.getFullMessage());
                    }
                }
            }


            getContext().get(TaskListener.class).getLogger().println(repository);

            //List<Ref> call = repository..tagList().call();
            //for (Ref ref : call) {
            //    getContext().get(TaskListener.class).getLogger().println(ref);
            //}


            // TODO get a list of commits between 'this' and the tag

            // TODO based on the commit list, determine how to bump the version

            // TODO write the version using the output template

            String nextVersion = "0.0.1";
            getContext().get(TaskListener.class).getLogger().println(nextVersion);

            return nextVersion;
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
