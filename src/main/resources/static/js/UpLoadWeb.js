var height=window.innerHeight;
var width=window.innerWidth;
var buttonWidth=width/5*3*80/600;
var picWidth=height/15;
var times=0;
var picArray=new Array();

/*
$(document).ready(function () {
    $("#menu").css({"padding-left":(width-picWidth*2-buttonWidth*4)/2+"px"})
})

$(document).ready(function(){
    $(".pic").css({"width":picWidth+"px"});
    $(".fontButton").css({"width":buttonWidth+"px","height":picWidth+"px","font-size":picWidth*0.4+"px"})
    $("#menu").css({"padding-left":(width-picWidth*2-buttonWidth*4)/2+"px"})
});
*/

function setImagePreviews(avalue) {

    var docObj = document.getElementById("doc");

    var photos = document.getElementById("left");

    var fileList = docObj.files;

    var test=document.getElementById("left").innerHTML
    console.log(test)

    for (var i = 0; i < fileList.length; i++) {
        photos.innerHTML += " <img id='img" + times + "'> ";

        var imgObjPreview = document.getElementById("img"+times);


        if (docObj.files && docObj.files[i]) {

            //火狐下，直接设img属性

            //imgObjPreview.style.display = 'block';

            imgObjPreview.style.width = '150px';

            imgObjPreview.style.height = '180px';

            //imgObjPreview.src = docObj.files[0].getAsDataURL();

            //火狐7以上版本不能用上面的getAsDataURL()方式获取，需要一下方式

            imgObjPreview.src = window.URL.createObjectURL(docObj.files[i]);


        }

        else {

            //IE下，使用滤镜

            docObj.select();

            var imgSrc = document.selection.createRange().text;

            alert(imgSrc)

            var localImagId = document.getElementById("img" + times);

            //必须设置初始大小

            localImagId.style.width = "150px";

            localImagId.style.height = "180px";

            //图片异常的捕捉，防止用户修改后缀来伪造图片

            try {

                localImagId.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale)";

                localImagId.filters.item("DXImageTransform.Microsoft.AlphaImageLoader").src = imgSrc;

            }

            catch (e) {

                alert("您上传的图片格式不正确，请重新选择!");

                return false;

            }


            imgObjPreview.style.display = 'none';

            document.selection.empty();

        }

        picArray.push(document.getElementById("img"+times).src);
        console.log(document.getElementById("img"+times).src);
        times++;
    }



    return true;

}

function submit() {
    var content=JSON.stringify(picArray);
    $.ajax({
        type: "GET",
        url: "/submit",
        contentType: "application/json",
        dataType: "json",
        data: {"content":content},
        //data: JSON.stringify(a),
        success: function (jsonResult) {
        }
    });
    window.location.href="/mark";
}

