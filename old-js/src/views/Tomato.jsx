"use strict";
import React from 'react';
import {colors, addHoverStroke} from 'views/style'; 

var Tomato = React.createClass({
    render (){
        var r = 10;
        return (
            <g>
                <circle {...addHoverStroke(this.props.isHover,{
                    r: r, fill: colors.red
                })}/>
            </g>
        );
    }
});

export default Tomato;
