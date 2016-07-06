function Bar(x, y, width, height, min, max){
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.min = min;
    this.max = max;
    this.currentValue = 0;

    this.render = function(ctx, canvas){
        var BORDER = 3;
        var x = Math.floor(this.x * canvas.width / 100);
        var y = Math.floor(this.y * canvas.height / 100);
        var width = Math.ceil(this.width * canvas.width / 100);
        var height = Math.ceil(this.height * canvas.height / 100);
        
        ctx.save();

        ctx.fillStyle ="#dadada";
        ctx.fillRect(x, y, width, height);
        ctx.fillStyle = "#1f1f1f";
        ctx.fillRect(x + BORDER, y + BORDER, width - BORDER * 2, height - BORDER * 2);
		
        var size = width - BORDER * 2;
        var n = Math.floor(size / 4);

        var v = this.currentValue / (max - min) * size;

        for(var i = Math.floor((size - n * 4) / 2); i < size; i += 4){
           if(i < size * 0.5){
                ctx.fillStyle = (i >= v) ? "#367942" : "#74f284";
           }
           else if(i >= size * 0.5 && i <= size * 0.8){
                ctx.fillStyle = (i >= v) ? "#796b00" : "#ffda00";
           }
           else{
                ctx.fillStyle = (i >= v) ? "#7f0001" : "#ff0400";
           }

           ctx.fillRect(x + BORDER + i, y + BORDER,  2, height - BORDER * 2); 
        }
        

        ctx.strokeStyle = "#000000";
        ctx.strokeRect(x, y, width, height);
        ctx.strokeRect(x + BORDER, y + BORDER, width - BORDER * 2, height - BORDER * 2);

        ctx.restore();
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
				
}