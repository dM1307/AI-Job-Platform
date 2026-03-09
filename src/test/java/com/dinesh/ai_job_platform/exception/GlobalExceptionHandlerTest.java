package com.dinesh.ai_job_platform.exception;

import com.neuralhire.platform.exception.*;
import org.junit.jupiter.api.*;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for GlobalExceptionHandler.
 * Verifies that each exception type maps to the correct HTTP status
 * and that the error response body contains required fields.
 */
@DisplayName("GlobalExceptionHandler — Unit Tests")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private ServletWebRequest request;

    @BeforeEach
    void setUp() {
        var servletRequest = new MockHttpServletRequest();
        servletRequest.setRequestURI("/api/test");
        request = new ServletWebRequest(servletRequest);
    }

    @Test
    @DisplayName("handles ResourceNotFoundException → 404")
    void notFound() {
        var ex = new ResourceNotFoundException("Resume not found: 42");
        var response = handler.handleNotFound(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getMessage()).contains("42");
        assertThat(response.getBody().getPath()).isEqualTo("/api/test");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("handles ResumeBadRequestException → 400")
    void badRequest() {
        var ex = new ResumeBadRequestException("Name must not be blank");
        var response = handler.handleBadRequest(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).contains("Name");
    }

    @Test
    @DisplayName("handles ResumeParseException → 422")
    void parseError() {
        var ex = new ResumeParseException("Failed to parse PDF");
        var response = handler.handleParseError(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody().getStatus()).isEqualTo(422);
    }

    @Test
    @DisplayName("handles AiServiceUnavailableException → 503")
    void serviceUnavailable() {
        var ex = new AiServiceUnavailableException("OpenAI timeout");
        var response = handler.handleAiUnavailable(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody().getStatus()).isEqualTo(503);
    }

    @Test
    @DisplayName("handles generic Exception → 500")
    void internalError() {
        var ex = new RuntimeException("Unexpected failure");
        var response = handler.handleGeneric(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getStatus()).isEqualTo(500);
    }

    @Test
    @DisplayName("error response always includes timestamp")
    void alwaysHasTimestamp() {
        var response = handler.handleNotFound(new ResourceNotFoundException("x"), request);
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("error response always includes path")
    void alwaysHasPath() {
        var response = handler.handleBadRequest(new ResumeBadRequestException("x"), request);
        assertThat(response.getBody().getPath()).isNotBlank();
    }
}
