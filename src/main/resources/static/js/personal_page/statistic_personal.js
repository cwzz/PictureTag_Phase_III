var statistics;
function Initial() {
    $.ajax({
        type : 'POST',
        url : '/user/Statistics',
        async:false,
        data :{username:JSON.parse(localStorage.getItem("current_user")).username},
        //此处data为新建项目的pid
        success : function (data) {
            statistics=data;
        },
        error: function (data) {
            alert("读取统计数据出错出错")
        }
    })

    var stars=statistics.stars;
    var acquire_state=statistics.contractPerState;
    var publish_state=statistics.releasePerState;
    star(stars);
    acquire(acquire_state);
    publish(publish_state);
}

function star(stars) {

    var keys=[];
    var value=[];
    for(var key in stars){
        keys.push(key);
        value.push(stars[key])
    }

    var myChart = echarts.init(document.getElementById('star'));
    option = {
        title: {
            text: '用户星级评分',
            left: 'center',
            top: 20,
            textStyle: {
                color: '#ccc'
            }
        },
        tooltip: {
            trigger: 'item',
            formatter: "{a} <br/>{b}: {c} ({d}%)"
        },
        legend: {
            orient: 'vertical',
            x: 'left',
            data:['1星','2星','3星','4星','5星']
        },
        series: [
            {
                name:'访问来源',
                type:'pie',
                radius: ['50%', '70%'],
                avoidLabelOverlap: false,
                label: {
                    normal: {
                        show: false,
                        position: 'center'
                    },
                    emphasis: {
                        show: true,
                        textStyle: {
                            fontSize: '30',
                            fontWeight: 'bold'
                        }
                    }
                },
                labelLine: {
                    normal: {
                        show: false
                    }
                },
                data:[
                    {value:value[0], name:'1星'},
                    {value:value[1], name:'2星'},
                    {value:value[2], name:'3星'},
                    {value:value[3], name:'4星'},
                    {value:value[4], name:'5星'}
                ]
            }
        ]
    };
    myChart.setOption(option);
}

function acquire(acquire) {
    var myChart = echarts.init(document.getElementById('state_a'));
    option = {
        title : {
            text: '我承包的项目状态',
            x:'center'
        },
        tooltip : {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend: {
            orient: 'vertical',
            left: 'left',
            data: ['审核中','标注中','未完成','投标中','申诉中','已完成']
        },
        series : [
            {
                name: '访问来源',
                type: 'pie',
                radius : '55%',
                center: ['50%', '60%'],
                data:[
                    {value:acquire.EXAMINE, name:'审核中'},
                    {value:acquire.Taging, name:'标注中'},
                    {value:acquire.UnFinished, name:'未完成'},
                    {value:acquire.APPLY, name:'投标中'},
                    {value:acquire.APPEAL, name:'申诉中'},
                    {value:acquire.FINISHED, name:'已完成'}
                ],
                itemStyle: {
                    emphasis: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    };
    myChart.setOption(option);
}

function publish(publish){
    var myChart = echarts.init(document.getElementById('state_p'));
    option = {
        title : {
            text: '某站点用户访问来源',
            subtext: '纯属虚构',
            x:'center'
        },
        tooltip : {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend: {
            orient: 'vertical',
            left: 'left',
            data: ['待承包','审核中','选择中','无人承包','标注中','未选择','未完成','申诉中','已完成']
        },
        series : [
            {
                name: '访问来源',
                type: 'pie',
                radius : '55%',
                center: ['50%', '60%'],
                data:[
                    {value:publish.WaitUndertake, name:'待承包'},
                    {value:publish.EXAMINE, name:'审核中'},
                    {value:publish.CHOOSING, name:'选择中'},,
                    {value:publish.NobodyUndertake, name:'无人承包'},
                    {value:publish.Taging, name:'标注中'},
                    {value:publish.ChooseNobody, name:'未选择'},
                    {value:publish.UnFinished, name:'未完成'},
                    {value:publish.APPEAL, name:'申诉中'},
                    {value:publish.FINISHED, name:'已完成'}

                ],
                itemStyle: {
                    emphasis: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    };

    myChart.setOption(option);
}

$(document).ready(function () {
    Initial();
})