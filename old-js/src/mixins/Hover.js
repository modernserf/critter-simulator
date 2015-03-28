"use strict";

import React from 'react';

var Hover = {
    propTypes: {
        hoverStyle : React.PropTypes.object
    },
    getInitialState (){
        return {
            __hover_mixin_isHovering: false
        };
    },
    onMouseEnter (){
        this.setState({__hover_mixin_isHovering: true});
    },
    onMouseLeave (){
        this.setState({__hover_mixin_isHovering: false});
    },
    addHoverStyle (style = this.props.style){
        return this.state.__hover_mixin_isHovering ?
            Object.assign({},(style || {}),this.props.hoverStyle) :
            style;
    }
};

export default Hover;
