"use strict";

import React from "react";
import rebound from "rebound";

const springSystem = new rebound.SpringSystem();

const SpringGroup = React.createClass({
    getDefaultProps (){
        return {
            tension: 40,
            friction: 7
        };
    },
    getInitialState (){
        const { tension, friction } = this.props;
        const spring = springSystem.createSpring(tension, friction);

        spring.addListener({
            onSpringUpdate: (it) => {
                this.setState({
                    value: it.getCurrentValue()
                });
            }
        });

        return {
            spring: spring,
            value: 0,
        };
    },
    componentWillReceiveProps (nextProps) {
        if (nextProps.tension !== this.props.tension ||
            nextProps.friction !== this.props.friction) {
            this.setSpringConfig(nextProps.tension, nextProps.friction);
        }
    },
    setSpringConfig (tension, friction) {
        const { spring } = this.state;
        spring.setSpringConfig(
            rebound.SpringConfig.fromOrigamiTensionAndFriction(tension, friction));
    },
    render (){
        const { getStyle, children, endValue, renderChildren, onClick, checked} = this.props; 
        const { spring, value, careAboutMouseLeave } = this.state;

        const content = renderChildren ? renderChildren(value) : children;

        return (
            <div aria-role="button" 
                onMouseDown={e => {
                    spring.setEndValue(checked ? 0 : 1);
                }}
                onClick={e => {
                    if (checked === undefined){
                        spring.setEndValue(0);                        
                    }
                    if (onClick){ onClick(e); }
                }}
                onMouseLeave={e => {
                    spring.setEndValue(checked ? 1 : 0);
                }}
                style={getStyle ? getStyle(value) : {}}>
                {content}
            </div>
        );
    }
});

const polarToCartesian = (r,t) => {
    return [ 
        r * Math.sin(t), 
        -r * Math.cos(-t)
    ];
};

const t7 = Math.PI * 2 / 7;

const PetalMenu = React.createClass({
    render () {
        const { active, onClick } = this.props;

        const renderFn = (s) => {
            // s = 1;
            const petals = [0,1,2,3].map((i) => {
                const r = s * 65;
                const t = i * t7 * s * s;

                const [x,y] = polarToCartesian(r,t);

                return (
                    <g key={i} transform={`translate(${x},${y})`}>
                        <circle r={20} fill="green"/>
                        <text fill="white" y={5} textAnchor="middle">{i}</text>
                    </g>
                );
            });

            return (
                <svg width={300} height={300} style={{filter:'url(#goo)'}}>
                    <g transform="translate(150,150)">
                        {petals}
                        <circle r={50 - s * 10} fill="green"/>
                    </g>
                </svg>
            );
        };

        return (
            <SpringGroup onClick={onClick} checked={active} renderChildren={renderFn}/>
        );
    }
});

const PageSprings = React.createClass({
    getInitialState () {
        return {
            hamburgerActive: false,
            petalActive: false,
            springActive: false
        };
    },
    render (){
        const { hamburgerActive, petalActive, springActive } = this.state;

        const getStyle = (s) => {
            const scale = 1 - (s /2); 
            return {
                width: 200,
                height: 200,
                transform: `scale(${scale})`,
            };
        };

        const hamburger = (s) => {
            const base = {
                width: 100,
                height: 20,
                fill: "blue",
                rx: 4,
                ry: 4
            };
            const a = `rotate(${s * 45},-5,20)`;
            const b = `translate(${s * 50}, 0)`;
            const c = `rotate(${-s *  45}, 0, 80)`;
            return (
                <svg style={{height: 300, width: 300, cursor: "pointer"}}>
                    <g transform="translate(100,100)">
                        <rect {...base} transform={a}/>
                        <rect {...base} y={40} width={Math.max(0,100 - s * 100)} transform={b}/>
                        <rect {...base} y={80} transform={c}/>
                    </g>
                </svg>
            );
        };

        const nodeBase = Array(10).join(',').split(',');

        const string2 = (s) => {

            const color = s > 0.5 ? "orange" : "purple";

            const style = {
                stroke: color,
                strokeWidth: 4,
                fill: "none",
                transition: "stroke 0.5s"
            };

            const boxStyle = {
                fill: color,
                transition: "fill 0.5s"
            };

            const f = 50;
            const l = s * 200 + 100;
            const nodes = nodeBase.map((z,i) => {
                const y = i % 2 ? 100 - f : 100 + f;
                const x2 = (i + 1) * l/nodeBase.length;
                const x1 = x2 - l/(nodeBase.length * 2);
                return `Q${x1} ${y} ${x2} 100`; 
            }).join(' ');


            const d =  "M0 100 " + nodes;

            return (
                <svg style={{width: 600, height: 200}}>
                    <g transform={`translate(${300 - l/2},0)`}>
                        <rect style={boxStyle} x={-50} y={75} width={50} height={50}/>
                        <path d={d} style={style}/>
                        <rect style={boxStyle} x={l} y={75} width={50} height={50}/>
                    </g>
                </svg>
            );
        };



        return (
            <div>
                <SpringGroup 
                    tension={20}
                    friction={4}
                    getStyle={getStyle}>
                    <div aria-role="button" 
                        style={{
                        width: 100, 
                        height: 100, 
                        top: 50, 
                        left: 50, 
                        cursor: "pointer",
                        backgroundColor: "red",
                        position: "relative"
                    }}/>
                </SpringGroup>

                <SpringGroup checked={hamburgerActive}
                    tension={80}
                    friction={6}                   
                    onClick={e => this.setState({hamburgerActive: !hamburgerActive})}
                    renderChildren={hamburger}/>

                <PetalMenu active={petalActive}
                    onClick={e => this.setState({petalActive: !petalActive})}/>

                <SpringGroup tension={70} friction={1}
                    checked={springActive} 
                    onClick={e => this.setState({springActive: !springActive})}
                    renderChildren={string2}/>
            </div>
        );


    }
});

export default PageSprings;
