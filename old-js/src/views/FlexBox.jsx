"use strict";

import React from "react";

var _t = React.PropTypes;

export var FlexBox = React.createClass({
    propTypes: {
        direction: _t.string,
        wrap: _t.string,
        justifyContent: _t.string,
        alignItems: _t.string,
        alignContent: _t.string,
        style: _t.object
    },
    getDefaultProps (){
        return {
            direction: "row",
            wrap: "nowrap",
            justifyContent: "flex-start",
            alignItems: "stretch",
            alignContent: "stretch",
            style: {}
        };
    },
    render (){
        return (
            <div style={Object.assign({
                display: "flex",
                flexDirection: this.props.direction,
                flexWrap: this.props.wrap,
                justifyContent: this.props.justifyContent,
                alignItems: this.props.alignItems,
                alignContent: this.props.alignContent
            }, this.props.style)}>
                {this.props.children}
            </div>
        );
    }
});

export var Flex = React.createClass({
    propTypes: {
        order: _t.number,
        grow: _t.number,
        shrink: _t.number,
        basis: _t.oneOfType([_t.number, _t.string]),
        alignSelf: _t.string,
        style: _t.object
    },
    getDefaultProps (){
        return {
            order: 1,
            grow: 0,
            shrink: 1,
            basis: "auto",
            alignSelf: "auto",
            style: {}
        };
    },
    render (){
        return (
            <div style={Object.assign({
                order: this.props.order,
                flexGrow: this.props.grow,
                flexShrink: this.props.shrink,
                flexBasis: this.props.basis,
                alignSelf: this.props.alignSelf,
            },this.props.style)}>
                {this.props.children}
            </div>
        );
    }
});
