// material
import { Box, Grid, Container, Typography } from '@mui/material';
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

// ----------------------------------------------------------------------

export default function DashboardApp() {
  let graphNodes = [
    {
      group: "nodes",
      classes: ["pathway"],
      data: {
        id: "pathway_xyz"
      }
    },
    {
      group: "nodes",
      classes: ["drug"],
      data: {
        id: 'Acetaminophen',
        chembl_code: 'CHEMBL112'
      }
    },
    {
      group: "nodes",
      classes: ["adverse event"],
      data: {
        id: 'Acute hepatic failure',
        meddraCode: '10000804'
      }
    },
    {
      group: "nodes",
      classes: ["adverse event"],
      data: {
        id: 'Toxicity to various agents',
        meddraCode: '10070863'
      }
    },
    {
      group: "nodes",
      classes: ["proteinTarget"],
      data: {
        id: 'Vanilloid receptor',
        parent: "pathway_xyz",
      }
    },
    {
      group: "nodes",
      classes: ["proteinTarget"],
      data: {
        id: 'XYZ receptor',
        parent: "pathway_xyz",
      }
    },
    {
      group: "nodes",
      classes: ["proteinTarget"],
      data: {
        id: 'Cyclooxygenase',
      },

    },
    {
      group: "edges",
      data: {
        id: 'edge_1',
        source: 'Acetaminophen',
        target: 'Acute hepatic failure',
        arrow: "vee",
        critval: 123
      }
    },
    {
      group: "edges",
      data: {
        id: 'edge_2',
        source: 'Acetaminophen',
        target: 'Toxicity to various agents',
        arrow: "vee",
        critval: 123
      }
    },
    {
      group: "edges",
      classes: ["drug_target"],
      data: {
        id: 'edge_3',
        source: 'Acetaminophen',
        target: 'Vanilloid receptor',
        arrow: "vee",
        action: "inhibits",
      }
    },
    {
      group: "edges",
      classes: ["drug_target"],
      data: {
        id: 'edge_4',
        source: 'Acetaminophen',
        target: 'Cyclooxygenase',
        arrow: "vee",
        action: "opener",
      }
    },
  ];

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
            <CytoCard graphNodes={graphNodes}/>
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
