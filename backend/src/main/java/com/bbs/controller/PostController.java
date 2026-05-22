package com.bbs.controller;

import com.bbs.common.Result;
import com.bbs.entity.Post;
import com.bbs.service.PostService;
import com.bbs.util.UserContext;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 帖子控制器
 * <p>处理帖子的创建、查询、删除等接口</p>
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * 获取帖子列表（分页）
     * <p>可通过boardId筛选指定板块的帖子</p>
     *
     * @param boardId 板块ID（可选）
     * @param page    页码（默认1）
     * @param size    每页数量（默认20）
     * @return 包含帖子列表和分页信息的Result
     */
    @GetMapping
    public Result<Map<String, Object>> getPostList(
            @RequestParam(required = false) Long boardId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Map<String, Object> data = postService.getPostList(boardId, page, size);
        return Result.success(data);
    }

    /**
     * 获取帖子详情
     *
     * @param id 帖子ID
     * @return 帖子详情（包含作者昵称）
     */
    @GetMapping("/{id}")
    public Result<Post> getPostDetail(@PathVariable Long id) {
        Post post = postService.getPostDetail(id);
        return Result.success(post);
    }

    /**
     * 发帖
     *
     * @param request 发帖请求体（title, content, boardId）
     * @return 创建的帖子
     */
    @PostMapping
    public Result<Post> createPost(@Validated @RequestBody PostRequest request) {
        Long userId = UserContext.getUserId();
        Post post = postService.createPost(userId, request.getTitle(), request.getContent(), request.getBoardId());
        return Result.success("发帖成功", post);
    }

    /**
     * 删除帖子
     * <p>只有帖子作者或管理员可以删除</p>
     *
     * @param id 帖子ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deletePost(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        Integer role = UserContext.getRole();
        postService.deletePost(id, userId, role);
        return Result.success("帖子删除成功", null);
    }

    /**
     * 发帖请求体
     */
    @Data
    public static class PostRequest {
        /** 标题 */
        @NotBlank(message = "标题不能为空")
        private String title;

        /** 内容 */
        @NotBlank(message = "内容不能为空")
        private String content;

        /** 板块ID */
        @NotNull(message = "板块ID不能为空")
        private Long boardId;
    }
}
