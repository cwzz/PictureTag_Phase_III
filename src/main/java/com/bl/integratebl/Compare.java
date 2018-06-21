package com.bl.integratebl;

import com.bl.integratebl.picture_compare_algorithm.ComparePicture;
import com.bl.integratebl.word_similarity.CiLin;
import com.vo.tag.PencilLineVO;
import com.vo.tag.PersonalTagVO;
import com.vo.tag.PictureVO;
import com.vo.tag.RectangleVO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Compare {


    public Compare(ArrayList<PersonalTagVO> personalTags, double totalCredits){
        this.totalCredits=totalCredits;
        users_credits=new HashMap<>();
        for(int i=0;i<personalTags.size();i++){
            users_credits.put(personalTags.get(i).getUid(), (double) 0);
        }
        totalBlocks=0;
        availableAroundDesc=0;
    }

    //后续写成enum
    double acceptable_similar_rate=0.75;                      /*可接受的图片相似度概率*/
    double acceptable_picture_emerge_rate=0.1;               /*可接受的图片标注频率*/
    double acceptable_word_similar_rate=0.9;
    double acceptable_word_emerge_rate=0.1;
    private Map<String,Double> users_credits; /*存放所有用户的所得积分，其中间的ArrayList第一个值是String存放userid,第二个值是double存放积分*/
    private double totalCredits;                         /*该项目的所有积分*/
    private double totalBlocks;                           /*最终返回项目中共有到少个标注框*/
    private double availableAroundDesc;                  /*有用的整体信息*/

    //将每个pictureBlocks进行整合并转换为原来的数据结构返回
    public ArrayList<PictureVO> comparePictureBlocks(ArrayList<PictureBlocks> pictureBlocks, int userNumber){
        ArrayList<PictureVO> pictures=new ArrayList<>();
        for(int i=0;i<pictureBlocks.size();i++){
            PictureVO picture=getResultPicture(pictureBlocks.get(i),userNumber);
            pictures.add(picture);
        }
        return pictures;
    }

    //通过某个图片的PictureBlocks获取这个图片的最终Picture对象
    public PictureVO getResultPicture(PictureBlocks pictureBlocks, int usersNumber){
        //1.先分辨出哪些标注块是可以合并的，未超过acceptable_picture_emerge_rate的舍去
        ArrayList<ArrayList<Block>> classified_blocks=classifyBlocks(pictureBlocks,usersNumber);
        totalBlocks+=classified_blocks.size();
        //2.对每个标注块组进行标注信息的合并
        ArrayList<Block> aggregate_blocks=joinBlocksByDescription(classified_blocks,usersNumber);
        //3.对图片的整体信息进行整合
        String aroundDesc=analyseAroundDescription(pictureBlocks.getAroundDesc());
        //4.将这个数据结构转为原来的数据结构
        PictureVO result_picture=changeDataStructureBack(pictureBlocks.getUrl(),aggregate_blocks,aroundDesc);
        //5.返回原来以用户为中心的数据结构
        return result_picture;
    }

    //1.将PictureBlocks中的相似的数据进行搜索,其中usersNumber是对该项目进行过标注的所有用户数
    public ArrayList<ArrayList<Block>> classifyBlocks(PictureBlocks pictureBlocks, int usersNumber){
        //1.先创建一个最终返回的数据结构
        ArrayList<ArrayList<Block>> new_picture_blocks=new ArrayList<>();

        //2.对所有的block进行两两比对
        //2.1得到所有的block
        ArrayList<Block> blocks=pictureBlocks.getBlocks();
        //2.2创建一个存放比对值的矩阵，初始化矩阵，对角线的值均为1，其余为0
        double [][] similar_matrix =new double[blocks.size()][blocks.size()];
        int [][] matrix_01=new int[blocks.size()][blocks.size()];
        for(int i=0;i<blocks.size();i++){
            for(int j=0;j<blocks.size();j++){
                if(i==j){
                    similar_matrix[i][j]=1;
                    matrix_01[i][j]=1;
                }
                else{
                    similar_matrix[i][j]=0;
                    matrix_01[i][j]=0;
                }
            }
        }
        //2.3开始进行两两比对，并填充矩阵
        for(int i=0;i<blocks.size()-1;i++){
            for(int j=i+1;j<blocks.size();j++){
                ComparePicture comparePicture=new ComparePicture();
                double similar_rate=comparePicture.imageSimilarity(blocks.get(i).getBlockdata(),blocks.get(j).getBlockdata());
                similar_matrix[i][j]=similar_rate;
                similar_matrix[j][i]=similar_rate;
                if(similar_rate>acceptable_similar_rate){
                    matrix_01[i][j]=1;
                    matrix_01[j][i]=1;
                }
                else{
                    matrix_01[i][j]=0;
                    matrix_01[j][i]=0;
                }
            }
        }

        //2.4进行相同block的分类
        //2.4.1先创建一个暂存分类的一维数组
        boolean[] is_classify_result=new boolean[blocks.size()];
        for(int i=0;i<blocks.size();i++){
            is_classify_result[i]=true;
        }

        //2.4.2根据matrix_01的取值将图组初步分类，其中is_classify_result[i]代表第i行是否可取
        for(int i=0;i<blocks.size()-1;i++){
            if(is_classify_result[i]){
                for(int j=i+1;j<blocks.size();j++){
                    if(is_classify_result[j]){
                        boolean isequal=isArrayEqual(matrix_01[i],matrix_01[j]);
                        if(isequal){
                            is_classify_result[j]=false;
                        }
                        else{
                            boolean isbelong1=isArrayBelong(matrix_01[j],matrix_01[i]);
                            if(isbelong1){
                                is_classify_result[j]=false;
                            }
                            boolean isbelong2=isArrayBelong(matrix_01[i],matrix_01[j]);
                            if(isbelong2){
                                is_classify_result[i]=false;
                            }
                        }
                    }
                }
            }
        }

        //2.4.3判断该block的标注频数是否达标
        for(int i=0;i<is_classify_result.length;i++){
            if(is_classify_result[i]){
                if(isArrayCertificated(matrix_01[i],usersNumber,acceptable_picture_emerge_rate)){
                    continue;
                }
                else {
                    is_classify_result[i]=false;
                }
            }
        }


        //2.4.4将分类封装到指定的数据结构中
        for(int i=0;i<is_classify_result.length;i++){
            if(is_classify_result[i]){
                ArrayList<Block> array=new ArrayList<>();
                for(int j=0;j<matrix_01[i].length;j++){
                    if(matrix_01[i][j]==1){
                        array.add(blocks.get(j));
                    }
                }
                new_picture_blocks.add(array);
            }
        }

        //2.5返回分好类的数据结构
        return new_picture_blocks;
    }

    //1.1 判断两个一位数组是否相同
    public boolean isArrayEqual(int[] array1, int[] array2){
        if(array1.length!=array2.length){
            return false;
        }
        else{
            for(int i=0;i<array1.length;i++){
                if(array1[i]==array2[i]){
                    continue;
                }
                else{
                    return false;
                }
            }
            return true;
        }
    }

    //1.2判断array1是否属于array2，其中array1的长度和array2的长度相同
    public boolean isArrayBelong(int[] array1, int[] array2){
        if(array1.length!=array2.length){
            return false;
        }
        else{
            for(int i=0;i<array1.length;i++){
                if((array1[i]==1)&&(array2[i]!=1)){
                    return false;
                }
                else{
                    continue;
                }
            }
            return true;
        }
    }

    //1.3判断array中1出现的评率是否达标
    public boolean isArrayCertificated(int[] array, int usersNumber, double rate){
        int number_of_1=0;
        for(int i=0;i<array.length;i++){
            if(array[i]==1){
                number_of_1++;
            }
        }

        if(number_of_1*1.0/usersNumber>=rate){
            return true;
        }
        else{
            return false;
        }
    }

    //2.将同一标注框中相近的中文释义合并
    public ArrayList<Block> joinBlocksByDescription(ArrayList<ArrayList<Block>> classified_blocks, int usersNumber){
        //1.创建最终需要返回的数据结构
        ArrayList<Block> result_blocks=new ArrayList<>();

        //2.对每个block进行操作
        for(int i=0;i<classified_blocks.size();i++){
            //2.1选择其中一个block的大小作为最终block的边界，默认先选择铅笔，再选择面积最小的block
            Block result_picture_block=chooseAppropriateBlock(classified_blocks.get(i));
            //2.2对该block中的文字信息进行合并
            String[] result_descriptions_block=chooseAppropriateDescription(classified_blocks.get(i),usersNumber);
            //2.3将选择的边界值，合并好的文字信息加入到返回的数据结构中去
            if(result_picture_block.isPencil()){
                Block block=new Block(result_picture_block.getBlockdata(),result_descriptions_block,"",result_picture_block.getPencil());
                result_blocks.add(block);
            }
            else{
                Block block=new Block(result_picture_block.getBlockdata(),result_descriptions_block,"",result_picture_block.getRectangle());
                result_blocks.add(block);
            }
        }
        return result_blocks;
    }

    //2.1选出由铅笔勾画的面积最小的block，否则选择由方框勾画的最小的block
    public Block chooseAppropriateBlock(ArrayList<Block> blocks){
        //1.判断有无铅笔
        boolean isPencil=false;
        for(int i=0;i<blocks.size();i++){
            if(blocks.get(i).isPencil()){
                isPencil=true;
                break;
            }
        }

        //2.根据有无铅笔做下一步的选择
        Block result_block=new Block();
        result_block=blocks.get(0);
        if(isPencil){
            for(int i=0;i<blocks.size();i++){
                Block temp_block=blocks.get(i);
                if(temp_block.isPencil()){
                    if(temp_block.getBlockdata().getWidth()*temp_block.getBlockdata().getHeight()<result_block.getBlockdata().getHeight()*result_block.getBlockdata().getWidth()){
                        result_block=temp_block;
                        continue;
                    }
                }
                else{
                    continue;
                }
            }
        }
        else{
            for(int i=0;i<blocks.size();i++){
                Block temp_block=blocks.get(i);
                if(temp_block.getBlockdata().getWidth()*temp_block.getBlockdata().getHeight()<result_block.getBlockdata().getHeight()*result_block.getBlockdata().getWidth()){
                    result_block=temp_block;
                    continue;
                }
            }
        }

        //3.返回最终的block
        return result_block;
    }

    //2.2对block数组中的文字进行合并，相似度达到acceptable_word_similar_rate以上的认为是同类事物，出现次数达到acceptable_word_emerge_rate的认为有效
    public String[] chooseAppropriateDescription(ArrayList<Block> blocks, int userNumber){
        //1.现将所有的标注信息拿到一个数据结构中
        ArrayList<String[]> info=new ArrayList<>();
        for(int i=0;i<blocks.size();i++){
            Block temp_block=blocks.get(i);
            for(int j=0;j<temp_block.getDescriptions().length;j++){
                String[] temp_string=new String[2];
                temp_string[0]=temp_block.getUserid();
                temp_string[1]=temp_block.getDescriptions()[j];
                info.add(temp_string);
            }
        }
        //1.1若这组block中只有一组信息的话则直接返回所有积分给该用户
        if(info.size()==1){
            users_credits.put(info.get(0)[0], users_credits.get(info.get(0)[0])+(double) (1));
            String[] result=new String[1];
            result[0]=info.get(0)[1];
            return result;
        }

        //2.两两之间判断相似性，保存到矩阵中
        double[][] similar_matrix=new double[info.size()][info.size()];
        int[][] matrix_01=new int[info.size()][info.size()];
        for(int i=0;i<info.size()-1;i++){
            for(int j=i;j<info.size();j++){
                double sim=CiLin.calcWordsSimilarity(info.get(i)[1],info.get(j)[1]);
                if(sim>acceptable_word_similar_rate){
                    similar_matrix[i][j]=sim;
                    similar_matrix[j][i]=sim;
                    matrix_01[i][j]=1;
                    matrix_01[j][i]=1;
                }
                else{
                    similar_matrix[i][j]=sim;
                    similar_matrix[j][i]=sim;
                    matrix_01[i][j]=0;
                    matrix_01[j][i]=0;
                }
            }
        }

//        for(int i=0;i<similar_matrix.length;i++){
//            for(int j=0;j<similar_matrix.length;j++){
//                System.out.print(similar_matrix[i][j]+" ");
//            }
//            System.out.println();
//        }
//
//        for(int i=0;i<similar_matrix.length;i++){
//            for(int j=0;j<similar_matrix.length;j++){
//                System.out.print(matrix_01[i][j]+" ");
//            }
//            System.out.println();
//        }


        //3.用result_line来判断哪些行符合要求可以使用
        //3.1初始化判断的是否要去的数据结构
        boolean[] result_line=new boolean[info.size()];
        for(int i=0;i<result_line.length;i++){
            result_line[i]=true;
        }

        //3.2删除相同或者被包含的数组
        for(int i=0;i<info.size()-1;i++){
            if(result_line[i]){
                for(int j=i+1;j<info.size();j++){
                    if(result_line[j]){
                        boolean isequal=isArrayEqual(matrix_01[i],matrix_01[j]);
                        if(isequal){
                            result_line[j]=false;
                        }
                        else{
                            boolean isbelong1=isArrayBelong(matrix_01[j],matrix_01[i]);
                            if(isbelong1){
                                result_line[j]=false;
                            }
                            boolean isbelong2=isArrayBelong(matrix_01[i],matrix_01[j]);
                            if(isbelong2){
                                result_line[i]=false;
                            }
                        }
                    }
                }
            }
        }


        //3.3去除个数不达标的行
        for(int i=0;i<info.size();i++){
            if(!isArrayCertificated(matrix_01[i],userNumber,acceptable_word_emerge_rate)){
                result_line[i]=false;
            }
        }

//        for(int i=0;i<result_line.length;i++){
//            System.out.println(result_line[i]);
//        }

        //4.将结果写入最终的数据结构中
        //4.1挑选出需要返回的数据
        ArrayList<String> result_descriptions=new ArrayList<>();
        double number_of_marks=0; /*一个block最终由多少标注信息*/
        for(int i=0;i<matrix_01.length;i++){
            if(result_line[i]){
                number_of_marks++;
                for(int j=0;j<matrix_01[i].length;j++){
                    if(matrix_01[i][j]==1){
                        result_descriptions.add(info.get(j)[1]);
                        break;
                    }
                }
            }
        }

        //4.2设置用户积分
        for(int i=0;i<result_line.length;i++){
            if(result_line[i]){
                //4.2.1先计算这条标注总共有多少人的标注被采纳，即matrix_01该行中有多少个1
                int temp_number=0;
                for(int j=0;j<matrix_01[i].length;j++){
                    if(matrix_01[i][j]==1){
                        temp_number++;
                    }
                }

                //4.2.2将用户所得积分分别划分给各个用户
                for(int j=0;j<matrix_01[i].length;j++){
                    if(matrix_01[i][j]==1){
                        users_credits.put(info.get(j)[0], users_credits.get(info.get(j)[0])+(double) (1/number_of_marks/temp_number));
                    }
                }
            }
        }

        //4.2按照指定的数据结构进行返回
        String[] results=new String[result_descriptions.size()];
        for(int i=0;i<results.length;i++){
            results[i]=result_descriptions.get(i);
        }

        //5.返回指定数据结构
        return results;
    }

    //3.将图片的整体信息进行整合，String[2]中String[0]为userid，String[1]为整体标注
    public String analyseAroundDescription(ArrayList<String[]> descriptions){
        //1.现将所有的数据全部加入到判断的数据结构中
        ArrayList<String[]> desc=new ArrayList<>();
        for(int i=0;i<descriptions.size();i++){
            if(descriptions.get(i)[1]==null){
                continue;
            }
            else if(descriptions.get(i)[1].equals("")){
                continue;
            }
            desc.add(descriptions.get(i));
        }

        if(desc.size()==0){
            return "暂无整体标注信息";
        }
        else if(desc.size()==1){
            availableAroundDesc++;
            users_credits.put(desc.get(0)[0], (double) 1+users_credits.get(desc.get(0)[0]));
            return desc.get(0)[1];
        }
        availableAroundDesc++;

        //2.建立矩阵
        double[][] similar_matrix=new double[desc.size()][desc.size()];
        int[][] matrix_01=new int[desc.size()][desc.size()];
        for(int i=0;i<matrix_01.length;i++){
            similar_matrix[i][i]=1;
            matrix_01[i][i]=1;
        }
        for(int i=0;i<desc.size()-1;i++){
            for(int j=i;j<desc.size();j++){
                double sim=CiLin.calcWordsSimilarity(desc.get(i)[1],desc.get(j)[1]);
                if(sim>acceptable_word_similar_rate){
                    similar_matrix[i][j]=sim;
                    similar_matrix[j][i]=sim;
                    matrix_01[i][j]=1;
                    matrix_01[j][i]=1;
                }
                else{
                    similar_matrix[i][j]=sim;
                    similar_matrix[j][i]=sim;
                    matrix_01[i][j]=0;
                    matrix_01[j][i]=0;
                }
            }
        }

        //3.寻找标注者最认可的整体描述，即matrix_01哪一行1最多，并将整体描述的标注值赋给result_desc
        //3.1
        String result_desc="";
        int number_for_1=0;
        ArrayList<Integer> result_index=new ArrayList<>();
        int index=0;
        String result_part="";
        for(int i=0;i<matrix_01.length;i++){
            int temp_number_for_1=0;
            for(int j=0;j<matrix_01[i].length;j++){
                if(matrix_01[i][j]==1){
                    temp_number_for_1++;
                    result_part=desc.get(j)[1];
//                    if(temp_number_for_1>number_for_1){
////                        number_for_1=temp_number_for_1;
////                        result_desc=desc.get(j)[1];
////                        index=i;
//                        result_index.clear();
//                        result_index.add(i);
//                        result_desc=desc.get(j)[1]+";";
//                    }
//                    else if(temp_number_for_1==number_for_1){
//                        result_index.add(i);
//                        result_desc+=desc.get(j)[1]+";";
//                    }
                }
            }
            if(temp_number_for_1>number_for_1){
                number_for_1=temp_number_for_1;
                result_index.clear();
                result_index.add(i);
                result_desc=result_part+";";
            }
            else if(temp_number_for_1==number_for_1){
                result_index.add(i);
                result_desc+=result_part+";";
            }
        }

        //3.2分配积分
//        for(int i=0;i<matrix_01[index].length;i++){
//            if(matrix_01[index][i]==1){
//                users_credits.put(desc.get(i)[0], (double) (1/number_for_1)+users_credits.get(desc.get(i)[0]));
//            }
//        }
        for(int j=0;j<result_index.size();j++){
            index=result_index.get(j);
            for(int i=0;i<matrix_01[index].length;i++){
            if(matrix_01[index][i]==1){
                users_credits.put(desc.get(i)[0], (1.0/result_index.size()/number_for_1)+users_credits.get(desc.get(i)[0]));
            }
        }
        }

        //4.返回要求的数据结构
        return result_desc;
    }

    //4.将现有数据结构保存为前端需要的数据结构
    public PictureVO changeDataStructureBack(String url, ArrayList<Block> blocks, String aroundDesc){
        //1.初始化需要的数据结构
        int pencil_num=0;
        int rect_num=0;
        for(int i=0;i<blocks.size();i++){
            if(blocks.get(i).isPencil()){
                pencil_num++;
            }
            else{
                rect_num++;
            }
        }
        PencilLineVO[] pencilTag=new PencilLineVO[pencil_num];
        RectangleVO[] recTag =new RectangleVO[rect_num];

        //2.进行数据结构的转化
        pencil_num=0;
        rect_num=0;
        for(int i=0;i<blocks.size();i++){
            Block temp_block=blocks.get(i);
            if(temp_block.isPencil()){
                pencilTag[pencil_num]=temp_block.getPencil();
                pencil_num++;
            }
            else{
                recTag[rect_num]=temp_block.getRectangle();
                rect_num++;
            }
        }

        //3.返回指定的数据结构
        PictureVO result_picture=new PictureVO(url,pencilTag,recTag,aroundDesc);
        return result_picture;
    }

    //5.根据userid获得该用户在这种整合的方式下所应该获得的积分奖励
    public double getCreditsByUserid(String userid){
        return totalCredits/(totalBlocks+availableAroundDesc)*users_credits.get(userid);
    }
}


