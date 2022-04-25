import {styled} from "@mui/material/styles";
import Button from "@mui/material/Button";
import Stack from "@mui/material/Stack";
import {useState} from "react";
import {Box, CircularProgress} from "@mui/material";
import CancelIcon from "@mui/icons-material/Cancel";
import CheckIcon from "@mui/icons-material/Check";
import HelpIcon from "@mui/icons-material/Help";
import {EntityProperty} from "../../../utils/entityProperties";

const CsvBox = styled(Box)({
  fontFamily: "monospace",
  whiteSpace: "pre-line",
  marginLeft: "2em"
});

const Input = styled("input")({
  display: "none"
});

const CenteredSpan = styled("span")({
  display: "flex",
  alignItems: "center"
});

const SpacedSpan = styled("span")({
  marginLeft: "1em",
  marginRight: "1em"
});

function IconicMessage(props) {
  return (
    <CenteredSpan>
      {" "}
      {props.icon} <SpacedSpan> {props.message} </SpacedSpan>{" "}
    </CenteredSpan>
  );
}

function Helper() {
  const [open, setOpen] = useState(false);

  const handleClick = () => {
    setOpen(prev => !prev);
  };

  const samples = [
    [
      "Node: AdverseEvent",
      "Import the adverse event somnolence with MedDRA code 10041349",
      "node," + EntityProperty.AdverseEventId + ",adverseEventId",
      "AdverseEvent,10041349,Somnolence"
    ],
    [
      "Node: Drug",
      "Import the drug acetaminophen with ChEMBL code CHEMBL112",
      "node," + EntityProperty.DrugId + ",drugId",
      "Drug,CHEMBL112,Acetaminophen"
    ],
    [
      "Node: Target",
      "Import the target vanilloid receptor with symbol TRPV1 and Ensembl code ENSG00000196689",
      "node," + EntityProperty.TargetId + ",symbol,name",
      "Target,ENSG00000196689,TRPV1,Vanilloid receptor"
    ],
    [
      "Node: Pathway",
      "Import the pathway TRP channels with Reactome identifier R-HSA-3295583 and top-level parent transport of small molecules",
      "node," + EntityProperty.PathwayId + ",pathwayCode,topLevelTerm",
      "Pathway,R-HSA-3295583,TRP channels,Transport of small molecules"
    ],
    [
      "Relationship: Associated With",
      "Import the relationship that Acetaminophen is associated with somnolence with a log likelihood ratio of 1023.49 and a critical value of 374.79",
      "relationship," + EntityProperty.DrugId + "," + EntityProperty.AdverseEventId + ",llr,critval",
      "ASSOCIATED_WITH,CHEMBL112,10041349,1023.49,374.79"
    ],
    [
      "Relationship: Targets",
      "Import the relationship that acetaminophen targets TRPV1 as an opener",
      "relationship," + EntityProperty.DrugId + "," + EntityProperty.TargetId + ",actionType",
      "TARGETS,CHEMBL112,ENSG00000196689,OPENER"
    ],
    [
      "Relationship: Participates",
      "Import the relationship that target TRPV1 participates in the TRP channels pathway",
      "relationship," + EntityProperty.TargetId + "," + EntityProperty.PathwayId,
      "PARTICIPATES_IN,ENSG00000196689,R-HSA-3295583"
    ]
  ];

  return (
    <Box sx={{ position: "relative" }}>
      <Button onClick={handleClick}>
        <HelpIcon color="info" />
        <SpacedSpan>{open ? "Hide help" : "CSV file format"}</SpacedSpan>
      </Button>
      {open ? (
        <Box margin="1em">
          <div>Here are examples of CSV files you can import.</div>
          {samples.map(s => {
            return (
              <div key={s[0]} style={{ marginTop: "1.5em" }}>
                <div style={{ marginBottom: "0.5em" }}>
                  <span style={{ fontWeight: "bold" }}>{s[0]}</span>
                  <span style={{ marginLeft: "1.5em" }}>{s[1]}</span>
                </div>
                <CsvBox>{s[2]}</CsvBox>
                <CsvBox>{s[3]}</CsvBox>
              </div>
            );
          })}
        </Box>
      ) : null}
    </Box>
  );
}

export default function Uploader(props) {
  const [INIT, SPINNING, SUCCESS, FAILURE] = [0, 1, 2, 3];
  const [progress, setProgress] = useState(INIT);

  function onFileChangeHandler(event) {
    event.preventDefault();
    const baseUrl = window.location.protocol + "//" + window.location.host;
    const formData = new FormData();
    formData.append("file", event.target.files[0]);
    formData.append("baseUrl", baseUrl);
    setProgress(SPINNING);
    fetch("/api/csv", {
      method: "post",
      body: formData
    })
      .then(response => {
        if (response.ok) {
          setProgress(SUCCESS);
          props.fetchData();
        } else {
          throw new Error(response.statusText);
        }
      })
      .catch(() => {
        setProgress(FAILURE);
      });
  }

  function getProgressIndicator() {
    switch (progress) {
      case SPINNING:
        return <CircularProgress />;
      case SUCCESS:
        return (
          <IconicMessage
            icon={<CheckIcon color="success" />}
            message={"Upload succeeded"}
          />
        );
      case FAILURE:
        return (
          <IconicMessage
            icon={<CancelIcon color="error" />}
            message={"Upload failed"}
          />
        );
      default:
        return;
    }
  }

  return (
    <Stack direction="column" spacing={2}>
      <Stack direction="row" alignItems="center" spacing={2}>
        <label htmlFor="contained-button-file">
          <Input
            accept=".csv"
            id="contained-button-file"
            type="file"
            onChange={onFileChangeHandler}
          />
          <Button variant="contained" component="span">
            Upload
          </Button>
        </label>
        {getProgressIndicator()}
      </Stack>
      <Helper />
    </Stack>
  );
}
