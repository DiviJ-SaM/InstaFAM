package me.divij.instafam.posts;

public class PostData {
    private String email;
    private String postID;
    private String desc;
    private String postURL;

    public PostData(String email, String postID, String desc, String postURL) {
        this.email = email;
        this.postID = postID;
        this.desc = desc;
        this.postURL = postURL;
    }

    public String getEmail() {
        return email;
    }

    public String getPostID() {
        return postID;
    }

    public String getDesc() {
        return desc;
    }

    public String getPostURL() {
        return postURL;
    }
}
