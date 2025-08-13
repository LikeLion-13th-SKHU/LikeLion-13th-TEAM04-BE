package com.likelion.cheongsanghoe.post.application;

import com.likelion.cheongsanghoe.exception.CustomException;
import com.likelion.cheongsanghoe.exception.Response;
import com.likelion.cheongsanghoe.exception.status.ErrorStatus;
import com.likelion.cheongsanghoe.exception.status.SuccessStatus;
import com.likelion.cheongsanghoe.post.api.dto.request.PostUpdateRequestDto;
import com.likelion.cheongsanghoe.post.domain.Category;
import com.likelion.cheongsanghoe.post.domain.Post;
import com.likelion.cheongsanghoe.post.domain.repository.PostRepository;
import com.likelion.cheongsanghoe.post.api.dto.request.PostSaveRequestDto;
import com.likelion.cheongsanghoe.post.api.dto.response.PostInfoResponseDto;
import com.likelion.cheongsanghoe.post.api.dto.response.PostListResponseDto_Detail;
import com.likelion.cheongsanghoe.post.api.dto.response.PostListResponseDto_Summary;
import com.likelion.cheongsanghoe.post.api.dto.response.PostSummaryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;

    //공고 저장
    @Transactional
    public Response<Long> postSave(PostSaveRequestDto postSaveRequestDto, Long userId) {
        if(postSaveRequestDto.category() == null){
            throw new CustomException(ErrorStatus.INVALID_PARAMETER);
        }

        Post post = Post.builder()
                .title(postSaveRequestDto.title())
                .content(postSaveRequestDto.content())
                .location(postSaveRequestDto.location())
                .salary(postSaveRequestDto.salary())
                .work_time(postSaveRequestDto.work_time())
                .deadline(postSaveRequestDto.deadline())
                .count(postSaveRequestDto.count())
                .work_period(postSaveRequestDto.work_period())
                .create_at(LocalDate.now()) //실시간 서버 시간 적용
                .category(postSaveRequestDto.category())
                .build();
        postRepository.save(post);
        return Response.success(SuccessStatus.CREATED, post.getPostId());
    }

    //PostId로 공고 상세 조회
    public PostInfoResponseDto getPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RESOURCE_NOT_FOUND));
        return PostInfoResponseDto.from(post);
    }

    //공고 전체 조회(요약)
    public PostListResponseDto_Summary postFindAll() {
        List<Post> posts = postRepository.findAll();
        List<PostSummaryResponseDto> postSummaryResponseDtos = posts.stream()
                .map(PostSummaryResponseDto::from)
                .toList();
        return PostListResponseDto_Summary.from(postSummaryResponseDtos);
    }
    //공고 수정
    @Transactional
    public void postUpdate(Long postId, PostUpdateRequestDto postUpdateRequestDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorStatus.BAD_REQUEST));
        post.update(postUpdateRequestDto);
    }
    //공고 삭제
    @Transactional
    public void postDelete(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorStatus.BAD_REQUEST));
        postRepository.delete(post);
    }
    //공고 요약 전체 조회(페이지네이션)
    public Page<Post> getPostPage(Pageable pageable) {
        return postRepository.findAll(pageable);
    }
}

