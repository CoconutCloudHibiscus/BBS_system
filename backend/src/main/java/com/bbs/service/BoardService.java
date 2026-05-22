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

import java.util.List;

/**
 * 板块服务类
 * <p>处理板块的创建、修改、删除等业务逻辑</p>
 */
@Service
public class BoardService {

    @Autowired
    private BoardMapper boardMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CommentMapper commentMapper;

    /**
     * 创建板块
     * <p>检查板块名称是否重复，创建新板块</p>
     *
     * @param name        板块名称
     * @param description 板块描述
     * @return 创建的板块实体
     */
    public Board createBoard(String name, String description) {
        // 检查名称是否已存在
        Board existingBoard = boardMapper.findByName(name);
        if (existingBoard != null) {
            throw new BusinessException("板块名称已存在");
        }

        Board board = new Board();
        board.setName(name);
        board.setDescription(description);

        boardMapper.insert(board);
        return board;
    }

    /**
     * 修改板块信息
     *
     * @param id          板块ID
     * @param name        新名称
     * @param description 新描述
     */
    public void updateBoard(Long id, String name, String description) {
        Board board = boardMapper.findById(id);
        if (board == null) {
            throw new BusinessException("板块不存在");
        }

        // 检查名称是否与其他板块重复
        Board existingBoard = boardMapper.findByName(name);
        if (existingBoard != null && !existingBoard.getId().equals(id)) {
            throw new BusinessException("板块名称已存在");
        }

        board.setName(name);
        board.setDescription(description);
        boardMapper.update(board);
    }

    /**
     * 删除板块
     * <p>级联删除板块下的所有帖子及其评论</p>
     *
     * @param id 板块ID
     */
    @Transactional
    public void deleteBoard(Long id) {
        Board board = boardMapper.findById(id);
        if (board == null) {
            throw new BusinessException("板块不存在");
        }

        // 先删除板块下所有帖子的评论
        commentMapper.deleteByBoardId(id);

        // 删除板块下所有帖子
        postMapper.deleteByBoardId(id);

        // 删除板块
        boardMapper.deleteById(id);
    }

    /**
     * 获取所有板块
     *
     * @return 板块列表
     */
    public List<Board> getAllBoards() {
        return boardMapper.findAll();
    }
}
