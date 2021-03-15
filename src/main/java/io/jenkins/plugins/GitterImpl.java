package io.jenkins.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class GitterImpl implements Gitter {

    @Override
    public List<String> tags() {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        List<String> tags = new ArrayList<>();

        try {
            Repository repository = builder
                .readEnvironment()
                .findGitDir()
                .build();

            Git git = new Git(repository);
            List<Ref> call = git.tagList().call();

            // TODO use stream processing here
            for (Ref ref : call) {
                tags.add(ref.getName());
            }
        } catch (Exception e) {
            // FIXME
            throw new IllegalStateException(e);
        }
        return tags;
    }

    @Override
    public String latestTag() {
        return null;
    }

    @Override
    public List<String> commits() {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        List<String> commits = new ArrayList<>();

        try {
            Repository repository = builder
                    .readEnvironment()
                    .findGitDir()
                    .build();

            Git git = new Git(repository);
            // fetch all commits for this tag
            LogCommand log = git.log();

            Iterable<RevCommit> logs = log.call();
            for (RevCommit rev : logs) {
                commits.add(rev.getFullMessage());
            }
        } catch (Exception e) {
            // FIXME
            throw new IllegalStateException(e);
        }
        return commits;
    }
}
