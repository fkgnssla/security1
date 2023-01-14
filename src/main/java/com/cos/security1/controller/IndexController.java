package com.cos.security1.controller;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.Board;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import com.cos.security1.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class IndexController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BoardService boardService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/test/login")
    @ResponseBody
    public String testLogin(Authentication authentication,
                            @AuthenticationPrincipal PrincipalDetails userDetails) { //Form 로그인
        System.out.println("/test/login======================");
        //1. 다운 캐스팅을 거쳐서 User 객체 찾기
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("authentication: " + principalDetails.getUser());

        //2. @AuthenticationPrincipal을 통해서 User 객체 찾기
        System.out.println("userDetails: " + userDetails.getUser());
        return "세션 정보 확인하기";
    }

    @GetMapping("/test/oauth/login")
    @ResponseBody
    public String testOAuthLogin(Authentication authentication,
                                 @AuthenticationPrincipal OAuth2User oauth) { //OAuth 로그인
        System.out.println("/test/oauth/login======================");
        //1. 다운 캐스팅을 거쳐서 Attributes 찾기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("authentication: " + oAuth2User.getAttributes());

        //2. @AuthenticationPrincipal을 통해서 Attributes 찾기
        System.out.println("oauth: " + oauth.getAttributes());
        return "OAuth 세션 정보 확인하기";
    }

    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal PrincipalDetails userDetails) {
        List<Board> boardAll = boardService.findAll();
        model.addAttribute("boards", boardAll);

        if(userDetails != null) { //로그인 했을 경우
            model.addAttribute("login", "true");
            model.addAttribute("nickname", userDetails.getUsername());
        } else model.addAttribute("login", "false");

        return "index";
    }

    //Form 로그인 또는 OAuth 로그인을 할 지 모르니 PrincipalDetails(UserDetails, OAuth2User 구현)로 받는다. (분기 할 필요가 없어진다.)
    @GetMapping("/user")
    @ResponseBody
    public String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println("principalDetails: " + principalDetails.getUser());
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
    public String loginForm(@AuthenticationPrincipal PrincipalDetails userDetails) {
        //로그인 되어있을 때, 로그인 폼으로 이동하는 경우 메인 페이지로 이동
        if(userDetails != null) {
            System.out.println("현재 사용자: " + userDetails.getUser());
            return "redirect:/";
        }
        return "loginForm";
    }

    @GetMapping("/loginForm/error")
    public String loginFormError(Model model) {
        model.addAttribute("loginErrorMsg","아이디 또는 비밀번호를 확인해주세요.");
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
