var map_source_script = $('script[src*=map-source]');

var lon_source = Number(map_source_script.attr('data-lon_source'));
var lat_source = Number(map_source_script.attr('data-lat_source'));


var raster = new ol.layer.Tile({
    source: new ol.source.OSM()
});

var style = {
    'MultiLineString': new ol.style.Style({
        stroke: new ol.style.Stroke({
            color: '#ff0d00',
            width: 2
        })
    })
};

var vector = new ol.layer.Vector({
    source: new ol.source.Vector({
        url: '/source.gpx',
        format: new ol.format.GPX()
    }),
    style: function (feature) {
        return style[feature.getGeometry().getType()];
    }
});

var map = new ol.Map({
    layers: [raster, vector],
    target: document.getElementById('original'),
    view: new ol.View({
        center: ol.proj.transform([lon_source, lat_source], 'EPSG:4326', 'EPSG:3857'),
zoom: 12
})
});