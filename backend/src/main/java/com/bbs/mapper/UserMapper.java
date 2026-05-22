package com.bbs.mapper;

import com.bbs.entity.User;
import org.apache.ibatis.annotations.*;

/**
 * 用户Mapper接口
 * <p>提供用户表的CRUD操作</p>
 */
@Mapper
public interface UserMapper {

    /**
     * 插入新用户
     *
     * @param user 用户实体
     */
    @Insert("INSERT INTO users(account, password, nickname, role, is_deleted) VALUES(#{account}, #{password}, #{nickname}, #{role}, #{isDeleted})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    /**
     * 根据账号查询用户（未删除）
     *
     * @param account 账号
     * @return 用户实体，不存在返回null
     */
    @Select("SELECT * FROM users WHERE account = #{account} AND is_deleted = 0")
    User findByAccount(String account);

    /**
     * 根据账号查询用户（包含已删除的，用于注册时检查账号是否已占用）
     *
     * @param account 账号
     * @return 用户实体，不存在返回null
     */
    @Select("SELECT * FROM users WHERE account = #{account}")
    User findByAccountIncludeDeleted(String account);

    /**
     * 根据ID查询用户（未删除）
     *
     * @param id 用户ID
     * @return 用户实体，不存在返回null
     */
    @Select("SELECT * FROM users WHERE id = #{id} AND is_deleted = 0")
    User findById(Long id);

    /**
     * 更新用户昵称
     *
     * @param id       用户ID
     * @param nickname 新昵称
     */
    @Update("UPDATE users SET nickname = #{nickname} WHERE id = #{id} AND is_deleted = 0")
    void updateNickname(@Param("id") Long id, @Param("nickname") String nickname);

    /**
     * 更新用户密码
     *
     * @param id       用户ID
     * @param password 新密码（BCrypt哈希）
     */
    @Update("UPDATE users SET password = #{password} WHERE id = #{id}")
    void updatePassword(@Param("id") Long id, @Param("password") String password);

    /**
     * 硬删除用户（物理删除）
     *
     * @param id 用户ID
     */
    @Delete("DELETE FROM users WHERE id = #{id}")
    void hardDeleteById(Long id);
}
