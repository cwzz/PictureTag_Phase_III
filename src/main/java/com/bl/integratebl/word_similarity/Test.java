package com.bl.integratebl.word_similarity;


public class Test {
    public static void main(String args[]) {
        String word1 = "女人", word2 = "王旭";
        double sim = 0;
        sim = CiLin.calcWordsSimilarity(word1, word2);//计算两个词的相似度
        System.out.println(word1 + "  " + word2 + "的相似度为：" + sim);
    }
}
