package com.cos.security1.config;

import com.cos.security1.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity //시큐리티 활성화: 스프링 시큐리티 필터(SecurityConfig)가 스프링 필터체인에 등록이 된다.
//@EnableGlobalMethodSecurity: Controller의 메소드에 직접적으로 Role을 부여할 수 있다.
@EnableGlobalMethodSecurity(securedEnabled = true) //@Secured 활성화! => 권한 설정 어노테이션(메서드에 선언 가능)
public class SecurityConfig {

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable(); //csrf 비활성화?
        http.authorizeRequests()
                //인증만 되면 들어갈 수 있는 주소
                .antMatchers("/user/**").authenticated()
                //인증 + 'ROLE_MANAGER' 또는 'ROLE_ADMIN' 권한이 있는 사용자만 들어갈 수 있는 주소
                .antMatchers("/manager/**").access("hasAnyRole('ROLE_MANAGER','ROLE_ADMIN')")
                //인증 + 'ROLE_ADMIN' 권한이 있는 사용자만 들어갈 수 있는 주소
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll() //이외 모든 경로는 인증을 받지 않아도 누구나 접근 가능
                .and()
                .formLogin() //form login 기능 작동
                .loginPage("/loginForm") //사용자 정의 로그인 페이지
                .loginProcessingUrl("/login") //login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해줍니다.
                .defaultSuccessUrl("/") //로그인이 성공하면 "/" 주소로 이동(원래 가려던 페이지로 이동)
                .failureUrl("/loginForm/error") //로그인 실패할 경우
                .and()
                .oauth2Login() //oauth2 login 기능 작동
                .loginPage("/loginForm") //사용자 정의 로그인 페이지, 구글 로그인 완료 => 액세스 토큰 + 사용자 정보를 받는다.
                .userInfoEndpoint() //oauth2Login 성공 이후의 설정을 시작
                .userService(principalOauth2UserService); //principalOauth2UserService에서 처리하겠다.
        return http.build();
    }
    /*
    기존: WebSecurityConfigurerAdapter를 상속하고 configure매소드를 오버라이딩하여 설정하는 방법
    => 현재: SecurityFilterChain을 리턴하는 메소드(filterChain)를 빈에 등록하는 방식(컴포넌트 방식으로 컨테이너가 관리)
     */
}
