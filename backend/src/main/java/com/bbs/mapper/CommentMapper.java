package com.bbs.mapper;

import com.bbs.entity.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 评论Mapper接口
 * <p>提供评论表的CRUD操作</p>
 */
@Mapper
public interface CommentMapper {

    /**
     * 插入新评论
     *
     * @param comment 评论实体
     */
    @Insert("INSERT INTO comments(content, post_id, user_id) VALUES(#{content}, #{postId}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Comment comment);

    /**
     * 根据帖子ID查询评论列表（关联查询作者昵称和作者删除状态）
     *
     * @param postId 帖子ID
     * @return 评论列表（包含authorName和authorDeleted）
     */
    @Select("SELECT c.*, CASE WHEN u.id IS NULL OR u.is_deleted = 1 THEN '已注销用户' ELSE u.nickname END as author_name, CASE WHEN u.id IS NULL OR u.is_deleted = 1 THEN 1 ELSE 0 END as author_deleted " +
            "FROM comments c LEFT JOIN users u ON c.user_id = u.id " +
            "WHERE c.post_id = #{postId} ORDER BY c.created_at ASC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "content", column = "content"),
            @Result(property = "postId", column = "post_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "authorName", column = "author_name"),
            @Result(property = "authorDeleted", column = "author_deleted")
    })
    List<Comment> findByPostId(Long postId);

    /**
     * 根据帖子ID删除所有评论
     *
     * @param postId 帖子ID
     */
    @Delete("DELETE FROM comments WHERE post_id = #{postId}")
    void deleteByPostId(Long postId);

    /**
     * 统计指定帖子的评论数量
     *
     * @param postId 帖子ID
     * @return 评论数量
     */
    @Select("SELECT COUNT(*) FROM comments WHERE post_id = #{postId}")
    int countByPostId(Long postId);

    /**
     * 根据板块ID删除所有帖子下的评论
     *
     * @param boardId 板块ID
     */
    @Delete("DELETE FROM comments WHERE post_id IN (SELECT id FROM posts WHERE board_id = #{boardId})")
    void deleteByBoardId(Long boardId);

    /**
     * 根据用户ID删除所有评论
     *
     * @param userId 用户ID
     */
    @Delete("DELETE FROM comments WHERE user_id = #{userId}")
    void deleteByUserId(Long userId);
}
