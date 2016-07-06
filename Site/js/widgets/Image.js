function ImageWidget(x, y, width, height, src){
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.src = src;
    
    
    this.render = function(ctx, canvas){
        var x = Math.floor(this.x * canvas.width / 100);
        var y = Math.floor(this.y * canvas.height / 100);
        var width = Math.ceil(this.width * canvas.width / 100);
        var height = Math.ceil(this.height * canvas.height / 100);
              
        var img = new Image;
        img.src = this.src;

        img.onload = function(){
            ctx.save();
            ctx.drawImage(img, x, y, width, height);
            //ctx.strokeRect(x, y, width, height);
            ctx.restore();
        };
        
    };
    
    this.update = function(newValue){
        this.src = newValue;
    };
    
    
}
