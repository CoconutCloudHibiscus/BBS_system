package com.bbs.mapper;

import com.bbs.entity.Post;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 帖子Mapper接口
 * <p>提供帖子表的CRUD操作，包含分页查询和关联查询</p>
 */
@Mapper
public interface PostMapper {

    /**
     * 插入新帖子
     *
     * @param post 帖子实体
     */
    @Insert("INSERT INTO posts(title, content, summary, user_id, board_id) VALUES(#{title}, #{content}, #{summary}, #{userId}, #{boardId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Post post);

    /**
     * 根据ID查询帖子详情（关联查询作者昵称）
     *
     * @param id 帖子ID
     * @return 帖子实体（包含authorName）
     */
    @Select("SELECT p.*, CASE WHEN u.id IS NULL OR u.is_deleted = 1 THEN 1 ELSE 0 END as author_deleted, CASE WHEN u.id IS NULL OR u.is_deleted = 1 THEN '已注销用户' ELSE u.nickname END as author_name FROM posts p LEFT JOIN users u ON p.user_id = u.id WHERE p.id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "content"),
            @Result(property = "summary", column = "summary"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "boardId", column = "board_id"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "authorName", column = "author_name"),
            @Result(property = "authorDeleted", column = "author_deleted")
    })
    Post findById(Long id);

    /**
     * 根据板块ID分页查询帖子列表（关联查询作者昵称）
     * <p>boardId为null时查询所有帖子</p>
     *
     * @param boardId 板块ID（可选）
     * @param offset  偏移量
     * @param limit   每页数量
     * @return 帖子列表（包含authorName）
     */
    @Select({"<script>",
            "SELECT p.*, CASE WHEN u.id IS NULL OR u.is_deleted = 1 THEN 1 ELSE 0 END as author_deleted, CASE WHEN u.id IS NULL OR u.is_deleted = 1 THEN '已注销用户' ELSE u.nickname END as author_name FROM posts p LEFT JOIN users u ON p.user_id = u.id",
            "<if test='boardId != null'> WHERE p.board_id = #{boardId}</if>",
            " ORDER BY p.created_at DESC LIMIT #{offset}, #{limit}",
            "</script>"})
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "content"),
            @Result(property = "summary", column = "summary"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "boardId", column = "board_id"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "authorName", column = "author_name"),
            @Result(property = "authorDeleted", column = "author_deleted")
    })
    List<Post> findByBoardId(@Param("boardId") Long boardId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 统计指定板块的帖子数量
     * <p>boardId为null时统计所有帖子</p>
     *
     * @param boardId 板块ID（可选）
     * @return 帖子数量
     */
    @Select({"<script>",
            "SELECT COUNT(*) FROM posts p",
            "<if test='boardId != null'> WHERE p.board_id = #{boardId}</if>",
            "</script>"})
    int countByBoardId(@Param("boardId") Long boardId);

    /**
     * 根据ID删除帖子
     *
     * @param id 帖子ID
     */
    @Delete("DELETE FROM posts WHERE id = #{id}")
    void deleteById(Long id);

    /**
     * 根据用户ID删除所有帖子
     *
     * @param userId 用户ID
     */
    @Delete("DELETE FROM posts WHERE user_id = #{userId}")
    void deleteByUserId(Long userId);

    /**
     * 根据板块ID删除所有帖子
     *
     * @param boardId 板块ID
     */
    @Delete("DELETE FROM posts WHERE board_id = #{boardId}")
    void deleteByBoardId(Long boardId);
}
