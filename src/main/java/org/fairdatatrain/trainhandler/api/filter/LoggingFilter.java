/**
 * The MIT License
 * Copyright © 2022 FAIR Data Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.fairdatatrain.trainhandler.api.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import static java.lang.String.format;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain fc
    ) throws IOException, ServletException {
        ThreadContext.put("ipAddress", request.getRemoteAddr());
        ThreadContext.put("requestMethod", request.getMethod());
        ThreadContext.put("requestURI", request.getRequestURI());
        ThreadContext.put("requestProtocol", request.getProtocol());
        ThreadContext.put("responseStatus", String.valueOf(response.getStatus()));
        ThreadContext.put("contentSize", response.getHeader(HttpHeaders.CONTENT_LENGTH));
        ThreadContext.put("traceId", UUID.randomUUID().toString());

        logger.info(format("%s %s", request.getMethod(), request.getRequestURL()));
        fc.doFilter(request, response);
        logger.info(format(
                "%s %s [Response: %s]",
                request.getMethod(), request.getRequestURL(), response.getStatus())
        );

        ThreadContext.clearAll();
    }

}
