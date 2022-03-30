package com.ladder.perfumism.article.service;

import com.ladder.perfumism.article.controller.dto.request.ArticleCreateRequest;
import com.ladder.perfumism.article.controller.dto.response.ArticleReadDetailResponse;
import com.ladder.perfumism.article.controller.dto.response.ArticleReadListResponse;
import com.ladder.perfumism.article.domain.Article;
import com.ladder.perfumism.article.domain.ArticleImage;
import com.ladder.perfumism.article.domain.ArticleImageRepository;
import com.ladder.perfumism.article.domain.ArticleRepository;
import com.ladder.perfumism.article.domain.ArticleSubject;
import com.ladder.perfumism.comment.domain.CommentRepository;
import com.ladder.perfumism.global.exception.BusinessException;
import com.ladder.perfumism.global.exception.ErrorCode;
import com.ladder.perfumism.image.ImageUploader;
import com.ladder.perfumism.member.domain.Member;
import com.ladder.perfumism.member.domain.MemberRepository;
import com.ladder.perfumism.member.service.MemberService;
import com.ladder.perfumism.vote.domain.Vote;
import com.ladder.perfumism.vote.domain.VoteItemRepository;
import com.ladder.perfumism.vote.domain.VoteMemberRepository;
import com.ladder.perfumism.vote.domain.VoteRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MemberService memberService;
    private final ImageUploader imageUploader;
    private final ArticleImageRepository articleImageRepository;
    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;
    private final VoteItemRepository voteItemRepository;
    private final VoteMemberRepository voteMemberRepository;

    public ArticleService(ArticleRepository articleRepository, MemberService memberService,
        ImageUploader imageUploader, ArticleImageRepository articleImageRepository,
        CommentRepository commentRepository, VoteRepository voteRepository,
        VoteItemRepository voteItemRepository, VoteMemberRepository voteMemberRepository){

        this.articleRepository = articleRepository;
        this.memberService = memberService;
        this.imageUploader = imageUploader;
        this.articleImageRepository = articleImageRepository;

        this.commentRepository = commentRepository;
        this.voteRepository = voteRepository;
        this.voteItemRepository = voteItemRepository;
        this.voteMemberRepository = voteMemberRepository;

    }

    public void ARTICLE_IS_NOT_YOURS_FUNC(String email, Article article){
        if(!article.getMember().getEmail().equals(email)){
            throw new BusinessException(ErrorCode.ARTICLE_IS_NOT_YOURS);
        }
    }

    public Article ARTICLE_NOT_FOUND_FUNC(Long articleId){
        return articleRepository.findById(articleId)
            .orElseThrow(()-> new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));
    }

    @Transactional
    public Long createArticle(String email, ArticleCreateRequest request) {

        Member member = memberService.findByEmail(email);

        Article article = Article.builder()
            .member(member)
            .title(request.getTitle())
            .content(request.getContent())
            .subject(request.getSubject())
            .build();

        articleRepository.save(article);

        return article.getId();
    }

    @Transactional
    public ArticleReadListResponse showArticleList(String email,Pageable pageable, ArticleSubject subject) {

        Member member = memberService.findByEmail(email);

        Page<Article> articleList;
        if (subject != null){
            articleList = articleRepository.findBySubject(subject,pageable);

        } else{
            articleList = articleRepository.findAll(pageable);
        }

        return ArticleReadListResponse.from(articleList);
    }

    @Transactional
    public ArticleReadDetailResponse showArticleDetail(String email, Long articleId) {

        Member member = memberService.findByEmail(email);
        Article article = ARTICLE_NOT_FOUND_FUNC(articleId);

        List<ArticleImage> articleImage = articleImageRepository.findByArticle(article);

        return ArticleReadDetailResponse.from(article, articleImage);



    }

    @Transactional
    public void updateArticle(String email, Long articleId, ArticleCreateRequest request) {

        Member member = memberService.findByEmail(email);
        Article article = ARTICLE_NOT_FOUND_FUNC(articleId);
        ARTICLE_IS_NOT_YOURS_FUNC(email, article);

        article.changeSubject(request.getSubject());
        article.changeTitle(request.getTitle());
        article.changeContent(request.getContent());

    }

    @Transactional
    public void removeArticle(String email,Long articleId) {

        Member member = memberService.findByEmail(email);
        Article article = ARTICLE_NOT_FOUND_FUNC(articleId);
        ARTICLE_IS_NOT_YOURS_FUNC(email, article);

        if(commentRepository.existsByArticle(article)){
            commentRepository.updateDeletedAtByArticle(articleId);
        }

        if(article.getVote_exist()){

            Optional<Vote> vote = voteRepository.findByArticle(article);

            voteRepository.updateDeletedAtByArticle(articleId);
            voteItemRepository.updateDeletedAtByVote(vote.get().getId());
            voteMemberRepository.updateDeletedAtByVote(vote.get().getId());
        }



        article.saveDeletedTime();
    }

    @Transactional
    public void createArticleImage(String email, Long articleId, List<MultipartFile> files) {

        Member member = memberService.findByEmail(email);
        Article article = ARTICLE_NOT_FOUND_FUNC(articleId);

        for(MultipartFile file: files){

            String url = null;
            try {
                url = imageUploader.upload(file, "article");

                ArticleImage articleImage = ArticleImage.builder()
                    .article(article)
                    .url(url)
                    .build();

                articleImageRepository.save(articleImage);

            } catch (IOException e) {

                throw new BusinessException(ErrorCode.GLOBAL_ILLEGAL_ERROR);
            }



        }


    }
}
