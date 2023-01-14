package com.cos.security1.config.auth;

import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//시큐리티 설정(SecurityConfig)에서 loginProcessUrl("/login")으로 걸어뒀기 때문에 /login 요청이오면 자동으로
//UserDetailsService 타입으로 IoC 되어있는 loadUserByUsername 함수가 수행 => "약속"
//함수 종료시 @AuthenticationPrncipal 어노테이션이 만들어진다.
@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    //Security session(내부 Authentication(내부 UserDetails(내부 User)))
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //username에 넘어온 데이터는 loginForm의 username 입력폼에 작성된 문자열이 넘어온다.
        User userEntity = userRepository.findByUsername(username);
        if(userEntity != null) {
            return new PrincipalDetails(userEntity); //UserDetails에 user 정보 저장하여 Authentication에 반환
        }
        throw new UsernameNotFoundException("아이디가 존재하지 않습니다.");
//        return null;
    }
}
