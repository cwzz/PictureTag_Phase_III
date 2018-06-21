package com.blservice;


import com.model.Message;
import com.vo.MessageSetRead;

import java.util.List;
import java.util.Set;

public interface MessageBLService {

    void generateMessage(String receiver, String event, String projectID);//系统自动发送消息
    void generateMessage(Set<String> receiver,String event,String projectID);

    void setRead(MessageSetRead messageSetRead);

    List<Message> getReadMessageList(String username);//显示用户的已读消息列表

    List<Message> getUnreadMessageList(String username);//显示用户未读的消息列表

}
