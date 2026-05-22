package com.bbs.controller;

import com.bbs.common.Result;
import com.bbs.entity.Comment;
import com.bbs.service.CommentService;
import com.bbs.util.UserContext;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 * <p>处理评论的创建和查询接口</p>
 */
@RestController
@RequestMapping("/api/posts")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 获取指定帖子的评论列表
     *
     * @param id 帖子ID
     * @return 评论列表（包含作者昵称和作者删除状态）
     */
    @GetMapping("/{id}/comments")
    public Result<List<Comment>> getComments(@PathVariable Long id) {
        List<Comment> comments = commentService.getCommentsByPostId(id);
        return Result.success(comments);
    }

    /**
     * 发表评论
     *
     * @param id      帖子ID
     * @param request 评论请求体（content）
     * @return 创建的评论
     */
    @PostMapping("/{id}/comments")
    public Result<Comment> createComment(@PathVariable Long id, @Validated @RequestBody CommentRequest request) {
        Long userId = UserContext.getUserId();
        Comment comment = commentService.createComment(userId, id, request.getContent());
        return Result.success("评论成功", comment);
    }

    /**
     * 评论请求体
     */
    @Data
    public static class CommentRequest {
        /** 评论内容 */
        @NotBlank(message = "评论内容不能为空")
        private String content;
    }
}
