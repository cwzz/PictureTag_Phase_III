package com.dao;

import com.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author:zhangping
 * @Description:
 * @CreateData: 2018/5/24 0:50
 */
@Repository
public interface LogDao extends JpaRepository<Log,Long> {
}
