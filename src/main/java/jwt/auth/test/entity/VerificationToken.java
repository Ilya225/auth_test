package jwt.auth.test.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "verification_token")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id")
    private User user;

    private String token;
    private LocalDateTime expired;

    public VerificationToken(String token, User user) {
        this.user = user;
        this.token = token;
        this.expired = LocalDateTime.now().plusDays(1);
    }

    public boolean isExpired() {
        return !this.expired.isAfter(LocalDateTime.now());
    }
}
