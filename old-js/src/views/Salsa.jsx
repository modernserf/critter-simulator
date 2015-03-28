"use strict";
import React from 'react';
import {colors, addHoverStroke} from 'views/style'; 

var salsaColors = [colors.red, colors.green, "purple"];

var Salsa = React.createClass({
    getInitialState (){
        return {
            chunks: []
        };
    },
    componentWillMount () {
        var x,y;
        for (var i = 0; i < 100; i++) {
            x = (i % 10) + Math.random() * 2;
            y = Math.floor(i / 10) + Math.random() * 2;
            this.state.chunks[i] = {
                key: i,
                stroke: "none",
                fill: salsaColors[Math.floor(Math.random() * 3)],
                width: 1 + Math.random(),
                height: 1 + Math.random(),
                x: x,
                y: y,
                opacity: Math.random() + 0.5,
                transform: `rotate(${Math.random() * 90}, ${x},${y})`
            };
        }
    },
    render (){
        var chunks = this.state.chunks.map(x => <rect {...x}/>);

        return (
            <g transform="translate(-10,-10)">
                <rect {...addHoverStroke(this.props.isHover, {
                    fill: "transparent",
                    width: 10,
                    height: 10
                })}></rect>
                {chunks}
            </g>
        );
    }
});

export default Salsa;
