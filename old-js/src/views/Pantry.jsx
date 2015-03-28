"use strict";
import React from 'react';
import GlobalAtom from 'mixins/GlobalAtom';
import {Flex, FlexBox} from 'views/FlexBox';

import Egg from 'views/Egg';
import Tomato from 'views/Tomato';
import Tortilla from 'views/Tortilla';
import Cheese from 'views/Cheese';
import Salsa from 'views/Salsa';

import {colors} from 'views/style'; 


var PantryItems = [Egg, Tomato, Tortilla, Salsa, Cheese];

export var PantryButton = React.createClass({
    onClick (e){
        this.props.onClick(this.props.item);
    },
    render (){
        var Component = this.props.item;

        return (
            <button onClick={this.onClick} style={{
                backgroundColor: colors.green,
                color: colors.gold
            }}>
                <svg viewBox="0 0 50 50" style={{
                    width: 100,
                    height: 100,
                    display: "block"
                }}>
                    <g transform={"translate(25,25)"}><Component /></g>
                </svg>
                {Component.displayName}
            </button>
        ); 
    }
});

export var Pantry = React.createClass({
    mixins: [GlobalAtom],
    onClick (component){
        var ings = this.getGlobal('ingredients');

        var next = {
            id: this.gensym(), 
            component: component,
            x: 25 + Math.random() * 50,
            y: 25 + Math.random() * 50,
            cook: 0.5
        };

        this.setGlobal('ingredients', ings.concat([next]));
    },
    render (){
        var items = PantryItems.map((p,i) => <Flex key={i}>
             <PantryButton item={p} onClick={this.onClick}/>
        </Flex>);

        return (
            <FlexBox>
                {items}
            </FlexBox>
        );
    }
});

export default Pantry;