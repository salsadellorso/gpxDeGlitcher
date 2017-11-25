var map_result_script = $('script[src*=map-result]');

var lon_result = Number(map_result_script.attr('data-lon_result'));
var lat_result = Number(map_result_script.attr('data-lat_result'));

var raster = new ol.layer.Tile({
    source: new ol.source.OSM() });

var style_res = {
    'MultiLineString': new ol.style.Style({
        stroke: new ol.style.Stroke({
            color: '#0400a5',
            width: 2
        })
    })
};

var vector = new ol.layer.Vector({
    source: new ol.source.Vector({
        url: '/result.gpx',
        format: new ol.format.GPX()
    }),
    style: function (feature) {
        return style_res[feature.getGeometry().getType()];
    }
});

var map = new ol.Map({
    layers: [raster, vector],
    target: document.getElementById('fixed'),
    view: new ol.View({
        center: ol.proj.transform([lon_result, lat_result], 'EPSG:4326', 'EPSG:3857'),
          zoom: 12
            })
        });