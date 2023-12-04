package com.example.reservation.service;

import com.example.reservation.domain.MemberEntity;
import com.example.reservation.exception.impl.AlreadyExistsUserException;
import com.example.reservation.exception.impl.NoMatchPasswordException;
import com.example.reservation.exception.impl.NoExistsUsernameException;
import com.example.reservation.model.Auth;
import com.example.reservation.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.memberRepository.findByUserName(username)
                .orElseThrow(NoExistsUsernameException::new);
    }

    //회원 가입 메소드
    //이미 등록이 되어있는 아이디일경우 예외처리
    public MemberEntity register(Auth.signUp member) {
        boolean exists = this.memberRepository.existsByUserName(member.getUserName());
        if (exists) {
            throw new AlreadyExistsUserException();
        }
        //password암호화
        member.setPassword(this.passwordEncoder.encode(member.getPassword()));
        return this.memberRepository.save(member.toEntity());
    }

    //로그인 검증 메소드
    //아이디와 비밀번호 입력 >> 데이터베이스에 등록되어있는 암호화(인코딩)된 비밀번호와 매칭
    public MemberEntity signIn(Auth.signIn member) {
        MemberEntity memberEntity = this.memberRepository.findByUserName(member.getUserName())
                .orElseThrow(NoExistsUsernameException::new);
        //passwordEncoder.matches(입력한 패스워드, 인코딩된패스워드)
        if (!this.passwordEncoder.matches(member.getPassword(), memberEntity.getPassword())) {
            throw new NoMatchPasswordException();
        }
        return memberEntity;
    }
}
