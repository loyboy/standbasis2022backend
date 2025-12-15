package basepackage.stand.standbasisprojectonev1.payload.onboarding;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class OnboardStepOneRequest {
    
    // Matches { "schRequest": { ... }, "accountRequest": { ... } }
    private SchoolRequest schRequest;
    private UserAccountRequest accountRequest;

    // --- Inner DTOs ---

    @Data
    public static class SchoolRequest {
        private String name;

        @JsonProperty("type_of") // Maps to frontend "type_of"
        private String typeOf; 

        private Long owner; // The School Group ID
        private String state;
        private String zone;
        private String lga;
        private String town;
        private String faith;
        private String operator;
        private String gender;
        private String residence;
        private String location; // GPS
        private String population; // Can be String or Integer depending on DB
        private String email;
        private String phone;
    }

    @Data
    public static class UserAccountRequest {
        private String name;     // Admin Name
        private String email;    // Admin Email
        private String username;
        private String password;
    }
}
