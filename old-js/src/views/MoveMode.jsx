"use strict";

import React from 'react';
import Drag from 'views/Drag';

const { DragState, DragArea, Draggable } = Drag('g');

const MoveMode = React.createClass({
    getInitialState (){
        return {
            dragState: DragState.create()
        };
    },
    render () {
        const { data, onChange, width, height } = this.props;
        const { dragState } = this.state;

        const items = data.map((it,index) => {
            const Element = typeof it.element === "string" ?
                it.element :
                it.element.show;

            const myProps = Object.assign({},it.props);
            myProps.x = 0;
            myProps.y = 0;

            const selectedItem = Object.assign({},myProps);
            selectedItem.style = {
                fill: "transparent",
                stroke: "rgba(0,0,255,0.5)",
                strokeWidth: 10
            };

            const selection = it.selected && (
                <g>
                    <Element {...selectedItem }/>
                </g>
            );

            return (
                <Draggable key={index} dragState={dragState}
                    x={it.props.x} y={it.props.y}
                    onDrag={(x,y) => { onChange(it,{ x, y }); }}>
                    <g className="move-mode-draggable">
                        <Element {...myProps}/>
                    </g>
                    {selection}
                </Draggable>
            );
        });

        return (
            <DragArea dragState={dragState} width={width} height={height}>
                {items}
            </DragArea>
        );
    }
});

export default MoveMode;
