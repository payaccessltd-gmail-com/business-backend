package com.jamub.payaccess.api.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.UserRolePermission;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.providers.TokenProvider;
import com.jamub.payaccess.api.services.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isEmpty;


//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtTokenFilter extends OncePerRequestFilter {

    private final TokenProvider jwtTokenUtil;

    private final UserService userService;

    public JwtTokenFilter(TokenProvider jwtTokenUtil, UserService userService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException, IOException {
        // Get authorization header and validate
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (isEmpty(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // Get jwt token and validate
        final String token = header.split(" ")[1].trim();
        try {
            if (!jwtTokenUtil.validateToken(token)) {
                chain.doFilter(request, response);
                return;


            }
        }
        catch(ExpiredJwtException e)
        {
            logger.info("Exception occured");
            Map<String, String> errors = new HashMap<>();
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTH_TOKEN_EXPIRED.label);
            payAccessResponse.setMessage("Token expired. Please provide a new token1");
            payAccessResponse.setResponseObject(errors);

            PrintWriter out = response.getWriter();
            response.setContentType("application/json");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
//            response.getWriter().write(new ObjectMapper().writeValueAsString(payAccessResponse));
            out.print(new ObjectMapper().writeValueAsString(payAccessResponse));
            out.flush();


            return;
        }

        String username = jwtTokenUtil.getUsernameFromToken(token);
        logger.info(username);
        User user = userService.getUserByEmailAddress(username);
        List<UserRolePermission> userRolePermissionList = userService.getPermissionsByRole(user.getUserRole().name());
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        Set authList = userRolePermissionList.stream().map(urp -> {
            return new SimpleGrantedAuthority("ROLE_" +urp.getPermission().name());
        }).collect(Collectors.toSet());

        logger.info(authList);

//        jsonObject.put("permissions", userRolePermissionList);


        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(
                user.getEmailAddress(), null, authList
        );

        logger.info(authentication.isAuthenticated());

        logger.info(authentication);

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        logger.info(2);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.info(3);
        chain.doFilter(request, response);
        logger.info(request.getParameter("userRole"));
    }

}