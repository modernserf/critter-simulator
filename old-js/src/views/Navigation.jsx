"use strict";

import React        from 'react';
import GlobalAtom   from 'mixins/GlobalAtom';

import Link         from "views/Link";

// TODO: named z-index layers 

var rootStyle = {
    position: "fixed",
    bottom: 0,
    right: 0,
    padding: 20,
    zIndex: 1000
};

var linkBaseStyle = {
    padding: 4,
    color: "#222"
};

var linkDisabledStyle = {
    color: "#999",
    cursor: "default"
};

var linkHoverStyle = {
    color: "red"
};

var Navigation = React.createClass({
    mixins: [GlobalAtom],
    propTypes: {},
    render (){
        var routes = this.getGlobal('routes');
        var currentRoute = this.getGlobal('currentRoute');
        
        var currentIndex = routes.indexOf(currentRoute);
        var hasPrev = currentIndex > 0;
        var hasNext = currentIndex < (routes.length - 1);

        var currentHref = '#' + currentRoute.id;
        var prevHref = hasPrev ? '#' + routes[currentIndex - 1].id : null;
        var nextHref = hasNext ? '#' + routes[currentIndex + 1].id : null;

        return (
            <nav style={rootStyle}>
                <Link href={prevHref} 
                    style={linkBaseStyle}
                    hoverStyle={linkHoverStyle}
                    disabledStyle={linkDisabledStyle}>
                    Prev
                </Link>
                <Link href={nextHref}
                    style={linkBaseStyle}
                    hoverStyle={linkHoverStyle}
                    disabledStyle={linkDisabledStyle}>
                    Next
                </Link>
            </nav>
        );
    }
});

export default Navigation;
