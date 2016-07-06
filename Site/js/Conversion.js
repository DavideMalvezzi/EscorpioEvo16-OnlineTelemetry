function parseBitFlag(dv, index, size){
    
}

function parseUInt(dv, index, size){
    switch (size){
        case 1:
            return dv.getUint8(index, true);
        case 2:
            return dv.getUint16(index, true);
        case 4:
            return dv.getUint32(index, true);
        case 8:
            return (dv.getUint32(index + 4, true) << 32) | dv.getUint32(index, true);
    }
    console.warn("parseUInt invalid size: " + size);
    return NaN;
}

function parseSInt(dv, index, size){
    switch (size){
        case 1:
            return dv.getInt8(index, true);
        case 2:
            return dv.getInt16(index, true);
        case 4:
            return dv.getInt32(index, true);
        case 8:
            return (dv.getInt32(index + 4, true) << 32) | dv.getInt32(index, true);
    }
    console.warn("parseInt invalid size: " + size);
    return NaN;
}

function parseDecimal(dv, index, size){
    switch (size){
        case 4:
            return dv.getFloat32(index, true);
        case 8:
            return dv.getFloat64(index, true);
    }
    console.warn("parseDecimal invalid size: " + size);
    return NaN;
}

function parseString(dv, index, size){
    var str = "";
    for(var i = 0; i < size; i++){
        str = str.concat(String.fromCharCode(dv.getUint8(index + i, true)));
    }
    return str;
}
