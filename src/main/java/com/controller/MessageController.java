package com.controller;

import com.blservice.MessageBLService;
import com.enums.ResultMessage;
import com.model.Message;
import com.vo.MessageSetRead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Author:zhangping
 * @Description:
 * @CreateData: 2018/4/27 23:41
 */
@Controller
@RequestMapping("/")
public class MessageController {

    @Autowired
    private MessageBLService messageBLService;

    @RequestMapping(value = "/setState")
    public @ResponseBody
    ResultMessage setMessageState(@RequestBody MessageSetRead messageSetRead){
        messageBLService.setRead(messageSetRead);
        return ResultMessage.SUCCESS;
    }
    @RequestMapping(value = "/addMessage")
    public @ResponseBody
    void addMessage(@RequestParam String receiver,@RequestParam String event,String projectID){
        messageBLService.generateMessage(receiver,event,projectID);
    }

    @RequestMapping(value = "/listRead")
    public @ResponseBody
    List<Message> listRead(@RequestParam String username){
        return messageBLService.getReadMessageList(username);
    }

    @RequestMapping(value = "/listUnread")
    public @ResponseBody
    List<Message> listUnread(@RequestParam String username){
        return messageBLService.getUnreadMessageList(username);
    }

}
