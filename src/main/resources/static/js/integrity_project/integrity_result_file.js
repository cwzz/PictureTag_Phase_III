
var urls=new Array();


$(document).ready(function () {
var pid=localStorage.getItem("result_pid");
var current_type=localStorage.getItem("view_result_type");
if(current_type=="worker_result"){
    var uid=localStorage.getItem("result_uid");
    $.ajax({
        type:'POST',
        url:'/personalTag/requestViewAllWork',
        data:{username:uid,projectID:pid},
        success:function(data){
            urls=data;
            for (var i = 0; i < urls.length; i++) {
                writePic(urls[i], i + 1);
            }
        },
        error:function(data){
            $.alert({
                title: '预览工人完成结果失败!',
                content: '由于不知名的原因，查看工人标注结果失败了',
                confirmButton: '重新查看',
                confirmButtonClass: 'btn-primary',
                icon: 'fa fa-info',
                animation: 'zoom'
            });
        }
    });
}else {
    $.ajax({
        type: 'POST',
        url: '/project/showCombineRes',
        async: false,
        data: {pid: pid},
        //此处data为新建项目的pid
        success: function (data) {
            urls = data;
            for (var i = 0; i < urls.length; i++) {
                writePic(urls[i], i + 1);
            }
        },
        error: function (data) {
            alert("新建任务出错")
        }
    })
}
initial();
})

//已知图片url将图片写入滚动条中
function writePic(url,index) {
    var text='<figure>\n' +
        '        <img src="'+url+'" />\n' +
        '        <figcaption>'+index+'</figcaption>\n' +
        '    </figure>'

    document.getElementById("integrity_photos").innerHTML+=text;
}

//初始化界面
function initial() {
    var photo_height=document.getElementById("integrity_photos").offsetHeight;
    var screen_height=window.innerHeight;
    $("#head").css({"height":screen_height-photo_height-15-20+"px"})
    $("#title").css({"height":screen_height-photo_height-15-20+"px"})
}
