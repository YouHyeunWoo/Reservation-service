package com.example.reservation.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//사용자가 api를 호출하면 바로 컨트롤러로 요청이 들어오는것이 아님
//필터 >> 서블릿 >> 인터셉터 >> AOP >> 컨트롤러
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { //한 요청당 한번 필터를 실행하는 클래스
    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer "; //인증타입을 나타내기 위해 사용, JWT토큰은 토큰 key로 Bearer이 붙는다

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //request로부터 토큰을 꺼내오기
        String token = this.resolveTokenFromRequest(request);

        if(StringUtils.hasText(token) && this.tokenProvider.validateToken(token)){ //토큰의 유효성 검사(토큰이 비어있지 않고, 만료되지 않았다
            Authentication authentication = this.tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        //필터가 연속적으로 실행될 수 있도록 하는 필터체인
        filterChain.doFilter(request, response);
    }

    private String resolveTokenFromRequest(HttpServletRequest request){
        String token = request.getHeader(TOKEN_HEADER); //이 키에 해당하는 토큰이 나옴
        if(!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)){ //토큰이 비어있지않고, TOKEN_PREFIX로 시작한다면
            //유효한지는 아직 모르나 토큰의 형태를 가지고 있다
            return token.substring(TOKEN_PREFIX.length()); //TOKEN_PREFIX 이후의 문자열 반환
        }
        return token;
    }
}
