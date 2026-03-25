import { Button } from '@mui/material';
import type { ButtonProps } from '@mui/material';
import { styled } from '@mui/material/styles';

export const PrimaryButton = styled(Button)<ButtonProps>(() => ({
  borderRadius: 8,
  padding: '10px 24px',
  fontWeight: 600,
  textTransform: 'none',
  boxShadow: 'none',
  '&:hover': {
    boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
  },
}));

export const SecondaryButton = styled(Button)<ButtonProps>(({ theme }) => ({
  borderRadius: 8,
  padding: '10px 24px',
  fontWeight: 500,
  textTransform: 'none',
  border: `2px solid ${theme.palette.primary.main}`,
  '&:hover': {
    border: `2px solid ${theme.palette.primary.dark}`,
  },
}));

export default PrimaryButton;
