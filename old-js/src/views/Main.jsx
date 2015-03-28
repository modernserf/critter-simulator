"use strict";

import React from 'react';

const xy = (x,y) => `translate(${x}px,${y}px)`;

class Critter extends React.Component {
    render () {
        const { x, y } = this.props;

        const style = {
            transform: xy(x,y),
            transition: "transform 0.5s"
        };

        return (
            <g style={style}>
                <circle fill='brown' r={10}/>
            </g>
        );
    }
}

class World extends React.Component {
    render () {
        const { data } = this.props;

        const critters = [];
        for (let [key, value] of data) {
            if (value.isCritter) {
                critters.push(<Critter key={key} {...value}/>);
            }
        }

        return (
            <g>                
                {critters}
            </g>
        );
    }
}

class Main extends React.Component {
    render () {
        const { data } = this.props;

        const style = { width: data.get('width'), height: data.get('height') };

        return (
            <div>
                <svg style={style}>
                    <rect width={style.width} height={style.height} fill="gray"/>
                    <World data={data}/>
                </svg>
            </div>
        );
    }
}

export default Main;
