function Spectrum(x, y, width, height, min, max, num){
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.num = num;
    
    this.bars = new Array();
    for(var i = 0; i < num; i++){
        this.bars[i] = new SpectrumBar(min, max, this);
    }
   
    this.render = function(ctx, canvas){
        var BORDER = 3;
        var TICK_H = 2;
        var x = Math.floor(this.x * canvas.width / 100);
        var y = Math.floor(this.y * canvas.height / 100);
        var width = Math.ceil(this.width * canvas.width / 100);
        var height = Math.ceil(this.height * canvas.height / 100);
        
        ctx.save();
        ctx.clearRect(x, y, width, height);

        ctx.fillStyle ="#dadada";
        ctx.fillRect(x, y, width, height);
        ctx.fillStyle = "#1f1f1f";
        ctx.fillRect(x + BORDER, y + BORDER, width - BORDER * 2, height - BORDER * 2);
        
        var v;
        var barW = (width - BORDER * 2) / this.num;
        var size = height - BORDER * 2;
        var n = Math.floor(size / 4);
        var offY = Math.floor((size - n * 4) / 2);
        var length;
        
        for(var j = 0; j < this.num; j++){
            v = this.bars[j].getValueNorm() * size;

            for(var i = 0 ; i < n; i ++){
                length = i * TICK_H * 2;
                if(length < size * 0.5){
                     ctx.fillStyle = (length >= v) ? "#367942" : "#74f284";
                }
                else if(length >= size * 0.5 && length <= size * 0.8){
                     ctx.fillStyle = (length >= v) ? "#796b00" : "#ffda00";
                }
                else{
                     ctx.fillStyle = (length >= v) ? "#7f0001" : "#ff0400";
                }
                ctx.fillRect(x + BORDER + 1 + j * barW, y + offY + size - length, barW - 2, TICK_H); 
           }
            
        }
        


        
         ctx.strokeStyle = "#000000";
        ctx.strokeRect(x, y, width, height);
        ctx.strokeRect(x + BORDER, y + BORDER, width - BORDER * 2, height - BORDER * 2);
      
        ctx.restore();
    }			
    
    this.getBar = function(index){
        return this.bars[index];
    }
}

function SpectrumBar(min, max, parent){
    this.min = min;
    this.max = max;
    this.parent = parent;
    this.currentValue = 0;
    
    this.render = function(ctx, canvas){
        this.parent.render(ctx, canvas);
    }
    
    this.update = function (newValue){
        if(newValue < min){
            this.currentValue = min;
        }
        else if(newValue > max){
            this.currentValue = max;
        }
        else{
            this.currentValue = newValue;
        }
    }
    
    this.getValueNorm = function(){
        return this.currentValue / (this.max - this.min);
    }
    
}


