package io.jenkins.plugins;

import com.github.zafarkhaja.semver.Version;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
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
        return latestTag(commits());
    }

    public String latestTag(List<String> in) {
        List<Version> tags = in.stream()
                .map(v -> Version.valueOf(v))
                .collect(Collectors.toList());

        tags.sort(Comparator.reverseOrder());

        // this is our starting version if there are no tags
        if (tags.isEmpty()) {
            return "0.0.0";
        }

        return tags.get(0).toString();
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
