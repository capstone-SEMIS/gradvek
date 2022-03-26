import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import IconButton from "@mui/material/IconButton";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import {Checkbox} from "@mui/material";
import ListItemText from "@mui/material/ListItemText";
import {useState} from "react";

// ----------------------------------------------------------------------
// React function for CheckboxList. This has hardcoded
// list of data sets. In future version, it will dynamically read from the backend.
// ----------------------------------------------------------------------
export default function CheckboxList() {
    const [checked, setChecked] = useState([]);

    const handleToggle = value => () => {
        const currentIndex = checked.indexOf(value);
        const newChecked = [...checked];

        if (currentIndex === -1) {
            newChecked.push(value);
        } else {
            newChecked.splice(currentIndex, 1);
        }

        setChecked(newChecked);
    };

    return (
        <List sx={{width: "100%", maxWidth: 360}}>
            {[
                "Core Annotation",
                "Molecular Interactions",
                "Baseline Expression",
                "Mouse Phenotype"
            ].map(value => {
                const labelId = `checkbox-list-label-${value}`;

                return (
                    <ListItem key={value} secondaryAction={<IconButton edge="end" aria-label="comments"/>}
                              disablePadding>
                        <ListItemButton role={undefined} onClick={handleToggle(value)} dense>
                            <ListItemIcon>
                                <Checkbox edge="start" checked={checked.indexOf(value) !== -1} tabIndex={-1}
                                          disableRipple inputProps={{"aria-labelledby": labelId}}/>
                            </ListItemIcon>
                            <ListItemText id={labelId} primary={`Target ${value}`}/>
                        </ListItemButton>
                    </ListItem>
                );
            })}
        </List>
    );
}
