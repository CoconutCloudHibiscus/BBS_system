package com.bbs.mapper;

import com.bbs.entity.Board;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 板块Mapper接口
 * <p>提供板块表的CRUD操作</p>
 */
@Mapper
public interface BoardMapper {

    /**
     * 插入新板块
     *
     * @param board 板块实体
     */
    @Insert("INSERT INTO boards(name, description) VALUES(#{name}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Board board);

    /**
     * 查询所有板块，按创建时间倒序
     *
     * @return 板块列表
     */
    @Select("SELECT * FROM boards ORDER BY created_at DESC")
    List<Board> findAll();

    /**
     * 根据ID查询板块
     *
     * @param id 板块ID
     * @return 板块实体，不存在返回null
     */
    @Select("SELECT * FROM boards WHERE id = #{id}")
    Board findById(Long id);

    /**
     * 根据名称查询板块
     *
     * @param name 板块名称
     * @return 板块实体，不存在返回null
     */
    @Select("SELECT * FROM boards WHERE name = #{name}")
    Board findByName(String name);

    /**
     * 更新板块信息
     *
     * @param board 板块实体（包含id、name、description）
     */
    @Update("UPDATE boards SET name = #{name}, description = #{description} WHERE id = #{id}")
    void update(Board board);

    /**
     * 根据ID删除板块
     *
     * @param id 板块ID
     */
    @Delete("DELETE FROM boards WHERE id = #{id}")
    void deleteById(Long id);
}
