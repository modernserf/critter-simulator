"use strict";

import React from 'react';
import Drag from 'views/Drag';
import { polarR } from 'types/Vector';

import InactiveMode from 'views/InactiveMode';

const { DragState, DragArea, Draggable } = Drag('g');

const AddCircle = React.createClass({
    getInitialState (){
        return {
            newItemProps: {x: 0, y: 0, r: 0},
            isCreating: false,
            dragState: DragState.create()
        };
    },
    render () {
        const { data, width, height, onAdd, style } = this.props;
        const { isCreating, dragState } = this.state;
        let { newItemProps } = this.state;

        const dragNew = {
            props: {},
            onDragStart: (x,y, e) => {
                newItemProps = { x: x , y: y, r: 1,
                        style: Object.assign({},style)};

                this.setState({
                    newItemProps: newItemProps,
                    isCreating: true
                });
            },
            onDrag: (x, y, e) => {
                const {x: _x, y: _y} = newItemProps;
                newItemProps.r = polarR([_x,_y],[x,y]);
                this.setState({newItemProps: newItemProps});
            },
            onDragEnd: (x, y, e) => {
                this.setState({
                    newItemProps: {x: 0, y: 0, r: 0},
                    isCreating: false
                });
                onAdd({ element: CircleElement, props: newItemProps });
            }
        };

        const preview = isCreating && (
            <g transform={`translate(${newItemProps.x},${newItemProps.y})`}>
                <circle {...newItemProps}/>
            </g>
        );

        return (
            <g>
                <InactiveMode width={width} height={height} data={data}/>

                <DragArea dragState={dragState} dragNew={dragNew}
                    width={width} height={height}>
                    {preview}
                </DragArea>
            </g>
        );
    }
});

const CircleElement = {
    show: "circle",
    add: AddCircle
};

export default CircleElement;
