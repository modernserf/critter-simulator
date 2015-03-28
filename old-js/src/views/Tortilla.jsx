"use strict";
import React from 'react';
import {colors, addHoverStroke} from 'views/style';

// React doesn't play nice with esoteric SVG components
var ClipPath = "clipPath";

var Tortilla = React.createClass({
    getInitialState () {
        return {
            spots: []
        };
    },
    componentWillMount (){
        for (var i = 0; i < 100; i++) {
            this.state.spots[i] = [Math.random(), Math.random() * 2 - 1,Math.random() * 2 -1];
        }
    },
    render () {
        var {data, isHover} = this.props;
        var density = data && data.cook && (data.cook * 10);

        var maskID = `mask_${data && data.id}`;

        var r = 25;

        var spots = this.state.spots.filter((x,i) => i < (density * 100))
            .map((x,i) => <circle key={i}
                    fill={colors.brown} r={x[0]}
                    cx={x[1] * r} cy={x[2] * r}/>);



        return (
            <g>
                <defs>
                    <ClipPath id={maskID}>
                        <circle r={r}/>
                    </ClipPath>
                </defs>
                <circle {...addHoverStroke(this.props.isHover,{
                    r: r, fill: "beige"
                })}/>
                <g style={{clipPath: `url(#${maskID})`}}>
                    {spots}
                </g>
            </g>
        );
    }
});

export default Tortilla;
