// material
import { alpha, styled } from '@mui/material/styles';
import {Card, IconButton, InputAdornment, Stack, TextField, Typography} from '@mui/material';
import { useState } from "react";

// utils
import { fShortenNumber } from '../../../utils/formatNumber';
// component
import Iconify from '../../../components/Iconify';
import * as Yup from "yup";
import {Form, FormikProvider, useFormik} from "formik";
import {useNavigate} from "react-router-dom";
import {LoadingButton} from "@mui/lab";

// ----------------------------------------------------------------------

const RootStyle = styled(Card)(({ theme }) => ({
  boxShadow: 'none',
  textAlign: 'center',
  padding: theme.spacing(5, 0),
  color: theme.palette.primary.darker,
  backgroundColor: theme.palette.primary.lighter
}));

const IconWrapperStyle = styled('div')(({ theme }) => ({
  margin: 'auto',
  display: 'flex',
  borderRadius: '50%',
  alignItems: 'center',
  width: theme.spacing(8),
  height: theme.spacing(8),
  justifyContent: 'center',
  marginBottom: theme.spacing(3),
  color: theme.palette.primary.dark,
  backgroundImage: `linear-gradient(135deg, ${alpha(theme.palette.primary.dark, 0)} 0%, ${alpha(
    theme.palette.primary.dark,
    0.24
  )} 100%)`
}));

// ----------------------------------------------------------------------

export default function AppWeeklySales() {
  const RegisterSchema = Yup.object().shape({
    geneID: Yup.string().required('Gene ID required'),
  });

  const formik = useFormik({
    initialValues: {
      geneID: ''
    },
    validationSchema: RegisterSchema,
    onSubmit: (values) => {
      const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ values })
      };
      fetch('/gene/'+ values.geneID, requestOptions)
          .then((response) => {
            console.log("Status " + response.status);
            formik.setSubmitting(false);
          });
    }
  });

  const { errors, touched, handleSubmit, isSubmitting, getFieldProps } = formik;

  return (
      <RootStyle>
      <FormikProvider value={formik}>
        <Form autoComplete="off" noValidate onSubmit={formik.handleSubmit}>
          <Stack spacing={3}>
              <TextField
                  fullWidth
                  label="Gene ID"
                  {...getFieldProps('geneID')}
                  error={Boolean(touched.geneID && errors.geneID)}
                  helperText={touched.geneID && errors.geneID}
                  onChange={formik.handleChange}
                  value={formik.values.geneID}
              />

            <LoadingButton
                fullWidth
                size="large"
                type="submit"
                variant="contained"
                loading={isSubmitting}
            >
              Add
            </LoadingButton>
          </Stack>
        </Form>
      </FormikProvider>
      </RootStyle>
  );



  //
  // let [searchText, setSearchText] = useState('');
  // const handleTextChange = event => {
  //   setSearchText(event.target.value)
  // }

  // var cy = cytoscape({
  //     container: document.getElementById('cy-container'), // container to render in
  //     elements: ['1', '2', '3']
  // });

  // const handleSearch = event => {
  //   event.preventDefault();
  //   const requestOptions = {
  //     method: 'POST',
  //     headers: { 'Content-Type': 'application/json' },
  //     body: JSON.stringify({ searchText })
  //   };
  //   // const requestOptions2 = {
  //   //     method: 'GET',
  //   //     headers: { 'Content-Type': 'application/json' },
  //   //     body: JSON.stringify({ searchText })
  //   // };
  //
  //   fetch('/gene/'+ searchText, requestOptions)
  //       .then(response => console.log(response.text()));

    // fetch('/gene/' + searchText)
    //     .then(cy.addToPool(['1', '2', '3']))


  // }

  // return (
  //     <RootStyle>
  //     <div className="App">
  //       <header className="App-header">
  //         <Box sx={{ display: 'flex', alignItems: 'flex-end' }}>
  //           <form onSubmit={handleSearch}>
  //             <TextField id="input-with-sx"
  //                        label="With sx"
  //                        variant="standard"
  //                        onChange={handleTextChange}
  //                        value={this.searchText}
  //             />
  //             <IconButton type="search"
  //                         color="primary"
  //                         aria-label="search"
  //                         component="span">
  //               <SearchIcon />
  //             </IconButton>
  //           </form>
  //         </Box>
  //       </header>
  //       <body>
  //       <div id="cy-container">
  //
  //       </div>
  //       </body>
  //     </div>
  //     </RootStyle>
  // );




  // return (
  //   <RootStyle>
  //
  //
  //     {/*<IconWrapperStyle>*/}
  //     {/*  <Iconify icon="ant-design:android-filled" width={24} height={24} />*/}
  //     {/*</IconWrapperStyle>*/}
  //     {/*<Typography variant="h3">{fShortenNumber(TOTAL)}</Typography>*/}
  //     {/*<Typography variant="subtitle2" sx={{ opacity: 0.72 }}>*/}
  //     {/*  Weekly Sales*/}
  //     {/*</Typography>*/}
  //   </RootStyle>
  // );
}
