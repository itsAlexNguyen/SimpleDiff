package simplediff.commenting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import simplediff.commenting.github.CommentRq;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This Controller is responsible for commenting on a Pull Request.
 */
public class CommentController {
    private final String repoSlug;
    private final String pullRequestId;
    private final String targetBranch;
    private Logger logger = LoggerFactory.getLogger(CommentController.class);

    /**
     * Constructor to provide necessary values for the comment controller.
     *
     * @param repoSlug      The GitHub Repository slug.
     * @param pullRequestId The Pull Request ID.
     * @param targetBranch  The Target Branch.
     */
    public CommentController(String repoSlug, String pullRequestId, String targetBranch) {
        this.repoSlug = repoSlug;
        this.pullRequestId = pullRequestId;
        this.targetBranch = targetBranch;
    }

    /**
     * Notifies the Comment Controller to make a comment on GitHub.
     */
    public void commentOnGitHub() {
        // Retrieve the HOST Url.
        String host = ServletUriComponentsBuilder.fromCurrentContextPath()
                .build().toString();

        UriComponents uriComponents = UriComponentsBuilder.fromPath(host)
                .pathSegment(Constants.URL_PATH)
                .queryParam(Constants.TARGET_BRANCH_KEY, targetBranch)
                .queryParam(Constants.REPO_SLUG_KEY, repoSlug)
                .queryParam(Constants.PULL_REQUEST_ID_KEY, pullRequestId)
                .build();

        CommentRq requestModel = new CommentRq("I generated a SimpleDiff here -> " + uriComponents.toUriString());
        MultiValueMap<String, String> headers= new LinkedMultiValueMap<>();

        if (StringUtils.isEmpty(System.getenv(Constants.HEROKU_GITHUB_TOKEN_KEY))) {
            headers.add("Authorization", "token " + Constants.GITHUB_API_TOKEN);
        } else {
            headers.add("Authorization", "token " + System.getenv(Constants.HEROKU_GITHUB_TOKEN_KEY));
        }

        HttpEntity<CommentRq> request = new HttpEntity<>(requestModel, headers);
        RestTemplate template = new RestTemplate();
        template.postForLocation(String.format(Constants.GITHUB_COMMENT_URL_FORMAT, repoSlug, pullRequestId),
                request);
    }
}
