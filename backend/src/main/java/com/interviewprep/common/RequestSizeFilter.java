/**
 * FILE: backend/src/main/java/com/interviewprep/common/RequestSizeFilter.java
 *
 * WHAT IT DOES:
 *   Rejects API bodies above the configured byte limit, including streamed bodies without a Content-Length header.
 *
 * ROLE IN THE PROJECT:
 *   Limits memory and AI-cost abuse before controllers deserialize user input.
 *
 * IF YOU REMOVE THIS FILE:
 *   Oversized JSON payloads can reach Spring MVC and consume unnecessary resources.
 *
 * DEPENDS ON: ApplicationProperties, ApiError, RequestIdFilter, and the servlet API.
 * USED BY: Every backend HTTP request with a body.
 *
 * KEY FUNCTIONS:
 *   - doFilterInternal(): checks declared size and wraps streamed request input.
 *   - writeTooLargeResponse(): writes the safe 413 response when the limit is exceeded.
 */
package com.interviewprep.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import com.interviewprep.config.ApplicationProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RequestSizeFilter extends OncePerRequestFilter {

    private final ApplicationProperties properties;
    private final ObjectMapper objectMapper;

    public RequestSizeFilter(ApplicationProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !("POST".equals(request.getMethod()) || "PUT".equals(request.getMethod()) || "PATCH".equals(request.getMethod()));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long limit = properties.request().maxBodyBytes();
        long declaredLength = request.getContentLengthLong();
        if (declaredLength > limit) {
            writeTooLargeResponse(request, response);
            return;
        }

        try {
            filterChain.doFilter(new LimitedBodyRequest(request, limit), response);
        }
        catch (RequestBodyTooLargeException exception) {
            if (!response.isCommitted()) {
                writeTooLargeResponse(request, response);
            }
        }
    }

    private void writeTooLargeResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(
                response.getOutputStream(),
                new ApiError(
                        "PAYLOAD_TOO_LARGE",
                        "Request body is too large.",
                        RequestIdFilter.getRequestId(request),
                        Instant.now()
                )
        );
    }

    private static final class LimitedBodyRequest extends HttpServletRequestWrapper {

        private final long limit;

        private LimitedBodyRequest(HttpServletRequest request, long limit) {
            super(request);
            this.limit = limit;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return new LimitedServletInputStream(super.getInputStream(), limit);
        }

        @Override
        public BufferedReader getReader() throws IOException {
            String encoding = getCharacterEncoding();
            Charset charset = encoding == null ? StandardCharsets.UTF_8 : Charset.forName(encoding);
            return new BufferedReader(new InputStreamReader(getInputStream(), charset));
        }
    }

    private static final class LimitedServletInputStream extends ServletInputStream {

        private final ServletInputStream delegate;
        private final long limit;
        private long bytesRead;

        private LimitedServletInputStream(ServletInputStream delegate, long limit) {
            this.delegate = delegate;
            this.limit = limit;
        }

        @Override
        public int read() throws IOException {
            int value = delegate.read();
            if (value != -1) {
                countBytes(1);
            }
            return value;
        }

        @Override
        public int read(byte[] buffer, int offset, int length) throws IOException {
            int count = delegate.read(buffer, offset, length);
            if (count > 0) {
                countBytes(count);
            }
            return count;
        }

        @Override
        public boolean isFinished() {
            return delegate.isFinished();
        }

        @Override
        public boolean isReady() {
            return delegate.isReady();
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            delegate.setReadListener(readListener);
        }

        private void countBytes(long additionalBytes) throws RequestBodyTooLargeException {
            bytesRead += additionalBytes;
            if (bytesRead > limit) {
                throw new RequestBodyTooLargeException();
            }
        }
    }
}
