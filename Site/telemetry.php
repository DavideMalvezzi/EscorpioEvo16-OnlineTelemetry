<html>
    <head>
        <title>Escorpio Online Telemetry</title>
        
        <!-- Fav icon -->
        <link href = "favicon.ico" rel = "icon"  type = "image/vnd.microsoft.icon">

        <!-- Page style -->
        <link href = "style.css" rel = "stylesheet" type = "text/css">
        
        <!-- Ws connection -->
        <script type = "text/javascript" src = "js/Connection.js"></script>
        <script type = "text/javascript" src = "js/Conversion.js"></script>
        
        <!-- JQuery -->
        <link href = "js/jquery/jquery-ui.css" rel="stylesheet">
        <script type = "text/javascript" src = "js/jquery/jquery-1.12.1.min.js"></script>
        <script type = "text/javascript" src = "js/jquery/jquery-ui.min.js"></script>

        <!-- JQuery widgets-->
        <script type = "text/javascript" src = "js/sparklines/jquery.sparkline.min.js"></script>
        <script type = "text/javascript" src = "js/widgets/Chart.js"></script>
        <script type = "text/javascript" src = "js/widgets/MsgDialog.js"></script>

        <!-- Dashboard widgets -->
        <script type = "text/javascript" src = "js/widgets/Form.js"></script>
        <script type = "text/javascript" src = "js/widgets/Bar.js"></script>
        <script type = "text/javascript" src = "js/widgets/Image.js"></script>
        <script type = "text/javascript" src = "js/widgets/Led.js"></script>
        <script type = "text/javascript" src = "js/widgets/LedDigit.js"></script>
        <script type = "text/javascript" src = "js/widgets/Label.js"></script>
        <script type = "text/javascript" src = "js/widgets/Spectrum.js"></script>
        
        <!-- CanvasJs charts -->
        <!--<script type = "text/javascript" src = "js/canvasjs/canvasjs.min.js"></script>-->

        <!-- Google map related script -->
        <!--<script type="text/javascript" src = "js/map/GoogleMapApis.js"></script>-->
        <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAD9MxVhOWQBHC9FHkkLrBO45zrnRKj6ng"></script>
        <!--<script type = "text/javascript" src = "js/map/GoogleMap.js"></script>-->
        <script type = "text/javascript" src = "js/map/Map.js"></script>

        <?php
			            
			include "dbaccess.php";
			
        ?>
        
        <script type = "text/javascript">
            var channels = new Array();
            
            <?php 
                $q = "SELECT * FROM Channel";
                $result = mysqli_query($conn, $q);
                if($result){
                    while(($row = $result->fetch_assoc())) {
                        echo "channels[" . $row["ID"] . "] = {"
                                /*name:'". $row["Name"] ."', */ . 
                                "size:" . $row["Size"] . "," . 
                                "type:'" . $row["Type"] . "'," . 
                                "conv: function(x){return " . $row["Conversion"]  . ";}" . 
                                "};\n";
                    }
                }
            
            ?>
        </script>
        
    </head>

    <body>
        <div id = "preLoadFont">.</div>

        <div id = "msgBox" class = "ui-widget" style="display: none;">
            <div class = "ui-state-highlight ui-corner-all msgContainer">
                <span class = "ui-icon ui-icon-info msgIcon"></span>
                <div id = "msgBoxText">Message</div>
            </div>
        </div>
        
        <div id = "alertBox" class = "ui-widget" style="display: none;">
            <div class = "ui-state-error ui-corner-all msgContainer">
                <span class = "ui-icon ui-icon-alert msgIcon"></span>
                <div id = "alertBoxText">Error</div>
            </div>
        </div>

        <div id = "mainContainer">

            <div id = "driverFormArea" class = "cell">
                <canvas id = "driverFormCanvas"></canvas> 
            </div>
            
            <div id = "bmsFormArea" class = "cell">
                <canvas id = "bmsFormCanvas"></canvas>
            </div>

            <div id = "motorFormArea" class = "cell">
                <div id = "motorCanvasArea" style = "display: inline-block; float:left; width: 41%; height: 100%; ">
                    <canvas id = "motorFormCanvas"></canvas>
                </div>
                
                <div id = "chartContainer" style = "display: inline-block; float:right; width: 59%; height: 100%;">
                    <div style="height: 20%;"><span id="chartCanvas1"></span></div>
                    <div style="height: 20%;"><span id="chartCanvas2"></span></div>
                    <div style="height: 20%;"><span id="chartCanvas3"></span></div>
                    <div style="height: 20%;"><span id="chartCanvas4"></span></div>
                    <div style="height: 20%;"><span id="chartCanvas5"></span></div> 
                </div>
            </div>

            <div id = "logoArea" class = "cell">
                <a href="index.php">
                    <img id = "logoImg" src="imgs/escorpioLogo.png">
                </a>
            </div>
            
            <div id = "mapCanvas" class = "cell"></div>

        </div>

        <script type = "text/javascript">
            var gpsMap, forms, charts; 

            function resize(){                     
                for(f in forms){
                    forms[f].resizeCanvas();
                    forms[f].redraw();
                }   
                                
                gpsMap.resize();

            }

            function loadMap(){
                var lat, lon;
                
                <?php
                    $trackID = $_GET["track"];
                    
                    $q = "SELECT CenterLat, CenterLon FROM Track WHERE ID = " . $trackID;
                    $result = mysqli_query($conn, $q);
                    if($result){
                        if($row = $result->fetch_assoc()){
                            echo "lat = " . $row["CenterLat"] . ";";
                            echo "lon = " . $row["CenterLon"] . ";";
                        }
                    }
                ?>
                
                var track = [
                    <?php
                        $q = "SELECT Lat, Lon FROM Coordinate WHERE TrackCod = " . $trackID . " ORDER BY ID";
                        $result = mysqli_query($conn, $q);
                        if($result){
                            while(($row = $result->fetch_assoc())) {
                                echo "{lat: " . $row["Lat"] . ", lng: " . $row["Lon"] . "},\n";
                            }
                        }
                    ?>
                ];
                
                gpsMap = new GpsMap("mapCanvas", lat, lon, track);
            }

            function loadForms(){
                forms = new Array();
              
                //Driver form
                forms["driver"] = new Form("driverFormArea", "driverFormCanvas");
                //Power bar
                forms["driver"].addWidget(0xA5, new Bar(2, 3, 96, 10, 0, 255));
                //Instant speed
                forms["driver"].addWidget(-1,    new Label(2, 20, 39, 5, "Instant Speed", "Tahoma", "#ffffff"));
                forms["driver"].addWidget(0x200, new LedDigit(2, 45.5, 40, 23, 2, 2, "#7fb36e"));
                //Avg speed
                forms["driver"].addWidget(-1,    new Label(58, 20, 39, 5, "Average Speed", "Tahoma", "#ffffff"));
                forms["driver"].addWidget(0x201, new LedDigit(58, 45.5, 40, 23, 2, 2, "#7fb36e"));
                //Lap
                forms["driver"].addWidget(-1,    new Label(45, 24, 9, 5, "Lap", "Tahoma", "#ffffff"));
                forms["driver"].addWidget(0x204, new LedDigit(45, 40, 10, 13, 2, 0, "#ecbc47"));
                //Current lap time
                forms["driver"].addWidget(-1,    new Label(5, 55, 26, 5, "Current Lap", "Tahoma", "#ffffff"));
                forms["driver"].addWidget(0x207, new LedDigit(3, 75, 30, 18, 2, 2, "#6ed1e1", false, true));
                //Left time
                forms["driver"].addWidget(-1,    new Label(42, 51, 16, 5, "Left Time", "Tahoma", "#ffffff"));
                forms["driver"].addWidget(0x203, new LedDigit(35, 71, 30, 18, 2, 2, "#6ed1e1", false, true));
                //Last lap time
                forms["driver"].addWidget(-1,    new Label(68, 55, 26, 5, "Last Lap", "Tahoma", "#ffffff"));
                forms["driver"].addWidget(0x208, new LedDigit(67, 75, 30, 18, 2, 2, "#6ed1e1", false, true));
                //Radio
                forms["driver"].addWidget(-1,    new Label(70, 85, 12, 5, "Radio", "Tahoma", "#ffffff"));
                forms["driver"].addWidget(0x70, new Led(85, 82, 4, "#e34d41", "#702723", 
                    function(value){
                        return value == 1;
                    }
                ));
                //Gas
                forms["driver"].addWidget(-1,    new Label(70, 97, 12, 5, "Gas", "Tahoma", "#ffffff"));
                forms["driver"].addWidget(0x20B, new Led(85, 94, 4, "#7fb36e", "#3f5937", 
                    function(value){
                        return value == 1;
                    }
                ));
                //Consumption
                forms["driver"].addWidget(-1,    new Label(5, 83, 27, 5, "Consumption", "Tahoma", "#ffffff"));
                forms["driver"].addWidget(0x20A, new LedDigit(5, 97, 27, 12, 3, 2, "#ebb939"));  
                //Gap
                forms["driver"].addWidget(-1,    new Label(40, 80, 20, 5, "Gap", "Tahoma", "#ffffff"));
                forms["driver"].addWidget(0x209, new LedDigit(38, 95, 20, 12, 3, 0, "#ebb939", true));
                
                //BMS form
                forms["bms"] = new Form("bmsFormArea", "bmsFormCanvas");
                //Cells voltage
                forms["bms"].addWidget(-1,      new Label(48, 9, 50, 6, "Cells Voltage", "Tahoma", "#ffffff"));
                var s = new Spectrum(48, 12, 50, 86, 0, 4.2, 12);
                for(var i = 0; i < 12; i++){
                    forms["bms"].addWidget(0x401 + i, s.getBar(i));                    
                }
                //BMS Status
                forms["bms"].addWidget(-1,      new Label(2, 14, 42, 6, "BMS Status", "Tahoma", "#ffffff"));
                forms["bms"].addWidget(0x400,   new Label(2, 33, 42, 15, "---", "Tahoma", "#ebb939"));
                //Battery voltage
                forms["bms"].addWidget(-1,      new Label(2, 44, 42, 6, "Battery V", "Tahoma", "#ffffff"));
                forms["bms"].addWidget(0x410,   new LedDigit(8, 67, 30, 18, 2, 2, "#7fb36e"));  
                //Temp1
                forms["bms"].addWidget(-1,      new Label(2, 77, 22, 6, "Temp1", "Tahoma", "#ffffff"));
                forms["bms"].addWidget(0x414,   new LedDigit(4, 95, 18, 14, 2, 1, "#e2463a"));  
                //Temp2
                forms["bms"].addWidget(-1,      new Label(24, 77, 22, 6, "Temp2", "Tahoma", "#ffffff"));
                forms["bms"].addWidget(0x415,   new LedDigit(26, 95, 18, 14, 2, 1, "#e2463a"));
                
                //Motor form
                forms["motor"] = new Form("motorCanvasArea", "motorFormCanvas");
                //Motor map
                forms["motor"].addWidget(-1,      new Label(2, 15, 45, 6, "Driver Map", "Tahoma", "#ffffff"));
                forms["motor"].addWidget(0x91,   new LedDigit(52, 20, 15, 16, 1, 0, "#7fb36e"));  
                //Chart labels         
                forms["motor"].addWidget(-1,      new Label(82, 14, 18, 6, "CUR", "Tahoma", "#ffffff"));
                forms["motor"].addWidget(-1,      new Label(82, 34, 18, 6, "VLT", "Tahoma", "#ffffff"));
                forms["motor"].addWidget(-1,      new Label(82, 54, 18, 6, "PWR", "Tahoma", "#ffffff"));
                forms["motor"].addWidget(-1,      new Label(82, 74, 18, 6, "TRQ", "Tahoma", "#ffffff"));
                forms["motor"].addWidget(-1,      new Label(82, 94, 18, 6, "SPD", "Tahoma", "#ffffff"));

            }

            function loadCharts(){
                charts = new Array();
                
                charts[0xA0] = new Chart("#chartCanvas1", 60, "#ee833a");
                charts[0xA1] = new Chart("#chartCanvas2", 60, "#6ed1e1");
                charts[0xA2] = new Chart("#chartCanvas3", 60, "#ebb939");
                charts[0xA3] = new Chart("#chartCanvas4", 60, "#7fb36e");
                charts[0x201] = new Chart("#chartCanvas5", 60, "#b742a7");
            }
            
            hideDialog();
            
            window.onload = function(){
                loadMap();
                loadForms();
                loadCharts();
                                
                resize();
                window.addEventListener("resize", function(){setTimeout(resize, 300);}, false);
                window.addEventListener("orientationchange", function(){setTimeout(resize, 300);}, false);
                
                connectToWs(onMsgReceived);

            }
           
            function onMsgReceived(msg){
                var dv = new DataView(msg.data);
                var id, value;
                
                var i = 0;
                
                /*
                console.log("received " + dv.byteLength);		
                var hexStr = ""
                for(var j=0; j < dv.byteLength; j++){
                    hexStr =  hexStr.concat(" " + dv.getUint8(j).toString(16));
                }
                console.log(hexStr);
        */
				   
                while(i < dv.byteLength){
                    id = dv.getUint16(i, true);
                    i += 2;
                    //console.log("read channel " + id);
                    if(id in channels){
                        
                        switch(channels[id].type){
                            case 'B':
                                value = parseBitFlag(dv, i, channels[id].size);
                                break;
                            case 'U':
                                value = parseUInt(dv, i, channels[id].size);
                                break;
                            case 'I':
                                value = parseSInt(dv, i, channels[id].size);
                                break;
                            case 'D':
                                value = parseDecimal(dv, i, channels[id].size);
                                break;
                            case 'S':
                                value = parseString(dv, i, channels[id].size);
                                break;
                            default:
                                console.warn(channels[id].type + " in channel " + id + " is not a valid data type");
                                break;
                        }
                        
                        i += channels[id].size;
                        //console.log("parsed channel id " +  id + " with value " + value);
                        updateData(id, channels[id].conv(value));
                    }
                    else{
                        console.error(id + " not a valid channel");
                        break;
                    }
                }     
            }
            
            function updateData(id, value){
                for(f in forms){
                    forms[f].onNewDataAvailable(id, value);
                }
                
                if(id in charts){
                    charts[id].addPoint(value);
                }
                
                //Lat
                if(id == 0x62){
                    gpsMap.setMarkerLat(value);
                }
                //Lon
                else if(id == 0x63){
                    gpsMap.setMarkerLon(value);
                }
                
            }
            

        </script>
            
        <?php
            mysqli_close($conn);
        ?>

    </body>
</html>