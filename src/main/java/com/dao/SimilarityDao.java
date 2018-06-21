package com.dao;

import com.model.Similarity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Table;
import java.util.ArrayList;

@Repository
@Table(name="similarity")
public interface SimilarityDao extends JpaRepository<Similarity,String> {

    @Modifying
    @Query("delete from Similarity where pid1=:pid1 and pid2=:pid2")
    void dropSimilarity(@Param("pid1") String pid1, @Param("pid2") String pid2);

    @Query("select s from Similarity s where s.pid1=:pid1 and s.pid2=:pid2 or s.pid1=:pid2 and s.pid2=:pid1")
    Similarity showSimi(@Param("pid1") String pid1, @Param("pid2") String pid2);

    @Query("select s from Similarity s where s.pid1=:pid or s.pid2=:pid")
    ArrayList<Similarity> getAllSimi(@Param("pid") String pid);
}
