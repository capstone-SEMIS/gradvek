// material
import {Grid} from '@mui/material';
import {Component} from 'react';
// components
import Page from '../components/Page';

import CytoCard from '../sections/@dashboard/app/CytoCard';
import AEList from '../sections/@dashboard/app/AEList';
import SearchControl from "../sections/@dashboard/app/SearchControl";
import CytoLegendCard from "../sections/@dashboard/app/CytoLegendCard";

// ----------------------------------------------------------------------

export default class DashboardApp extends Component {
    constructor(props) {
        super(props)
        this.state = {
            "nodeFilter": '*', //initially, all nodes are visible
            "focusNode": {},
            "resultNodes": [],
            "target": "",
            "tableResults": [],
            "availableActions": [],
            "selectedActions": []
        };
        this.refreshResults = this.refreshResults.bind(this);
        this.refreshViz = this.refreshViz.bind(this);
        this.refreshActions = this.refreshActions.bind(this);
    }

    componentDidMount() {
        this.refreshActions();
    }

    refreshViz(target, actions = null, ae = null, drug = null) {
        this.setState({focusNode: {}});

        let url = `/api/ae/path/${encodeURIComponent(target)}`;
        if (ae !== null) {
            url = `${url}/${encodeURIComponent(ae)}`
        }
        if (drug !== null) {
            url = `${url}/${encodeURIComponent(drug)}`
        }
        if (actions && actions.length) {
            url = `${url}?actions=${actions.map(a => encodeURIComponent(a)).join(',')}`;
        }

        fetch(url).then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error(response.statusText);
            }
        }).then(body => {
            body.forEach(o => {
                if (o.hasOwnProperty("data") && o["data"].hasOwnProperty("llr")) {
                    o["data"]["llr"] = Number(o["data"]["llr"]).toFixed(2);
                }
            })
            this.setState({resultNodes: body})
        }).catch(error => {
            console.error(`${error.name}: ${error.message}`);
        });
    }

    refreshResults(target, actions) {
        this.setState({target: target});
        this.setState({selectedActions: actions});

        let url = `/api/weight/${encodeURIComponent(target)}`
        if (actions && actions.length) {
            url = `${url}?actions=${actions.map(a => encodeURIComponent(a)).join(',')}`;
        }

        fetch(url).then(response => {
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

        this.refreshViz(target, actions);
        this.refreshActions(target);
    }

    refreshActions(target = null) {
        const urlStart = '/api/actions';
        const url = target === null ? urlStart : `${urlStart}/${encodeURIComponent(target)}`;

        fetch(url).then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error(response.statusText);
            }
        }).then(body => {
            this.setState({availableActions: body});
        }).catch(error => {
            console.error(`${error.name}: ${error.message}`);
        })
    }

    render() {
        return (
            <Page title="Gradvek">
                <Grid container spacing={3}>
                    <Grid item xs={12} md={4}>
                        <SearchControl onResultsChange={this.refreshResults} actions={this.state.availableActions}/>
                        <AEList target={this.state.target} actions={this.state.selectedActions}
                                tableResults={this.state.tableResults} filterHandler={this.refreshViz}/>
                    </Grid>
                    <Grid item xs={12} md={8} position='sticky' top={0} alignSelf='flex-start'>
                        <CytoLegendCard graphNodes={[
                            {'id': 1, 'group': 'nodes', 'data': {'name': 'Pathway'}, 'classes': 'pathway'},
                            {'id': 2, 'group': 'nodes', 'data': {'name': 'Drug'}, 'classes': 'drug'},
                            {'id': 3, 'group': 'nodes', 'data': {'name': 'Target'}, 'classes': 'target'},
                            {'id': 4, 'group': 'nodes', 'data': {'name': 'Adverse Event'}, 'classes': 'adverse-event'},
                        ]} title="Legend" id='cyto_legend'/>
                        <CytoCard graphNodes={this.state.resultNodes} nodeFilter={this.state.nodeFilter}
                                  focusNode={this.state.focusNode} title="" id='cyto_canvas'/>
                    </Grid>
                </Grid>
            </Page>
        );
    }
}
