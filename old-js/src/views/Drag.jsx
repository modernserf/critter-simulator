"use strict";

import React from 'react';
import EventStream from 'types/EventStream';

const _t = React.PropTypes;

// private state to handle drag & drop
class DragState {
    constructor (it) {
        this.draggedItem = it.draggedItem;
        this.stream = it.stream;
    }

    onDragStart (item, e) {
        if (!item){ return; }

        this.draggedItem = item;
        item._x = item.x - e.clientX;
        item._y = item.y - e.clientY;
        if (this.draggedItem.onDragStart){
            this.draggedItem.onDragStart(item.x,item.y,e);
        }

        this.update();
    }

    onDrag (e) {

        if (this.draggedItem && this.draggedItem.onDrag) {
            const it = this.draggedItem;
            this.draggedItem.onDrag(
                e.clientX + it._x,
                e.clientY + it._y,
                e);
        }

        this.update();
    }

    onDragEnd (x, y, e) {
        if (this.draggedItem && this.draggedItem.onDragEnd) {
            this.draggedItem.onDragEnd();
        } else if (this.draggedItem && this.draggedItem.onClick) {
            this.draggedItem.onClick(x, y, e);
        }

        this.draggedItem = null;

        this.update();
    }


    onUpdate (callback) {
        this.stream.listen(callback);
    }
    update () {
        this.stream.put(this);
    }

}

DragState.create = () => {
    return new DragState({
        draggedItem: null,
        stream: new EventStream()
    });
};

// element defined at runtime to allow use in svg or html
export default function (E) {

    // TODO: handle x/y offset
    const DragArea = React.createClass({
        getInitialState () {
            return {
                x: 0,
                y: 0,
            };
        },
        getOffset () {
            const el = this.getDOMNode();
            const rect = el.getBoundingClientRect();
            this.setState({
                x: rect.left,
                y: rect.top,
            });
        },
        componentDidMount () {
            this.getOffset();
            window.addEventListener('scroll', e => { this.getOffset(); });
            window.addEventListener('resize', e => { this.getOffset(); });
        },
        render () {
            const { children, dragState, width, height, dragNew } = this.props;
            let { x, y } = this.state;

            const dragNewStart = dragNew && (e => {
                dragState.onDragStart(
                    Object.assign({
                        x : e.clientX - x, y: e.clientY - y
                    }, dragNew),
                    e
                );
            });

            const filler = E === "g" && (
                <rect width={width} height={height} fill="transparent" />
            );

            const style = E !== "g" && {
                width: width,
                height: height,
                position: "absolute"
            };


            return (
                <E  style={style || {}}
                    onMouseDown={dragNewStart}
                    onMouseMove={e => dragState.onDrag(e)}
                    onMouseUp={e => {
                        dragState.onDragEnd(e.clientX - x, e.clientY - y, e);
                        this.forceUpdate();
                    }}>
                    {filler}
                    {children}
                </E>
            );
        }
    });

    const Draggable = React.createClass({
        propTypes: {
            x: _t.number,
            y: _t.number,
            onDrag: _t.func.isRequired,
            onDragStart: _t.func,
            onDragEnd: _t.func,
            dragState: _t.instanceOf(DragState).isRequired
        },
        getDefaultProps () {
            return {
                x: 0,
                y: 0
            };
        },
        getDefaultActions () {
            return {
                onDragStart: (x,y,e) => {
                    this.setState({isDragging: true});
                },
                onDragEnd: (x,y,e) => {
                    this.setState({isDragging: false});
                }
            };
        },
        getInitialState () {
            return {
                isDragging: false
            };
        },
        render (){
            const { x, y, children, dragState, isHandle } = this.props;

            const className = this.state.isDragging ? "is-dragging" : "";

            const transform = isHandle ? "" : `translate(${x},${y})`;

            const props = Object.assign(this.getDefaultActions(), this.props);

            return (
                <E  transform={transform}
                    onMouseDown={e => dragState.onDragStart(props, e)}
                    className={className}>
                    {children}
                </E>
            );

        }
    });

    return {
        DragState: DragState,
        DragArea: DragArea,
        Draggable: Draggable
    };

}
