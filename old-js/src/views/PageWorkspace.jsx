"use strict";

import React        from 'react';
import GlobalAtom   from 'mixins/GlobalAtom';

import PageBase from 'views/PageBase';
import {Flex,FlexBox} from 'views/FlexBox';
import {colors, fonts, addHoverStroke} from 'views/style';

import Egg from 'views/Egg';
import Tomato from 'views/Tomato';
import Tortilla from 'views/Tortilla';
import Pantry from 'views/Pantry';
import Plate from 'views/Plate';

import IngredientList from 'views/IngredientList';
import Ingredients from 'views/IngredientSVG';


var Workspace = React.createClass({
    mixins: [GlobalAtom],
    getInitialState(){
        return { w: 0, h: 0 };
    },
    componentWillMount (){
        this.setGlobal('ingredients',[
            {id: this.gensym(), component: Tortilla, x: 50, y: 50, cook: 0.5},
            {id: this.gensym(), component: Tomato, x: 60, y: 35, cook: 0},
            {id: this.gensym(), component: Egg, x: 25, y: 25, cook: 0.5}
        ]);
    },
    componentDidMount (){
        this.setWindowSize();
        window.addEventListener('resize', this.setWindowSize);
    },
    setWindowSize (){
        this.setState({
            w: window.innerWidth,
            h: window.innerHeight
        });
    },
    // These should move into the mixin
    onMove (e){
        var selectedID = this.getGlobal('selectedID');
        var hoverID = this.getGlobal('hoverID');
        if (selectedID === null){ return; }

        var { w, h} = this.state;
        var igs = this.getGlobal('ingredients');

        // "active ingredient" is last item
        var nextIgs = igs.slice(0);
        var item = nextIgs[nextIgs.length - 1];

        var size = Math.min(w,h);

        item.x = (100 * (e.clientX + (size - w)/2) /w * w/size);
        item.y = (100 * (e.clientY + (size - h)/2) /h * h/size);

        this.setGlobal('ingredients',nextIgs);
    },
    onRelease (e){
        this.setGlobal('selectedID',null);
    },
    ///
    render (){
        var ingredients = this.getGlobal('ingredients');
        var hoverID = this.getGlobal('hoverID');
        var tomato = ingredients[0];

        var {w, h} = this.state;

        return (
            <section onMouseMove={this.onMove}
                onMouseUp={this.onRelease}
                style={{
                    position: "absolute",
                    width: "100%",
                    height: "100%"
                }}>
                <svg viewBox="0 0 100 100" style={{
                    display: "block",
                    width: "100%",
                    height: "100%"
                }}>
                    <Plate/>
                    <Ingredients />
                </svg>  
            </section>
        );
    }
});

var PageWorkspace = React.createClass({
    mixins: [GlobalAtom],
    propTypes: {},
    render (){
        var pantryStyle = {
            position: "fixed",
            bottom: 0,
            width: "100%",
            backgroundColor: "white",
            padding: 10
        };

        return (
            <PageBase color={colors.green}>
                <Workspace />
                <IngredientList />
                <section style={pantryStyle}>
                    <Pantry />
                </section>
            </PageBase>
        );
    }
});

export default PageWorkspace;
