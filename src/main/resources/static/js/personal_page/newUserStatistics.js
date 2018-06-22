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
    initialGUI();
    writeBar(data.contractPerState, "graph" ,"我承包的处在各个状态的项目");
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

function initialGUI2() {
    document.getElementById("inner").innerHTML='<div id="graph">\n' +
        '                        <div id="graph1" style=" float: left"></div>\n' +
        '                        <div id="graph2" style="float: left"></div>\n' +
        '                    </div>\n' +
        '                    <div id="chart" style="overflow-y: auto">\n' +
        '                        <div id="chart1" style="float: left">\n' +
        '                            <table class="table table-hover personal-task">\n' +
        '                                <thead id="table_title1">\n' +
        '                                </thead>\n' +
        '                                <tbody id="table_data1">\n' +
        '                                </tbody>\n' +
        '                            </table>\n' +
        '                        </div>\n' +
        '                        <div id="chart2" style="float: left">\n' +
        '                            <table class="table table-hover personal-task">\n' +
        '                                <thead id="table_title2">\n' +
        '                                </thead>\n' +
        '                                <tbody id="table_data2">\n' +
        '                                </tbody>\n' +
        '                            </table>\n' +
        '                        </div>\n' +
        '                    </div>';
    $("#graphics").css({"width":width+"px","height":gheight+"px"});
    $("#pagination2").css({"margin-left":(width-page_width)/2+"px"});
    $("#graph").css({"width":width+"px","height":gheight*0.6+"px"});
    $("#chart").css({"width":width+"px","height":gheight*0.38+"px"});
    $("#graph1").css({"width":width/2-10+"px","height":gheight*0.6+"px","margin-left":"5px","margin-right":"5px"});
    $("#graph2").css({"width":width/2-10+"px","height":gheight*0.6+"px","margin-left":"5px","margin-right":"5px"});
    $("#chart1").css({"width":width/2-10+"px","height":gheight*0.38+"px","margin-left":"5px","margin-right":"5px"});
    $("#chart2").css({"width":width/2-10+"px","height":gheight*0.38+"px","margin-left":"5px","margin-right":"5px"});

}

function writeBar(data, divg, title) {
    var keysAndvalues=getKeysAndValues(data,true);
    var keys=keysAndvalues.keys;
    var value=keysAndvalues.values;
    var value2=[];
    value2.push(0);
    var temp_value=0;
    for(var i=1;i<value.length;i++){
        var t=parseFloat(value[i]);
        temp_value+=t;
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

    var table_title=["标注分类","数量统计"];
    var tempkeyvalue=getKeysAndValues(data,false);
    writeChart(tempkeyvalue.keys,tempkeyvalue.values,table_title,"table_title","table_data");
}

function writeBar2(data, divg, title, table_ti, table_data) {
    var keysAndvalues=getKeysAndValues(data,true);
    var keys=keysAndvalues.keys;
    var value=keysAndvalues.values;
    if(title=="我在给定项目的积分上的投入产出比"){
        keys=["合计","0-10","10-25","25-50","50-100",">100"];
        value=[value[0],data["0-10"],data["10-25"],data["25-50"],data["50-100"],data[">100"]]
    }
    else if((title=="我在一定时间内的完成的项目贡献率")||(title=="系统在一定时间内的完成的项目贡献率")){
        keys=["合计","0-0.5","0.5-1","1-2","2-4","4-6","6-8",">8"];
        value=[value[0],data["0-0.5"],data["0.5-1"],data["1-2"],data["2-4"],data["4-6"],data["6-8"],data[">8"]]
    }
    var value2=[];
    value2.push(0);
    var temp_value=0;
    for(var i=1;i<value.length;i++){
        var t=parseFloat(value[i]);
        temp_value+=t;
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

    if(title=="我在给定项目的积分上的投入产出比"){
        var table_title=["积分区间","投入产出比"];
        keys=["0-10","10-25","25-50","50-100",">100"];
        value=[data["0-10"],data["10-25"],data["25-50"],data["50-100"],data[">100"]]
        writeChart(keys,value,table_title,table_ti,table_data);
    }
    else if((title=="我在一定时间内的完成的项目贡献率")||(title=="系统在一定时间内的完成的项目贡献率")){
        var table_title=["工作时间","贡献率"];
        keys=["0-0.5","0.5-1","1-2","2-4","4-6","6-8",">8"];
        value=[data["0-0.5"],data["0.5-1"],data["1-2"],data["2-4"],data["4-6"],data["6-8"],data[">8"]]
        $("#chart2").css({"width":width/2-40+"px"});
        writeChart(keys,value,table_title,table_ti,table_data);
    }
    else{
        var table_title=["标注分类","数量统计"];
        var tempkeyvalue=getKeysAndValues(data,false);
        writeChart(tempkeyvalue.keys,tempkeyvalue.values,table_title,table_ti,table_data);
    }

}

function writePie(data, divg, title) {
    var keysAndvalues=getKeysAndValues(data, false);
    var keys=keysAndvalues.keys;
    var values=keysAndvalues.values;

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
    writeChart(keys,values,table_title,"table_title","table_data");
}

function writePie2(data, divg, title, table_ti, table_data) {
    var keysAndvalues=getKeysAndValues(data, false);
    var keys=keysAndvalues.keys;
    var values=keysAndvalues.values;
    if(title=="我在给定项目的积分上的投入产出比"){
        keys=["0-10","10-25","25-50","50-100",">100"];
        value=[data["0-10"],data["10-25"],data["25-50"],data["50-100"],data[">100"]]
    }
    else if((title=="我在一定时间内的完成的项目贡献率")||(title=="系统在一定时间内的完成的项目贡献率")){
        keys=["0-0.5","0.5-1","1-2","2-4","4-6","6-8",">8"];
        value=[data["0-0.5"],data["0.5-1"],data["1-2"],data["2-4"],data["4-6"],data["6-8"],data[">8"]]
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


    if(title=="我在给定项目的积分上的投入产出比"){
        var table_title=["积分区间","投入产出比"];
        writeChart(keys,value,table_title,table_ti,table_data);
    }
    else if((title=="我在一定时间内的完成的项目贡献率")||(title=="系统在一定时间内的完成的项目贡献率")){
        var table_title=["工作时间","贡献率"];
        $("#chart2").css({"width":width/2-40+"px"});
        writeChart(keys,value,table_title,table_ti,table_data);
    }
    else{
        var table_title=["标注分类","数量统计"];
        writeChart(keys,values,table_title,table_ti,table_data);
    }
}

function writeRadar(data, divg, title) {
    var keysAndvalues=getKeysAndValues(data, false);
    var keys=keysAndvalues.keys;
    var values=keysAndvalues.values;
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
    writeChart(keys,values,table_title,"table_title","table_data");
}

function writeRadar2(data, divg, title, table_ti, table_data) {
    var keysAndvalues=getKeysAndValues(data, false);
    var keys=keysAndvalues.keys;
    var values=keysAndvalues.values;
    if(title=="我在给定项目的积分上的投入产出比"){
        keys=["0-10","10-25","25-50","50-100",">100"];
        value=[data["0-10"],data["10-25"],data["25-50"],data["50-100"],data[">100"]]
    }
    else if((title=="我在一定时间内的完成的项目贡献率")||(title=="系统在一定时间内的完成的项目贡献率")){
        keys=["0-0.5","0.5-1","1-2","2-4","4-6","6-8",">8"];
        value=[data["0-0.5"],data["0.5-1"],data["1-2"],data["2-4"],data["4-6"],data["6-8"],data[">8"]]
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

    if(title=="我在给定项目的积分上的投入产出比"){
        var table_title=["积分区间","投入产出比"];
        writeChart(keys,value,table_title,table_ti,table_data);
    }
    else if((title=="我在一定时间内的完成的项目贡献率")||(title=="系统在一定时间内的完成的项目贡献率")){
        var table_title=["工作时间","贡献率"];
        $("#chart2").css({"width":width/2-40+"px"});
        writeChart(keys,value,table_title,table_ti,table_data);
    }
    else{
        var table_title=["标注分类","数量统计"];
        writeChart(keys,values,table_title,table_ti,table_data);
    }
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


//title是标题栏
function writeChart(keys,values,title,title_part, data_part) {
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

function getKeysAndValues(data, need_all) {
    var keys=[];
    var value=[];
    var total_value=0;
    for(var key in data){
        keys.push(key);
        value.push(data[key])
        var t=parseFloat(data[key]);
        //total_value+=data[key];
        total_value+=t;
    }
    //alert(JSON.stringify(keys)+"inkey");
    //alert(JSON.stringify(value)+"inkey");
    //alert(total_value)
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

function changeStatistic() {
    var statistic_graph=$("#statistics").val();
    initialPage();
    if((statistic_graph=="我在每类标注平均花费的时间")||(statistic_graph=="我在每类标注平均获得的积分")||(statistic_graph=="我在每类任务花费时间和得到积分的投入产出比")){
        initialGUI2();
    }
    else{
        initialGUI();
    }
    changePage(1);
}

function changePage(num) {
    var statistic_graph=$("#statistics").val();
    if((statistic_graph=="我在不同类别项目平均投入产出比/在给定项目的积分上的投入产出比")||(statistic_graph=="我（系统）在各个类别的贡献率")||(statistic_graph=="我（系统）在一定时间内的完成的项目贡献率")){
        initialGUI2();
    }
    else {
        initialGUI();
    }
    if(num==1){
        if((statistic_graph=="我承包的处在各个状态的项目")||(statistic_graph=="")){
            writeBar(data.contractPerState, "graph" ,"我承包的处在各个状态的项目");
        }
        else if(statistic_graph=="我承包的处在各个类别的项目"){
            writeBar(data.contractPerType, "graph" ,"我承包的处在各个类别的项目");
        }
        else if(statistic_graph=="我发布的处在各个状态的项目"){
            writeBar(data.releasePerState, "graph" ,"我发布的处在各个状态的项目");
        }
        else if(statistic_graph=="我发布的处在各个类别的项目"){
            writeBar(data.releasePerType, "graph" ,"我发布的处在各个类别的项目");
        }
        else if (statistic_graph=="我在不同类别项目平均投入产出比/在给定项目的积分上的投入产出比") {
            writeBar2(data.chanChuBiPerType, "graph1" ,"我在不同类别项目平均投入产出比","table_title1","table_data1");
            writeBar2(data.chanChuBiByCredits, "graph2" ,"我在给定项目的积分上的投入产出比","table_title2","table_data2");
        }
        else if(statistic_graph=="我（系统）在各个类别的贡献率"){
            writeBar2(data.gongxianPerType, "graph1" ,"我在各个类别的贡献率","table_title1","table_data1");
            writeBar2(data.gongxianPerTypeAllUser, "graph2" ,"系统在各个类别的贡献率","table_title2","table_data2");
        }
        else if(statistic_graph=="我（系统）在一定时间内的完成的项目贡献率"){
            writeBar2(data.gongxianAndTime, "graph1" ,"我在一定时间内的完成的项目贡献率","table_title1","table_data1");
            writeBar2(data.gongxianAndTimeAllUser, "graph2" ,"系统在一定时间内的完成的项目贡献率","table_title2","table_data2");
        }
        else if(statistic_graph=="发布项目的积分和承包人数的关系图"){
            writeScatter(data.creditsAndContractNum,"graph","发布项目的积分和承包人数的关系图");
        }
    }
    else if(num==2){
        if((statistic_graph=="我承包的处在各个状态的项目")||(statistic_graph=="")){
            writePie(data.contractPerState, "graph" ,"我承包的处在各个状态的项目");
        }
        else if(statistic_graph=="我承包的处在各个类别的项目"){
            writePie(data.contractPerType, "graph" ,"我承包的处在各个类别的项目");
        }
        else if(statistic_graph=="我发布的处在各个状态的项目"){
            writePie(data.releasePerState, "graph" ,"我发布的处在各个状态的项目");
        }
        else if(statistic_graph=="我发布的处在各个类别的项目"){
            writePie(data.releasePerType, "graph" ,"我发布的处在各个类别的项目");
        }
        else if (statistic_graph=="我在不同类别项目平均投入产出比/在给定项目的积分上的投入产出比") {
            writePie2(data.chanChuBiPerType, "graph1" ,"我在不同类别项目平均投入产出比","table_title1","table_data1");
            writePie2(data.chanChuBiByCredits, "graph2" ,"我在给定项目的积分上的投入产出比","table_title2","table_data2");
        }
        else if(statistic_graph=="我（系统）在各个类别的贡献率"){
            writePie2(data.gongxianPerType, "graph1" ,"我在各个类别的贡献率","table_title1","table_data1");
            writePie2(data.gongxianPerTypeAllUser, "graph2" ,"系统在各个类别的贡献率","table_title2","table_data2");
        }
        else if(statistic_graph=="我（系统）在一定时间内的完成的项目贡献率"){
            writePie2(data.gongxianAndTime, "graph1" ,"我在一定时间内的完成的项目贡献率","table_title1","table_data1");
            writePie2(data.gongxianAndTimeAllUser, "graph2" ,"系统在一定时间内的完成的项目贡献率","table_title2","table_data2");
        }
        else if(statistic_graph=="发布项目的积分和承包人数的关系图"){

        }
    }
    else if(num==3){
        if((statistic_graph=="我承包的处在各个状态的项目")||(statistic_graph=="")){
            writeRadar(data.contractPerState, "graph" ,"我承包的处在各个状态的项目");
        }
        else if(statistic_graph=="我承包的处在各个类别的项目"){
            writeRadar(data.contractPerType, "graph" ,"我承包的处在各个类别的项目");
        }
        else if(statistic_graph=="我发布的处在各个状态的项目"){
            writeRadar(data.releasePerState, "graph" ,"我发布的处在各个状态的项目");
        }
        else if(statistic_graph=="我发布的处在各个类别的项目"){
            writeRadar(data.releasePerType, "graph" ,"我发布的处在各个类别的项目");
        }
        else if (statistic_graph=="我在不同类别项目平均投入产出比/在给定项目的积分上的投入产出比") {
            writeRadar2(data.chanChuBiPerType, "graph1" ,"我在不同类别项目平均投入产出比","table_title1","table_data1");
            writeRadar2(data.chanChuBiByCredits, "graph2" ,"我在给定项目的积分上的投入产出比","table_title2","table_data2");
        }
        else if(statistic_graph=="我（系统）在各个类别的贡献率"){
            writeRadar2(data.gongxianPerType, "graph1" ,"我在各个类别的贡献率","table_title1","table_data1");
            writeRadar2(data.gongxianPerTypeAllUser, "graph2" ,"系统在各个类别的贡献率","table_title2","table_data2");
        }
        else if(statistic_graph=="我（系统）在一定时间内的完成的项目贡献率"){
            writeRadar2(data.gongxianAndTime, "graph1" ,"我在一定时间内的完成的项目贡献率","table_title1","table_data1");
            writeRadar2(data.gongxianAndTimeAllUser, "graph2" ,"系统在一定时间内的完成的项目贡献率","table_title2","table_data2");
        }
        else if(statistic_graph=="发布项目的积分和承包人数的关系图"){

        }
    }
}
