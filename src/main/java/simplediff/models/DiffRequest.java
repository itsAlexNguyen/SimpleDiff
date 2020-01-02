package simplediff.models;

public class DiffRequest {
  private final String gitRepo;
  private final String srcBranch;
  private final String targetBranch;

  public DiffRequest(String gitRepo, String srcBranch, String targetBranch) {
    this.gitRepo = gitRepo;
    this.srcBranch = srcBranch;
    this.targetBranch = targetBranch;
  }

  public String getGitRepo() {
    return gitRepo;
  }

  public String getTargetBranch() {
    return targetBranch;
  }

  public String getSrcBranch() {
    return srcBranch;
  }
}
