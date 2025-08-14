package com.likelion.cheongsanghoe.post.domain;

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
    private int salary;
    private String work_time;
    private String deadline;
    private int num;
    private String work_period;
    private LocalDate createAt;

    @Enumerated(EnumType.STRING)
    @Column(length =20)
    private Category category;

    @Builder
    private Post(String title, String content, String location, int salary, String work_time, String deadline,
                 int num, String work_period, LocalDate createAt, Category category ) {
        this.title = title;
        this.content = content;
        this.location = location;
        this.salary = salary;
        this.work_time = work_time;
        this.deadline = deadline;
        this.num = num;
        this.work_period = work_period;
        this.createAt = createAt;
        this.category = category;

    }
    //공고 수정
    public void update(PostUpdateRequestDto postUpdateRequestDto) {
        this.title = postUpdateRequestDto.title();
        this.salary = postUpdateRequestDto.salary();
        this.num = postUpdateRequestDto.num();
        this.work_time = postUpdateRequestDto.work_time();
        this.work_period = postUpdateRequestDto.work_period();
        this.content = postUpdateRequestDto.content();
        this.deadline = postUpdateRequestDto.deadline();
    }


}
