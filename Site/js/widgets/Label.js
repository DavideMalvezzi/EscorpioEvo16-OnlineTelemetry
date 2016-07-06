function Label(x, y, width, height, caption, font, color){
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.font = font;
    this.color = color;
    this.caption = caption;
    
    this.render = function(ctx, canvas){
        var x = Math.floor(this.x * canvas.width / 100);
        var y = Math.floor(this.y * canvas.height / 100);
        var boundWidth = Math.ceil(this.width * canvas.width / 100);
        var boundHeight = Math.ceil(this.height * canvas.height / 100);
        
        ctx.save();
        
        ctx.font = boundHeight + "pt " + this.font;
        ctx.fillStyle = this.color;
        ctx.textAlign = "center"; 
        
        var txtWidth = ctx.measureText(this.caption).width;
        var rateo = boundWidth / txtWidth;
        
        ctx.clearRect(x, y - boundHeight, boundWidth, boundHeight);

        //ctx.strokeStyle = "#ff0000";
        //ctx.strokeRect(x, y - boundHeight, boundWidth, boundHeight);
        //ctx.strokeRect(x, y - boundHeight, txtWidth, boundHeight);
        
        ctx.translate(x + boundWidth / 2, y);
        if(rateo < 1){
            ctx.scale(rateo, 1);
        }
        
        ctx.fillText(this.caption, 0, 0);
        
        ctx.scale(1, 1);
        ctx.translate(-x - boundWidth / 2, -y);

        ctx.restore();
        
    };
    
    this.update = function(newValue){
        this.caption = newValue;
    }
  
}


