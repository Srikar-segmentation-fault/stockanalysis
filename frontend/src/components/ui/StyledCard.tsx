import { Card } from '@mui/material';
import type { CardProps } from '@mui/material';
import { styled } from '@mui/material/styles';

export const StyledCard = styled(Card)<CardProps>(() => ({
  borderRadius: 12,
  boxShadow: '0 2px 8px rgba(0,0,0,0.08)',
  transition: 'box-shadow 0.3s ease',
  '&:hover': {
    boxShadow: '0 4px 16px rgba(0,0,0,0.12)',
  },
}));

export default StyledCard;
