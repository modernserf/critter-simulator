"use strict";

import React from 'react';
import Drag from 'views/Drag';
import InactiveMode from 'views/InactiveMode';
import ClickArea from 'views/ClickArea';

import { add, sub, offset } from 'types/Vector';

const { DragState, DragArea, Draggable } = Drag('g');

const flatcat = (arr) => arr instanceof Array ?
    arr.map(flatcat).join(' ') :
    String(arr);

const DottedLine = React.createClass({
    render () {
        const { s, e } = this.props;

        return <line x1={s[0]} y1={s[1]} x2={e[0]} y2={e[1]}
            strokeDasharray="5,5" stroke="gray"/>;
    }
});

const AddBezier = React.createClass({
    getInitialState (){
        return {
            pathData: [],
            isCreating: false,
            dragState: DragState.create()
        };
    },
    parsePathData (data){
        if (!data.length){ return ""; }

        const last = data[data.length - 1];

        const start = ['M', last[0]];

        let offset = [];

        // rearrange path data from logical to svg order
        for (let i = 0; i < data.length; i++) {
            let lIndex = i ? i - 1 : data.length - 1;
            offset.push(['C',data[lIndex][2],data[i][1],data[i][0]]);
        }

        const d = flatcat([start].concat(offset)) + " Z";
        return d;
    },
    addPoint (x,y,e) {
        const point = [x,y];

        const cmd = [point, offset(point,-20), offset(point,20)];
        const pathData = this.state.pathData.concat([cmd]);

        this.setState({
            isCreating: true,
            pathData: pathData
        });
    },
    deletePoint (cmd) {
        const { pathData } = this.state;
        this.setState({
            pathData: pathData.filter(c => c !== cmd)
        });
    },
    dragPoint (params) {
        const { isAnchor, id, pos, x, y } = params;
        const { pathData } = this.state;

        // if anchor point move all points together
        if (isAnchor) {
            const diff = sub([x,y],pathData[id][pos]);
            pathData[id] = pathData[id].map(p => add(p, diff));
        // if control point move just control point
        } else {
            pathData[id][pos] = [x, y];
        }

        this.forceUpdate();
    },
    render (){
        const { data, width, height, onAdd, style } = this.props;
        const { isCreating, dragState, pathData } = this.state;

        const path = this.parsePathData(pathData);

        const preview = isCreating && (
            <path d={path} style={style}/>
        );

        const commands = pathData.map((cmd,id) => {

            const points = cmd.map((p,pos) => {
                const [x,y] = p;
                const isAnchor = pos === 0;

                return (
                    <Draggable key={pos} dragState={dragState}
                        x={x} y={y}
                        onDragStart={(_x,_y,e)=> {
                            if (e.shiftKey){
                                this.deletePoint(cmd);
                            }
                        }}
                        onDrag={(_x,_y,e)=> this.dragPoint({
                            isAnchor: isAnchor,
                            id: id,
                            pos: pos,
                            x: _x,
                            y: _y,
                            event: e
                        })}>
                        <circle r={5} fill={isAnchor ? "red" : "blue"}/>
                    </Draggable>
                );
            });

            const lines = cmd.length > 1 ?
                <g>
                    <DottedLine s={cmd[0]} e={cmd[1]}/>
                    <DottedLine s={cmd[0]} e={cmd[2]}/>
                </g> :
                null;

            return (
                <g key={id}>
                    <g>{lines}</g>
                    <g>{points}</g>
                </g>
            );
        });

        const commit = (e) => {
            onAdd({ element: Bezier, props: {
                d: path,
                style: Object.assign({},style)
            }});
            this.setState({
                isCreating: false,
                pathData: []
            });
        };

        return (
            <g>
                <InactiveMode width={width} height={height} data={data}/>
                {preview}
                <DragArea dragState={dragState}
                    width={width} height={height}>
                    <ClickArea width={width} height={height}
                        onClick={(x,y,e) => this.addPoint(x,y,e)}/>
                    {commands}
                </DragArea>
                <text y={height} onClick={commit}>Commit</text>
            </g>
        );
    }
});

const Bezier = {
    show: "path",
    add: AddBezier
};

export default Bezier;
