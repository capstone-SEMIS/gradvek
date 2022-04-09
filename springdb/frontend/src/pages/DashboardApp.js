// material
import { Grid, Container } from '@mui/material';
import { Component } from 'react';
// components
import Page from '../components/Page';

import CytoCard from '../sections/@dashboard/app/CytoCard';
import AEList from '../sections/@dashboard/app/AEList';
import dummyNodes from '../utils/dummyNodes';
import AEResults from "../sections/@dashboard/app/AEResults";
import Searchbar from "../layouts/dashboard/Searchbar";

// ----------------------------------------------------------------------

export default class DashboardApp extends Component {
  constructor(props){
    super(props)
    this.handleResultsChange = this.handleResultsChange.bind(this);
    this.state = {
      "nodeFilter": '*', //initially, all nodes are visible
      "focusNode": {},
      "adverseEvents": [],
      "resultNodes": []
    };
    this.AEfilterCallback = this.AEfilterCallback.bind(this);
  }

  handleResultsChange(results) {
    // this.parseJsonToCytoscapeNodes(results);
    // this.setState({adverseEvents: results});
    this.setState({resultNodes: results})
  }

  // parseJsonToCytoscapeNodes(results) {
  //   const obj = JSON.parse(results);
  // }
  
  AEfilterCallback(AE_id) {
    this.setState({
      "focusNode": {"AE": AE_id}
    });
  }

  renderAETable() {
    if (this.state.adverseEvents.length > 0) {
      return (
          <Grid item xs={12} md={6} lg={8}>
            <AEResults results={this.state.adverseEvents} onResultsChange={this.handleResultsChange}/>
          </Grid>
          )
    }
  }

  render(){
    return (
      <Page title="Gradvek">
        <Container maxWidth="xl">
          <Searchbar onResultsChange={this.handleResultsChange}/>

          <Grid container spacing={3}>
            {this.renderAETable()}
            <Grid item xs={12} md={6} lg={8}>
              <AEList graphNodes={this.state.resultNodes} filterHandler={this.AEfilterCallback}/>
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
