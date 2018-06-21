package com.vo;

import lombok.Data;

import java.util.ArrayList;

/**
 * @Author:zhangping
 * @Description:
 * @CreateData: 2018/5/6 20:22
 */

@Data
public class MessageSetRead {
    private String username;
    private ArrayList<String> messageList;
}
