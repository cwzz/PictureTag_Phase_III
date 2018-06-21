//向html相应的预览列表中写图片，参数是方框的id,图片的url，以及其整体描述
$(document).ready(function () {
    writePicture("div1","source.unsplash.com/600x400/?fire","XXXXXX")
})





function writePicture(htmlid,url,description) {
    var div=document.getElementById(htmlid);
    var text='<div class="mhn-item">\n' +
        '\t\t\t\t<div class="mhn-inner">\n' +
        '\t\t\t\t\t<img src="http://'+url+'">\n' +
        '\t\t\t\t\t<div class="mhn-img"><div class="loader-circle"><div class="loader-stroke-left"></div><div class="loader-stroke-right"></div></div></div>\n' +
        '\t\t\t\t\t<div class="mhn-text">\n' +
        '\t\t\t\t\t\t<h4>整体描述</h4>\n' +
        '\t\t\t\t\t\t<p>'+description+'</p>\n' +
        '\t\t\t\t\t</div>\n' +
        '\t\t\t\t</div>\n' +
        '\t\t\t</div>'
    div.innerHTML+=text;
}