// material
import {Grid} from '@mui/material';
import {Component} from 'react';
// components
import Page from '../components/Page';

import CytoCard from '../sections/@dashboard/app/CytoCard';
import AEList from '../sections/@dashboard/app/AEList';
import Searchbar from "../layouts/dashboard/Searchbar";

// ----------------------------------------------------------------------

export default class DashboardApp extends Component {
    constructor(props) {
        super(props)
        this.state = {
            "nodeFilter": '*', //initially, all nodes are visible
            "focusNode": {},
            "resultNodes": [],
            "target": "",
            "tableResults": []
        };
        this.refreshResults = this.refreshResults.bind(this);
        this.refreshViz = this.refreshViz.bind(this);
    }

    refreshViz(target, ae = null) {
        this.setState({focusNode: {}});

        const urlStart = `/api/ae/path/${target}`;
        const url = ae === null ? urlStart : `${urlStart}/${ae}`;

        fetch(url).then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error(response.statusText);
            }
        }).then(body => {
            this.setState({resultNodes: body})
        }).catch(error => {
            console.error(`${error.name}: ${error.message}`);
        });
    }

    refreshResults(target) {
        this.setState({target: target});

        fetch(`/api/weight/${target}`).then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error(response.statusText);
            }
        }).then(body => {
            this.setState({tableResults: body});
        }).catch(error => {
            console.error(`${error.name}: ${error.message}`);
        });

        this.refreshViz(target);
    }


    render() {
        return (
            <Page title="Gradvek">
                <Grid container spacing={3}>
                    <Grid item xs={12} md={6}>
                        <Searchbar onResultsChange={this.refreshResults}/>
                        <AEList target={this.state.target} tableResults={this.state.tableResults}
                                filterHandler={this.refreshViz}/>
                    </Grid>
                    <Grid item xs={12} md={6} position='sticky' top={0} alignSelf='flex-start'>
                        <CytoCard graphNodes={this.state.resultNodes} nodeFilter={this.state.nodeFilter}
                                  focusNode={this.state.focusNode}/>
                    </Grid>
                </Grid>
            </Page>
        );
    }
}
