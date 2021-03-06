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
var default_Data_title;

var newData1;
var newData2;
var newData3;
var newData4;
var newData5;
var newData6;
function initial() {
    width=document.getElementById("second").offsetWidth;
    height=window.innerHeight;
    page_width=document.getElementById("pagination2").offsetWidth;
    gheight=height-200;
    var date=new Date();
    var year=date.getFullYear();
    $.ajax({
        type : 'POST',
        url : '/user/UserToAdmin',
        async:false,
        data :{"year":year},
        //此处data为新建项目的pid
        success : function (Data) {
            data=Data;
        },
        error: function (data) {
            alert("获取统计数据出错")
        }
    })

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
    array3.push(data2.waitUndertakePersonNum);
    array3.push(data2.waitUndertakeGoodsNum);
    array3.push(data2.waitUndertakeOthersNum);

    array4=new Array();
    array4.push(data2.finishedAnimalNum);
    array4.push(data2.finishedSceneNum);
    array4.push(data2.finishedPersonNum);
    array4.push(data2.finishedGoodsNum);
    array4.push(data2.finishedOthersNum);

    $.ajax({
        type : 'POST',
        url : '/project/picNumAndFinishTimeToSatisfy',
        async:false,
        data :{},
        success : function (Data) {
            newData1=Data;
        },
        error: function (data) {
            alert("获取统计数据出错")
        }
    })

    $.ajax({
        type : 'POST',
        url : '/project/picNumAndContractNumToSatisfy',
        async:false,
        data :{},
        success : function (Data) {
            newData2=Data;
        },
        error: function (data) {
            alert("获取统计数据出错")
        }
    })

    $.ajax({
        type : 'POST',
        url : '/project/picNumAndFinishTimeToGiveUp',
        async:false,
        data :{},
        success : function (Data) {
            newData3=Data;
        },
        error: function (data) {
            alert("获取统计数据出错")
        }
    })

    $.ajax({
        type : 'POST',
        url : '/project/picNumAndFinishTimeToComplete',
        async:false,
        data :{},
        success : function (Data) {
            newData4=Data;
        },
        error: function (data) {
            alert("获取统计数据出错")
        }
    })

    $.ajax({
        type : 'POST',
        url : '/project/picNumToPoints',
        async:false,
        data :{},
        success : function (Data) {
            newData5=Data;
        },
        error: function (data) {
            alert("获取统计数据出错")
        }
    })

    $.ajax({
        type : 'POST',
        url : '/project/picNumToAvgPoints',
        async:false,
        data :{},
        success : function (Data) {
            newData6=Data;
        },
        error: function (data) {
            alert("获取统计数据出错")
        }
    })

    document.getElementById("totalnum").innerHTML=data.totalNum;
    document.getElementById("onlinnum").innerHTML=data.onlineNum;
    document.getElementById("register_thisweek").innerHTML=data.registerThisWeek;
    document.getElementById("register_thismonth").innerHTML=data.registerThisMonth

    initialGUI();
    default_Data_title=["动物类标注","风景类标注","人物类标注","物品类标注","其他类标注"];
    writeLine(data.registerPerMonth,"graph" ,"每月新注册的用户数");
    //writeNewLine(data2.avgReleasedNum,"graph","XXX");
    
}

function initialGUI() {
    document.getElementById("inner").innerHTML='<div id="graph"></div>\n' +
        '                    <div id="chart" style="overflow-y: auto">\n' +
        '                        <table class="table table-hover personal-task">\n' +
        '                            <thead id="table_title">\n' +
        '                            </thead>\n' +
        '                            <tbody id="table_data">\n' +
        '                            </tbody>\n' +
        '                        </table>\n' +
        '                    </div>';
    $("#graphics").css({"width":width+"px","height":gheight+"px"});
    $("#pagination2").css({"margin-left":(width-page_width)/2+"px"});
    $("#graph").css({"width":width-400+"px","height":gheight*0.6+"px","margin-left":"200px", "margin-right":"200px"})
    $("#chart").css({"width":width-500+"px","height":gheight*0.38+"px","margin-left":"250px", "margin-right":"250px"})
}

//一维
function writeBar(data, divg, title) {
    var keysAndvalues=getKeysAndValues(data,true);
    var keys=keysAndvalues.keys;
    var value=keysAndvalues.values;
    if(title=="不同图片数量 用户追加积分的平均值"){
        keys=["合计","<500","500-1000","1000-2000","2000-3000",">3000"];
        value=[value[0],data['<500'],data['500-1000'],data['1000-2000'],data['2000-3000'],data['>3000']]
    }

    var value2=[];
    value2.push(0);
    var temp_value=0;
    for(var i=1;i<value.length;i++){
        temp_value+=value[i];
        value2.push(value[0]-temp_value);
    }

    var myChart = echarts.init(document.getElementById(divg));
    option = {
        title: {
            text: title,
            left: 'center',
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

    // var string="";
    // if(keys.length<=5){
    //     for(var i=0;i<keys.length;i++){
    //         string+="<h3>"+keys[i]+":"+value[i]+"<h3>";
    //     }
    // }
    // else{
    //     for(var i=0;i<keys.length;i++){
    //         if(i+1<keys.length){
    //             string+="<h3>"+keys[i]+":"+value[i]+"   "+keys[i+1]+":"+value[i+1]+"<h3>";
    //             i++;
    //         }
    //         else{
    //             "<h3>"+keys[i]+":"+value[i]+"<h3>";
    //         }
    //
    //     }
    // }
    // document.getElementById(divt).innerHTML=string;

    var table_title=["标注分类","数量统计"];
    if(title=="不同图片数量 用户追加积分的平均值"){
        var tkeys=["<500","500-1000","1000-2000","2000-3000",">3000"];
        var tvalue=[data['<500'],data['500-1000'],data['1000-2000'],data['2000-3000'],data['>3000']]
        writeChartFor1(tkeys,tvalue,table_title,"table_title","table_data")
    }
    else{
        writeChartFor1(getKeysAndValues(data,false).keys,getKeysAndValues(data,false).values,table_title,"table_title","table_data");
    }
}

function writeBarForNew1(data, divg, title) {
    var myChart = echarts.init(document.getElementById(divg));
    var labelRight = {
        normal: {
            position: 'right'
        }
    };
    option = {
        title: {
            text: title,
            left: 'center',
        },
        tooltip : {
            trigger: 'axis',
            axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
            }
        },
        grid: {
            top: 80,
            bottom: 30
        },
        xAxis: {
            type : 'value',
            position: 'top',
            splitLine: {lineStyle:{type:'dashed'}},
        },
        yAxis: {
            type : 'category',
            axisLine: {show: false},
            axisLabel: {show: false},
            axisTick: {show: false},
            splitLine: {show: false},
            data : ['<500', '500-1000', '1000-2000', '2000-3000', '>3000']
        },
        series : [
            {
                name:'生活费',
                type:'bar',
                stack: '总量',
                label: {
                    normal: {
                        show: true,
                        formatter: '{b}'
                    }
                },
                data:[
                    {value: data['<500'], label: labelRight},
                    {value: data['500-1000'], label: labelRight},
                    {value: data['1000-2000'], label: labelRight},
                    {value: data['2000-3000'], label: labelRight},
                    {value: data['>3000'], label: labelRight},
                ]
            }
        ]
    };
    myChart.setOption(option);

    var tkeys=["<500","500-1000","1000-2000","2000-3000",">3000"];
    var tvalue=[data['<500'],data['500-1000'],data['1000-2000'],data['2000-3000'],data['>3000']]
    writeChartFor1(tkeys,tvalue,table_title,"table_title","table_data")
}

function writePie(data, divg, title) {
    var keysAndvalues=getKeysAndValues(data, false);
    var keys=keysAndvalues.keys;
    var values=keysAndvalues.values;
    if(title=="不同图片数量 用户追加积分的平均值"){
        keys=["<500","500-1000","1000-2000","2000-3000",">3000"];
        values=[data['<500'],data['500-1000'],data['1000-2000'],data['2000-3000'],data['>3000']]
    }
    var need_data=[];
    for(var i=0;i<keys.length;i++){
        var temp={};
        temp.value=values[i];
        temp.name=keys[i];
        need_data.push(temp);
    }

    var myChart = echarts.init(document.getElementById(divg));
    option = {
        title: {
            text: title,
            left: 'center',
        },
        tooltip : {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend: {
            x : 'center',
            y : 'bottom',
            data:keys
        },
        calculable : true,
        series : [

            {
                name:title,
                type:'pie',
                radius : [30, 150],
                center : ['50%', '50%'],
                roseType : 'area',
                data:need_data
            }
        ]
    };
    myChart.setOption(option);

    var table_title=["标注分类","数量统计"];
    writeChartFor1(keys,values,table_title,"table_title","table_data");
}

function writeRadar(data, divg, title) {
    var keysAndvalues=getKeysAndValues(data, false);
    var keys=keysAndvalues.keys;
    var values=keysAndvalues.values;
    if(title=="不同图片数量 用户追加积分的平均值"){
        keys=["<500","500-1000","1000-2000","2000-3000",">3000"];
        values=[data['<500'],data['500-1000'],data['1000-2000'],data['2000-3000'],data['>3000']]
    }
    var biggest_value=getBiggestValue(values);
    var need_indicator=[];
    for(var i=0;i<keys.length;i++){
        var temp={};
        temp.text=keys[i];
        temp.max=biggest_value;
        need_indicator.push(temp);
    }

    var myChart = echarts.init(document.getElementById(divg));
    option = {
        title: {
            text: title,
            left: 'center',
        },
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            x : 'center',
            y : 'bottom',
            data:['我的项目']
        },
        radar: [
            {
                indicator: need_indicator,
                center: ['50%','50%'],
                radius: 150
            }
        ],
        series: [
            {
                type: 'radar',
                tooltip: {
                    trigger: 'item'
                },
                itemStyle: {normal: {areaStyle: {type: 'default'}}},
                data: [
                    {
                        value: values,
                        name: '我的项目'
                    }
                ]
            }
        ]
    };
    myChart.setOption(option);

    var table_title=["标注分类","数量统计"];
    writeChartFor1(keys,values,table_title,"table_title","table_data");
}

function writeLine(data, divg, title) {
    var keysAndvalues=getOrderKeysAndValues(data);
    var need_key=keysAndvalues.keys;
    var need_value=keysAndvalues.values;
    var myChart = echarts.init(document.getElementById(divg));
    option = {
        title: {
            text: title,
            left: 'center',
        },
        xAxis: {
            type: 'category',
            data: need_key
        },
        yAxis: {
            type: 'value'
        },
        series: [{
            data: need_value,
            type: 'line'
        }]
    };
    myChart.setOption(option);

    var need_title=[];
    need_title.push("日期","数量")
    writeChartFor1(need_key,need_value,need_title,"table_title", "table_data");
}

function writeNewLine(data, divg, title) {
    var keysandvalues=getKeysAndValues(data,false);
    var keys=keysandvalues.keys;
    var values=keysandvalues.values;
    if(title=="不同图片数量 用户追加积分的平均值"){
        keys=["<500","500-1000","1000-2000","2000-3000",">3000"];
        values=[data['<500'],data['500-1000'],data['1000-2000'],data['2000-3000'],data['>3000']]
    }
    var myChart = echarts.init(document.getElementById(divg));
    option = {
        title: {
            text: title,
            left: 'center',
        },
        xAxis: {
            type: 'category',
            data: keys
        },
        yAxis: {
            type: 'value'
        },
        series: [{
            data: values,
            type: 'line'
        }]
    };
    myChart.setOption(option);

    var need_title=[];
    need_title.push("项目","数量")
    writeChartFor1(keys,values,need_title,"table_title", "table_data");
}

function writeChartFor1(keys,values,title,title_part, data_part) {
    var table_title="<tr>";
    for(var i=0;i<title.length;i++){
        table_title+='<th scope="col" style="text-align: center;">'+title[i]+'</th>'
    }
    table_title+='</tr>';
    document.getElementById(title_part).innerHTML=table_title;

    var table_data="";
    for(var i=0;i<keys.length;i++){
        table_data+='<tr><td style="text-align: center">'+keys[i]+'</td><td style="text-align: center">'+values[i]+'</td></tr>'
    }
    document.getElementById(data_part).innerHTML=table_data;
    //<tr><th scope="col" style="text-align: center;">项目名称</th></tr>
    //<tr><td>1</td><td>George Washington</td><td>two</td><td>1789-1797</td></tr>
}

//二维
function writeMulLine(data, data_title, divg, title) {
    var keys=getOrderKeysAndValues(data[0]).keys;
    var series=[];
    for(var i=0;i<data.length;i++){
        var tempkeyandvalue=getOrderKeysAndValues(data[i]);
        var temp={};
        temp.name=data_title[i];
        temp.type='line';
        temp.stack='总量';
        temp.areaStyle={normal: {}};
        temp.data=tempkeyandvalue.values;
        series.push(temp);
    }

    var myChart = echarts.init(document.getElementById(divg));
    option = {
        title: {
            text: title,
            left: 'center',
        },
        legend: {
            x : 'center',
            y : 'bottom',
            data:data_title
        },
        toolbox: {
            feature: {
                saveAsImage: {}
            }
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '5%',
            containLabel: true
        },
        xAxis : [
            {
                type : 'category',
                boundaryGap : false,
                data : keys
            }
        ],
        yAxis : [
            {
                type : 'value'
            }
        ],
        series : series
    };

    myChart.setOption(option);
    writeChart(keys,data_title,"table_title","table_data",data);
}

function writeMulBar(data, data_title, divg, title) {
    var keys=getOrderKeysAndValues(data[0]).keys;
    var need_keys=[];
    need_keys.push('month');
    for(var i=0;i<data_title.length;i++){
        need_keys.push(data_title[i]);
    }
    var source=[];
    for(var i=0;i<keys.length;i++){
        var temp={};
        temp.month=keys[i];
        for(var j=0;j<data_title.length;j++){
            temp[data_title[j]]=data[j][keys[i]];
        }
        source.push(temp);
    }
    var series=[];
    for(var i=0;i<data_title.length;i++){
        series.push({type: 'bar'});
    }
    var myChart = echarts.init(document.getElementById(divg));
    option = {
        legend: {},
        dataset: {
            dimensions: need_keys,
            source: source
        },
        xAxis: {type: 'category'},
        yAxis: {},
        // Declare several bar series, each will be mapped
        // to a column of dataset.source by default.
        series: series
    };
    myChart.setOption(option);

    writeChart(keys,data_title,"table_title","table_data",data);
}

function writeMulPie(data, data_title, divg, title) {
    var need_data=[];
    for(var i=0;i<data_title.length;i++){
        var temp={};
        temp.value=calculateSum(data[i]);
        temp.name=data_title[i];
        need_data.push(temp);
    }

    var myChart = echarts.init(document.getElementById(divg));
    option = {
        title : {
            text: title,
            x:'center'
        },
        legend: {
            x : 'center',
            y : 'bottom',
            data:data_title
        },
        calculable : true,
        series : [
            {
                name:'总量',
                type:'pie',
                radius : [30, 110],
                center : ['50%', '50%'],
                roseType : 'area',
                data:need_data
            }
        ]
    };
    myChart.setOption(option);

    writeChart(getOrderKeysAndValues(data[0]).keys,data_title,"table_title","table_data",data);
}

function writeMulRadar(data, data_title, divg, title) {
    var max=[];
    for(var i=0;i<data.length;i++){
        max.push(getBiggestValue(getKeysAndValues(data[i]).values));
    }
    var max_num=getBiggestValue(max);
    var indicator=[];
    for(var i=0;i<data_title.length;i++){
        var temp={};
        temp.text=data_title[i];
        temp.max=max_num;
        indicator.push(temp);
    }
    var keys=getOrderKeysAndValues(data[0]).keys;
    var need_value=[];
    for(var i=0;i<keys.length;i++){
        var temp_object={};
        var temp=[];
        for(var j=0;j<data_title.length;j++){
            temp.push(data[j][keys[i]]);
        }
        temp_object.value=temp;
        temp_object.name=keys[i];
        need_value.push(temp_object);
    }

    var myChart = echarts.init(document.getElementById(divg));
    option = {
        title: {
            text: title
        },
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            x: 'center',
            data:keys
        },
        radar: [
            {
                indicator: indicator,
                center: ['50%','50%'],
                radius: 150
            },
        ],
        series: [
            {
                type: 'radar',
                tooltip: {
                    trigger: 'item'
                },
                itemStyle: {normal: {areaStyle: {type: 'default'}}},
                data: need_value
            }
        ]
    };

    myChart.setOption(option);
    writeChart(keys,data_title,"table_title","table_data",data);
}

//title是标题栏
function writeChart(keys,data_title,title_part, data_part, array) {
    var table_title="<tr><th scope=\"col\" style=\"text-align: center;\"></th>";
    for(var i=0;i<keys.length;i++){
        table_title+='<th scope="col" style="text-align: center;">'+keys[i]+'</th>'
    }
    table_title+='</tr>';
    document.getElementById(title_part).innerHTML=table_title;

    var table_data="";
    for(var i=0;i<data_title.length;i++){
        var temp='<tr><td style=\"text-align: center\">'+data_title[i]+'</td>';
        var values=getOrderKeysAndValues(array[i]).values;
        for(var j=0;j<keys.length;j++){
            temp+='<td style="text-align: center">'+values[j]+'</td>'
        }
        temp+='</tr>';
        table_data+=temp;
        //table_data+='<tr><td style="text-align: center">'+data_title[i]+'</td><td style="text-align: center">'+values[i]+'</td></tr>'
    }
    document.getElementById(data_part).innerHTML=table_data;
    //<tr><th scope="col" style="text-align: center;">项目名称</th></tr>
    //<tr><td>1</td><td>George Washington</td><td>two</td><td>1789-1797</td></tr>
}

//三维
//data是个包含对象的数组
function writeScatter(data,divg,title) {
    var need_data=[["X","Y","Z"]];
    for(var i=0;i<data.length;i++){
        var temp=[];
        temp.push(data[i].zhibiao1);
        temp.push(data[i].zhibiao2);
        temp.push(data[i].zhibiao3);
        need_data.push(temp);
    }

    var symbolSize = 2.5;
    var myChart = echarts.init(document.getElementById(divg));
    option = {
        title: {
            text: title,
            left: 'center',
        },
        grid3D: {},
        xAxis3D: {
            type: 'category'
        },
        yAxis3D: {},
        zAxis3D: {},
        dataset: {
            dimensions: [
                'X',
                'Y',
                'Z',

            ],
            source: need_data
        },
        series: [
            {
                type: 'scatter3D',
                symbolSize: symbolSize,
                encode: {
                    x: 'X',
                    y: 'Y',
                    z: 'Z',

                }
            }
        ]
    };

    myChart.setOption(option);
    var charttitle=["X","Y","Z"];
    writeChartFor3D(charttitle,need_data,"table_title","table_data");
}

//title是数组，data是返回的数据结构
function writeChartFor3D(title,data,title_part,data_part) {
    var table_title="<tr><th scope=\"col\" style=\"text-align: center;\">编号</th>";
    for(var i=0;i<title.length;i++){
        table_title+='<th scope="col" style="text-align: center;">'+title[i]+'</th>'
    }
    table_title+='</tr>';
    document.getElementById(title_part).innerHTML=table_title;

    var table_data="";
    for(var i=1;i<data.length;i++){
        var temp='<tr><td style=\"text-align: center\">'+i+'</td>';
        for(var j=0;j<3;j++){
            temp+='<td style="text-align: center">'+data[i][j]+'</td>'
        }
        temp+='</tr>';
        table_data+=temp;
        //table_data+='<tr><td style="text-align: center">'+data_title[i]+'</td><td style="text-align: center">'+values[i]+'</td></tr>'
    }
    document.getElementById(data_part).innerHTML=table_data;
}

function getKeysAndValues(data, need_all) {
    var keys=[];
    var value=[];
    var total_value=0.00;
    for(var key in data){
        keys.push(key);
        value.push(data[key])
        var t=data[key];
        t=parseFloat(data[key]);
        total_value+=t;
    }
    if(need_all){
        var result_keys=[];
        var result_values=[];
        result_keys.push("合计");
        result_values.push(total_value);
        for(var i=0;i<keys.length;i++){
            result_keys.push(keys[i]);
            result_values.push(value[i]);
        }

        var result={keys:result_keys,values:result_values};
        return result;
    }

    else{
        var result={keys:keys,values:value};
        return result;
    }


}

function getOrderKeysAndValues(map) {
    var result_keys=[];
    var result_values=[];
    for(var i=1;i<=12;i++){
        if(i<10){
            if(map["0"+i]==undefined){
                continue;
            }
            else{
                result_keys.push("0"+i);
                result_values.push(map["0"+i]);
            }
        }
        else{
            if(map[i]==undefined){
                continue;
            }
            else{
                result_keys.push(i);
                result_values.push(map[i]);
            }
        }
    }

    var result={};
    result.keys=result_keys;
    result.values=result_values;
    return result;
}

//找到data数组中的最大值
function getBiggestValue(data) {
    var result=data[0];
    for(var i=0;i<data.length;i++){
        if(data[i]>result){
            result=data[i];
        }
    }
    return result;
}

//计算一个map中值的和
function calculateSum(map) {
    var values=getKeysAndValues(map,false).values;
    var result=0;
    for(var i=0;i<values.length;i++){
        result+=values[i];
    }
    return result;
}

function changeStatisticM() {
    initialPage();
    initialGUI();
    changePageM(1);
}

function changePageM(num) {
    initialGUI();
    var statistic_graph=$("#statistics").val();
    if(num==1){
        if(statistic_graph=="每月新注册的用户数"){
            writeLine(data.registerPerMonth,"graph" ,"每月新注册的用户数");
        }
        else if(statistic_graph=="每月用户发布，工作，完成的项目数据统计"){
            var data_title=["用户发布的项目数","用户进行中的项目数","用户完成的项目数"];
            writeMulLine(array1,data_title,"graph","每月用户发布，工作，完成的项目数据统计");
        }
        else if(statistic_graph=="每月新发布项目类型数据统计"){
            writeMulLine(array2,default_Data_title,"graph","每月新发布项目类型数据统计");
        }
        else if(statistic_graph=="每月项目进行中数据统计"){
            writeMulLine(array3,default_Data_title,"graph","每月项目进行中数据统计");
        }
        else if(statistic_graph=="每月已完成项目类型数据统计"){
            writeMulLine(array4,default_Data_title,"graph","每月已完成项目类型数据统计");
        }
        else if(statistic_graph=="一年来每类项目的平均发布数"){
            writeNewLine(data2.avgReleasedNum,"graph","一年来每类项目的平均发布数")
        }

        else if(statistic_graph=="不同图片数量 推荐积分与用户给出的积分的平均差值"){
            writeBarForNew1(newData5,"graph","不同图片数量 推荐积分与用户给出的积分的平均差值");
        }
        else if(statistic_graph=="不同图片数量 用户追加积分的平均值"){
            writeNewLine(newData6,"graph","不同图片数量 用户追加积分的平均值");
        }
        else if(statistic_graph=="不同图片数量,不同完成时间的用户平均满意度"){
            writeScatter(newData1,"graph","不同图片数量,不同完成时间的用户平均满意度");
        }
        else if(statistic_graph=="不同图片数量,不同承包人数的用户平均满意度"){
            writeScatter(newData2,"graph","不同图片数量,不同承包人数的用户平均满意度");
        }
        else if(statistic_graph=="不同图片数量 不同完成时间 项目放弃率"){
            writeScatter(newData3,"graph","不同图片数量 不同完成时间 项目放弃率");
        }
        else if(statistic_graph=="不同图片数量 不同完成时间的项目完成率"){
            writeScatter(newData4,"graph","不同图片数量 不同完成时间的项目完成率");
        }
        else{
            writeLine(data.registerPerMonth,"graph" ,"每月新注册的用户数");
        }
    }
    else if(num==2){
        if(statistic_graph=="每月新注册的用户数"){
            writeBar(data.registerPerMonth,"graph" ,"每月新注册的用户数");
        }
        else if(statistic_graph=="每月用户发布，工作，完成的项目数据统计"){
            var data_title=["用户发布的项目数","用户进行中的项目数","用户完成的项目数"];
            writeMulBar(array1,data_title,"graph","每月用户发布，工作，完成的项目数据统计");
        }
        else if(statistic_graph=="每月新发布项目类型数据统计"){
            writeMulBar(array2,default_Data_title,"graph","每月新发布项目类型数据统计");
        }
        else if(statistic_graph=="每月项目进行中数据统计"){
            writeMulBar(array3,default_Data_title,"graph","每月项目进行中数据统计");
        }
        else if(statistic_graph=="每月已完成项目类型数据统计"){
            writeMulBar(array4,default_Data_title,"graph","每月已完成项目类型数据统计");
        }
        else if(statistic_graph=="一年来每类项目的平均发布数"){
            writeBar(data2.avgReleasedNum,"graph","一年来每类项目的平均发布数")
        }
        else if(statistic_graph=="不同图片数量 用户追加积分的平均值"){
            writeBar(newData6,"graph","不同图片数量 用户追加积分的平均值");
        }
        else{
            writeBar(data.registerPerMonth,"graph" ,"每月新注册的用户数");
        }
    }
    else if(num==3){
        if(statistic_graph=="每月新注册的用户数"){
            writePie(data.registerPerMonth,"graph" ,"每月新注册的用户数");
        }
        else if(statistic_graph=="每月用户发布，工作，完成的项目数据统计"){
            var data_title=["用户发布的项目数","用户进行中的项目数","用户完成的项目数"];
            writeMulPie(array1,data_title,"graph","每月用户发布，工作，完成的项目数据统计");
        }
        else if(statistic_graph=="每月新发布项目类型数据统计"){
            writeMulPie(array2,default_Data_title,"graph","每月新发布项目类型数据统计");
        }
        else if(statistic_graph=="每月项目进行中数据统计"){
            writeMulPie(array3,default_Data_title,"graph","每月项目进行中数据统计");
        }
        else if(statistic_graph=="每月已完成项目类型数据统计"){
            writeMulPie(array4,default_Data_title,"graph","每月已完成项目类型数据统计");
        }
        else if(statistic_graph=="一年来每类项目的平均发布数"){
            writePie(data2.avgReleasedNum,"graph","一年来每类项目的平均发布数")
        }
        else if(statistic_graph=="不同图片数量 用户追加积分的平均值"){
            writePie(newData6,"graph","不同图片数量 用户追加积分的平均值");
        }
        else{
            writePie(data.registerPerMonth,"graph" ,"每月新注册的用户数");
        }
    }
    else if(num==4){
        if(statistic_graph=="每月新注册的用户数"){
            writeRadar(data.registerPerMonth,"graph" ,"每月新注册的用户数");
        }
        else if(statistic_graph=="每月用户发布，工作，完成的项目数据统计"){
            var data_title=["用户发布的项目数","用户进行中的项目数","用户完成的项目数"];
            writeMulRadar(array1,data_title,"graph","每月用户发布，工作，完成的项目数据统计");
        }
        else if(statistic_graph=="每月新发布项目类型数据统计"){
            writeMulRadar(array2,default_Data_title,"graph","每月新发布项目类型数据统计");
        }
        else if(statistic_graph=="每月项目进行中数据统计"){
            writeMulRadar(array3,default_Data_title,"graph","每月项目进行中数据统计");
        }
        else if(statistic_graph=="每月已完成项目类型数据统计"){
            writeMulRadar(array4,default_Data_title,"graph","每月已完成项目类型数据统计");
        }
        else if(statistic_graph=="一年来每类项目的平均发布数"){
            writeRadar(data2.avgReleasedNum,"graph","一年来每类项目的平均发布数")
        }
        else if(statistic_graph=="不同图片数量 用户追加积分的平均值"){
            writeRadar(newData6,"graph","不同图片数量 用户追加积分的平均值");
        }
        else{
            writeRadar(data.registerPerMonth,"graph" ,"每月新注册的用户数");
        }
    }
    else if(num==5){

    }
}