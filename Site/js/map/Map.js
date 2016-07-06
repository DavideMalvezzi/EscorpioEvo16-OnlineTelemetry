function GpsMap(container, centerLat, centerLon, track){
    this.container = document.getElementById(container);
    this.mapOptions = {
        zoom: 16,
        disableDefaultUI: true,
        //draggable: false,
        mapTypeId: google.maps.MapTypeId.SATELLITE
    };

    var centerCoord = new google.maps.LatLng(centerLat, centerLon);
    var map = new google.maps.Map(this.container, this.mapOptions);
    map.addListener('zoom_changed', function() {
        map.panTo(centerCoord);
    });
	
	var image = new google.maps.MarkerImage('imgs/gps.png',
        null, 
        new google.maps.Point(0,0),
        new google.maps.Point(16, 16)
    );

    this.marker = new google.maps.Marker({
        map: map,  
        icon: image,
        position: centerCoord
    });
    
    
    this.track = new google.maps.Polyline({
        map: map,
        path: track,
        geodesic: true,
        strokeColor: '#ff0000',
        strokeOpacity: 1,
        strokeWeight: 5
    });
    
    

    this.setCurrentPos = function(lat, lon){
        this.marker.setPosition(new google.maps.LatLng(lat, lon));
        map.setCenter(centerCoord);
    };

    this.resize = function(){
        map.setCenter(centerCoord);
    };
    
    this.setMarkerLat = function(lat){
        this.marker.setPosition(new google.maps.LatLng(lat, this.marker.getPosition().lng()));
    };
    
    this.setMarkerLon = function(lon){
        this.marker.setPosition(new google.maps.LatLng(this.marker.getPosition().lat(), lon));
    };
}

