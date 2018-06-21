function getData() {
    $.ajax({
        type : 'POST',
        url : '/project/cal',
        data :{},
        async:false,
        //此处data为新建项目的pid
        success : function (data) {
            statistic=data;
        },
        error: function (data) {
            alert("获取数据出错")
        }
    })
}

var statistic;
$(document).ready(function () {
    getData();
    $(".statistic_graph").css({"width":"300px","height":"300px","float":"left","background-color":"#FFFFFF","margin":"2px"})
    publish(statistic.realeaseNum);
    ask(statistic.appealNum);
    star(statistic.stars);
    state(statistic.proStateRate);
    type(statistic.proTypeRate);
    average(statistic.proTypeNumRate);
})

function publish(data) {
    var keys=[];
    var value=[];
    for(var key in data){
        keys.push(key);
        value.push(data[key])
    }

    var myChart = echarts.init(document.getElementById('project_published_monthly'));
    option = {

        title: {
            text: '每月发布项目',
            left: 'center',
            top: 20,
            textStyle: {
                color: '#ccc'
            }
        },

        xAxis: {
            type: 'category',
            data: keys,
        },
        yAxis: {
            type: 'value'
        },
        series: [{
            data: value,
            type: 'line'
        }]
    };
    myChart.setOption(option);
}

function ask(data) {
    var keys=[];
    var value=[];
    for(var key in data){
        keys.push(key);
        value.push(data[key])
    }

    var myChart = echarts.init(document.getElementById('project_ask_for_reply_monthly'));
    option = {
        title: {
            text: '每月申诉项目',
            left: 'center',
            top: 20,
            textStyle: {
                color: '#ccc'
            }
        },

        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: keys,
        },
        yAxis: {
            type: 'value'
        },
        series: [{
            data: value,
            type: 'line',
            areaStyle: {}
        }]
    };
    myChart.setOption(option);
}

function star(data) {
    var keys=[];
    var value=[];
    for(var key in data){
        keys.push(key);
        value.push(data[key])
    }

    var myChart = echarts.init(document.getElementById('star_rate'));
    option = {
        backgroundColor: '#FFFFFF',

        title: {
            text: '星级百分比',
            left: 'center',
            top: 20,
            textStyle: {
                color: '#ccc'
            }
        },

        tooltip : {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },

        visualMap: {
            show: false,
            min: 80,
            max: 600,
            inRange: {
                colorLightness: [0, 1]
            }
        },
        series : [
            {
                name:'好评打星',
                type:'pie',
                radius : '55%',
                center: ['50%', '50%'],
                data:[
                    {value:value[0], name:'一颗星'},
                    {value:value[1], name:'两颗星'},
                    {value:value[2], name:'三颗星'},
                    {value:value[3], name:'四颗星'},
                    {value:value[4], name:'五颗星'}
                ].sort(function (a, b) { return a.value - b.value; }),
                roseType: 'radius',
                label: {
                    normal: {
                        textStyle: {
                            color: 'rgba(255, 255, 255, 0.3)'
                        }
                    }
                },
                labelLine: {
                    normal: {
                        lineStyle: {
                            color: 'rgba(255, 255, 255, 0.3)'
                        },
                        smooth: 0.2,
                        length: 10,
                        length2: 20
                    }
                },
                itemStyle: {
                    normal: {
                        color: '#c23531',
                        shadowBlur: 200,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                },

                animationType: 'scale',
                animationEasing: 'elasticOut',
                animationDelay: function (idx) {
                    return Math.random() * 200;
                }
            }
        ]
    };
    myChart.setOption(option);
}

function state(data) {
    var keys=[];
    var value=[];
    for(var key in data){
        keys.push(key);
        value.push(data[key])
    }

    var myChart = echarts.init(document.getElementById('project_type_rate'));
    option = {
        title: {
            text: '项目状态',
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
            data:keys,
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
                    {value:value[0], name:keys[0]},
                    {value:value[1], name:keys[1]},
                    {value:value[2], name:keys[2]},
                    {value:value[3], name:keys[3]},
                    {value:value[4], name:keys[4]},
                    {value:value[5], name:keys[5]},
                    {value:value[6], name:keys[6]},
                    {value:value[7], name:keys[7]},
                    {value:value[8], name:keys[8]},
                ]
            }
        ]
    };

    myChart.setOption(option);
}

function type(data) {
    var keys=[];
    var value=[];
    for(var key in data){
        keys.push(key);
        value.push(data[key])
    }

    var myChart = echarts.init(document.getElementById('project_state_rate'));
    var weatherIcons = {
        'Sunny': './data/asset/img/weather/sunny_128.png',
        'Cloudy': './data/asset/img/weather/cloudy_128.png',
        'Showers': './data/asset/img/weather/showers_128.png'
    };

    option = {
        title: {
            text: '项目状态',
            left: 'center',
            top: 20,
            textStyle: {
                color: '#ccc'
            }
        },
        tooltip : {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend: {
            // orient: 'vertical',
            // top: 'middle',
            bottom: 10,
            left: 'center',
            data: keys,
        },
        series : [
            {
                type: 'pie',
                radius : '65%',
                center: ['50%', '50%'],
                selectedMode: 'single',
                data:[
                    // {
                    //     value:1548,
                    //     name: '幽州',
                    //     label: {
                    //         normal: {
                    //             formatter: [
                    //                 '{title|{b}}{abg|}',
                    //                 '  {weatherHead|天气}{valueHead|天数}{rateHead|占比}',
                    //                 '{hr|}',
                    //                 '  {Sunny|}{value|202}{rate|55.3%}',
                    //                 '  {Cloudy|}{value|142}{rate|38.9%}',
                    //                 '  {Showers|}{value|21}{rate|5.8%}'
                    //             ].join('\n'),
                    //             backgroundColor: '#eee',
                    //             borderColor: '#777',
                    //             borderWidth: 1,
                    //             borderRadius: 4,
                    //             rich: {
                    //                 title: {
                    //                     color: '#eee',
                    //                     align: 'center'
                    //                 },
                    //                 abg: {
                    //                     backgroundColor: '#333',
                    //                     width: '100%',
                    //                     align: 'right',
                    //                     height: 25,
                    //                     borderRadius: [4, 4, 0, 0]
                    //                 },
                    //                 Sunny: {
                    //                     height: 30,
                    //                     align: 'left',
                    //                     backgroundColor: {
                    //                         image: weatherIcons.Sunny
                    //                     }
                    //                 },
                    //                 Cloudy: {
                    //                     height: 30,
                    //                     align: 'left',
                    //                     backgroundColor: {
                    //                         image: weatherIcons.Cloudy
                    //                     }
                    //                 },
                    //                 Showers: {
                    //                     height: 30,
                    //                     align: 'left',
                    //                     backgroundColor: {
                    //                         image: weatherIcons.Showers
                    //                     }
                    //                 },
                    //                 weatherHead: {
                    //                     color: '#333',
                    //                     height: 24,
                    //                     align: 'left'
                    //                 },
                    //                 hr: {
                    //                     borderColor: '#777',
                    //                     width: '100%',
                    //                     borderWidth: 0.5,
                    //                     height: 0
                    //                 },
                    //                 value: {
                    //                     width: 20,
                    //                     padding: [0, 20, 0, 30],
                    //                     align: 'left'
                    //                 },
                    //                 valueHead: {
                    //                     color: '#333',
                    //                     width: 20,
                    //                     padding: [0, 20, 0, 30],
                    //                     align: 'center'
                    //                 },
                    //                 rate: {
                    //                     width: 40,
                    //                     align: 'right',
                    //                     padding: [0, 10, 0, 0]
                    //                 },
                    //                 rateHead: {
                    //                     color: '#333',
                    //                     width: 40,
                    //                     align: 'center',
                    //                     padding: [0, 10, 0, 0]
                    //                 }
                    //             }
                    //         }
                    //     }
                    // },
                    // {value:535, name: '荆州'},
                    // {value:510, name: '兖州'},
                    // {value:634, name: '益州'},
                    // {value:735, name: '西凉'}
                    {value:value[0],name:keys[0]},
                    {value:value[1],name:keys[1]},
                    {value:value[2],name:keys[2]},
                    {value:value[3],name:keys[3]},
                    {value:value[4],name:keys[4]},

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

function average(data) {
    var keys=[];
    var value=[];
    for(var key in data){
        keys.push(key);
        value.push(data[key])
    }

    var myChart = echarts.init(document.getElementById('project_average_request'));
    option = {
        title: {
            text: '项目状态',
            left: 'center',
            top: 20,
            textStyle: {
                color: '#ccc'
            }
        },

        xAxis: {
            type: 'category',
            data: ['动物','风景','人物','物品','其他'],
        },
        yAxis: {
            type: 'value'
        },
        series: [{
            data: value,
            type: 'bar'
        }]
    };

    myChart.setOption(option);
}