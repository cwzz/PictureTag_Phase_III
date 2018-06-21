//得到后台的user
var user;
function initialUser(){
    $.ajax({
        type : 'POST',
        url : '/user/Personal',
        data :{username:JSON.parse(localStorage.getItem("current_user")).username},
        //此处data为新建项目的pid
        success : function (data) {
            //2.1根据获得的数据初始化页面
            user=data;
            $("#username").val(data.username);
            document.getElementById("empirical_value").innerHTML=data.experience;
            document.getElementById("credits_value").innerHTML=data.credits;
            document.getElementById("ranking_value").innerHTML="超过"+data.rank+"%的用户";
            $(".my-rating-5").starRating({
                initialRating:data.GoodCommentRate,
                readonly:true
            })
            $("#self_info").val(data.description)
            $("#email").val(data.email)
            document.getElementById("project_getting").innerHTML=data.numContract;
            document.getElementById("project_publishing").innerHTML=data.numRelease;
            document.getElementById("star_value").innerHTML=(data.GoodCommentRate/5)+"%";




            if(data.comments.length==0){
                $("#comment1").css({"display":"none"})
                $("#comment2").css({"display":"none"})
                $("#warn").css({"display":"block"})
            }
            else if(data.comments.length==1){
                $("#comment1").css({"display":"block"})
                $("#comment2").css({"display":"none"})
                $("#warn").css({"display":"none"})
                var com1=data.comments[0];
                document.getElementById("comment_name1").innerHTML=com1.userFrom;
                $("#comment_area1").val(com1.content);
            }
            else{
                $("#comment1").css({"display":"block"})
                $("#comment2").css({"display":"block"})
                $("#warn").css({"display":"none"})
                var com1=data.comments[0];
                document.getElementById("comment_name1").innerHTML=com1.userFrom;
                $("#comment_area1").val(com1.content);
                var com2=data.comments[1];
                document.getElementById("comment_name2").innerHTML=com2.userFrom;
                $("#comment_area2").val(com2.content);
            }

            var page;
            if(data.comments.length<=2){
                page=1;
            }
            else{
                page=data.comments.length/2+1;
            }
            $.jqPaginator('#pagination2', {
                totalPages: page,
                visiblePages: 10,
                currentPage: 1,
                first: '<li class="first"><a href="javascript:void(0);">首页</a></li>',
                prev: '<li class="prev"><a href="javascript:;">前一页</a></li>',
                next: '<li class="next"><a href="javascript:void(0);">下一页</a></li>',
                last: '<li class="last"><a href="javascript:void(0);">尾页</a></li>',
                page: '<li class="page"><a href="javascript:;">{{page}}</a></li>',
                onPageChange: function (num) {
                    //超过一页
                    if(data.comments.length>2){
                        //如果最后一页只有一项
                        if(num*2>data.comments.length){
                            $("#comment1").css({"display":"block"})
                            $("#comment2").css({"display":"none"})
                            $("#warn").css({"display":"none"})
                            var com1=data.comments[num*2-2];
                            document.getElementById("comment_name1").innerHTML=com1.userFrom;
                            $("#comment_area1").val(com1.content);
                        }
                        //如果最后一页有两项
                        else{
                            $("#comment1").css({"display":"block"})
                            $("#comment2").css({"display":"block"})
                            $("#warn").css({"display":"none"})
                            var com1=data.comments[num*2-2];
                            document.getElementById("comment_name1").innerHTML=com1.userFrom;
                            $("#comment_area1").val(com1.content);
                            var com2=data.comments[num*2-1];
                            document.getElementById("comment_name2").innerHTML=com2.userFrom;
                            $("#comment_area2").val(com2.content);
                        }
                    }
                }
            });


        },
        error: function (data) {
            alert("获取用户信息出错")
        }
    })
}

//将后台的user数据显示在前台
$(document).ready(function () {
    initialUser();
})

function edit() {
var state=document.getElementById("edit").innerHTML;
if(state==" 编 辑 "){
    $("#self_info").removeAttr("readonly")
    $("#email").removeAttr("readonly")
    document.getElementById("edit").innerHTML=" 完 成 ";
}
else if(state==" 完 成 "){
    document.getElementById("edit").innerHTML=" 编 辑 ";
    $("#self_info").attr("readonly","readonly");
    $("#email").attr("readonly","readonly");
    var update_user=user;
    update_user.description=$("#self_info").val();
    update_user.email=$("#email").val();
    $.ajax({
        type : 'POST',
        url : '/user/reset',
        contentType:"application/json",
        dataType:"json",
        data :JSON.stringify(update_user),
        //此处data为新建项目的pid
        success : function (data) {
            alert(data);
        },
        error: function (data) {
            alert("修改信息出错")
        }
    })
    document.getElementById("edit").innerHTML=" 编 辑 ";
}
}