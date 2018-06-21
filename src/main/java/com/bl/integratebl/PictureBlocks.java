package com.bl.integratebl;

import lombok.Data;

import java.util.ArrayList;
import java.util.Map;

@Data
public class PictureBlocks {
    private String url;
    private ArrayList<Block> blocks;
    private ArrayList<String[]> aroundDesc;

    public PictureBlocks(){
        url="";
        blocks=new ArrayList<>();
        aroundDesc=new ArrayList<>();
    }

    public PictureBlocks(String url){
        this.url=url;
        blocks=new ArrayList<>();
        aroundDesc=new ArrayList<>();
    }

    public PictureBlocks(String url, ArrayList<Block> blocks, ArrayList<String[]> aroundDesc){
        this.url=url;
        this.blocks=blocks;
        this.aroundDesc=aroundDesc;
    }

    //增加一个block
    public boolean addBlock(Block block){
        if(block==null){
            return false;
        }
        else{
            blocks.add(block);
            return true;
        }
    }


    //增加一个整体描述
    public boolean addAroundDesc(String[] desc){
        if(desc==null){
            return false;
        }
        else{
            aroundDesc.add(desc);
            return true;
        }
    }

}
