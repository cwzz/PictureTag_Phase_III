package com.dao;

import com.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Table;
import java.util.List;

/**
 * @Author:zhangping
 * @Description:
 * @CreateData: 2018/5/24 10:52
 */
@Table(name = "message")
@Repository
public interface MessageDao extends JpaRepository<Message,Long>{

    @Query(value = "select m from Message m where m.receiver=:receiver and m.time=:time ")
    Message find(@Param("receiver") String username, @Param("time") String messageTime);

    @Query(value = "select m from Message m where m.receiver=:receiver")
    List<Message> listAll(@Param("receiver") String username);
}
