function Chart(area, maxPoints, lineColor){
    var opt = {
            type: "line", 
            width:"100%", 
            height:"100%",
            lineColor: lineColor,
            fillColor: false,
            spotColor: false,
            minSpotColor: false,
            maxSpotColor: false,
            highlightSpotColor: "#ffffff",
            spotRadius: 3,
            lineWidth: 2
        }
        
    var area = area;
    var maxPoints = maxPoints;
    var points = [];
    
    this.addPoint = function(newValue){
        points.push(newValue);
        if(points.length > maxPoints){
            points.shift();
        }

        $(area).sparkline(points, opt);
    };
    
}
