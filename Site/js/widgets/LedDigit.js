function LedDigit(x, y, width, height, intDigit, decDigit, color, signed, time){
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.intDigit = intDigit;
    this.decDigit = decDigit;
    this.color = color;
    this.value = 0;
    this.signed = signed;
    this.time = time || false;
    
    this.render = function(ctx, canvas){
        var x = Math.floor(this.x * canvas.width / 100);
        var y = Math.floor(this.y * canvas.height / 100);
        var size = Math.ceil(this.height * 1.7 * canvas.height / 100);
        var boundWidth = Math.ceil(this.width * canvas.width / 100);
        var boundHeight = Math.ceil(this.height * canvas.height / 100);
        
        var intPart = Math.floor(this.value);
        var decPart = this.value - intPart;
        
        var intStr = intPart.toString();
       
        while(intStr.length > this.intDigit)intStr = intStr.slice(1);
        while(intStr.length < this.intDigit)intStr = '0' + intStr;
        
        //var decStr = Math.ceil(decPart * Math.pow(10, this.decDigit)).toString();

        //while(decStr.length > this.decDigit)decStr = decStr.slice(0, -1);
        //while(decStr.length < this.decDigit)decStr = decStr + '0';

        var decStr = this.value.toFixed(decDigit);
        decStr = decStr.substr(decStr.indexOf('.') + 1);

        ctx.save();

        ctx.font = size + "px DigitFont";
        ctx.fillStyle = color;  
        
        var signSize = ctx.measureText("+").width;
        var zeroSize = ctx.measureText("0").width;
        var pointSize = ctx.measureText(".").width;
        
        var txtWidth;
        
        if(decDigit > 0){
            txtWidth = (intDigit + decDigit) * zeroSize + pointSize;
        }
        else{
            txtWidth = intDigit * zeroSize;
        }
        
        if(this.signed){
            txtWidth += signSize;
        }

        ctx.clearRect(x - 1, y - boundHeight - 1, boundWidth + 1, boundHeight + 1);

        //ctx.strokeStyle = "#ff0000";
        //ctx.strokeRect(x, y - boundHeight, boundWidth, boundHeight);
        //ctx.strokeRect(x, y - boundHeight, txtWidth, boundHeight);

        var startPos = 0;

        ctx.translate(x, y);
        ctx.scale(boundWidth / txtWidth, 1);
        
        if(this.signed){
            if(intPart >= 0){
                ctx.fillText("+", startPos, 0);
            }
            else{
                ctx.fillText("-", startPos, 0);
            }
            startPos += signSize;
        }
        
        for(var i = 0; i < intStr.length; i++){
            ctx.fillText(intStr.charAt(i), startPos, 0);
            startPos += zeroSize;
        }
        
        if(this.decDigit > 0){
            if(this.time){
                ctx.fillText(":", startPos, 0);
            }
            else{
                ctx.fillText(".", startPos, 0);
            }
            startPos += pointSize;

            for(var i = 0; i < decStr.length; i++){
                ctx.fillText(decStr.charAt(i), startPos, 0);
                startPos += zeroSize;
            }
        }
        
        ctx.scale(1, 1);
        ctx.translate(-x, -y);

        ctx.restore();     
    };
    
    this.update = function(newValue){
        this.value = newValue;
    };
}


