var map = L.map('map');

//var mydata = JSON.parse()

var xhr = new XMLHttpRequest();
  xhr.open('GET', 'https://maps.dwd.de/geoserver/dwd/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=dwd%3APollenfluggebiete&maxFeatures=50&outputFormat=application%2Fjson');
  xhr.setRequestHeader('Content-Type', 'application/json');
  xhr.onload = function() {
    if (xhr.status === 200) {
      var geojsonLayer = L.geoJSON(JSON.parse(xhr.responseText), {
        onEachFeature: function(feature, layer) {
            console.log(feature);
            layer.bindPopup(feature.properties.GEN);
            }})
        .addTo(map);
      map.fitBounds(geojsonLayer.getBounds());
    }
  };
  xhr.send();

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
}).addTo(map);