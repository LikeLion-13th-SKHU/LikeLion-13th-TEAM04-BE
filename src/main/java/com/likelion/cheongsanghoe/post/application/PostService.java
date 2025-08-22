package com.likelion.cheongsanghoe.post.application;

import com.likelion.cheongsanghoe.auth.security.JwtTokenProvider;
import com.likelion.cheongsanghoe.exception.CustomException;
import com.likelion.cheongsanghoe.exception.Response;
import com.likelion.cheongsanghoe.exception.status.ErrorStatus;
import com.likelion.cheongsanghoe.exception.status.SuccessStatus;
import com.likelion.cheongsanghoe.mainpage.api.dto.response.MainCategoryResponseDto;
import com.likelion.cheongsanghoe.post.api.dto.request.PostUpdateRequestDto;
import com.likelion.cheongsanghoe.post.api.dto.response.*;
import com.likelion.cheongsanghoe.post.domain.Category;
import com.likelion.cheongsanghoe.post.domain.Post;
import com.likelion.cheongsanghoe.post.domain.repository.PostRepository;
import com.likelion.cheongsanghoe.post.api.dto.request.PostSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final JwtTokenProvider jwtTokenProvider;

    //공고 저장
    @Transactional
    public Response<Long> postSave(PostSaveRequestDto postSaveRequestDto, String token) {
        if(postSaveRequestDto.category() == null){
            throw new CustomException(ErrorStatus.POST_CREATE_FAILED);
        }
        //토큰 유효성 검사
        String userEmail = jwtTokenProvider.getEmailFromToken(token);
        if(userEmail == null){
            throw new CustomException(ErrorStatus.INVALID_TOKEN);
        }
        //이미지 업로드 처리
        String imgaeUrl = null;
        if(postSaveRequestDto.image() != null && !postSaveRequestDto.image().isEmpty()){
            try{
                String originalFilename = postSaveRequestDto.image().getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));//확장자 추출
                String fileName = UUID.randomUUID().toString() + extension; //UUID + 확장자

                //업로드 경로
                String uploadDir = System.getProperty("user.dir") + "/uploads";
                File dir = new File(uploadDir);
                if (!dir.exists()){
                    dir.mkdirs();
                }
                //저장
                File dest = new File(uploadDir + "/" + fileName);
                postSaveRequestDto.image().transferTo(dest);
                //DB에 URL형식 저장
                imgaeUrl = "/uploads/" + fileName;

            } catch (IOException e) {
                throw new CustomException(ErrorStatus.IMAGE_UPLOAD_FAILED);
            }
        }

        Post post = Post.builder()
                .title(postSaveRequestDto.title())
                .content(postSaveRequestDto.content())
                .location(postSaveRequestDto.location())
                .salary(postSaveRequestDto.salary())
                .work_time(postSaveRequestDto.work_time())
                .tags(postSaveRequestDto.tags())
                .deadline(postSaveRequestDto.deadline())
                .num(postSaveRequestDto.num())
                .work_period(postSaveRequestDto.work_period())
                .createAt(LocalDate.now()) //실시간 서버 시간 적용
                .category(postSaveRequestDto.category())
                .imageUrl(imgaeUrl)
                .build();
        postRepository.save(post);
        return Response.success(SuccessStatus.POST_CREATED, post.getPostId());
    }

    //PostId로 공고 상세 조회
    public PostInfoResponseDto getPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorStatus.POST_NOT_FOUND));
        return PostInfoResponseDto.from(post);
    }

    //공고 수정
    @Transactional
    public void postUpdate(Long postId, PostUpdateRequestDto postUpdateRequestDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorStatus.POST_NOT_FOUND));
        post.update(postUpdateRequestDto);
    }
    //공고 삭제
    @Transactional
    public void postDelete(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorStatus.POST_NOT_FOUND));
        postRepository.delete(post);
    }
    //공고 요약 전체 조회(페이지네이션)
    public Page<Post> getPostPage(Pageable pageable) {
        return postRepository.findAll(pageable);
    }
    //카테고리별 공고 개수 반환 메서드
    public List<CategoryCountDto> getCategoryCount(){
        Category[] allCategories = Category.values();

        return java.util.Arrays.stream(allCategories)
                .map(category -> {
                            long count = postRepository.countByCategory(category);
                            return CategoryCountDto.builder()
                                    .category(category)
                                    .count(count)
                                    .build();
                        })
                .sorted((c1, c2) -> Long.compare(c2.count(), c1.count()))
                .toList();
    }
    //메인페이지 인기 카테고리 메서드
    public List<MainCategoryResponseDto> getMainCategory(int limit){
        List<CategoryCountDto> categoryCount = getCategoryCount();//전체 개수

        return categoryCount.stream()
                .limit(limit) //상위 limit만큼 자른다
                .map(categoryCountDto -> MainCategoryResponseDto.from(categoryCountDto.category()))
                .collect(Collectors.toList());
    }
    //메인페이지 최신 공고 메서드
    public List<PostSummaryResponseDto> getMainPost(int limit){
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createAt"));

        Page<Post> recentPost = postRepository.findAll(pageable);

        //recentPost에서 데이터리스트를 뽑아 PostSummaryResponseDto로 변환
        return recentPost.getContent().stream()
                .map(PostSummaryResponseDto::from)
                .collect(Collectors.toList());
    }
}

