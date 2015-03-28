"use strict";

import React from 'react';

const _t = React.PropTypes;

const E = "g";

const ClickArea = React.createClass({
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
        const { children, onClick, width, height } = this.props;
        let { x, y } = this.state;

        return (
            <E  onClick={e => onClick(e.clientX - x, e.clientY - y, e)}>
                <rect width={width} height={height} fill="transparent" />
                {children}
            </E>
        );
    }
});

export default ClickArea;
