// material
import { alpha, styled } from '@mui/material/styles';
import {Card, Stack, TextField, Typography} from '@mui/material';
// utils
import { fShortenNumber } from '../../../utils/formatNumber';
// component
import Iconify from '../../../components/Iconify';
import {Form, FormikProvider, useFormik} from "formik";
import {LoadingButton} from "@mui/lab";
import * as Yup from "yup";

// ----------------------------------------------------------------------

const RootStyle = styled(Card)(({ theme }) => ({
  boxShadow: 'none',
  textAlign: 'center',
  padding: theme.spacing(5, 0),
  color: theme.palette.info.darker,
  backgroundColor: theme.palette.info.lighter
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
  color: theme.palette.info.dark,
  backgroundImage: `linear-gradient(135deg, ${alpha(theme.palette.info.dark, 0)} 0%, ${alpha(
    theme.palette.info.dark,
    0.24
  )} 100%)`
}));

// ----------------------------------------------------------------------

export default function AppNewUsers() {

  const RegisterSchema = Yup.object().shape({
    type: Yup.string().required('Type required'),
  });

  const formik = useFormik({
    initialValues: {
      type: ''
    },
    validationSchema: RegisterSchema,
    onSubmit: (values) => {
      const requestOptions = {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
      };
      fetch('/queryType/'+ values.type, requestOptions)
          .then((response) => {
            formik.setSubmitting(false);
            console.log("Status " + response.status);
            response.text().then(data => console.log(data));
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
                  label="Type"
                  {...getFieldProps('type')}
                  error={Boolean(touched.type && errors.type)}
                  helperText={touched.type && errors.type}
                  onChange={formik.handleChange}
                  value={formik.values.type}
              />

              <LoadingButton
                  fullWidth
                  size="large"
                  type="submit"
                  variant="contained"
                  loading={isSubmitting}
              >
                Retrieve
              </LoadingButton>
            </Stack>
          </Form>
        </FormikProvider>
      </RootStyle>
  );
}
