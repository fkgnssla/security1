package com.cos.security1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity //시큐리티 활성화: 스프링 시큐리티 필터(SecurityConfig)가 스프링 필터체인에 등록이 된다.
public class SecurityConfig {

    //해당 메서드의 반환값을 IoC(제어의 역전)로 등록해준다.
    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

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
                .defaultSuccessUrl("/"); //로그인이 성공하면 "/" 주소로 이동(원래 가려던 페이지로 이동)
        return http.build();
    }
    /*
    기존: WebSecurityConfigurerAdapter를 상속하고 configure매소드를 오버라이딩하여 설정하는 방법
    => 현재: SecurityFilterChain을 리턴하는 메소드(filterChain)를 빈에 등록하는 방식(컴포넌트 방식으로 컨테이너가 관리)
     */
}
