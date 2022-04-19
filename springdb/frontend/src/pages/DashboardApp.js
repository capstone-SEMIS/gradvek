// material
import { Grid, Container } from '@mui/material';
import { Component } from 'react';
// components
import Page from '../components/Page';

import CytoCard from '../sections/@dashboard/app/CytoCard';
import AEList from '../sections/@dashboard/app/AEList';
import Searchbar from "../layouts/dashboard/Searchbar";

// ----------------------------------------------------------------------

export default class DashboardApp extends Component {
  constructor(props){
    super(props)
    this.handleResultsChange = this.handleResultsChange.bind(this);
    this.state = {
      "nodeFilter": '*', //initially, all nodes are visible
      "focusNode": {},
      "resultNodes": [],
      "target": ""
    };
    this.AEfilterCallback = this.AEfilterCallback.bind(this);
  }

  handleResultsChange(newTarget, results) {
    this.setState({target: newTarget});
    this.setState({focusNode:{}});
    this.setState({resultNodes: results});
  }

  AEfilterCallback(AE_id) {
    this.setState({
      "focusNode": {"AE": AE_id}
    });
  }

  render(){
    return (
      <Page title="Gradvek">
        <Container maxWidth="xl">
          <Searchbar onResultsChange={this.handleResultsChange}/>

          <Grid container spacing={3}>
            <Grid item xs={12} md={6} lg={8}>
              <AEList target={this.state.target} graphNodes={this.state.resultNodes} filterHandler={this.AEfilterCallback}/>
            </Grid>

            <Grid item xs={12} md={6} lg={8}>
              <CytoCard graphNodes={this.state.resultNodes} nodeFilter={this.state.nodeFilter} focusNode={this.state.focusNode}/>
            </Grid>

          </Grid>
        </Container>
      </Page>
    );
  }
}
