package simplediff.commenting.github;

import com.google.gson.annotations.SerializedName;

public class CommentRq {
    @SerializedName("body")
    public final String body;

    /**
     * Constructor. Creates a Request JSON for GitHub Commenting API.
     *
     * @param body The message to include in the body.
     */
    public CommentRq(String body) {
        this.body = body;
    }
}
