$(document).ready(function () {
    initial();
})

var data;
var width;
var height;
var page_width;
var gheight;
var data2;

var array1;
var array2;
var array3;
var array4;

function initial() {
    width=document.getElementById("second").offsetWidth;
    height=window.innerHeight;
    page_width=document.getElementById("pagination2").offsetWidth;
    gheight=height-200;
    $.ajax({
        type : 'POST',
        url : '/user/UserToAdmin',
        async:false,
        data :{},
        //此处data为新建项目的pid
        success : function (Data) {
            data=Data;
        },
        error: function (data) {
            alert("获取统计数据出错")
        }
    })

    var date=new Date();
    var year=date.getFullYear();
    $.ajax({
        type : 'POST',
        url : '/project/statistics',
        async:false,
        data :{"year":year},
        success : function (Data) {
            data2=Data;
        },
        error: function (data) {
            alert("获取统计数据出错")
        }
    })
    $("#graphics").css({"width":width+"px","height":gheight+"px"});
    $("#pagination2").css({"margin-left":(width-page_width)/2+"px"});

    array1=new Array();
    array1.push(data2.releasedPerMonth);
    array1.push(data2.waitUndertakePerMonth);
    array1.push(data2.finishedPerMonth);

    array2=new Array();
    array2.push(data2.releasedAnimalNum);
    array2.push(data2.releasedSceneNum);
    array2.push(data2.releasedPersonNum);
    array2.push(data2.releasedGoodsNum);
    array2.push(data2.releasedOthersNum);

    array3=new Array();
    array3.push(data2.waitUndertakeAnimalNum);
    array3.push(data2.waitUndertakeSceneNum);
    array3.push(data2.waitUndertakePersonNu);
    array3.push(data2.waitUndertakeGoodsNum);
    array3.push(data2.waitUndertakeOthersNum);

    array4=new Array();
    array4.push(data2.finishedAnimalNum);
    array4.push(data2.finishedSceneNum);
    array4.push(data2.finishedPersonNum);
    array4.push(data2.finishedGoodsNum);
    array4.push(data2.finishedOthersNum);

    page();
    writeState(data.registerPerMonth,"gra1","text1","用户每月的注册数");
    writeMulState(array1,"gra2","text2","每月新发布的项目");
}

function writeState(data, divg, divt, title) {
    $("#"+divg).css({"width":width/2+"px","height":gheight*0.7+"px"});
    $("#"+divt).css({"width":width/2+"px","height":gheight*0.3+"px"})
    var keys=[];
    var value=[];
    for(var key in data){
        keys.push(key);
        value.push(data[key])
    }
    var value2=[];
    for(var i=0;i<value.length;i++){
        value2.push(value[0]-value[i]);
    }

    var myChart = echarts.init(document.getElementById(divg));
    option = {
        title: {
            text: title,
        },
        tooltip : {
            trigger: 'axis',
            axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
            },
            formatter: function (params) {
                var tar = params[1];
                return tar.name + '<br/>' + tar.seriesName + ' : ' + tar.value;
            }
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type : 'category',
            splitLine: {show:false},
            data : keys
        },
        yAxis: {
            type : 'value'
        },
        series: [
            {
                name: '辅助',
                type: 'bar',
                stack:  '总量',
                itemStyle: {
                    normal: {
                        barBorderColor: 'rgba(0,0,0,0)',
                        color: 'rgba(0,0,0,0)'
                    },
                    emphasis: {
                        barBorderColor: 'rgba(0,0,0,0)',
                        color: 'rgba(0,0,0,0)'
                    }
                },
                data: value2
            },
            {
                name: 'ALL',
                type: 'bar',
                stack: '总量',
                label: {
                    normal: {
                        show: true,
                        position: 'inside'
                    }
                },
                data:value
            }
        ]
    };
    myChart.setOption(option);

    var string="";
    if(keys.length<=5){
        for(var i=0;i<keys.length;i++){
            string+="<h3>"+keys[i]+":"+value[i]+"<h3>";
        }
    }
    else{
        for(var i=0;i<keys.length;i++){
            if(i+1<keys.length){
                string+="<h3>"+keys[i]+":"+value[i]+"   "+keys[i+1]+":"+value[i+1]+"<h3>";
                i++;
            }
            else{
                "<h3>"+keys[i]+":"+value[i]+"<h3>";
            }

        }
    }
    document.getElementById(divt).innerHTML=string;
}

//data是数组
function writeMulState(data, divg, divt, title) {
    $("#"+divg).css({"width":width/2+"px","height":gheight*0.7+"px"});
    $("#"+divt).css({"width":width/2+"px","height":gheight*0.3+"px"})
    var keys=[];
    var values=[];
    var title_keys=[];
    for(var key in data){
        title_keys.push(key);
        var temp_keys=[];
        var temp_values=[];
        var temp_data=data[key];
        for(var i in temp_data){
            temp_keys.push(i);
            temp_values.push(temp_data[i]);
        }
        keys.push(temp_keys);
        values.push(temp_values);
    }

    var string="";
    for(var i=0;i<title_keys.length;i++){
        string+='{\n' +
            '                name:'+title_keys[i]+',\n' +
            '                type:\'line\',\n' +
            '                stack: \'总量\',\n' +
            '                areaStyle: {normal: {}},\n' +
            '                data:'+values[i]+'\n' +
            '            },'
    }

    var myChart = echarts.init(document.getElementById(divg));
    option = {
        title: {
            text: title
        },
        tooltip : {
            trigger: 'axis',
            axisPointer: {
                type: 'cross',
                label: {
                    backgroundColor: '#6a7985'
                }
            }
        },
        legend: {
            data:title_keys
        },
        toolbox: {
            feature: {
                saveAsImage: {}
            }
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis : [
            {
                type : 'category',
                boundaryGap : false,
                data : keys[0]
            }
        ],
        yAxis : [
            {
                type : 'value'
            }
        ],
        series : [
            string,
        ]
    };
    myChart.setOption(option);

    var headbegin='<table>\n' +
        '                            <tr>\n' +
        '                                <td></td>\n';
    var headend= '                            </tr>\n' +
        '                        </table>';
    var str=headbegin;
    for(var i=0;i<keys[0].length;i++){
        str+='<td>'+keys[0][i]+'</td>\n';
    }
    str+='</tr>\n';

    for(var i=0;i<data.length;i++){
        str+='<tr><td>'+title_keys[i]+'</td>\n';
        for(var j=0;j<keys[i].length;j++){
            str+='<td>'+values[i][j]+'</td>\n';
        }
        str+='</tr>\n';
    }

    str+='</table>'
    document.getElementById(divt).innerHTML=str;
}

function page() {
    document.getElementById("graphics").innerHTML='<div style="float: left" id="left-graphic">\n' +
        '                    <div id="gra1"></div>\n' +
        '                    <div id="text1" style="text-align: center"></div>\n' +
        '                </div>\n' +
        '                <div style="float: left" id="right_graphic">\n' +
        '                    <div id="gra2"></div>\n' +
        '                    <div id="text2" style="text-align: center"></div>\n' +
        '                </div>'
}

function page1() {
    page();
    writeState(data.registerPerMonth,"gra1","text1","用户每月的注册数");
    writeMulState(array1,"gra2","text2","每月新发布的项目");
}

function page2() {
    page();
    writeMulState(array2,"gra1","text1","每月新发布的项目（按分类）");
    writeMulState(array3,"gra2","text2","每月进行中的项目（按分类）");
}

function page3() {
    page();
    writeMulState(array4,"gra1","text1","每月已完成的项目（按分类）");
    writeState(data2.avgReleasedNum,"gra2","text2","每类项目一年来的发布数");
}