package posmy.interview.boot.constants;

public enum StatusResponse {

    OK(200, "ok"),
    DUPLICATED_ENTITY(409, "Duplicated Entity"),
    ENTITY_NOT_FOUND(404, "Entity Not Found"),
    RESOURCE_NOT_AVAILABLE(410, "Resource Not Available"),
    INVALID_OR_MISSING_PARAMETERS(403, "Invalid Or Missing Parameters");

    private final int code;
    private final String description;

    StatusResponse(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
