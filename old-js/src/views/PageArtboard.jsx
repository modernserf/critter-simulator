"use strict";

import React from 'react';
import MoveMode from 'views/MoveMode';
import Circle from 'views/Circle';
import Bezier from 'views/Bezier';
import Drag from 'views/Drag';
import LayerList from 'views/LayerList';

import {colors} from 'views/style';

const { DragState, DragArea, Draggable } = Drag('div');

const _t = React.PropTypes;

const ModeSelector = React.createClass({
    render () {
        const { data, onChange } = this.props;
        const modes = [
            ['Move', MoveMode],
            ['Circle', Circle.add],
            ['Path',Bezier.add],
        ];

        return (
            <div>
                {modes.map(m => <button key={m[0]}
                    style={{
                        display: "block",
                        color: data === m[1] ? "red" : "black"
                    }}
                    onClick={e => onChange(m[1])}>
                    {m[0]}
                </button>)}
            </div>
        );
    }
});

const ColorPicker = React.createClass({
    render (){
        const { data, onChange } = this.props;

        const cl = ['transparent', 'black'].concat(
            Object.keys(colors).map(k => colors[k])
        );

        const getStyle =  (c, type) => ({
            width: 20,
            height: 20,
            cursor: "pointer",
            backgroundColor: c,
            borderStyle: "solid",
            borderWidth: 1,
            borderColor: data[type] === c ? "red" : "white"
        });

        const rows = cl.map(c => {
            return (
                <tr key={c}>
                    <td style={getStyle(c,'stroke')}
                        onClick={e => onChange({stroke: c})}>&nbsp;</td>
                    <td style={getStyle(c,'fill')}
                        onClick={e => onChange({fill: c})}>&nbsp;</td>
                </tr>
            );
        });

        return (
            <table style={{backgroundColor: "white"}}><tbody>
                {rows}
            </tbody></table>
        );

    }
});

const Window = React.createClass({
    render (){
        const { x, y, dragState, onDrag, children, name } = this.props;

        const container = {
            position: "fixed",
            top: 0,
            left: 0,
            transform: `translate(${x}px,${y}px)`,
            border: "1px solid #999"
        };

        const handle = { minWidth: 20, height: 20, backgroundColor: "#999"};

        return (
            <div style={container}>
                <Draggable dragState={dragState} onDrag={onDrag}
                    x={x} y={y} isHandle={true}>
                    <div style={handle} className="move-mode-draggable">
                        {name}
                    </div>
                </Draggable>
                <div>
                    {children}
                </div>
            </div>
        );
    }
});

const windowSet = [
    { x: 0, y: 0, render (){
        return (
            <ModeSelector data={this.state.mode}
                onChange={m => this.setState({mode: m})}/>
        );
    }},
    { x: 0, y: 100, render (){
        return (
            <ColorPicker data={this.state.style}
            onChange={s => this.setState(
                {style: Object.assign(this.state.style,s)})}/>
        );
    }},
    { x: 100, y: 0, name: "Artboard", render (){
        const boardWidth = 600;
        const boardHeight = 400;

        const { data, mode: Mode, style} = this.state;

        const artboard = {
            width: boardWidth,
            height: boardHeight,
            backgroundColor: "#fff",
            display: "block",
        };

        const onChange = (x, props) => {
            const item = data.find(it => it === x);
            if (item){
                Object.assign(item.props,props);
                this.forceUpdate();
            }
        };

        const onAdd = (newElement) => {
            data.push(newElement);
            this.forceUpdate();
        };

        return (
            <svg style={artboard}>
                <Mode width={boardWidth} height={boardHeight} data={data}
                    style={style}
                    onChange={onChange} onAdd={onAdd}/>
            </svg>
        );
    }},
    { x: 720, y: 0, name: "Layers", render (){
        const onRemove = (it) => {
            const { data } = this.state;

            const nextData = data.filter(x => x !== it);
            this.setState({
                data: nextData
            });
        };

        const onSelect = (it) => {
            const { data } = this.state;

            const nextData = data.map(x => {
                x.selected = x === it;

                return x;
            });
            this.setState({
                data: nextData
            });
        };

        return (
            <LayerList data={this.state.data}
                onSelect={onSelect}
                onRemove={onRemove}/>
        );
    }}
];

const Artboard = React.createClass({
    getInitialState (){
        return {
            data : [],
            mode: MoveMode,
            style: {
                stroke: 'black',
                fill: 'transparent'
            },
            dragState: DragState.create(),
            windows: windowSet,
            width: 100,
            height: 100
        };
    },
    setSize (){
        this.setState({
            width: window.innerWidth,
            height: window.innerHeight
        });
    },
    componentDidMount (){
        this.setSize();

        window.addEventListener('scroll', e => { this.setSize(); });
        window.addEventListener('resize', e => { this.setSize(); });
    },
    render () {
        const { data, mode: Mode, style, windows, dragState,
            width, height } = this.state;

        const container = {
            position: "fixed",
            backgroundColor: "#000",
            height: "100%",
            width: "100%"
        };

        const windowTags = windows.map((w,i) => {
            return (
                <Window key={i} x={w.x} y={w.y} dragState={dragState}
                    name={w.name}
                    onDrag={(x,y) => {
                        w.x = x;
                        w.y = y;
                        this.forceUpdate();
                    }}>
                    {w.render.call(this)}
                </Window>
            );
        });

        return (
            <div style={container}>
                <DragArea dragState={dragState} width={width} height={height}>
                    {windowTags}
                </DragArea>
            </div>
        );
    }
});

export default Artboard;
