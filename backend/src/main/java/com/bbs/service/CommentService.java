package com.bbs.service;

import com.bbs.common.BusinessException;
import com.bbs.entity.Comment;
import com.bbs.entity.Post;
import com.bbs.mapper.CommentMapper;
import com.bbs.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论服务类
 * <p>处理评论的创建和查询业务逻辑</p>
 */
@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    /**
     * 发表评论
     * <p>验证帖子是否存在，创建评论</p>
     *
     * @param userId  评论用户ID
     * @param postId  帖子ID
     * @param content 评论内容
     * @return 创建的评论实体
     */
    public Comment createComment(Long userId, Long postId, String content) {
        // 验证帖子是否存在
        Post post = postMapper.findById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPostId(postId);
        comment.setUserId(userId);

        commentMapper.insert(comment);
        return comment;
    }

    /**
     * 获取指定帖子的评论列表
     *
     * @param postId 帖子ID
     * @return 评论列表（包含作者昵称和作者删除状态）
     */
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentMapper.findByPostId(postId);
    }
}
