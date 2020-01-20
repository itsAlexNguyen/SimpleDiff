package simplediff.commenting;

public final class Constants {

    /**
     * SimpleDiff URL Constants.
     */
    public static final String URL_PATH = "diff";
    public static final String TARGET_BRANCH_KEY = "targetBranch";
    public static final String REPO_SLUG_KEY = "repoSlug";
    public static final String PULL_REQUEST_ID_KEY = "pullRequestID";
    public static final String GITHUB_API_TOKEN = "";

    /**
     * Github URL Constants.
     */
    public static final String GITHUB_COMMENT_URL_FORMAT = "https://api.github.com/repos/%s/issues/%s/comments";

    /**
     * Heroku Environment Variables
     */
    public static final String HEROKU_GITHUB_TOKEN_KEY = "GITHUB_API_TOKEN";

    private Constants() {
        // Not to be initialized.
    }
}
