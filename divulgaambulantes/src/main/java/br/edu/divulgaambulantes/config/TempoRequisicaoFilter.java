package br.edu.divulgaambulantes.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Component
public class TempoRequisicaoFilter
        extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        long inicio = System.nanoTime();

        ContentCachingResponseWrapper wrapper =
                new ContentCachingResponseWrapper(response);

        try {

            filterChain.doFilter(
                    request,
                    wrapper
            );

        } finally {

            long fim = System.nanoTime();

            double tempoMs =
                    (fim - inicio)
                    / 1_000_000.0;

            wrapper.setHeader(
                    "X-Response-Time-ms",
                    String.format("%.3f", tempoMs)
            );

            System.out.printf(
                    "[JDBC] %s %s -> %.3f ms%n",
                    request.getMethod(),
                    request.getRequestURI(),
                    tempoMs
            );

            wrapper.copyBodyToResponse();
        }
    }
}