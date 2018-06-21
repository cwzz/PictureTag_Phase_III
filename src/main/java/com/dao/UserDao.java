package com.dao;

import com.enums.UserIdentity;
import com.model.BrowseRecord;
import com.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

@Repository
@Table(name = "user")
public interface UserDao extends JpaRepository<User,String> {

    @Query(value = "select t from User t where t.username =:username")
    User searchUserById(@Param("username") String username);

    @Query(value = "select t.records from User t where t.username =:username")
    Set<BrowseRecord> getBrowseRecord(@Param("username") String username);

    @Query(value = "select t.username from User t where t.identity=:identity order by t.experience desc")
    ArrayList<String> ListUserByRank(@Param("identity")UserIdentity identity);

    @Query(value = "select t from User t order by t.activeContract desc")
    ArrayList<User> getActiveWorker();

    @Query(value = "select t from User t order by t.activeRelease desc")
    ArrayList<User> getActiveRequest();

    @Query(value = "select count(t) from User t")
    int countTotalUser();

    @Query(value = "select count(t) from User t where t.isOn=TRUE")
    int countOnlineUser();

    @Query(value = "select count(t) from User t where t.dateRegister>?1")
    int countRegisterInDays(@Param("date") Date date);
}
