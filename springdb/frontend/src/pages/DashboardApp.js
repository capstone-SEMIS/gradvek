// material
import { Box, Grid, Container, Typography } from '@mui/material';
import { Component } from 'react';
// components
import Page from '../components/Page';
import {
  AppTasks,
  AppNewUsers,
  AppBugReports,
  AppItemOrders,
  AppWeeklySales,
  AppOrderTimeline,
  AppCurrentVisits,
  AppWebsiteVisits,
  AppCurrentSubject,
  AppConversionRates
} from '../sections/@dashboard/app';

import CytoCard from '../sections/@dashboard/app/CytoCard';
import AEList from '../sections/@dashboard/products/AEList';
import dummyNodes from '../utils/dummyNodes';

// ----------------------------------------------------------------------

export default class DashboardApp extends Component {
 

  constructor(props){
    super(props)
    this.state = {
      "nodeFilter": '*' //initially, all nodes are visible
    };
    
    this.AEfilterCallback = this.AEfilterCallback.bind(this);
  }
  
  AEfilterCallback(AE_id) {
    let filterString = `node[id="${AE_id}"]`;
    this.setState({
      "nodeFilter": filterString
    });
  }

  render(){
    return (
      <Page title="Dashboard | Minimal-UI">
        <Container maxWidth="xl">
          <Box sx={{ pb: 5 }}>
            <Typography variant="h4">Hi, Welcome back</Typography>
          </Box>
          <Grid container spacing={3}>
            <Grid item xs={12} sm={6} md={3}>
              <AppWeeklySales />
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <AppNewUsers />
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <AppItemOrders />
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <AppBugReports />
            </Grid>

            <Grid item xs={12} md={6} lg={8}>
              <CytoCard graphNodes={dummyNodes} nodeFilter={this.state.nodeFilter}/>
            </Grid>
            
            <Grid item xs={12} md={6} lg={8}>
              <AEList graphNodes={dummyNodes} filterHandler={this.AEfilterCallback} />
            </Grid>

            <Grid item xs={12} md={6} lg={8}>
              <AppWebsiteVisits />
            </Grid>

            <Grid item xs={12} md={6} lg={4}>
              <AppCurrentVisits />
            </Grid>

            <Grid item xs={12} md={6} lg={8}>
              <AppConversionRates />
            </Grid>

            <Grid item xs={12} md={6} lg={4}>
              <AppCurrentSubject />
            </Grid>

            <Grid item xs={12} md={6} lg={8}>
              <AppTasks />
            </Grid>

            <Grid item xs={12} md={6} lg={4}>
              <AppOrderTimeline />
            </Grid>

          </Grid>
        </Container>
      </Page>
    );
  }
}
