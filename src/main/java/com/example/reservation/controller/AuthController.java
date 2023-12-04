package com.example.reservation.controller;

import com.example.reservation.domain.MemberEntity;
import com.example.reservation.model.Auth;
import com.example.reservation.security.TokenProvider;
import com.example.reservation.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;

    private final TokenProvider tokenProvider;

    //회원 가입 >> 모든 이용자에게 허가
    @PostMapping("/auth/signup")
    public ResponseEntity<?> singUp(@RequestBody Auth.signUp request) {
        MemberEntity memberEntity = this.memberService.register(request);
        return ResponseEntity.ok(memberEntity);
    }

    //로그인 >> 모든 이용자에게 허가
    @PostMapping("/auth/signin")
    public ResponseEntity<?> singIn(@RequestBody Auth.signIn request) {
        MemberEntity memberEntity = this.memberService.signIn(request);
        //memberEntity를 기준으로 토큰 생성
        String token = tokenProvider
                .generateToken(memberEntity.getUsername(), memberEntity.getRoles());
        return ResponseEntity.ok(token);
    }
}
