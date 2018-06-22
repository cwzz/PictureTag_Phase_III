'use strict';

//这里是模板和上传照片自带的js，不得更改
(function($) {
  $.fn.phAnim = function( options ) {

    var settings = $.extend({}, options),
    		label,
  			ph;
    
    function getLabel(input) {
      return $(input).parent().find('label');
    }
    
    function makeid() {
      var text = "";
      var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
      for( var i=0; i < 5; i++ )
        text += possible.charAt(Math.floor(Math.random() * possible.length));
      return text;
    }
    
    return this.each( function() {
			
      if( $(this).attr('id') == undefined ) {
        $(this).attr('id', makeid());
      }

      if( getLabel($(this)).length == 0 ) {
        if( $(this).attr('placeholder') != undefined ) {
          ph = $(this).attr('placeholder');
          $(this).attr('placeholder', '');
          $(this).parent().prepend('<label for='+ $(this).attr('id') +'>'+ ph +'</label>');
        }
      } else {
        $(this).attr('placeholder', '');
        if(getLabel($(this)).attr('for') == undefined ) {
          getLabel($(this)).attr('for', $(this).attr('id'));
        }
      }
      $(this).on('focus', function() {
        label = getLabel($(this));
        label.addClass('active focusIn');
      }).on('focusout', function() {
        if( $(this).val() == '' ) {
          label.removeClass('active');
        }
        label.removeClass('focusIn');
      });
    });
  };
}(jQuery));
$(document).ready(function() {
	$('input').phAnim();
});


$(document).ready(function () {
    initial();
})


//初始化界面 新建界面获知pid 或 从草稿箱打开指定pid的项目
var id;
function initial() {
    //-1.清空所有的图片数组
    url_result=[];
    //0.获得项目id
    id=localStorage.getItem("project_id");
    localStorage.setItem("project_id","");
    document.getElementById("hide_pid").value=id;

    //1.获取该id下的项目对象,并初始化页面
    $.ajax({
        type : 'POST',
        url : '/project/read',
        data :{"pid":id},
        //此处data为新建项目的pid
        success : function (data) {
            //该data为项目对象
            if(data==""){
            }
            else{
                $("#require_name").val(data.pro_name);
                $("#credits_available").val(data.points);
                $("#project_note").val(data.note)
                $("#project_brief_intro").val(data.brief_intro)
                $("#detail_require").val(data.detailRequire)
                $("#required").setAttribute("data_placeholder",data.pro_type)
                $("#work_time").val(data.deadLine)

                for(var i=0;i<data.urls.length;i++){
                    var photo=document.getElementById("photos");
                    photo.innerHTML+='<a><img src="'+'http://'+data.urls[i]+'"></a>'
                }

            }

        },
        error: function (data) {
            alert("新建任务出错")
        }
    })
}

//上传图片至上传页面
var times=0;
var array=new Array(); /*这里是装有图片base64的数组*/
function setImagePreviews(e) {
    var dom=$('#doc');
    var fileList=document.getElementById("doc").files;
    var docObj = document.getElementById("doc");
    var photos = document.getElementById("photos");
    for (var i = 0; i < fileList.length; i++) {
        var picResult=window.URL.createObjectURL(fileList[i]);
        if (docObj.files && docObj.files[i]) {
            var reader=new FileReader();
            reader.onload=(function (ev) {
                return function (e) {
                    if(array.indexOf(this.result)>=0){
                        //该图片已经上传过
                    }
                    else{
                        photos.innerHTML+='<a id="a'+times+'"></a>';
                        var i=new Image();
                        i.src=this.result;
                        i.style.width="300px";
                        i.style.height="200px";
                        array.push(this.result);
                        document.getElementById("a"+times).appendChild(i);
                        times++;
                    }
                }
            })(e.target.files[i]);
            reader.readAsDataURL(e.target.files[i]);
        }
        else{
            //do nothing
        }
    }

/*
    var reader=new FileReader();
    reader.onload=(function (ev) {
        return function (e) {
            console.info(this.result);
        }
    })(e.target.files[0]);
    reader.readAsDataURL(e.target.files[0]);

    var docObj = document.getElementById("doc");

    var photos = document.getElementById("photos");

    var fileList = docObj.files;
    for (var i = 0; i < fileList.length; i++) {
        photos.innerHTML += '<a>\n' + '<img id="img'+times+'" width="300" height="200" >\n' + '</a>'

        var imgObjPreview = document.getElementById("img"+times);

        var picURL=window.URL.createObjectURL(docObj.files[i]);

        if (docObj.files && docObj.files[i]) {
            imgObjPreview.src =picURL;
        }
        else{
          //do nothing
        }
        times++;



        var url = "related/1.jpg";//这是站内的一张图片资源，采用的相对路径
        convertImgToBase64(url, function(base64Img){
            //转化后的base64
            alert(base64Img);
        });


    }*/

    return true;

}

//上传压缩包
function uploadRAR() {
    var formdata=new FormData($("#test_form")[0]);
    $.ajax({
        type : 'POST',
        url : '/project/uploadZip',
        data: formdata,
        async: false,
        cache: false,
        contentType: false,
        processData: false,
        //此处data为新建项目的pid
        success : function (data) {
            url_result=data;
            document.getElementById("rar_photos").innerHTML='<a><img src="/js/upload/zip_photo.png" style="width: 300px;height: 200px;"></a>';
        },
        error: function (data) {
            alert("压缩包转换出错");
        }
    })

}

//将base64形式的image转为url并保存到url_result数组中去
var url_result=[];
function changeBase64ToURL() {
    var length=array.length;
    for(var i=0;i<length;i++){
        var date=new Date();
        var name=date.getFullYear()+date.getMonth()+date.getDate()+date.getHours()+date.getMinutes()+date.getSeconds()+i;
        $.ajax({
            type: "POST",
            url:"/project/changeBaseToUrl",
            async:false,
            data: {base64:array.pop(),filename:name,projectID:id},
            success: function (data) {
                //此处省略
                url_result.push(data);
            },
            error: function (data) {
                alert("图片转换失败"+id)
            }
        });
    }
}

//实现草稿箱保存的功能
function save() {
    if(array.length>0){
        //-1.先得将图片转换为url
        changeBase64ToURL();
    }
    
    //0.获得用户输入
    var require_name=$("#require_name").val();
    var creator_name=JSON.parse(localStorage.getItem("current_user")).username;
    var credits_available=$("#credits_available").val();
    var project_note=$("#project_note").val();
    var project_brief_intro=$("#project_brief_intro").val();
    var detail_require=$("#detail_require").val();
    var category=$("#required").val();
    if(category=="动物类标注"){
        category="ANIMALTAG";
    }
    else if(category=="风景类标注"){
        category="SCENETAG";
    }
    else if(category=="人物类标注"){
        category="PERSONTAG";
    }
    else if(category=="物件类标注"){
        category="GOODSTAG"
    }
    else if(category=="其他类标注"){
        category="OTHERSTAG"
    }
    var work_time=$("#work_time").val();

    //1.初始化项目对象
    var initial_project={};
    initial_project.pro_ID=id;
    initial_project.pro_name=require_name;
    initial_project.points=credits_available;
    initial_project.brief_intro=project_brief_intro;
    initial_project.deadLine=work_time;
    initial_project.remainTime=-1;
    initial_project.pro_type=category;
    initial_project.pro_requester=creator_name;
    initial_project.detailRequire=detail_require;
    initial_project.note=project_note;
    initial_project.urls=url_result;

    if(creator_name==""){
        alert("发布者姓名不能为空")
    }
    else if(category==""){
        alert("项目分类不能为空")
    }
    else{
        /*这里可能有问题，base64可能过大无法用这个方式传过去*/
        $.ajax({
            type : 'POST',
            url : '/project/save',
            contentType:"application/json",
            dataType:"json",
            data :JSON.stringify(initial_project),
            success : function (data) {
                window.location.href="/cwzz/personal_center"
            },
            error: function (data) {
                alert("保存出错");
            }
        })
    }


}

function upload_final() {

    if(array.length>0){
        //0.先把图片转为url
        changeBase64ToURL();
    }

    var require_name=$("#require_name").val();
    var creator_name=JSON.parse(localStorage.getItem("current_user")).username;
    var credits_available=$("#credits_available").val();
    var project_note=$("#project_note").val();
    var project_brief_intro=$("#project_brief_intro").val();
    var detail_require=$("#detail_require").val();
    var category=$("#required").val();
    if(category=="动物类标注"){
        category="ANIMALTAG";
    }
    else if(category=="风景类标注"){
        category="SCENETAG";
    }
    else if(category=="人物类标注"){
        category="PERSONTAG";
    }
    else if(category=="物件类标注"){
        category="GOODSTAG"
    }
    else if(category=="其他类标注"){
        category="OTHERSTAG"
    }
    var work_time=$("#work_time").val();

    //1.初始化项目对象
    var initial_project={};
    initial_project.pro_ID=id;
    initial_project.pro_name=require_name;
    initial_project.points=credits_available;
    initial_project.brief_intro=project_brief_intro;
    initial_project.deadLine=work_time;
    initial_project.remainTime=calculate_time(work_time);
    initial_project.pro_type=category;
    initial_project.pro_requester=creator_name;
    initial_project.detailRequire=detail_require;
    initial_project.note=project_note;
    initial_project.urls=url_result;

    //2.判断是否有遗漏的项目未填写，并进行上传操作
    var warn_result=new Array();
    if(require_name==""){
        warn_result.push("抱歉，您的‘需求名称’不合规")
    }
    if(creator_name==""){
        warn_result.push("抱歉，您的‘发布者姓名’不合规")
    }
    if(credits_available==""||credits_available<=0){
        warn_result.push("抱歉，您的‘项目价值积分’不合规")
    }
    if(project_note==""){
        warn_result.push("抱歉，您的‘项目备注’不合规")
    }
    if(category==""){
        warn_result.push("抱歉，您的‘项目分类’不合规")
    }
    if(project_brief_intro==""){
        warn_result.push("抱歉，您的‘项目简介’不合规")
    }
    if(detail_require==""){
        warn_result.push("抱歉，您的‘项目具体描述’不合规")
    }
    if(work_time==""){
        warn_result.push("抱歉，您的‘工作截止时间’不合规")
    }


    var warn="";
    //2.1 有必填项未填
    if(warn_result.length!=0){
        for(var i=0;i<warn_result.length;i++){
            warn+=warn_result[i]+"\r\n"
        }
        alert(warn);
    }
    //2.2所有项目均填写正确,上传
    else{
        $.ajax({
                type: "POST",
                url:"/project/upload",
                contentType: "application/json",
                dataType:"json",
                data:JSON.stringify(initial_project),
                success: function (data) {
                    if(data=="SUCCESS"){
                        window.location.href="/cwzz/personal_center";
                    }
                    else if(data=="CREDITNOTENOUGH"){
                        alert("抱歉，您的积分不足，不能发布项目，请先去充值积分");
                    }
                    else {
                        alert("抱歉，由于某些不可知的原因您暂时无法发布项目");
                    }
                },
                error: function (data) {
                    alert("上传文件出错");
                }
        });
    }
}

function calculate_time(worktime) {
    var da=new Date();
    var db=new Date(worktime.substring(0,10))
    var result=(db-da)/1000/60/60/24;
    return Math.floor(result);
}

function getRecommend() {
    var worktime=$("#work_time").val();
    var project_size=$("#project_size").val();
    var credits=document.getElementById("credits_available");

    var warn_array=new Array();
    if(worktime==""){
        warn_array.push("请先填写工作截止时间");
    }
    if(project_size==""){
        warn_array.push("请先选择项目预估大小");
    }

    if(array.length>0){
        var warn_str="";
        for(var i=0;i<array.length;i++){
            warn_str+=array.get(i)+"\r\n";
        }
        alert(warn_str);
    }
    else{
        var size=project_size;
        var time=calculate_time(worktime);
        $.ajax({
            type : 'POST',
            url : '/project/predictPrice',
            async:false,
            data :{pictureNum:size},
            //此处data为新建项目的pid
            success : function (data) {
                $("#credits_available").val(data);
            },
            error: function (data) {
                alert("获取预估积分失败")
            }
        })
    }
}