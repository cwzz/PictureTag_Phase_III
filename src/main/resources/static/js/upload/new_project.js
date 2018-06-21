function newProject() {
    $.ajax({
        type : 'POST',
        url : '/project/add',
        async:false,
        data :{username:JSON.parse(localStorage.getItem("current_user")).username},
        //此处data为新建项目的pid
        success : function (data) {
            localStorage.setItem("project_id",data);
        },
        error: function (data) {
            alert("新建任务出错")
        }
    })
}