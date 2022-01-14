//var map = L.map('map');
//
//
//
//
////var mydata = JSON.parse()
//
//var xhr = new XMLHttpRequest();
//  xhr.open('GET', 'https://maps.dwd.de/geoserver/dwd/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=dwd%3APollenfluggebiete&maxFeatures=50&outputFormat=application%2Fjson');
//  xhr.setRequestHeader('Content-Type', 'application/json');
//  xhr.onload = function() {
//    if (xhr.status === 200) {
////      var geojsonLayer = L.geoJSON(JSON.parse(xhr.responseText), {
////        onEachFeature: function(feature, layer) {
////            console.log(feature);
////            }})
////        .addTo(map);
////        geojsonLayer.bindPopup(function (layer) {
////
////                                   Android.getTestData();
////                                   return "test";
////                               });
////      map.fitBounds(geojsonLayer.getBounds());
//
//        var geojsonLayer = L.geoJSON(JSON.parse(xhr.responseText), {onEachFeature: onEachFeatureFnc});
//        geojsonLayer.bindPopup(function (layer){
//        return "test";
//        });
//        geojsonLayer.addTo(map);
//    }
//  };
//  xhr.send();
//
//L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
//    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
//}).addTo(map);
//
//function onEachFeatureFnc(feature, layer) {
//layer._leaflet_id = feature.id;
//};

var map = L.map('map');

var xhr = new XMLHttpRequest();
  xhr.open('GET', 'https://maps.dwd.de/geoserver/dwd/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=dwd%3APollenfluggebiete&maxFeatures=50&outputFormat=application%2Fjson');
  xhr.setRequestHeader('Content-Type', 'application/json');
  xhr.onload = function() {
    if (xhr.status === 200) {
      var geojsonLayer = L.geoJSON(JSON.parse(xhr.responseText));
      map.fitBounds(geojsonLayer.getBounds());
      geojsonLayer.bindPopup(function (layer){
            regionId = layer.feature.properties.GF;
            pollenData = Android.getPollenData(regionId);
        return pollenData;
      });
      geojsonLayer.addTo(map);
    }
  };
  xhr.send();

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
}).addTo(map);