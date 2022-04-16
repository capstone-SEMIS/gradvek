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

  let listOfDrugs = [
    {
      title: "Tylenol",
      list: [
        {
          drug: "drugtest",
          weight: 3435
        },
        {
          drug: "drugtest2",
          weight: 45435
        },
        {
          drug: "drugtest3",
          weight: 9835
        }
      ]
    },
    {
      title: "Prozac",
      list: [
        {
          drug: "Prozdrugtest",
          weight: 43435
        },
        {
          drug: "Prozdrugtest2",
          weight: 5435
        },
        {
          drug: "Prozdrugtest3",
          weight: 79935
        }
      ]
    }
  ];
  return (
    <Card>
      {listOfDrugs.map(item => (
        <Accordion title={item.title} list={item.list} />
      ))}
    </Card>
  );
}
