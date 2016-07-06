function Led(x, y, radius, onColor, offColor, check){
    this.x = x;
    this.y = y;
    this.radius = radius / 2;
    this.onColor = onColor;
    this.offColor = offColor;
    this.check = check;
    this.state = false;
    
    this.render = function(ctx, canvas){
        var BORDER = 2;
        var x = Math.floor(this.x * canvas.width / 100);
        var y = Math.floor(this.y * canvas.height / 100);
        var radius = Math.ceil(this.radius * canvas.width / 100);
        
        ctx.save();
        
        ctx.fillStyle = "#dadada";
        ctx.beginPath();
        ctx.arc(x, y, radius, 0, 2 * Math.PI);
        ctx.closePath();
        ctx.fill();
        
        
        if(this.state){
            ctx.fillStyle = this.onColor;
        }
        else{
            ctx.fillStyle = this.offColor;
        }
        
        ctx.beginPath();
        ctx.arc(x, y, radius - BORDER, 0, 2 * Math.PI);
        ctx.closePath();
        ctx.fill();
        
        ctx.restore();
    };
    
    this.update = function(newValue){
        this.state = this.check(newValue);
    };
    
    
}



