"use strict";
import React from 'react';
import {colors} from 'views/style';

var Plate = React.createClass({
    render (){
        var r = 50;
        return (
            <circle cx={r} cy={r} r={r}
                fill={colors.gold}/>
        );
    }
});

export default Plate;
