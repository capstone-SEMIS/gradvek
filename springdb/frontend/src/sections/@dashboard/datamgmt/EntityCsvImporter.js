import { styled } from "@mui/material/styles";
import Button from "@mui/material/Button";
import Stack from "@mui/material/Stack";

const Input = styled("input")({
  display: "none"
});

export default function UploadButton() {
  function onFileChangeHandler(event) {
    event.preventDefault();
    const baseUrl = window.location.protocol + "//" + window.location.host;
    const formData = new FormData();
    formData.append("file", event.target.files[0]);
    formData.append("baseUrl", baseUrl);
    fetch("/api/csv", {
      method: "post",
      body: formData
    })
      .then(response =>
        response.json().then(body => ({
          ok: response.ok,
          body: body
        }))
      )
      .then(result => {
        if (result.ok) {
          alert(`File ${result.body.name} uploaded successfully.`);
        }
      });
  }

<<<<<<< HEAD
  return (
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
      {/* <label htmlFor="icon-button-file">Import CSV</label> */}
    </Stack>
  );
}
=======
    function onFileChangeHandler(event) {
        event.preventDefault();
        const baseUrl = window.location.protocol + '//' + window.location.host;
        const formData = new FormData();
        formData.append('file', event.target.files[0]);
        formData.append('baseUrl', baseUrl);
        fetch('/api/csv', {
            method: 'post',
            body: formData
        }).then(response => response.json().then(
            body => ({
                ok: response.ok,
                body: body
            }))
        ).then(result => {
            if (result.ok) {
                alert(`File ${JSON.stringify(result.body)} uploaded successfully.`);
            }
        })
    }

    return (
        <Stack direction="row" alignItems="center" spacing={2}>
            <label htmlFor="contained-button-file">
                <Input accept=".csv" id="contained-button-file" type="file" onChange={onFileChangeHandler}/>
                <Button variant="contained" component="span">Upload</Button>
            </label>
            <label htmlFor="icon-button-file">Import CSV</label>
        </Stack>
    );
}
>>>>>>> 1514afe1ddbac5361b9bf0e9a511f458654abe60
