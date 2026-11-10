package tarefas02.api.exceptions;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tarefas02.api.services.exceptions.DataBindingViolationException;
import tarefas02.api.services.exceptions.ObjectNotFoundException;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler implements AuthenticationFailureHandler {

    @Value("${server.error.include-exception:false}")
    private boolean printStackTrace;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(status.value(), "Erro de validação. Confira os campos.");
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtException(Exception exception, WebRequest request) {
        final String errorMessage = "Ocorreu um erro inesperado no servidor.";
        return buildErrorResponse(exception, errorMessage, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataIntegrityViolationException(
            DataIntegrityViolationException dataIntegrityViolationException, WebRequest request) {
        String errorMessage = dataIntegrityViolationException.getMostSpecificCause().getMessage();
        return buildErrorResponse(dataIntegrityViolationException, errorMessage, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleObjectNotFoundException(
            ObjectNotFoundException objectNotFoundException, WebRequest request) {
        return buildErrorResponse(objectNotFoundException, objectNotFoundException.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(DataBindingViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataBindingViolationException(
            DataBindingViolationException dataBindingViolationException, WebRequest request) {
        return buildErrorResponse(dataBindingViolationException, dataBindingViolationException.getMessage(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleAuthenticationException(
            AuthenticationException authenticationException, WebRequest request) {
        return buildErrorResponse(authenticationException, authenticationException.getMessage(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleAccessDeniedException(
            AccessDeniedException accessDeniedException, WebRequest request) {
        return buildErrorResponse(accessDeniedException, accessDeniedException.getMessage(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(AuthorizationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleAuthorizationException(
            AuthorizationException authorizationException, WebRequest request) {
        return buildErrorResponse(authorizationException, authorizationException.getMessage(), HttpStatus.FORBIDDEN, request);
    }

    private ResponseEntity<Object> buildErrorResponse(
            Exception exception, String message, HttpStatus httpStatus, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message);
        if (printStackTrace) {
            errorResponse.setStackTrace(ExceptionUtils.getStackTrace(exception));
        }
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        Integer status = HttpStatus.UNAUTHORIZED.value();
        response.setStatus(status);
        response.setContentType("application/json");
        ErrorResponse errorResponse = new ErrorResponse(status, "E-mail ou senha inválidos.");
        response.getWriter().append(errorResponse.toJson());
    }
}
