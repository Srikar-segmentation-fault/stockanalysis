import { useState } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Box,
  Typography,
  Alert,
} from '@mui/material';
import type { Stock } from '../types';

interface TransactionModalProps {
  open: boolean;
  onClose: () => void;
  stock: Stock;
  type: 'BUY' | 'SELL';
  onConfirm: (quantity: number, price: number) => Promise<void>;
  maxQuantity?: number;
}

export default function TransactionModal({
  open,
  onClose,
  stock,
  type,
  onConfirm,
  maxQuantity,
}: TransactionModalProps) {
  const [quantity, setQuantity] = useState<string>('');
  const [price, setPrice] = useState<string>(stock.currentPrice.toFixed(2));
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    const quantityNum = parseInt(quantity);
    const priceNum = parseFloat(price);

    // Validation
    if (isNaN(quantityNum) || quantityNum <= 0) {
      setError('Quantity must be a positive number');
      return;
    }

    if (quantityNum > 1000000) {
      setError('Quantity exceeds maximum allowed value of 1,000,000');
      return;
    }

    if (isNaN(priceNum) || priceNum <= 0) {
      setError('Price must be a positive number');
      return;
    }

    if (priceNum > 1000000) {
      setError('Price exceeds maximum allowed value of $1,000,000');
      return;
    }

    // Check decimal places
    const priceStr = price.toString();
    const decimalIndex = priceStr.indexOf('.');
    if (decimalIndex !== -1 && priceStr.length - decimalIndex - 1 > 2) {
      setError('Price can have at most 2 decimal places');
      return;
    }

    if (type === 'SELL' && maxQuantity !== undefined && quantityNum > maxQuantity) {
      setError(`Cannot sell more than ${maxQuantity} shares`);
      return;
    }

    setLoading(true);

    try {
      await onConfirm(quantityNum, priceNum);
      setSuccess(true);
      setTimeout(() => {
        handleClose();
      }, 1500);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Transaction failed. Please try again.';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setQuantity('');
    setPrice(stock.currentPrice.toFixed(2));
    setError(null);
    setSuccess(false);
    onClose();
  };

  const totalValue = (parseFloat(quantity) || 0) * (parseFloat(price) || 0);

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <form onSubmit={handleSubmit}>
        <DialogTitle>
          {type === 'BUY' ? 'Buy' : 'Sell'} {stock.symbol}
        </DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 1 }}>
            <Typography variant="body2" color="text.secondary" gutterBottom>
              {stock.name}
            </Typography>
            <Typography variant="h6" gutterBottom>
              Current Price: ${stock.currentPrice.toFixed(2)}
            </Typography>

            {type === 'SELL' && maxQuantity !== undefined && (
              <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                Available to sell: {maxQuantity} shares
              </Typography>
            )}

            <TextField
              fullWidth
              label="Quantity"
              type="number"
              value={quantity}
              onChange={(e) => setQuantity(e.target.value)}
              required
              sx={{ mb: 2 }}
              inputProps={{ min: 1, step: 1 }}
              disabled={loading || success}
            />

            <TextField
              fullWidth
              label="Price per share"
              type="number"
              value={price}
              onChange={(e) => setPrice(e.target.value)}
              required
              sx={{ mb: 2 }}
              inputProps={{ min: 0.01, step: 0.01 }}
              disabled={loading || success}
            />

            {totalValue > 0 && (
              <Box
                sx={{
                  p: 2,
                  bgcolor: 'background.default',
                  borderRadius: 1,
                  mb: 2,
                }}
              >
                <Typography variant="body2" color="text.secondary">
                  Total {type === 'BUY' ? 'Cost' : 'Proceeds'}
                </Typography>
                <Typography variant="h6">
                  ${totalValue.toFixed(2)}
                </Typography>
              </Box>
            )}

            {error && (
              <Alert severity="error" sx={{ mb: 2 }}>
                {error}
              </Alert>
            )}

            {success && (
              <Alert severity="success" sx={{ mb: 2 }}>
                Transaction completed successfully!
              </Alert>
            )}
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} disabled={loading || success}>
            Cancel
          </Button>
          <Button
            type="submit"
            variant="contained"
            color={type === 'BUY' ? 'success' : 'error'}
            disabled={loading || success}
          >
            {loading ? 'Processing...' : `Confirm ${type === 'BUY' ? 'Buy' : 'Sell'}`}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
}
