"use strict";

import React        from 'react';
import GlobalAtom   from 'mixins/GlobalAtom';
import IngredientManager from 'mixins/IngredientManager';

export var IngredientSVGWrapper  = React.createClass({
    onSelect (e){
        this.props.onSelect(this.props.item.id);
    },
    onMouseEnter (e){
        e.preventDefault();
        this.props.onHover(this.props.item.id);  
    },
    onMouseLeave (e){
        e.preventDefault();
        this.props.onHover(null);
    },
    render (){
        var {item, hoverID} = this.props;
        var Component = item.component;

        return (
            <g  onMouseEnter={this.onMouseEnter}
                onMouseLeave={this.onMouseLeave}
                onMouseDown={this.onSelect}
                style={{cursor: "move"}}
                transform={`translate(${item.x},${item.y})`}>
                <Component isHover={item.id === hoverID} data={item}/>
            </g>
        );
    }
});

var Ingredients = React.createClass({
    mixins: [GlobalAtom, IngredientManager],
    render () {
        var ings = this.getGlobal('ingredients');
        var hoverID = this.getGlobal('hoverID');

        return (
            <g>
                {ings.map(item => <IngredientSVGWrapper key={item.id}
                    item={item}
                    hoverID={hoverID}
                    onSelect={this.onSelect}
                    onHover={this.onHover}/>)}
            </g>
        );
    }
});

export default Ingredients;