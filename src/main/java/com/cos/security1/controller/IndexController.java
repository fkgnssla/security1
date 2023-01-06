package com.cos.security1.controller;

import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
public class IndexController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/user")
    @ResponseBody
    public String user() {
        return "user";
    }

    @GetMapping("/admin")
    @ResponseBody
    public String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    @ResponseBody
    public String manager() {
        return "manager";
    }

    //스프링 시큐리티가 해당 주소를 낚아채버린다. => SecurityConfig 생성 후 스프링 시큐리티 동작X(디폴트 시큐리티?)
    @GetMapping("/loginForm")
    public String loginForm(HttpSession session) {
        //로그인 되어있을 때, 로그인 폼으로 이동하는 경우 메인 페이지로 이동
        if (session.getAttribute("SPRING_SECURITY_CONTEXT") != null) {
            return "redirect:/";
        }
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(@ModelAttribute User user) {
        System.out.println("user = " + user);
        user.setRole("ROLE_USER");
        String rawPassword = user.getPassword(); //입력받은 pw
        String encPassword = bCryptPasswordEncoder.encode(rawPassword); //인코딩한 pw
        user.setPassword(encPassword);

        //DB에 잘 저장되지만, PW가 암호화 되어있지 않기에 시큐리티로 로그인 불가 =>
        //bCryptPasswordEncoder로 인코딩해서 시큐리티로 로그인 가능
        userRepository.save(user);
        return "redirect:/loginForm";
    }

    @GetMapping("/data")
    @ResponseBody
    @Secured("ROLE_ADMIN") //"ROLE_ADMIN" 권한인 사용자만 접근 가능
    public String data() {
        return "데이터 정보";
    }
}
