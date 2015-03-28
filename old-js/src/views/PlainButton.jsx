"use strict";

import React from 'react';

// generic component. accepts all vDOM props.

var PlainButton = React.createClass({
    render (){
        var plainButtonStyle = {
            WebkitAppearance: "none",
            appearance: "none",
            background: "none",
            border: "none",
            padding: 0,
            margin: 0,
            cursor: "pointer",
            outline: 0
        };

        var style = this.props.style ? 
            Object.assign(plainButtonStyle, this.props.style) :
            plainButtonStyle;

        var props =  Object.assign({},this.props, {style: style});

        return (
            <button {...props}/>
        );
    }
});

export default PlainButton;
