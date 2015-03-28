"use strict";

export const add = (a, b) => [a[0] + b[0], a[1] + b[1]];
export const sub = (a, b) => [a[0] - b[0], a[1] - b[1]];

export const polarR = (a, b) => {
    const diff = sub(a, b);
    return Math.sqrt((diff[0] * diff[0]) + (diff[1] * diff[1]));
};

export const offset = (pair, x) => add(pair, [-x,x]);

