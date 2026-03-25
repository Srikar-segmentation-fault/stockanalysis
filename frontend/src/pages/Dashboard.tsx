import { useState, useEffect } from 'react';
import { Box, Typography, CircularProgress, Alert } from '@mui/material';
import PortfolioDashboardComponent from '../components/PortfolioDashboardComponent';
import { api } from '../services/api';
import type { PortfolioSummary } from '../types';

export default function Dashboard() {
  const [summary, setSummary] = useState<PortfolioSummary | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchPortfolioSummary = async () => {
      setLoading(true);
      setError(null);

      try {
        // TODO: Get actual user ID from auth context
        const userId = 1;
        const data = await api.get(`/portfolio/${userId}/summary`);
        setSummary(data);
      } catch (err) {
        setError('Failed to load portfolio summary. Please try again.');
      } finally {
        setLoading(false);
      }
    };

    fetchPortfolioSummary();
  }, []);

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 5 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return <Alert severity="error">{error}</Alert>;
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom sx={{ mb: 3 }}>
        Portfolio Dashboard
      </Typography>
      {summary && <PortfolioDashboardComponent summary={summary} />}
    </Box>
  );
}
