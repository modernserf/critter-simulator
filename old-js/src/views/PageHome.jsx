"use strict";

import React        from 'react';
import GlobalAtom   from 'mixins/GlobalAtom';
import PageBase from 'views/PageBase';
// import {Flex,FlexBox} from 'views/FlexBox';
import {colors, fonts} from 'views/style';


var PageHome = React.createClass({
    mixins: [GlobalAtom],
    propTypes: {},
    render (){
        return (
            <PageBase image="img-placeholder/huevos-rancheros-overhead.jpg">
                <div style={{
                    display: "flex",
                    flexDirection: "column",
                    justifyContent: "center",
                    height: "100%"
                }}>
                    <div>
                        <h1 style={Object.assign({
                            color: colors.gold,
                            textAlign: "left",
                            fontSize: 126,
                            textTransform: "uppercase",
                            wordSpacing: "0.2em",
                            textShadow: `${colors.red} 1px 4px`
                        },fonts.hedHeavy)}>
                            <div>How to</div>
                            <div>Draw an</div>
                            <span style={{fontSize: 200}}>egg</span></h1>
                        <h2 style={Object.assign({
                            color: colors.gold,
                            textAlign: "left",
                            fontSize: 48,
                            textShadow: `${colors.red} 1px 1px`,
                            // marginTop: -24
                        },fonts.hedLight)}>(the hard way)</h2>
                    </div>
                </div>
            </PageBase>
        );
    }
});

export default PageHome;
