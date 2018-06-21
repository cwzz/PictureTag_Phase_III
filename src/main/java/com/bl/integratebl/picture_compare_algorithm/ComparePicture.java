package com.bl.integratebl.picture_compare_algorithm;

import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Service
public class ComparePicture {
    private double Mean_Hash_Rate=0.33;
    private double Hamming_Rate=0.33;
    private double Overlap_Rate=0.33;

    public double imageSimilarity(BufferedImage image1, BufferedImage image2){
        //1.以下为哈希值算法的相似度
        MeanHash fp1=new MeanHash(image1);
        MeanHash fp2=new MeanHash(image2);
        double similarity1=fp1.compare(fp2);

//        //2.以下为汉明距离相似度算法
//        Hamming hamming =new Hamming();
//        double similarity2= hamming.getSimilarity(image1,image2);
//
//        //3.以下为重叠率的算法
//        CalculateOverlap calculateOverlap =new CalculateOverlap();
//        double similarity3= calculateOverlap.compareImage(image1,image2);
//

//        System.out.println(similarity2);
//        System.out.println(similarity3);
        //double result=similarity1*Mean_Hash_Rate+similarity2*Hamming_Rate+similarity3*Overlap_Rate;
        double result=similarity1;
        return result;
    }

}
