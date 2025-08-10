package com.dw.common;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DWEntity {
    /**
     * 主键ID（自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间（插入时自动填充）
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间（插入和更新时自动填充）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记（0-未删除，1-已删除）
     */
    @TableLogic // MyBatis-Plus 逻辑删除注解
    @TableField(fill = FieldFill.INSERT)
    private Integer isDeleted = 0;
}
