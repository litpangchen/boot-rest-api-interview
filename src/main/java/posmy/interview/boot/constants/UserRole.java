package posmy.interview.boot.constants;

public enum UserRole {

    LIBRARIAN(1, "LIBRARIAN"),
    MEMBER(2, "MEMBER");

    private final Integer id;
    private final String role;

    UserRole(Integer id, String role) {
        this.id = id;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public static String getRoleById(Integer id) {
        for (UserRole userRole : values()) {
            if (userRole.getId().equals(id)) {
                return userRole.getRole();
            }
        }
        throw new IllegalArgumentException();
    }
}
