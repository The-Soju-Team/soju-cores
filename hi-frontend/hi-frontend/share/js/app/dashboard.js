var myColors = ["#8DCA35","#33414E","#00BFDD","#FF702A","#DA3610",
                    "#80CDC2","#A6D969","#D9EF8B","#FFFF99","#F7EC37","#F46D43",
                    "#E08215","#D73026","#A12235","#8C510A","#14514B","#4D9220",
                    "#542688", "#4575B4", "#74ACD1", "#B8E1DE", "#FEE0B6","#FDB863",                                                
                    "#C51B7D","#DE77AE","#EDD3F2"];
d3.scale.myColors = function() {
    return d3.scale.ordinal().range(myColors);
};

revenueChart = function() {    
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/acdsdqascd',
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {   
            // Bieu do doanh thu
            nv.addGraph(function() {
                var chart = nv.models.lineChart();

                chart.margin({
                            top : 40,
                            right : 40,
                            bottom : 40,
                            left : 80
                }).color(d3.scale.myColors().range()).x(function(d) {
                    return d.stt;
                }).x(function(d) { return d.time }).y(function(d) {
                    return d.amount;
                });


                chart.xAxis.tickFormat(function(d){
                        return d3.time.format('%H:%M:%S')(new Date(d));
                    });
                chart.yAxis
                    .tickFormat(function(d) { return d3.format(",")(d) });
                    
                chart.forceY([0]);
                
                d3.select('#revenueDiv svg').datum(
                    data.revenue).transition().duration(350).call(
                    chart);

                nv.utils.windowResize(chart.update);

                return chart;
            });
            
            setTimeout(revenueChart, 1000);
        }
    });
}