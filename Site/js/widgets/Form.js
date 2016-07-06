function Form(areaID, canvasID){
    var area = document.getElementById(areaID);
    var canvas = document.getElementById(canvasID);
    var ctx = canvas.getContext("2d");

    this.widgets = new Array();
       
    this.addWidget = function(id, widget){
        if(id in this.widgets){           
            this.widgets[id].push(widget);        
        }
        else{     
            this.widgets[id] = new Array(widget);
            this.widgets[id].push(widget);
        }
    };
    
    this.redraw = function(){
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        //ctx.strokeStyle = "#ff0000";
        //ctx.strokeRect(0, 0, canvas.width, canvas.height);
        
        for(id in this.widgets){
            for(var i = 0; i < this.widgets[id].length; i++){
                this.widgets[id][i].render(ctx, canvas);
            }
        }
    };
    
    this.resizeCanvas = function(){
        //alert("area " + area.clientWidth + "  " + area.clientHeight);
        
        canvas.style.width = area.clientWidth;
        canvas.style.height = area.clientHeight;
        canvas.width = area.clientWidth;
        canvas.height = area.clientHeight;
        
        //alert("canvas " + canvas.clientWidth + "  " + canvas.clientHeight);
    };
    
    this.onNewDataAvailable = function(id, newValue){
        if(id in this.widgets){
            for(var i = 0; i < this.widgets[id].length; i++){
                this.widgets[id][i].update(newValue);
                this.widgets[id][i].render(ctx, canvas);
            }
        }   
        
    }
    
    this.getCanvas = function(){ return canvas; }
    this.getContext = function(){ return ctx; }


}

