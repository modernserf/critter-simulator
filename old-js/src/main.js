"use strict";

import 'babel/polyfill';
import 'css/main.sass';
import React from 'react';

import Main from 'views/Main';

Main = React.createFactory(Main);

const Point = {
    add (a, b) {
        return {
            x: a.x + b.x,
            y: a.y + b.y
        };
    },
    sub (a, b) {
        return {
            x: a.x - b.x,
            y: a.y - b.y
        };
    },
    div (a, d) {
        return {
            x: a.x / d,
            y: a.y / d
        };
    },
    clamp (a, w) {
        return {
            x: Math.min(Math.max(0,a.x),w.width),
            y: Math.min(Math.max(0,a.y),w.height),
        };
    }
};


class State {
    constructor (params) {
        this.current = params.current;
        this.next = params.next;
        this.listeners = params.listeners;
    }
    static create (init) {
        return new State({
            current: new Map(init),
            next: new Map(init),
            listeners: []
        });
    }
    [Symbol.iterator] () {
        return this.current[Symbol.iterator]();
    }
    get (key) {
        return this.current.get(key);
    }
    set (key, value) {
        this.next.set(key, value);
        return this;
    }
    step () {
        this.current = this.next;
        this.next = new Map(this.next);
        this._publish();
    }
    onChange (fn){
        this.listeners.push(fn);
    }
    _publish () {
        window.setTimeout(() => {
            for (let fn of this.listeners) {
                fn(this);
            }
        },1);
    }
}

class Critter {
    constructor (params) {
        this.x = params.x;
        this.y = params.y;
        this.world = params.world;
    }
    static create (world) {
        return new Critter({
            x: Math.random() * world.get('width'),
            y: Math.random() * world.get('height'),
            world: world
        });
    }
    getNeighborAverage () {
        let coords = {x: 0, y: 0};
        let count = 0;
        // sum coords
        for (let [key, value] of this.world) {
            if (value.isCritter) {
                coords = Point.add(coords,Point.sub(value,this));
                count += 1;
            }
        }        
        if (count) {
            // mean coords for center
            coords = Point.div(coords,count);
            return coords;
        } else {
            return this;
        }
    }
    avoidCollisions () {
        let coords = {x: 0, y: 0};
        let space = 30;

        for (let [key, value] of this.world) {
            if (value.isCritter && value !== this &&
                Math.abs(this.x - value.x) < space &&
                Math.abs(this.y - value.y) < space) {
                coords = Point.sub(coords,Point.sub(value, this));
            }
        }        
        return coords;
    }
    goToOffsetWithVelocity (offset, velocity) {
        const pOffset = Point.div(offset,10);
        const nextCoords = Point.add(this,pOffset);



        Object.assign(this, Point.clamp(nextCoords,{
            width: this.world.get('width'),
            height: this.world.get('height')
        }));
        return this;
    }
    step () {
        let next = new Critter(this);
        // let nextCoords = Point.add(this,);

        next.goToOffsetWithVelocity(
            // this.getNeighborAverage(),
            Point.add(this.getNeighborAverage(),this.avoidCollisions()),
            10);
        return next;
    }
}

Critter.prototype.isCritter = true;

let state = State.create([
    ['width', 500],
    ['height',200]
]);

state.set('critters/allegra', Critter.create(state));
state.set('critters/slipper', Critter.create(state));
state.set('critters/squeaky', Critter.create(state));
state.set('critters/sarah-jane', Critter.create(state));

state.step();

const run = () => {
    window.setTimeout(() => {
        for (let [key, value] of state) {
            if (value.step) {
                state.set(key, value.step());
            }
        }
        state.step();
        run();
    },100);
};

const render = () => {
    React.render(Main({data: state}),document.getElementById('main'));
};

document.addEventListener('DOMContentLoaded', render);
state.onChange(render);

run();