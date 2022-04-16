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
          drug: "test",
          weight: 3435
        },
        {
          drug: "test2",
          weight: 45435
        },
        {
          drug: "test3",
          weight: 9835
        }
      ]
    },
    {
      title: "Prozac",
      list: [
        {
          drug: "Proztest",
          weight: 43435
        },
        {
          drug: "Proztest2",
          weight: 5435
        },
        {
          drug: "Proztest3",
          weight: 79935
        }
      ]
    }
  ];
  return (
    <Card>
      {listOfDrugs.map(item => (
        <Accordion
          title="Tylenol"
          drugs="Drugs"
          weight={9812734}
          list={item.list}
        />
      ))}
    </Card>
  );
}
