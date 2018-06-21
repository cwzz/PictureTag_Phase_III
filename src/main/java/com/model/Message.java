package com.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long mid;//消息的ID，作为消息在数据库中的唯一标识

    private String receiver;//消息的接收者,即用户名
    private String event;//发生的事件，承包？到期？提醒？……
    private String projectID;//与该消息相关的项目ID
    private String time;//消息生成的时间
    private boolean isRead;//消息是否已读，未读设为false，已读设为true

    public Message(){}
}
