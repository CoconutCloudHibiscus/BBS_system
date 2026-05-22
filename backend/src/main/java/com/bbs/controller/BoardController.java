package com.bbs.controller;

import com.bbs.common.BusinessException;
import com.bbs.common.Result;
import com.bbs.entity.Board;
import com.bbs.service.BoardService;
import com.bbs.util.UserContext;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 板块控制器
 * <p>处理板块的创建、修改、删除和查询接口</p>
 * <p>创建、修改、删除板块需要管理员权限</p>
 */
@RestController
@RequestMapping("/api/boards")
public class BoardController {

    @Autowired
    private BoardService boardService;

    /**
     * 获取所有板块列表（公开接口）
     *
     * @return 板块列表
     */
    @GetMapping
    public Result<List<Board>> getAllBoards() {
        List<Board> boards = boardService.getAllBoards();
        return Result.success(boards);
    }

    /**
     * 创建板块（管理员）
     *
     * @param request 板块创建请求体
     * @return 创建的板块
     */
    @PostMapping
    public Result<Board> createBoard(@Validated @RequestBody BoardRequest request) {
        // 管理员权限验证
        Integer role = UserContext.getRole();
        if (role == null || !role.equals(1)) {
            throw new BusinessException(403, "无管理员权限");
        }
        Board board = boardService.createBoard(request.getName(), request.getDescription());
        return Result.success("板块创建成功", board);
    }

    /**
     * 修改板块（管理员）
     *
     * @param id      板块ID
     * @param request 板块修改请求体
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public Result<Void> updateBoard(@PathVariable Long id, @Validated @RequestBody BoardRequest request) {
        // 管理员权限验证
        Integer role = UserContext.getRole();
        if (role == null || !role.equals(1)) {
            throw new BusinessException(403, "无管理员权限");
        }
        boardService.updateBoard(id, request.getName(), request.getDescription());
        return Result.success("板块修改成功", null);
    }

    /**
     * 删除板块（管理员）
     * <p>级联删除板块下的所有帖子和评论</p>
     *
     * @param id 板块ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteBoard(@PathVariable Long id) {
        // 管理员权限验证
        Integer role = UserContext.getRole();
        if (role == null || !role.equals(1)) {
            throw new BusinessException(403, "无管理员权限");
        }
        boardService.deleteBoard(id);
        return Result.success("板块删除成功", null);
    }

    /**
     * 板块请求体
     */
    @Data
    public static class BoardRequest {
        /** 板块名称 */
        @NotBlank(message = "板块名称不能为空")
        private String name;

        /** 板块描述 */
        private String description;
    }
}
