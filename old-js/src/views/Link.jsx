"use strict";

import React from 'react';
import Hover from 'mixins/Hover';

var Link = React.createClass({
    mixins: [Hover],
    render (){
        var style = this.props.href ? 
            this.addHoverStyle() : 
            Object.assign({}, this.props.style, this.props.disabledStyle);

        var props = Object.assign({},this.props,{
            style: style,
            onMouseEnter: this.onMouseEnter,
            onMouseLeave: this.onMouseLeave
        });

        var Element = props.href ? "a" : "span";

        return (
            <Element {...props}/>
        );
    }
});

export default Link;
