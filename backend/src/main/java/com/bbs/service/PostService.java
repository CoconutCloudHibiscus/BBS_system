package com.bbs.service;

import com.bbs.common.BusinessException;
import com.bbs.entity.Board;
import com.bbs.entity.Post;
import com.bbs.mapper.BoardMapper;
import com.bbs.mapper.CommentMapper;
import com.bbs.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 帖子服务类
 * <p>处理帖子的创建、查询、删除等业务逻辑</p>
 */
@Service
public class PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private BoardMapper boardMapper;

    @Autowired
    private CommentMapper commentMapper;

    /**
     * 创建帖子
     * <p>自动生成摘要（取内容前200字符），验证板块是否存在</p>
     *
     * @param userId  发帖用户ID
     * @param title   标题
     * @param content 内容
     * @param boardId 板块ID
     * @return 创建的帖子实体
     */
    public Post createPost(Long userId, String title, String content, Long boardId) {
        // 验证板块是否存在
        Board board = boardMapper.findById(boardId);
        if (board == null) {
            throw new BusinessException("板块不存在");
        }

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        // 自动生成摘要，取内容前200字符
        post.setSummary(content.length() > 200 ? content.substring(0, 200) + "..." : content);
        post.setUserId(userId);
        post.setBoardId(boardId);

        postMapper.insert(post);
        return post;
    }

    /**
     * 获取帖子列表（分页）
     *
     * @param boardId 板块ID（可选，为null时查询所有帖子）
     * @param page    页码（从1开始）
     * @param size    每页数量
     * @return 包含帖子列表和分页信息的Map
     */
    public Map<String, Object> getPostList(Long boardId, int page, int size) {
        int offset = (page - 1) * size;
        List<Post> posts = postMapper.findByBoardId(boardId, offset, size);
        int total = postMapper.countByBoardId(boardId);

        Map<String, Object> result = new HashMap<>();
        result.put("posts", posts);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", (int) Math.ceil((double) total / size));
        return result;
    }

    /**
     * 获取帖子详情
     *
     * @param id 帖子ID
     * @return 帖子实体（包含作者昵称）
     */
    public Post getPostDetail(Long id) {
        Post post = postMapper.findById(id);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        return post;
    }

    /**
     * 删除帖子
     * <p>验证权限：只有帖子作者或管理员可以删除，级联删除帖子下的所有评论</p>
     *
     * @param id     帖子ID
     * @param userId 当前用户ID
     * @param role   当前用户角色
     */
    @Transactional
    public void deletePost(Long id, Long userId, Integer role) {
        Post post = postMapper.findById(id);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }

        // 权限验证：只有帖子作者或管理员可以删除
        if (!post.getUserId().equals(userId) && !role.equals(1)) {
            throw new BusinessException("无权删除此帖子");
        }

        // 级联删除帖子下的所有评论
        commentMapper.deleteByPostId(id);

        // 删除帖子
        postMapper.deleteById(id);
    }
}
