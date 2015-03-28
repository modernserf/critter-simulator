"use strict";

var webpack = require('webpack');
var config = require('./webpack.config');
var DevServer = require('webpack-dev-server');

var app = new DevServer(webpack(config),{
    contentBase: "dist/",
    hot: true
});

app.listen(4000);
