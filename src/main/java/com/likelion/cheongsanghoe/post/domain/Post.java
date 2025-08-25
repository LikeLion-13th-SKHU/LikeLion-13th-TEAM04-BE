package com.likelion.cheongsanghoe.post.domain;

import com.likelion.cheongsanghoe.auth.domain.User;
import com.likelion.cheongsanghoe.post.api.dto.request.PostUpdateRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    private String title;
    private String content;
    private String location;
    private Integer salary;
    private String work_time;
    private String tags;
    private String deadline;
    private int num;
    private String work_period;
    private String imageUrl;
    private LocalDate createAt;

    @Enumerated(EnumType.STRING)
    @Column(length =20)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)//외래키
    private User user;

    @Builder
    private Post(String title, String content, String location, Integer salary, String work_time, String tags,String deadline,
                 int num, String work_period, LocalDate createAt, Category category, String imageUrl, User user) {
        this.title = title;
        this.content = content;
        this.location = location;
        this.salary = salary;
        this.work_time = work_time;
        this.tags = tags;
        this.deadline = deadline;
        this.num = num;
        this.work_period = work_period;
        this.createAt = createAt;
        this.category = category;
        this.imageUrl = imageUrl;
        this.user = user;

    }
    //공고 수정
    public void update(String title, String content, String location, int salary,
                       String work_time, String tags, String deadline, int num,
                       String work_period, Category category, String imageUrl) {
        this.title = title;
        this.content = content;
        this.location = location;
        this.salary = salary;
        this.work_time = work_time;
        this.tags = tags;
        this.deadline = deadline;
        this.num = num;
        this.work_period = work_period;
        this.category = category;
        this.imageUrl = imageUrl;
        this.createAt = LocalDate.now();

    }


}
