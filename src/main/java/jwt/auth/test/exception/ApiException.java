package jwt.auth.test.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {

    private String message;
    private HttpStatus httpStatus;

    public ApiException(String s, HttpStatus httpStatus) {
        super(s);
        this.message = s;
        this.httpStatus = httpStatus;
    }
}
