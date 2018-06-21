package com.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "similarity")
@Data
public class Similarity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long smid;
    private String pid1;
    private String pid2;
    private double similarity;

    public Similarity(){}

    public Similarity(String pid1,String pid2,double similarity){
        this.pid1=pid1;
        this.pid2=pid2;
        this.similarity=similarity;
    }
}
