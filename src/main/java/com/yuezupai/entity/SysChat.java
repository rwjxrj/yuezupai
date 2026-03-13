package com.yuezupai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_chat")
public class SysChat {

    @TableId(type = IdType.AUTO)
    private Long messageId;

    private Long senderId;
    private Long receiverId;
    private Long itemId;
    private String msgType;
    private String content;
    private Integer isRead;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}