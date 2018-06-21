package com.bl;

import com.blservice.MessageBLService;
import com.dao.MessageDao;
import com.model.Message;
import com.vo.MessageSetRead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @Author:zhangping
 * @Description:
 * @CreateData: 2018/4/24 19:37
 */
@Service
public class MessageBL implements MessageBLService {

    private MessageDao messageDao;

    @Autowired
    public MessageBL(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    @Override
    public void generateMessage(String receiver, String event, String projectID) {
        Message message=new Message();
        message.setEvent(event);
        message.setProjectID(projectID);
        message.setRead(false);
        message.setReceiver(receiver);
        message.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        messageDao.saveAndFlush(message);
    }

    @Override
    public void generateMessage(Set<String> receiver, String event,String projectID) {
        for (String s:receiver){
            Message message=new Message();
            message.setReceiver(s);
            message.setEvent(event);
            message.setProjectID(projectID);
            message.setRead(false);
            message.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            messageDao.saveAndFlush(message);
        }
    }

    @Override
    public void setRead(MessageSetRead messageSetRead) {
        for(String messageTime:messageSetRead.getMessageList()){
            Message m=this.getOne(messageSetRead.getUsername(),messageTime);
            m.setRead(true);
            messageDao.saveAndFlush(m);
        }
    }


    @Override
    public List<Message> getReadMessageList(String username) {
        List<Message> read=new ArrayList<>();
        for(Message m :messageDao.listAll(username)){
            if(m.isRead()){
                read.add(m);
            }
        }
        return  read;
    }

    @Override
    public List<Message> getUnreadMessageList(String username) {
        List<Message> unRead=new ArrayList<>();
        for(Message m :messageDao.listAll(username)){
            if(!m.isRead()){
                unRead.add(m);
            }
        }
        return  unRead;
    }

    private Message getOne(String username, String messageTime){
        return messageDao.find(username,messageTime);
    }
}
