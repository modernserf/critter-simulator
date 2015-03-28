"use strict";

import React from 'react';

const InactiveMode = React.createClass({
    render () {
        const { data } = this.props;

        const items = data.map((it,index) => {
            const Element = typeof it.element === "string" ?
                it.element :
                it.element.show;

            const { x, y } = it.props;

            const selectedItem = Object.assign({}, it.props, { style: {
                fill: "transparent",
                stroke: "rgba(0,0,255,0.5)",
                strokeWidth: 10
            }});

            const selection =  it.selected && (
                <Element {...selectedItem }/>
            );

            return (
                <g key={index} transform={`translate(${x||0},${y||0})`}>
                    <Element {...it.props}/>
                    {selection}
                </g>

            );
        });

        return (
            <g>
                {items}
            </g>
        );
    }
});

export default InactiveMode;
