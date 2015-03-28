"use strict";

export var fonts = {
    hedLight: {
        fontFamily: "nimbus-sans-extended, sans-serif",
        fontWeight: 300
    },
    hedHeavy: {
        fontFamily: "nimbus-sans-extended, sans-serif",
        fontWeight: 900
    }
};

export var colors = {
    gold: "#fabb00",
    red: "#890f00",
    blue: "#1a1aaf",
    green: "#00661b",
    brown: "#402c28"
};

export var addHoverStroke = function (isHover, props){
    return isHover ? Object.assign({},props,{
        stroke: colors.blue,
        strokeWidth: 1,
        strokeOpacity: 0.5
    }) : props;
};
