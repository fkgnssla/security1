package com.cos.security1.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@ToString(exclude = "user")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column
    private String createdBy;

    @Column
    private String title;

    @Column
    private String content;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Board(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.createdBy = user.getUsername();
        createBoard(user);
    }

    //연관관계 편의 메서드
    public void createBoard(User user) {
        this.user = user;
        user.getBoards().add(this);
    }

    public void edit(Board board) {
        this.title = board.getTitle();
        this.content = board.getContent();
    }
}
