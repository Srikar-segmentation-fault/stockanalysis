import { useState, useEffect } from 'react';
import { Box, CircularProgress, Alert } from '@mui/material';
import { useParams } from 'react-router-dom';
import StockDetailsComponent from '../components/StockDetailsComponent';
import TransactionModal from '../components/TransactionModal';
import { api } from '../services/api';
import type { Stock } from '../types';

export default function StockDetails() {
  const { symbol } = useParams<{ symbol: string }>();
  const [stock, setStock] = useState<Stock | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [buyModalOpen, setBuyModalOpen] = useState(false);
  const [sellModalOpen, setSellModalOpen] = useState(false);

  useEffect(() => {
    const fetchStockDetails = async () => {
      if (!symbol) return;

      setLoading(true);
      setError(null);

      try {
        const data = await api.get(`/stocks/${symbol}/details`);
        setStock(data);
      } catch (err) {
        setError('Failed to load stock details. Please try again.');
      } finally {
        setLoading(false);
      }
    };

    fetchStockDetails();
  }, [symbol]);

  const handleBuyConfirm = async (quantity: number, price: number) => {
    // TODO: Get actual user ID from auth context
    const userId = 1;
    await api.post(`/portfolio/${userId}/buy?symbol=${symbol}&quantity=${quantity}&price=${price}`, {});
  };

  const handleSellConfirm = async (quantity: number, price: number) => {
    // TODO: Get actual user ID from auth context
    const userId = 1;
    await api.post(`/portfolio/${userId}/sell?symbol=${symbol}&quantity=${quantity}&price=${price}`, {});
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 5 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error || !stock) {
    return <Alert severity="error">{error || 'Stock not found'}</Alert>;
  }

  return (
    <Box>
      <StockDetailsComponent
        stock={stock}
        onBuy={() => setBuyModalOpen(true)}
        onSell={() => setSellModalOpen(true)}
      />

      <TransactionModal
        open={buyModalOpen}
        onClose={() => setBuyModalOpen(false)}
        stock={stock}
        type="BUY"
        onConfirm={handleBuyConfirm}
      />

      <TransactionModal
        open={sellModalOpen}
        onClose={() => setSellModalOpen(false)}
        stock={stock}
        type="SELL"
        onConfirm={handleSellConfirm}
      />
    </Box>
  );
}
