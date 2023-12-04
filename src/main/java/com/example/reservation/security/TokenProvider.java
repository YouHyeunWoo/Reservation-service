package com.example.reservation.security;

import com.example.reservation.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {
    private static final String KEY_ROLES = "roles";
    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; //1시간

    private final MemberService memberService;

    @Value("{spring.jwt.secret}")
    private String secretKey;

    public String generateToken(String userName, List<String> roles) {
        //사용자의 권한 정보를 저장하기 위한 Claim
        Claims claims = Jwts.claims().setSubject(userName);
        claims.put(KEY_ROLES, roles);

        //토큰이 생성된 시간
        var now = new Date();
        //토큰 만료 시간
        var expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

        //claims 정보와 토큰만료시간을 jwt에 넣어 토큰 생성
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)  //토큰 생성 시간
                .setExpiration(expiredDate) //토큰 만료 시간
                .signWith(SignatureAlgorithm.HS512, this.secretKey) //시그니쳐알고리즘 HS512
                .compact(); //빌더 끝내기
    }

    private String getUserName(String token) {
        return this.parseClaims(token).getSubject(); //만들어지 토큰을 파싱하여 토큰의 userName을 알 수 있다
    }

    //토큰에서 유저의 이름을 가져오고 그 이름으로 member entity에서  member정보를 가져온다
    //사용자의 정보와, 사용자의 권한 정보를 가지고있는 Authentication을 반환
    public Authentication getAuthentication(String jwt) {
        UserDetails userDetails = this.memberService.loadUserByUsername(this.getUserName(jwt));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    //생성된 토큰의 유효성검사
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) return false; //토큰이 빈값이라면 유효하지않음

        var claims = this.parseClaims(token);
        return !claims.getExpiration().before(new Date()); //토큰의 만료시간이 현재시간 이전이면 false리턴
    }                                                                 //(토큰이 만료된 상황)

    //토큰이 유효한지 확인하는 메소드
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(this.secretKey) //사용한 키
                    .parseClaimsJws(token).getBody();  //만들어진 토큰
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }

    }

}
