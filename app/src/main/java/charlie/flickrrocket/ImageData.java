package charlie.flickrrocket;

/**
 * Created by charlie on 2/9/15.
 */
public class ImageData {

    private String id;
    private String farm;
    private String server;
    private String secret;
    private String description;

    public ImageData(String id, String farm, String server, String secret, String description) {
        this.id = id;
        this.farm = farm;
        this.server = server;
        this.secret = secret;
        this.description = description;
    }

    public String[] toStringArray() {
        return new String[]{this.id, this.farm, this.server, this.secret, this.description};
    }
}
