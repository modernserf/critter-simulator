"use strict";

import React from 'react';
import {colors} from 'views/style';

const LayerList = React.createClass({
    render () {
        const { data, onSelect, onRemove } = this.props;

        const style = {
            backgroundColor: "white",
            width: 100
        };

        const remove = {
            display: "inline-block",
            color: "#ccc",
            cursor: "pointer",
            paddingLeft: 10
        };

        const items = data.map((it,index) => {
            const li = it.selected ? {
                color: "white",
                backgroundColor: colors.blue
            } : {};

            return (
                <li key={index}
                    style={li}
                    onMouseEnter={e => onSelect(it)}
                    onMouseLeave={e => onSelect(null)}>
                    {it.element.show}
                    <span aria-role="button"
                        style={remove}
                        onClick={e => onRemove(it)}>
                        &times;
                    </span>
                </li>
            );
        });

        return (
            <ul style={style}>
                {items}
            </ul>
        );
    }
});

export default LayerList;
