package com.example.reservation.model;

import com.example.reservation.domain.MemberEntity;
import lombok.Data;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public class Auth {

    @Data //로그인할때 쓸 클래스
    public static class signIn {
        private String userName;
        private String password;
    }


    @Data //회원가입할때 쓸 클래스
    public static class signUp {
        private String userName;
        private String password;
        private List<String> roles;

        //MemberEntity로 변환
        public MemberEntity toEntity() {
            return MemberEntity.builder()
                    .userName(this.userName)
                    .password(this.password)
                    .registerDate(LocalDateTime.now())
                    .roles(this.roles)
                    .build();
        }
    }


}
