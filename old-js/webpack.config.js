"use strict";

var webpack =   require('webpack'),
    path =      require('path');

var sassPath  = "includePaths[]=" + (path.resolve(__dirname, "./src"));

module.exports = {
    entry: {
        main: [
            'webpack/hot/dev-server',
            "./src/main.js"
        ]
    },
    output: {
        filename: "js/[name].js",
        path: process.cwd() + "/dist",
        publicPath: "/"
    },
    module: {
        loaders: [
            {test: /\.js$/, exclude: /node_modules/, loader: 'babel'},
            {test: /\.jsx$/, loaders: ["react-hot", "babel"] },
            {test: /\.sass$/, loaders: ["style","css","autoprefixer",
                "sass?indentedSyntax&" + sassPath]},
        ]
    },
    plugins: [
        new webpack.HotModuleReplacementPlugin()
    ],
    resolve: {
        extensions: ['', '.js','.json','.jsx'],
        modulesDirectories: ['node_modules','src']
    }
};
