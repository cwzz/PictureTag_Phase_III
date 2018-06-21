package com.dao;


import com.model.PersonalTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import javax.persistence.Table;
import java.util.ArrayList;


@Repository
@Table(name = "personal_tag")
public interface PersonalTagDao extends JpaRepository<PersonalTag, Long> {

    @Query("select p from PersonalTag p where p.pid=:pid and p.uid=:uid")
    PersonalTag searchByPidAndUid(@Param("pid")String pid, @Param("uid") String uid);

    @Modifying
    @Query("delete from PersonalTag where pid=:pid and uid=:uid")
    void dropByPidAndUid(@Param("pid") String pid,@Param("uid") String uid);

    @Query("select p from PersonalTag p where p.pid=:pid")
    ArrayList<PersonalTag> searchByPid(@Param("pid")String pid);

    @Query("select p from PersonalTag p where p.uid=:username")
    ArrayList<PersonalTag> searchByUid(@Param("username")String username);
}
