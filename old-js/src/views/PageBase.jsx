"use strict";

import React from 'react';

var PageBase = React.createClass({
    propTypes: {
        image: React.PropTypes.string,
        color: React.PropTypes.string
    },
    render (){
        var {color, image, children} = this.props;

        var style = {
            position: "absolute",
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            // minHeight: "800px",
            backgroundColor: color,
            backgroundPosition: "center center",
            backgroundSize: "cover"
        };

        if (image){
            style.backgroundImage = `url(${image})`;
        }

        return (
            <section style={style}>
                {children}
            </section>
        );
    }
});

export default PageBase;
