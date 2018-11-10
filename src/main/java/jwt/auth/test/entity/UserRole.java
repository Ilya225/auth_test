package jwt.auth.test.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.List;

@Data
@ToString(exclude = {"users"})
@NoArgsConstructor
@Entity
@Table(name = "user_role")
public class UserRole implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "role", unique = true)
    private String role;

    @ManyToMany(mappedBy = "authorities")
    @JsonIgnore
    private List<User> users;

    public UserRole(String role) {
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return role;
    }

}