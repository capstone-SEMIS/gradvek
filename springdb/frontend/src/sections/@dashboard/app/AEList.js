import PropTypes from "prop-types";
// material

import { Card } from "@mui/material";
import Accordion from "./Accordion.js";

// ----------------------------------------------------------------------

AEList.propTypes = {
  graphNodes: PropTypes.array.isRequired
};

export default function AEList({ graphNodes, filterHandler }) {
  let AEs = graphNodes.filter(graphNode => {
    return graphNode.classes?.includes("adverse event");
  });
  console.log("AEs", AEs);

  listOfDrugs = {};
  return (
    <Card>
      <Accordion title="Title" drugs="Drugs" weight={9812734} />
    </Card>
  );
}
