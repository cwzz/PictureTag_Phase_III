$(document).ready(function () {
    initial();
})

var width;
var height;
var data;
var page_width;
var gheight;
function initial() {
    width=document.getElementById("third").offsetWidth;
    height=window.innerHeight;
    page_width=document.getElementById("pagination2").offsetWidth;
    gheight=height-200;
    $.ajax({
        type : 'POST',
        url : '/user/Statistics',
        async:false,
        data :{username:JSON.parse(localStorage.getItem("current_user")).username},
        //此处data为新建项目的pid
        success : function (Data) {
            data=Data;
        },
        error: function (data) {
            alert("获取统计数据出错")
        }
    })

    $("#graphics").css({"width":width+"px","height":gheight+"px"});
    $("#pagination2").css({"margin-left":(width-page_width)/2+"px"});
    page();

    writeState(data.contractPerState,"gra1","text1","我承包的处在各个状态的项目");
    writeState(data.releasePerState,"gra2","text2","我发布的处在各个状态的项目数");
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
    writeState(data.contractPerState,"gra1","text1","我承包的处在各个状态的项目");
    writeState(data.releasePerState,"gra2","text2","我发布的处在各个状态的项目数");
}

function page2() {
    page();
    writeState(data.contractPerType,"gra1","text1","我承包的处在各个类别的项目数");
    writeState(data.releasePerType,"gra2","text2","我发布的处在各个类别的项目数");
}