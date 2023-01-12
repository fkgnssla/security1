package com.cos.security1.controller;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.Board;
import com.cos.security1.model.User;
import com.cos.security1.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/create")
    public String createBoardForm() {
        return "createBoardForm";
    }

    @PostMapping("/create")
    public String createBoard(@ModelAttribute Board board,
                              @AuthenticationPrincipal PrincipalDetails userDetails) {
        User loginUser = userDetails.getUser();
        board.setCreatedBy(loginUser.getUsername());
        board.createBoard(loginUser);

        boardService.save(board);
        return "redirect:/";
    }

    @GetMapping("/visit/{boardId}")
    public String visitBoard(@PathVariable Long boardId, Model model) {
        Board board = boardService.findById(boardId);
        model.addAttribute("board", board);

        return "boardContent";
    }

    @GetMapping("/edit/{boardId}")
    public String editForm(@PathVariable Long boardId, @ModelAttribute Board board, Model model) {
        //Board board = boardService.findById(boardId); => 쿼리 1번 줄임
        model.addAttribute("boardId", boardId);
        //model.addAttribute("board", board);

        return "editBoardForm";
    }

    @PostMapping("/edit/{boardId}")
    public  String edit(@PathVariable Long boardId, @ModelAttribute Board board) {
        boardService.update(boardId, board);

        return "redirect:/";
    }
}
