package placement_OS.demo.dto;

public class LoginResponseDTO {

    private String token;
    private String message;
    private String role;
    private String name;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String token, String message, String role, String name) {
        this.token = token;
        this.message = message;
        this.role = role;
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}