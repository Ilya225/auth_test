package jwt.auth.test.service.event;

import jwt.auth.test.entity.User;
import org.springframework.context.ApplicationEvent;

public class OnUserRegisterEvent extends ApplicationEvent {

    private User user;

    public OnUserRegisterEvent(User user) {
        super(user);

        this.user = user;
    }

    public User getUser() {
        return this.user;
    }
}
