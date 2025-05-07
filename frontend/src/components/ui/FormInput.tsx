import React from 'react';
import { TextField, TextFieldProps } from '@mui/material';
import { useField } from 'formik';
import { motion } from 'framer-motion';

interface FormInputProps extends Omit<TextFieldProps, 'name'> {
  name: string;
  label: string;
}

const FormInput: React.FC<FormInputProps> = ({ name, label, ...props }) => {
  const [field, meta] = useField(name);
  const errorText = meta.error && meta.touched ? meta.error : '';

  return (
    <motion.div
      initial={{ y: 20, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      transition={{ duration: 0.5 }}
    >
      <TextField
        {...field}
        {...props}
        id={name}
        label={label}
        variant="outlined"
        fullWidth
        margin="normal"
        error={!!errorText}
        helperText={errorText}
        sx={{
          '& .MuiOutlinedInput-root': {
            transition: 'all 0.3s ease-in-out',
            '&.Mui-focused': {
              transform: 'scale(1.01)',
              '& fieldset': {
                borderWidth: '2px',
              },
            },
            '&:hover fieldset': {
              borderColor: 'primary.main',
            },
          },
          '& .MuiInputLabel-root': {
            transition: 'all 0.3s ease-in-out',
            '&.Mui-focused': {
              color: 'primary.main',
            },
          },
        }}
      />
    </motion.div>
  );
};

export default FormInput; 