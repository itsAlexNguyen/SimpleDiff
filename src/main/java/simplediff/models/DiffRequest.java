package simplediff.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DiffRequest {
    public final int pullRequestId;
    public final String repoSlug;
    public final String targetBranch;

    @JsonCreator
    public DiffRequest(@JsonProperty(required = true) int pullRequestId,
                       @JsonProperty(required = true) String repoSlug,
                       @JsonProperty(required = true) String targetBranch) {
        this.pullRequestId = pullRequestId;
        this.repoSlug = repoSlug;
        this.targetBranch = targetBranch;
    }
}
