//初始化长宽高
var height=window.innerHeight;
var width=window.innerWidth;
var pictureWidth=width*4/5;
var pictureHeight=height*14/15;
//var pictureWidth=400;
//var pictureHeight=400;
var out_box_height=height*14*0.12/15;
var buttonWidth=width/5*3*80/600;
var picWidth=height/15;

var showPicHeight=height/4;

///////////////////////////////////////////////
var pictures=new Array();
var times=0;
var pencils=new Array();
var rectangles=new Array();
var des=new Array();

//为存储矩形标记自定义的存储类
var RecStore={
    beginX:0,
    beginY:0,
    endX:0,
    endY:0,
    description:[],
    border_color:"",
    border_width:"",
    font_color:"",
    font_width:"",
    isBold:false
};

//为记录当前某个点的位置自定义的位置存储类
var  Position={
    x:0,
    y:0
};

//为记录某条铅笔勾画出来的标记自定义的存储类
//path为该线上所有点的位置的集合 是Position型的数组
var PencilTag={
    path:[],
    description:[],
    border_color:"",
    border_width:"",
    font_color:"",
    font_width:"",
    isBold:false
};

//为存储一张图片的所有信息自定义的存储类
var PictureStore={
    pictureTotalDes:"",
    pictureURL:"",
    recOfPic:[],
    pencilOfPic:[]
}

//画布
var canvas ;
//画板内的所有内容
var context ;
//蒙版
var canvas_bak;
var context_bak;

//需要修改 画板大小
var canvasWidth = width*4/5;
var canvasHeight = height*14/15;

var canvasTop;
var canvasLeft;

//画笔大小
var size = 1;
var color = '#000000';

//保存一个项目中的所有图片信息
var ProjectInfo=[];

//撤销的array 存储的是一幅图片
var cancelList = [];
//重做的array
var redoList=[];

//用于存储矩形标注
var reclist = [];
//用于存储撤销的矩形标注以便重做时恢复
var reclistForRedo=[];

//用于存储铅笔所经过的路径
var pencil_path=[];
//用于存储画笔轨迹 是Pencil_Tag型数组
var pencilList=[];
//用于撤销和重做的存储画笔轨迹
var pencilListForRedo=[];

//用于撤销和重做的操作类型array
var cancelListForType=[];
var redoListForType=[];

//为选中线条而设置的微调参数
var pencil_para=2;
const pencil_para_level1=2;
const pencil_para_level2=3;
const pencil_para_level3=4;
const pencil_para_level4=5;

//为查看标注的描述内容而设置的当前操作类型
var current_operate="no_choose";

//为得知当前操作的是第几个
var current_picture=0;

//为修改描述设置的全局变量
var again_des_type;
var again_des_position;

//实现曲线自动闭合，自己拟合的点
var storage_ratio=61/46;//每1px平均存储的点的个数
var mousedown_x;
var mousedown_y;
var test_num=10;//以距离曲线起始位置分别多少个点画线拟合曲线的切线
var test_paralla_para=0.2;//斜率在多少误差内认为两条直线平行

//1.初始化界面

var project_id;
var workOrsee;
var user_id;
var urls=[];
$(document).ready(function () {
    workOrsee=localStorage.getItem("workOrsee");
    project_id=localStorage.getItem("work_id");
    user_id=JSON.parse(localStorage.getItem("current_user")).username;
    var picdata;
    var temp_urls=[];
    var pencil_tag=[];
    var rec_tag=[];
    var descript=[];
    $.ajax({
        type : 'POST',
        url : '/project/read',
        async:false,
        data :{pid:project_id},
        //此处data为新建项目的pid
        success : function (data) {
            //temp_urls=data.urls;
            document.getElementById("name").innerText="当前项目名称："+data.pro_name;

        },
        error: function (data) {
            alert("读取项目出错")
        }
    });
    var temp_data={pid:project_id,uid:user_id};
    $.ajax({
        type: 'POST',
        url: '/personalTag/showPersonalTag',
        async:false,
        contentType:"application/json",
        dataType:"json",
        data:JSON.stringify(temp_data),
        success:function (result) {
            picdata=result.pictures;
        },
        error:function(result){
            alert("/personalTag/showPersonalTag出错");
        }
    });

    // for(var j=0;j<temp_urls.length;j++){
    //     urls.push("http://"+temp_urls[j]);
    // }

    for(var i=0;i<picdata.length;i++){
        urls.push("http://"+picdata[i].url);
        pencil_tag.push(picdata[i].pencilTag);
        rec_tag.push(picdata[i].recTag);
        descript.push(picdata[i].aroundDesc);
    }

    pictures=urls;
    pencils=pencil_tag;
    rectangles=rec_tag;
    des=descript;
    //initCanvas();
    addPic();
})

//1.1调整边框大小
function initialize(){
    //调节主屏幕大小
    document.getElementById("menu").style.height=height/15+"px";
    document.getElementById("left").style.height=height/15*14+"px";
    document.getElementById("left").style.overflowY="auto";
    document.getElementById("middle").style.height=height/15*14+"px";
    document.getElementById("menu").style.width=width+"px";
    document.getElementById("left").style.width=width/5+"px";
    document.getElementById("middle").style.width=width/5*4+"px";
    document.getElementById("workspace").style.width=width/5*4+"px";
    document.getElementById("workspace").style.height=(height/15*14-out_box_height)+"px";
    document.getElementById("image-box").style.width=width/5*4+"px";
    document.getElementById("image-box").style.height=(height/15*14-out_box_height)+"px";
    document.getElementById("middle").style.top=height/15+"px";
    document.getElementById("middle").style.left=width/5+"px";
    document.getElementsByClassName("outer-box")[0].style.height=out_box_height+"px";
    document.getElementsByClassName("outer-box")[0].style.width=width/5*4+"px";
}

function getPic() {
    $.ajax({
        type: "GET",
        url: "retrieve",
        contentType: "application/json",
        dataType: "json",
        success: function (jsonResult) {
            alert(jsonResult);
            var t=JSON.parse();
            console.log(t);
        },
        erro: function () {
            alert("又错了")
        }
    });
}
//1.2调整按钮大小
$(document).ready(function(){
    $(".pic").css({"width":picWidth+"px"});
    $(".fontButton").css({"width":buttonWidth+"px","height":picWidth+"px","font-size":picWidth*0.4+"px"});
    $("#menu").css({"padding-left":(width-picWidth*2-buttonWidth*4)/2+"px"});
});

//1.3初始化图片信息调用的方法，需要传入一个url数组
function addPic() {

    $("#fontSize").val("18px");

    var html=document.getElementsByClassName("scrollbot-inner-parent")[0].innerHTML;
    for(var i=0;i<pictures.length;i++){
        html=html+'<div class="row" style="color: #FFFFFF;width: 100%;height: 10px"></div><img class="addpics" src='+pictures[i]+' onclick=choosePic("'+pictures[i]+'",'+i+') >';
        //初始化存储信息
        var tempPicInfo=Object.create(PictureStore);
        tempPicInfo.pictureURL=pictures[i];
        tempPicInfo.pencilOfPic=pencils[i];
        tempPicInfo.recOfPic=rectangles[i];
        tempPicInfo.pictureTotalDes=des[i];
        ProjectInfo.push(tempPicInfo);
    }
    document.getElementsByClassName("scrollbot-inner-parent")[0].innerHTML=html;
    imageLabel({
        img: pictures[0],
        editPop: !0,
        close: function(t) {
            return t.length && alert(JSON.stringify(t)), !0
        },
        clickArea: function() {},
        edit: function(t) {},
        startArea: function() {},
        confirm: function(t) {
            return t.length && alert(JSON.stringify(t)), !0
        }
    });
    reclist=ProjectInfo[0].recOfPic;
    pencilList=ProjectInfo[0].pencilOfPic;
    changePic(pictures[0]);
    $(".addpics").css({"width":"90%","height":showPicHeight+"px","margin-left":"9%"});
}

function new_addPic(){
    $("#fontSize").val("18px");
    var html=document.getElementsByClassName("scrollbot-inner-parent")[0].innerHTML;
    for(var i=0;i<pictures.length;i++){
        html=html+'<div class="row" style="color: #FFFFFF;width: 100%;height: 10px"></div><img class="addpics" src='+pictures[i]+' onclick=choosePic("'+pictures[i]+'",'+i+') >';
        //初始化存储信息
        var tempPicInfo=Object.create(PictureStore);
        tempPicInfo.pictureURL=pictures[i];
        tempPicInfo.pencilOfPic=pencils[i];
        tempPicInfo.recOfPic=rectangles[i];
        tempPicInfo.pictureTotalDes=des[i];
        ProjectInfo.push(tempPicInfo);
    }
    document.getElementsByClassName("scrollbot-inner-parent")[0].innerHTML=html;
    reclist=ProjectInfo[0].recOfPic;
    pencilList=ProjectInfo[0].pencilOfPic;
    choosePic(pictures[0],0);
    $(".addpics").css({"width":"90%","height":showPicHeight+"px","margin-left":"9%"});
}

//2.点击时将图片加载到主画板中去,并保留之前的标记及标注信息
function choosePic(str,i) {
    //alert(str+"..."+current_picture+"...."+JSON.stringify(ProjectInfo));
    reclist=[];
    pencilList=[];
    var recs=$(".imageLabel-imgdrop");
    var current_pic_width=$("#tag-img").css("width");
    current_pic_width=current_pic_width.substring(0,current_pic_width.length-2);
    var current_pic_height=$("#tag-img").css("height");
    current_pic_height=current_pic_height.substring(0,current_pic_height.length-2);
    for(var y=0;y<recs.length;y++){
        var temp_data=JSON.parse(recs[y].getAttribute("data-json"));
        if(temp_data.isRec) {
            var tempRec = Object.create(RecStore);
            tempRec.beginX = temp_data.x ;
            tempRec.beginY = temp_data.y ;
            tempRec.endX = temp_data.ex ;
            tempRec.endY = temp_data.ey ;
            tempRec.description = [];
            tempRec.description.push(temp_data.name);
            tempRec.border_color=temp_data.border_color;
            tempRec.border_width=temp_data.border_width;
            tempRec.font_color=temp_data.font_color;
            tempRec.font_width=temp_data.font_width;
            tempRec.isBold=temp_data.isbold==400?false:true;
            reclist.push(tempRec);
        }else{
            var tempPencil=Object.create(PencilTag);
            tempPencil.path=temp_data.path;
            tempPencil.description=[];
            tempPencil.description.push(temp_data.name);
            tempPencil.border_color=temp_data.border_color;
            tempPencil.border_width=temp_data.border_width;
            tempPencil.font_color=temp_data.font_color;
            tempPencil.font_width=temp_data.font_width;
            tempPencil.isBold=temp_data.isbold==400?false:true;
            pencilList.push(tempPencil);
        }
    }

    //数据的更新
    ProjectInfo[current_picture].pictureTotalDes=document.getElementById("field2").value;
    ProjectInfo[current_picture].recOfPic=reclist;
    ProjectInfo[current_picture].pencilOfPic=pencilList;

    reclist=ProjectInfo[i].recOfPic;
    pencilList=ProjectInfo[i].pencilOfPic;

    redoList=[];
    cancelList=[];

    reclistForRedo=[];
    pencilListForRedo=[];

    cancelListForType=[];
    redoListForType=[];

    current_operate='no_choose';
    current_picture=i;

    save();
    changePic(str);
}

//重新点击新的图片时候 把新的图片的标注重新画出来
function draw_again(){
    $(".imageLabel-content").empty();
    var current_pic_width=$("#tag-img").css("width");
    current_pic_width=current_pic_width.substring(0,current_pic_width.length-2);
    var current_pic_height=$("#tag-img").css("height");
    current_pic_height=current_pic_height.substring(0,current_pic_height.length-2);

    for(var x=0;x<ProjectInfo[current_picture].recOfPic.length;x++) {
        var new_rec = $('<div class="imageLabel-imgdrop hasBorder ' + (ProjectInfo[current_picture].recOfPic[x].description[0]!="" ? "imageLabel-drop-has" : "") + '"><span class="imageLabel-imgdrop-font">' + (ProjectInfo[current_picture].recOfPic[x].description[0] || "") + '</span><div class="imageLable-i-s"></div></div>');
        //8个矩形区域中用于调整矩形区域大小的点
        for (var y = 0; 8 > y; y++) new_rec.find(".imageLable-i-s").append('<i class="imageLable-i">');
        for (var a = 0; 4 > a; a++) new_rec.append('<em class="imageLable-em">');
        var current_data={
            x:ProjectInfo[current_picture].recOfPic[x].beginX,
            y:ProjectInfo[current_picture].recOfPic[x].beginY,
            ex:ProjectInfo[current_picture].recOfPic[x].endX,
            ey:ProjectInfo[current_picture].recOfPic[x].endY,
            name:ProjectInfo[current_picture].recOfPic[x].description[0],
            isRec:true,
            border_color:ProjectInfo[current_picture].recOfPic[x].border_color,
            border_width:ProjectInfo[current_picture].recOfPic[x].border_width,
            font_width:ProjectInfo[current_picture].recOfPic[x].font_width,
            font_color:ProjectInfo[current_picture].recOfPic[x].font_color,
            isbold:ProjectInfo[current_picture].recOfPic[x].isBold?"700":"400"
        };
        var borderColor =ProjectInfo[current_picture].recOfPic[x].border_color ;
        var borderWidth  = ProjectInfo[current_picture].recOfPic[x].border_width;
        var sr = borderColor +" "+borderWidth+ " solid";
        new_rec.css({
            left: 100 * (0 < current_data.ex - current_data.x ? current_data.x : current_data.ex) + "%",
            top: 100 * (0 < current_data.ey - current_data.y ? current_data.y : current_data.ey) + "%",
            width: 100 * Math.abs(current_data.ex - current_data.x) + "%",
            height: 100 * Math.abs(current_data.ey - current_data.y) + "%",
            border: sr
        }).attr("data-json", JSON.stringify(current_data));
        new_rec.find(".imageLabel-imgdrop-font").css("font-size",ProjectInfo[current_picture].recOfPic[x].font_width);
        new_rec.find(".imageLabel-imgdrop-font").css("color",ProjectInfo[current_picture].recOfPic[x].font_color);
        new_rec.find(".imageLabel-imgdrop-font").css("font-weight",ProjectInfo[current_picture].recOfPic[x].isBold?"bold":"normal");
        $(".imageLabel-content").append(new_rec);
    }
    var left,right,top,low;
    for(x=0;x<ProjectInfo[current_picture].pencilOfPic.length;x++){
        context.strokeStyle= ProjectInfo[current_picture].pencilOfPic[x].border_color;
        var temp_width= ProjectInfo[current_picture].pencilOfPic[x].border_width;
        context.lineWidth=temp_width.substring(0,temp_width.length-2);
        left=ProjectInfo[current_picture].pencilOfPic[x].path[0].x*current_pic_width;
        right=ProjectInfo[current_picture].pencilOfPic[x].path[0].x*current_pic_width;
        top=ProjectInfo[current_picture].pencilOfPic[x].path[0].y*current_pic_height;
        low=ProjectInfo[current_picture].pencilOfPic[x].path[0].y*current_pic_height;
        context.beginPath();
        for (y = 0; y < ProjectInfo[current_picture].pencilOfPic[x].path.length - 1; y++) {
            context.moveTo(ProjectInfo[current_picture].pencilOfPic[x].path[y].x*current_pic_width, ProjectInfo[current_picture].pencilOfPic[x].path[y].y*current_pic_height);
            context.lineTo(ProjectInfo[current_picture].pencilOfPic[x].path[y + 1].x*current_pic_width, ProjectInfo[current_picture].pencilOfPic[x].path[y + 1].y*current_pic_height);
            context.stroke();
            if(ProjectInfo[current_picture].pencilOfPic[x].path[y + 1].x*current_pic_width<left) left=ProjectInfo[current_picture].pencilOfPic[x].path[y + 1].x*current_pic_width;
            if(ProjectInfo[current_picture].pencilOfPic[x].path[y + 1].x*current_pic_width>right) right=ProjectInfo[current_picture].pencilOfPic[x].path[y + 1].x*current_pic_width;
            if(ProjectInfo[current_picture].pencilOfPic[x].path[y + 1].y*current_pic_height<top) top=ProjectInfo[current_picture].pencilOfPic[x].path[y + 1].y*current_pic_height;
            if(ProjectInfo[current_picture].pencilOfPic[x].path[y + 1].y*current_pic_height>low) low=ProjectInfo[current_picture].pencilOfPic[x].path[y + 1].y*current_pic_height;
        }
        var temp_tag = ProjectInfo[current_picture].pencilOfPic[x].description.length > 0 ? ProjectInfo[current_picture].pencilOfPic[x].description[0] : "";
        var new_image = document.getElementById("test1");
        new_image.setAttribute("src",canvas.toDataURL());
        while(!new_image.complete){
            sleep(50);
        }
        //download(canvas);
        var jietu = document.getElementById("jietu_canvas");
        jietu.width = right - left + size * 4;
        jietu.height = low - top + size * 4;
        var jietu_context = jietu.getContext("2d");
        jietu_context.drawImage(new_image, left - size*2, top - size*2, right - left + size * 4, low - top + size * 4, 0, 0, right - left + size * 4, low - top + size * 4);
        var jietu_img = document.getElementById("test2");
        jietu_img.setAttribute("src",jietu.toDataURL());
        while(!jietu_img.complete) {
            sleep(50);
        }
        console.log(jietu_img.complete);
        var imgdata = jietu.toDataURL();
        context.clearRect(0, 0, canvasWidth, canvasHeight);
        jietu_context.clearRect(0, 0, jietu.width, jietu.height);
        new_rec = $('<div class="imageLabel-imgdrop noBorder ' + (temp_tag != "" ? "imageLabel-drop-has" : "") + '"><span class="imageLabel-imgdrop-font">' + (temp_tag || "") + '</span><div class="imageLable-i-s"></div></div>');
        //8个矩形区域中用于调整矩形区域大小的点
        //for (var z = 0; 8 > z; z++) new_rec.find(".imageLable-i-s").append('<i class="imageLable-i">');
        for (a = 0; 4 > a; a++) new_rec.append('<em class="imageLable-em">');
        current_data = {
            x: left / current_pic_width,
            y: top / current_pic_height,
            ex: right / current_pic_width,
            ey: low / current_pic_height,
            name: temp_tag,
            isRec: false,
            path: ProjectInfo[current_picture].pencilOfPic[x].path,
            border_color:ProjectInfo[current_picture].pencilOfPic[x].border_color,
            border_width:ProjectInfo[current_picture].pencilOfPic[x].border_width,
            font_width:ProjectInfo[current_picture].pencilOfPic[x].font_width,
            font_color:ProjectInfo[current_picture].pencilOfPic[x].font_color,
            isbold:ProjectInfo[current_picture].pencilOfPic[x].isBold?"700":"400"
        };
        new_rec.css({
            left: 100 * (0 < current_data.ex - current_data.x ? current_data.x : current_data.ex) + "%",
            top: 100 * (0 < current_data.ey - current_data.y ? current_data.y : current_data.ey) + "%",
            width: 100 * Math.abs(current_data.ex - current_data.x) + "%",
            height: 100 * Math.abs(current_data.ey - current_data.y) + "%",
            background: "url(" + imgdata + ")"
        }).attr("data-json", JSON.stringify(current_data));
        new_rec.find(".imageLabel-imgdrop-font").css("font-size",ProjectInfo[current_picture].pencilOfPic[x].font_width);
        new_rec.find(".imageLabel-imgdrop-font").css("color",ProjectInfo[current_picture].pencilOfPic[x].font_color);
        new_rec.find(".imageLabel-imgdrop-font").css("font-weight",ProjectInfo[current_picture].pencilOfPic[x].isBold?"bold":"normal");
        new_rec.css("background-size", "100% 100%");
        $(".imageLabel-content").append(new_rec);
    };
    $(".imageLabel-imgdrop").mouseover(function(event){
        var is=event.target.getElementsByClassName("imageLable-i-s");
        is[0].style.display="block";
    });
    $(".imageLabel-content").click(function(event){
        $(".imageLable-i-s").css("display","none");
    });
    document.getElementById("field2").value=ProjectInfo[current_picture].pictureTotalDes?ProjectInfo[current_picture].pictureTotalDes:"";
}

//5.对标注页面信息及图片url以及标记进行存储
/*
* 不完整
* 待填充
* */
function save() {
    //4.1保存暂时标注页面的文字信息
    // var describe=document.getElementById("right").innerHTML;
    //4.2保存主工作板页面的图片信息（不完整待填充）
    var picture=document.getElementById("workspace").innerHTML;

}

//6.工具栏下的函数调用
/*
* 不完整
* 待填充
* */
//6.1手（id:hand）的函数调用
function clickHand() {
    //back();
    alert("选中选择按钮 现在你可以通过单击图片上的标记 查看或修改该标记的描述");
    current_operate="no_choose";
}
//6.2铅笔(id:pen)的函数调用
function clickPen() {
    //document.getElementById("work_area").style.cursor=url("https://i.imgur.com/m1pR4Fs.png");
    current_operate="pencil";
    $(".imageLabel-content").css("pointer-events","none");
    $("#canvas_bak").css("cursor","url(\"https://i.imgur.com/CoXAzgD.gif\"),auto");
    $("#canvas").css("cursor","url(\"https://i.imgur.com/CoXAzgD.gif\"),auto");
    $("#canvas_bak").css("pointer-events","auto");
    document.getElementById("canvas").style.display="";
    document.getElementById("canvas_bak").style.display="";
    draw_graph('pencil',this);
}
//6.3矩形(id:rectangle)的函数调用
function clickRectangle() {
    $(".imageLabel-content").css("cursor","crosshair");
    current_operate="rectangle";
    $(".imageLabel-content").css("pointer-events","auto");
    document.getElementById("canvas").style.display="none";
    document.getElementById("canvas_bak").style.display="none";
    //draw_graph('square',this);
}

//实现清屏功能
function clickEmpty(){
    $.confirm({
        title: '确认清屏',
        content: '是否确认清空该图片上所做的所有标注和描述，清屏将无法还原，确认您的选择',
        confirmButton: '确认清屏',
        cancelButton: '取消',
        confirmButtonClass: 'btn-info',
        icon: 'fa fa-question-circle',
        animation: 'rotate',
        closeAnimation: 'right',
        confirm: function () {
            $(".imageLabel-content").empty();
        }
    });
}

//--------------------------------------------------公共函数供调用----------------------------------------------------//

//画图形
function draw_graph(graphType,obj){
    //设置当前操作类型
    current_operate=graphType;
    var canDraw = false;

    var startX;
    var startY;

    //鼠标按下获取 开始xy开始画图
    function mousedown(e){

        if(current_operate=='no_choose')
            return;

        context.strokeStyle= "#"+ $("#colorpicker-popup").val();
        context.lineWidth=$("#penWidth").val();
        context_bak.strokeStyle= "#"+ $("#colorpicker-popup").val();
        context_bak.lineWidth = $("#penWidth").val();
        e=e||window.event;
        startX = (e.clientX - canvasLeft);
        startY = (e.clientY - canvasTop);
        context_bak.moveTo(startX ,startY );
        canDraw = true;
        mousedown_x=startX;
        mousedown_y=startY;
        if(graphType == 'pencil'){
            context_bak.beginPath();
        }else if(graphType == 'circle'){
            context.beginPath();
            context.moveTo(startX ,startY );
            context.lineTo(startX +2 ,startY+2);
            context.stroke();

        }else if(graphType == 'rubber'){
            context.clearRect(startX - size * 10 , startY - size * 10 , size * 20 , size * 20);
        }
    };

    //鼠标离开 把蒙版canvas的图片生成到canvas中
    function mouseup(e){
        if(current_operate=='no_choose')
            return;
        e=e||window.event;
        canDraw = false;
        cancelListForType.push(graphType);
        var current_pic_width=$("#tag-img").css("width");
        current_pic_width=current_pic_width.substring(0,current_pic_width.length-2);
        var current_pic_height=$("#tag-img").css("height");
        current_pic_height=current_pic_height.substring(0,current_pic_height.length-2);
        if(pencil_path.length>50) {
            //实现曲线自动闭合
            var mouseup_x = pencil_path[pencil_path.length - 1].x*current_pic_width;
            var mouseup_y = pencil_path[pencil_path.length - 1].y*current_pic_height;
            var distance = Math.pow((Math.pow((mouseup_x - mousedown_x), 2) + Math.pow((mouseup_y - mousedown_y), 2)), 0.5);
            var num_of_pos = Math.floor(distance * storage_ratio);
            var mousedown_ten_x = pencil_path[test_num - 1].x*current_pic_width;
            var mousedown_ten_y = pencil_path[test_num - 1].y*current_pic_height;
            var mouseup_ten_x = pencil_path[pencil_path.length - test_num].x*current_pic_width;
            var mouseup_ten_y = pencil_path[pencil_path.length - test_num].y*current_pic_height;
            var k1 = (mousedown_ten_y - mousedown_y) / (mousedown_ten_x - mousedown_x);
            var k2 = (mouseup_ten_y - mouseup_y) / (mouseup_ten_x - mouseup_x);
            var b1 = mousedown_y - k1 * mousedown_x;
            var b2 = mouseup_y - k2 * mouseup_x;
            if (Math.pow((Math.pow((mouseup_x - mousedown_x), 2) + Math.pow((mouseup_y - mousedown_y), 2)), 0.5)<100) {
                var result_x,result_y;
                if(Math.abs(k1 - k2) <= test_paralla_para){
                    result_x=(mouseup_x+mousedown_x)/2;
                    result_y=(mouseup_y+mousedown_y)/2;
                }else {
                    result_x = (b2 - b1) / (k1 - k2);
                    result_y = k1 * result_x + b1;
                }
                var mousedown_pos = Object.create(Position);
                mousedown_pos.x = mousedown_x;
                mousedown_pos.y = mousedown_y;
                var mouseup_pos = Object.create(Position);
                mouseup_pos.x = mouseup_x;
                mouseup_pos.y = mouseup_y;
                var result_pos = Object.create(Position);
                result_pos.x = result_x;
                result_pos.y = result_y;
                var p = new Array();
                p.push(mouseup_pos);
                p.push(result_pos);
                p.push(mousedown_pos);
                var cur = curve(p, num_of_pos);
                for (var ttt = 0; ttt <= num_of_pos; ttt++) {
                    var u = cur(ttt);
                    context_bak.lineTo(u.x, u.y);
                    //console.log(u.x+" "+u.y);
                    context_bak.stroke();
                    var current_position = Object.create(Position);
                    current_position.x = u.x/current_pic_width;
                    current_position.y = u.y/current_pic_height;
                    pencil_path.push(current_position);
                }
                context_bak.lineTo(mousedown_x, mousedown_y);
                context_bak.stroke();
            }
            var image = new Image();
            image.src = canvas_bak.toDataURL();
            image.onload = function () {
                context.drawImage(image, 0, 0, image.width, image.height, 0, 0, canvasWidth, canvasHeight);
                saveImageToAry();
                var x = (e.clientX - canvasLeft);
                var y = (e.clientY - canvasTop);
                if (graphType == 'square') {
                    //describeInitial();
                    var tempRec = Object.create(RecStore);
                    tempRec.beginX = startX;
                    tempRec.beginY = startY;
                    tempRec.endX = x;
                    tempRec.endY = y;
                    tempRec.description = [];
                    reclist.push(tempRec);
                }

                if (graphType == 'pencil') {
                    var left = pencil_path[0].x*current_pic_width;
                    var right = pencil_path[0].x*current_pic_width;
                    var top = pencil_path[0].y*current_pic_height;
                    var low = pencil_path[0].y*current_pic_height;
                    for (var temp_i = 0; temp_i < pencil_path.length; temp_i++) {
                        if (pencil_path[temp_i].x*current_pic_width < left) left = pencil_path[temp_i].x*current_pic_width;
                        if (pencil_path[temp_i].x*current_pic_width > right) right = pencil_path[temp_i].x*current_pic_width;
                        if (pencil_path[temp_i].y*current_pic_height < top) top = pencil_path[temp_i].y*current_pic_height;
                        if (pencil_path[temp_i].y*current_pic_height > low) low = pencil_path[temp_i].y*current_pic_height;
                    }
                    var jietu = document.getElementById("jietu_canvas");
                    jietu.width = right - left + size * 4;
                    jietu.height = low - top + size * 4;
                    var jietu_context = jietu.getContext("2d");
                    jietu_context.drawImage(image, left - size*2, top - size*2, right - left + size * 4, low - top + size * 4, 0, 0, right - left + size * 4, low - top + size * 4);
                    var imgdata = jietu.toDataURL();
                    jietu_context.clearRect(0, 0, jietu.width, jietu.height);
                    var temp_tag = "";
                    var new_rec = $('<div class="imageLabel-imgdrop noBorder "><span class="imageLabel-imgdrop-font">' + (temp_tag || "") + '</span><div class="imageLable-i-s"></div></div>');
                    //8个矩形区域中用于调整矩形区域大小的点
                    //for (var z = 0; 8 > z; z++) new_rec.find(".imageLable-i-s").append('<i class="imageLable-i">');
                    for (var a = 0; 4 > a; a++) new_rec.append('<em class="imageLable-em">');
                    new_rec.addClass("imageLabel-drop-edit");
                    new_rec.siblings().removeClass("imageLabel-drop-edit");
                    $.confirm({
                        title: '请您为做出的标注添加描述',
                        content: 'url:/html-usage/AddTagConfirm.html',
                        confirmButton: '确认',
                        cancelButton: '取消',
                        confirmButtonClass: 'btn-info',
                        icon: 'fa fa-question-circle',
                        animation: 'rotate',
                        closeAnimation: 'right',
                        confirm: function () {
                            temp_tag = document.getElementById("input-name").value;
                        },
                        onClose: function () {
                            context.clearRect(0, 0, canvasWidth, canvasHeight);
                            context_bak.clearRect(0, 0, canvasWidth, canvasHeight);
                            var current_pic_width = $("#tag-img").css("width");
                            current_pic_width = current_pic_width.substring(0, current_pic_width.length - 2);
                            var current_pic_height = $("#tag-img").css("height");
                            current_pic_height = current_pic_height.substring(0, current_pic_height.length - 2);
                            var current_data = {
                                x: left / current_pic_width,
                                y: top / current_pic_height,
                                ex: right / current_pic_width,
                                ey: low / current_pic_height,
                                name: temp_tag,
                                isRec: false,
                                path: pencil_path
                            };
                            var fontColor = "#" + $("#colorpicker-font").val();
                            var fontWidth = $("#fontSize").val();
                            new_rec.find(".imageLabel-imgdrop-font").css("font-size", fontWidth);
                            new_rec.find(".imageLabel-imgdrop-font").css("color", fontColor);
                            var fontWeight = $("#boldOption").get(0).checked;
                            fontWeight = fontWeight ? "bold" : "normal";
                            new_rec.find(".imageLabel-imgdrop-font").css("font-weight", fontWeight);
                            new_rec.css({
                                left: 100 * (0 < current_data.ex - current_data.x ? current_data.x : current_data.ex) + "%",
                                top: 100 * (0 < current_data.ey - current_data.y ? current_data.y : current_data.ey) + "%",
                                width: 100 * Math.abs(current_data.ex - current_data.x) + "%",
                                height: 100 * Math.abs(current_data.ey - current_data.y) + "%",
                                background: "url(" + imgdata + ")"
                            }).attr("data-json", JSON.stringify(current_data));
                            new_rec.css("background-size", "100% 100%");
                            new_rec.find(".imageLabel-imgdrop-font").html(temp_tag);
                            if (temp_tag != "") new_rec.addClass("imageLabel-drop-has");
                            new_rec.removeClass("imageLabel-drop-edit");
                            $(".imageLabel-content").append(new_rec);
                            new_rec.addClass("imageLabel-drop-now");
                            new_rec.siblings().removeClass("imageLabel-drop-now");
                            new_rec.click(function(event){
                                new_rec.addClass("imageLabel-drop-now");
                                new_rec.siblings().removeClass("imageLabel-drop-now");
                            });
                            recordStyle(new_rec);
                            var thisPencilPath = Object.create(PencilTag);
                            thisPencilPath.path = pencil_path;
                            thisPencilPath.description = [];
                            thisPencilPath.description.push(temp_tag);
                            pencilList.push(thisPencilPath);
                            pencil_path = [];
                        }
                    });
                    //}
                }
            }
        }else{
            $.alert({
                title: '请您为做出的标注添加描述',
                content: '您绘制的曲线过小或过短，不是有效标注，请重新画一条',
                confirmButton: '重画',
                confirmButtonClass: 'btn-info',
                icon: 'fa fa-question-circle',
                animation: 'rotate',
                closeAnimation: 'right',
            });
            context.clearRect(0, 0, canvasWidth, canvasHeight);
            context_bak.clearRect(0, 0, canvasWidth, canvasHeight);
            pencil_path = [];
        }

            //context.beginPath();
            //context.moveTo(x ,y );
            //context.lineTo(x +2 ,y+2);
            //context.stroke();
            //jietu_context.clearRect(0,0,jietu.width,jietu.height);
            clearContext();
    };

    // 鼠标移动
    function mousemove(e){

        var current_pic_width=$("#tag-img").css("width");
        current_pic_width=current_pic_width.substring(0,current_pic_width.length-2);
        var current_pic_height=$("#tag-img").css("height");
        current_pic_height=current_pic_height.substring(0,current_pic_height.length-2);

        if(current_operate=='no_choose')
            return;

        e=e||window.event;
        var x = (e.clientX - canvasLeft);
        var y = (e.clientY - canvasTop);
        //方块 4条直线搞定
        if(graphType == 'square'){
            if(canDraw){
                context_bak.beginPath();
                clearContext();
                context_bak.moveTo(startX , startY);
                context_bak.lineTo(x ,startY );
                context_bak.lineTo(x ,y );
                context_bak.lineTo(startX ,y );
                context_bak.lineTo(startX ,startY );
                context_bak.stroke();
            }
            //直线
        }else if(graphType =='line'){
            if(canDraw){
                context_bak.beginPath();
                clearContext();
                context_bak.moveTo(startX , startY);
                context_bak.lineTo(x ,y );
                context_bak.stroke();
            }
            //画笔
        }else if(graphType == 'pencil'){
            if(canDraw){
                context_bak.lineTo(e.clientX - canvasLeft ,e.clientY - canvasTop);
                context_bak.stroke();
                var current_position=Object.create(Position);
                current_position.x=(e.clientX - canvasLeft)/current_pic_width;
                current_position.y=(e.clientY - canvasTop)/current_pic_height;
                pencil_path.push(current_position);
            }
            //圆 未画得时候 出现一个小圆
        }else if(graphType == 'circle'){
            clearContext();
            if(canDraw){
                context_bak.beginPath();
                var radii = Math.sqrt((startX - x) * (startX - x) + (startY - y) * (startY - y));
                context_bak.arc(startX,startY,radii,0,Math.PI * 2,false);
                context_bak.stroke();
            }else{
                context_bak.beginPath();
                context_bak.arc(x,y,20,0,Math.PI * 2,false);
                context_bak.stroke();
            }
            //涂鸦 未画得时候 出现一个小圆
        }else if(graphType == 'handwriting'){
            if(canDraw){
                context_bak.beginPath();
                context_bak.strokeStyle = color;
                context_bak.fillStyle = color;
                context_bak.arc(x,y,size*10,0,Math.PI * 2,false);
                context_bak.fill();
                context_bak.stroke();
                context_bak.restore();
            }else{
                clearContext();
                context_bak.beginPath();
                context_bak.fillStyle = color;
                context_bak.arc(x,y,size*10,0,Math.PI * 2,false);
                context_bak.fill();
                context_bak.stroke();
            }
            //橡皮擦 不管有没有在画都出现小方块 按下鼠标 开始清空区域
        }else if(graphType == 'rubber'){
            context_bak.lineWidth = 1;
            clearContext();
            context_bak.beginPath();
            context_bak.strokeStyle = '#000000';
            context_bak.moveTo(x - size * 10 , y - size * 10 );
            context_bak.lineTo(x + size * 10 , y - size * 10 );
            context_bak.lineTo(x + size * 10 , y + size * 10 );
            context_bak.lineTo(x - size * 10 , y + size * 10 );
            context_bak.lineTo(x - size * 10 , y - size * 10 );
            context_bak.stroke();
            if(canDraw){
                context.clearRect(x - size * 10 , y - size * 10 , size * 20 , size * 20);
            }
        }
    };

    //鼠标离开区域以外 除了涂鸦 都清空
    function mouseout(){
        if(graphType != 'handwriting'){
            clearContext();
        }
    }

    $("#canvas_bak").unbind();
    $("#canvas_bak").bind('mousedown',mousedown);
    $("#canvas_bak").bind('mousemove',mousemove);
    $("#canvas_bak").bind('mouseup',mouseup);
    $("#canvas_bak").bind('mouseout',mouseout);
}

//清空层
function clearContext(type){
    if(!type){
        context_bak.clearRect(0,0,canvasWidth,canvasHeight);
    }else{
        context.clearRect(0,0,canvasWidth,canvasHeight);
        context_bak.clearRect(0,0,canvasWidth,canvasHeight);
    }
}
//初始化
function initCanvas(){
    var current_pic_width=$("#tag-img").css("width");
    current_pic_width=current_pic_width.substring(0,current_pic_width.length-2);
    var current_pic_height=$("#tag-img").css("height");
    current_pic_height=current_pic_height.substring(0,current_pic_height.length-2);
    canvasHeight=current_pic_height;
    canvasWidth=current_pic_width;
    canvasTop = $(".imageLabel-jisuan").offset().top;
    canvasLeft = $(".imageLabel-jisuan").offset().left;
    canvas =  document.getElementById("canvas");
    canvas.width = canvasWidth;
    canvas.height = canvasHeight;
    context = canvas.getContext('2d');

    canvas_bak =  document.getElementById("canvas_bak");
    canvas_bak.width = canvasWidth;
    canvas_bak.height = canvasHeight;
    context_bak = canvas_bak.getContext('2d');
}

//撤销上一个操作
// function cancel(){
//     if(cancelList.length>0) {
//         context.clearRect(0, 0, canvasWidth, canvasHeight);
//         var image = new Image();
//         redoList.push(cancelList[cancelList.length - 1]);
//         cancelList.pop();
//         redoListForType.push(cancelListForType[cancelListForType.length-1]);
//         cancelListForType.pop();
//         if(redoListForType[redoListForType.length-1]=='square'){
//             reclistForRedo.push(reclist[reclist.length-1]);
//             reclist.pop();
//         }else if(redoListForType[redoListForType.length-1]=='pencil'){
//             pencilListForRedo.push(pencilList[pencilList.length-1]);
//             pencilList.pop();
//         }
//         if(cancelList.length!=0) {
//             var url = cancelList[cancelList.length - 1];
//             image.src = url;
//             image.onload = function () {
//                 context.drawImage(image, 0, 0, image.width, image.height, 0, 0, canvasWidth, canvasHeight);
//             }
//         }else{
//             clearContext();
//         }
//     }
// }

//重做上一个操作
// function next(){
//     if(redoList.length>0) {
//         context.clearRect(0,0,canvasWidth,canvasHeight);
//         var  image = new Image();
//         cancelList.push(redoList[redoList.length - 1]);
//         redoList.pop();
//         cancelListForType.push(redoListForType[redoListForType.length-1]);
//         redoListForType.pop();
//         if(cancelListForType[cancelListForType.length-1]=='square'){
//             reclist.push(reclistForRedo[reclistForRedo.length-1]);
//             reclistForRedo.pop();
//         }else if(cancelListForType[cancelListForType.length-1]=='pencil'){
//             pencilList.push(pencilListForRedo[reclistForRedo.length-1]);
//             pencilListForRedo.pop();
//         }
//         var url = cancelList[cancelList.length-1];
//         image.src = url;
//         image.onload = function () {
//             context.drawImage(image, 0, 0, image.width, image.height, 0, 0, canvasWidth, canvasHeight);
//         }
//     }
// }

//保存历史 用于撤销
function saveImageToAry(){
    var dataUrl =  canvas.toDataURL();
    cancelList.push(dataUrl);
}

function close() {
    save_change();
}


function save_change() {

    //alert("yes");

    //选择一下现在正在工作的这张图片，来存储这张图片的标注信息
    choosePic(pictures[current_picture],current_picture);

    //1.随后对数据的图片进行更新
    var pics=[];
    for(var i=0;i<ProjectInfo.length;i++){
        var temp={};
        //ProjectInfo[i].pictureTotalDes
        temp.aroundDesc=ProjectInfo[i].pictureTotalDes;
        //ProjectInfo[i].pencilOfPic
        temp.pencilTag=ProjectInfo[i].pencilOfPic;
        //ProjectInfo[i].recOfPic
        temp.recTag=ProjectInfo[i].recOfPic;
        temp.url=ProjectInfo[i].pictureURL.substring(7);
        pics.push(temp);
    }

    //alert(JSON.stringify(ProjectInfo));

    var temp_data={pid:project_id,uid:user_id,pictures:pics};

    //2.进行最终的保存
    $.ajax({
        type : 'POST',
        url : '/personalTag/update',
        contentType:"application/json",
        dataType:"json",
        data :JSON.stringify(temp_data),
        success : function (data) {
            $.alert({
                title: '系统已自动保存修改结果！',
                content: '自动保存成功 <br> 下次可重新进入该工作页面继续修改该项目执行结果',
                confirmButton: '我知道了',
                confirmButtonClass: 'btn-primary',
                icon: 'fa fa-info',
                animation: 'rotate',
                closeAnimation: 'right',
                opacity: 0.5,
                confirm: function () {
                    window.location.href="/cwzz/project_detail";
                }
            });
        },
        error: function (data) {
            $.alert({
                title: '保存本次修改结果失败！',
                content: '由于某些不知名的原因，项目修改结果保存失败，请尝试重新保存',
                confirmButton: '重新保存',
                confirmButtonClass: 'btn-primary',
                icon: 'fa fa-info',
                animation: 'rotate',
                closeAnimation: 'right',
                opacity: 0.5,
            });
        }
    })
}

//demo
function imageLabel(l) {
    function x() {
        function v() {
            var a = $(".imageLabel-imgdrop"),
                e = [];
            //遍历所有的矩形框
            a.each(function(a, c) {
                a = $(c).width() * $(c).height();
                e.push(a)
            });
            a.each(function(a, c) {
                a = $(c);
                var b = a.width() * a.height(),
                    d = 0;
                //遍历e这个数组
                $.each(e, function(a, c) {
                    b <= c && d++
                });
                a.css({
                    "z-index": d
                })
            })
        }

        //控制图片比例和原图片一致，控制图片不失真
        function l() {
            var a = $(".imageLabel-img"),
                b = $(".imageLabel-jisuan"),
                d = a[0].naturalWidth,
                f = a[0].naturalHeight,
                g = a.parents(".imageLabel-img-body").width();
            a = a.parents(".imageLabel-img-body").height();
            d / f > g / a ? b.css({
                width: "100%",
                height: f / d * g
            }) : b.css({
                height: "100%",
                width: d / f * a
            })
        }
        var b, d, a, f, w, k = $(".imageLabel-content"),
            r = !1,
            m = !1,
            q = !1,
            n = $(".imageLabel-drap-menu");
        $(".imageLabel-content")[0].oncontextmenu = function(e) {
            //alert(e.clientX+" "+e.clientY);
            return !1
        };
        n[0].oncontextmenu = function() {
            return !1
        };
        $("body").click(function(a) {
            n.hide()
        });
        //构造每一个标注区域
        $.each(g.data, function(a, b) {
            d = $('<div class="imageLabel-imgdrop ' + (b.name ? "imageLabel-drop-has" : "") + '"><span class="imageLabel-imgdrop-font">' + (b.name || "") + '</span><div class="imageLable-i-s"></div></div>');
            //8个矩形区域中用例调整矩形区域大小的点
            for (a = 0; 8 > a; a++) d.find(".imageLable-i-s").append('<i class="imageLable-i">');
            if (g.shade) for (a = 0; 4 > a; a++) d.append('<em class="imageLable-em">');
            d.css({
                left: 100 * (0 < b.ex - b.x ? b.x : b.ex) + "%",
                top: 100 * (0 < b.ey - b.y ? b.y : b.ey) + "%",
                width: 100 * Math.abs(b.ex - b.x) + "%",
                height: 100 * Math.abs(b.ey - b.y) + "%"
            }).attr("data-json", JSON.stringify(b));
            k.append(d)
        });
        v();
        var path=[];
        var result_path=[];
        $(".imageLabel-content").mousedown(function(c) {
            console.log("yes");
            if (2 != c.button) {
                //点击了方框
                if (n.hide(), r = !0, k = $(this), b = {
                        x: k.offset().left,
                        y: k.offset().top,
                        cx: c.clientX,
                        cy: c.clientY,
                        w: k.width(),
                        h: k.height()
                    }, a = {
                        x: (b.cx - b.x) / b.w,
                        y: (b.cy - b.y) / b.h,
                        ex: 0,
                        ey: 0
                    }, $(c.target).hasClass("imageLabel-imgdrop")) {
                    m = !0, d = $(c.target), f = JSON.parse(d.attr("data-json")), a = $.extend({}, f), g.startArea(), JSON.parse(d.attr("data-json")).isRec ? path = [] : path = JSON.parse(d.attr("data-json")).path;
                    result_path=path;
                    //点击了调整方框大小的按钮
                }else if ($(c.target).hasClass("imageLable-i")) q = !0, w = $(c.target), d = $(c.target).parents(".imageLabel-imgdrop"), f = JSON.parse(d.attr("data-json")), a = $.extend({}, f);
                //创建新的标记
                else {
                    (new Date).getTime();
                    m = !1;
                    d = $('<div class="imageLabel-imgdrop hasBorder "><span class="imageLabel-imgdrop-font"></span><div class="imageLable-i-s"></div></div>');
                    for (var e = 0; 8 > e; e++) d.find(".imageLable-i-s").append('<i class="imageLable-i">');
                    if (g.shade) for (e = 0; 4 > e; e++) d.append('<em class="imageLable-em">');
                    setStyle(d);
                    d.addClass("imageLabel-drop-edit").appendTo(k);
                    d.addClass("imageLabel-drop-now");
                    d.siblings().removeClass("imageLabel-drop-now");
                    d.siblings().removeClass("imageLabel-drop-edit");
                    d.find(".imageLable-i-s").css("display","block");
                    d.mouseover(function(event){
                        var is=event.target.getElementsByClassName("imageLable-i-s");
                        is[0].style.display="block";
                    });
                    d.click(function(event){
                        d.addClass("imageLabel-drop-now");
                        d.siblings().removeClass("imageLabel-drop-now");
                    });
                }
                d.addClass("imageLabel-drop-now");
                g.only && $(this).find(".imageLabel-imgdrop").hide();
            } else $(c.target).hasClass("imageLabel-imgdrop") && (d = $(c.target), setTimeout(function() {
                n.css({
                    left: c.clientX-width/5,
                    top: c.clientY-height/15-72
                }).show()
            }, 0))
        });
        //控制拖拽移动矩形标注
        $(".imageLabel-img-boxs").mousemove(function(c) {
            if (r) {
                if (m) {
                    a.x = f.x + (c.clientX - b.cx) / b.w, a.ex = f.ex + (c.clientX - b.cx) / b.w, a.y = f.y + (c.clientY - b.cy) / b.h, a.ey = f.ey + (c.clientY - b.cy) / b.h;
                    if(!JSON.parse(d.attr("data-json")).isRec){
                        var fuck_bug=JSON.parse(d.attr("data-json")).path;
                        for(var temp=0;temp<fuck_bug.length;temp++){
                            result_path[temp].x=fuck_bug[temp].x+(c.clientX-b.cx);
                            result_path[temp].y=fuck_bug[temp].y+(c.clientY-b.cy);
                        }
                    }
                }else if (q) {
                    //点击拉动大小的按钮
                    var e = w.index();
                    0 == e && (a.x = f.x + (c.clientX - b.cx) / b.w, a.y = f.y + (c.clientY - b.cy) / b.h);
                    1 == e && (a.ex = f.ex + (c.clientX - b.cx) / b.w, a.y = f.y + (c.clientY - b.cy) / b.h);
                    2 == e && (a.ex = f.ex + (c.clientX - b.cx) / b.w, a.ey = f.ey + (c.clientY - b.cy) / b.h);
                    3 == e && (a.x = f.x + (c.clientX - b.cx) / b.w, a.ey = f.ey + (c.clientY - b.cy) / b.h);
                    4 == e && (a.y = f.y + (c.clientY - b.cy) / b.h);
                    5 == e && (a.ex = f.ex + (c.clientX - b.cx) / b.w);
                    6 == e && (a.ey = f.ey + (c.clientY - b.cy) / b.h);
                    7 == e && (a.x = f.x + (c.clientX - b.cx) / b.w);
                } else {
                    a.ex = (c.clientX - b.x) / b.w, a.ey = (c.clientY - b.y) / b.h;
                }
                0 > a.y && (a.y = 0);
                0 > a.x && (a.x = 0);
                0 > a.ey && (a.ey = 0);
                0 > a.ex && (a.ex = 0);
                1 < a.ey && (a.ey = 1);
                1 < a.ex && (a.ex = 1);
                1 < a.y && (a.y = 1);
                1 < a.x && (a.x = 1);
                d.css({
                    left: 100 * (0 < a.ex - a.x ? a.x : a.ex) + "%",
                    top: 100 * (0 < a.ey - a.y ? a.y : a.ey) + "%",
                    width: 100 * Math.abs(a.ex - a.x) + "%",
                    height: 100 * Math.abs(a.ey - a.y) + "%"
                }).addClass("imageLabel-drop-move")
            }
        }).mouseup(function(c) {
            if (r) {
                var e = {};
                a.x < a.ex ? (e.x = a.x, e.ex = a.ex) : (e.x = a.ex, e.ex = a.x);
                a.y < a.ey ? (e.y = a.y, e.ey = a.ey) : (e.y = a.ey, e.ey = a.y);
                //e.isRec=d.attr("data-json").isRec;
                d.attr("data-json")?e.isRec=JSON.parse(d.attr("data-json")).isRec:e.isRec=true;
                if(d.attr("data-json"))
                    e.name=JSON.parse(d.attr("data-json")).name;
                else
                    e.name="";
                if(!e.isRec) e.path=result_path;
                d.attr("data-json", JSON.stringify(e));
                recordStyle(d);
                //如果创建的矩形过小，则不允许创建
                10 < Math.abs(c.clientX - b.cx) && 10 < Math.abs(c.clientY - b.cy) && !m && !q ? (g.editPop && (setTimeout(function() {
                    $.confirm({
                        title: '请您为做出的标注添加描述',
                        content: 'url:/html-usage/AddTagConfirm.html',
                        confirmButton: '确认',
                        cancelButton: '取消',
                        confirmButtonClass: 'btn-info',
                        icon: 'fa fa-question-circle',
                        animation: 'rotate',
                        closeAnimation: 'right',
                        confirm: function () {
                            //alert(document.getElementById("input-name").value);
                            d.find(".imageLabel-imgdrop-font").html(document.getElementById("input-name").value);
                            var a = JSON.parse(d.attr("data-json"));
                            a.isRec=true;
                            a.name = document.getElementById("input-name").value;
                            d.attr("data-json", JSON.stringify(a));
                            document.getElementById("input-name").value!="" ? d.addClass("imageLabel-drop-has") : d.removeClass("imageLabel-drop-has");
                        },
                    });
                }, 500)), g.edit(d)) : m || q || d.remove();
                q = m = r = !1;
                v();
                d.removeClass("imageLabel-drop-move");
            }
            g.only && $(this).find(".imageLabel-imgdrop").show()
        });

        //右键菜单中的删除和编辑对应事件
        $(".imageLabel-delete").click(function() {
            d.remove();
            n.hide()
        });
        $(".imageLabel-edit").click(function() {
            g.edit(d);
            d.addClass("imageLabel-drop-edit").siblings().removeClass("imageLabel-drop-edit");
            // g.editPop && (h.addClass("imageLabel-active").find("input").val(d.find(".imageLabel-imgdrop-font").html()), setTimeout(function() {
            //     h.find("input").focus()[0].setSelectionRange(-1, -1)
            // }, 500));
            g.editPop;
            // h.addClass("imageLabel-active");
            n.hide();
            setTimeout(function(){
                $.confirm({
                    title: '请您为做出的标注添加描述',
                    content: 'url:/html-usage/AddTagConfirm.html',
                    confirmButton: '确认',
                    cancelButton: '取消',
                    confirmButtonClass: 'btn-info',
                    icon: 'fa fa-question-circle',
                    animation: 'rotate',
                    closeAnimation: 'right',
                    confirm: function () {
                        d.find(".imageLabel-imgdrop-font").html(document.getElementById("input-name").value);
                        var a = JSON.parse(d.attr("data-json"));
                        a.name = document.getElementById("input-name").value;
                        d.attr("data-json", JSON.stringify(a));
                        document.getElementById("input-name").value!="" ? d.addClass("imageLabel-drop-has") : d.removeClass("imageLabel-drop-has");
                    },
                });
            },500);
        });
        l();
        $(window).resize(l);
        //图片下面的关闭和确认按钮
        $(".imageLabel-closes").click(function() {
            g.close(u.getData()) && (p.removeClass("imageLabel-box-active"), setTimeout(function() {
                p.remove()
            }, 500))
        }).next().click(function() {
            g.confirm(u.getData()) && p.removeClass("imageLabel-box-active")
        })
    }
    if (!l.img) return alert("请填写图片地址"), !1;
    var u = {
            getData: function() {
                var g = [];
                return $(".imageLabel-imgdrop").each(function() {
                    g.push(JSON.parse($(this).attr("data-json")))
                }), g
            },
            clearArea: function() {
                $(".imageLabel-imgdrop").remove()
            },
            close: function() {
                $(".imageLabel-closes").click()
            }
        },
        g = {
            only: !1,
            shade: !0,
            editPop: !0,
            close: function() {
                return !0
            },
            edit: function() {},
            confirm: function() {
                return !0
            },
            startArea: function() {},
            clickArea: function() {},
            data: []
        };
    g = $.extend(g, l);
    var p=$("#image-box");
    $("#tag-img").attr("src",l.img);
    p.find(".imageLabel-img").one("load", function() {
        $(this).addClass("imageLabel-img-active");
        $(".imageLabel-loading-body").hide();
        x();
    });
    setTimeout(function() {
        p.addClass("imageLabel-box-active")
    }, 0);
    return u;
};

//依照demo自定义的点击左侧图片栏之后切换工作区域图片的函数
function changePic(str){
    $("#tag-img").attr("src",str);
    $("#image-box").find(".imageLabel-img").one("load", function() {
        $(this).addClass("imageLabel-img-active");
        $(".imageLabel-loading-body").hide();
        var a = $(".imageLabel-img"),
            b = $(".imageLabel-jisuan"),
            d = a[0].naturalWidth,
            f = a[0].naturalHeight,
            g = a.parents(".imageLabel-img-body").width();
        a = a.parents(".imageLabel-img-body").height();
        d / f > g / a ? b.css({
            width: "100%",
            height: f / d * g
        }) : b.css({
            height: "100%",
            width: d / f * a
        });
        initCanvas();
        draw_again();
    });
}

function sleep(numberMillis) {
    var now = new Date();
    var exitTime = now.getTime() + numberMillis;
    while (true) {
        now = new Date();
        if (now.getTime() > exitTime)
            return;
    }
}

//下载截图 供测试使用
function download(data) {
    var type = "png";
    //将mime-type改为image/octet-stream,强制让浏览器下载
    var imgdata = data.toDataURL();
    type = type.toLocaleLowerCase().replace(/jpg/i, 'jpeg');
    var r = type.match(/png|jpeg|bmp|gif/)[0];
    var fixtype = 'image/' + r
    imgdata = imgdata.replace(fixtype, 'image/octet-stream')
    //将图片保存到本地
    var filename = new Date().toLocaleDateString() + '.' + type;
    var link = document.createElement('a');
    link.href = imgdata;
    link.download = filename;
    var event = document.createEvent('MouseEvents');
    event.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
    link.dispatchEvent(event);
}

//回退到项目详细信息界面
function back(){
    save_change();
}

function setStyle(d){
    //线条颜色和线条粗细
    var borderColor = "#"+ $("#colorpicker-popup").val();
    var borderWidth  = $("#penWidth").val()+"px";
    var sr = borderColor +" "+borderWidth+ " solid";
    d.css("border",sr);
    var fontColor="#"+$("#colorpicker-font").val();
    var fontWidth=$("#fontSize").val();
    d.find(".imageLabel-imgdrop-font").css("font-size",fontWidth);
    d.find(".imageLabel-imgdrop-font").css("color",fontColor);
    var fontWeight = $("#boldOption").get(0).checked;
    fontWeight = fontWeight ? "bold" : "normal";
    d.find(".imageLabel-imgdrop-font").css("font-weight",fontWeight);
}

function recordStyle(d){
    var temp_data=JSON.parse(d.attr("data-json"));
    //线条颜色和线条粗细
    var borderColor = "#"+ $("#colorpicker-popup").val();
    var borderWidth  = $("#penWidth").val()+"px";
    temp_data.border_color=borderColor;
    temp_data.border_width=borderWidth;
    temp_data.font_color=d.find(".imageLabel-imgdrop-font").css("color");
    temp_data.font_width=d.find(".imageLabel-imgdrop-font").css("font-size");
    temp_data.isbold=d.find(".imageLabel-imgdrop-font").css("font-weight");
    d.attr("data-json", JSON.stringify(temp_data));
}

//绘制贝塞尔曲线，自动生成相应的点
function curve(p,lt)//贝塞尔曲线
{
    var sx=0, sy=0, s=p.length-1;
    var q = [];
    for(var i=0, l = p.length-2; i<l; ++i)
    {
        q[i] = {
            x: (p[i+1].x - p[i].x) * s - sx ,
            y: (p[i+1].y - p[i].y) * s - sy
        };
        sx += q[i].x;
        sy += q[i].y;
    }
    q[i] = {
        x: p[i+1].x - p[0].x - sx ,
        y: p[i+1].y - p[0].y - sy
    };
    sx=sy=s=i=l=undefined;
    return function(t)
    {
        t /= lt;
        var x=0, y=0;
        for(var i=q.length-1; i>=0; --i)
        {
            x = (x + q[i].x) * t;
            y = (y + q[i].y) * t;
        }
        return {x: x+p[0].x , y: y+p[0].y};
    };
}

