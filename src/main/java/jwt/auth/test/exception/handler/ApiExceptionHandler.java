package jwt.auth.test.exception.handler;

import com.google.common.collect.ImmutableMap;
import jwt.auth.test.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
@Component
public class ApiExceptionHandler extends AbstractHandlerExceptionResolver {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected ModelAndView doResolveException
            (HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            if (ex instanceof ApiException) {
                return handleIllegalArgument((ApiException) ex, response);
            }

        } catch (Exception handlerException) {
            logger.warn("Handling of [" + ex.getClass().getName() + "]Exception", handlerException);
        }
        return null;
    }

    private ModelAndView handleIllegalArgument(ApiException ex, HttpServletResponse response) throws IOException {

        response.setStatus(ex.getHttpStatus().value());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getOutputStream()
                .write(objectMapper.writeValueAsBytes(ImmutableMap.of("message", ex.getMessage())));

        return new ModelAndView();
    }
}
