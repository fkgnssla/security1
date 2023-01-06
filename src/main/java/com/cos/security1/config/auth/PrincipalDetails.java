package com.cos.security1.config.auth;

import com.cos.security1.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

//시큐리티가 "/login" 주소 요청이 오면 낚아채서 로그인을 진행
//로그인 진행이 완료가 되면 시큐리티 session을 만들어 준다. => Security ContextHolder
//시큐리티 session에 들어갈 수 있는 객체 => Authentication 타입 객체
//Authentication 안에 User 정보가 있어야 됨
//User 객체 타입 => UserDetails 타입 객체
//Security session에 들어갈 수 있는 객체 => Authentication
//Authentication에서 User 정보를 저장할 때 UserDetails 타입(PrincipalDetails)이어야 한다.
public class PrincipalDetails implements UserDetails {

    private User user;

    public PrincipalDetails(User user) {
        this.user = user;
    }

    //해당 user의 권한을 반환하는 메서드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        //만약 1년동안 로그인을 하지 않은 계정을 휴먼계정으로 처리한다면
        //User의 속성에 loginDate 추가
        //현재시간 - 최근 로그인 시간(loginDate) => 1년 초과하면 return false;
        return true;
    }
}
