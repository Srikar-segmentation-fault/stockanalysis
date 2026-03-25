import { create } from 'zustand';
import type { PortfolioHolding } from '../types';

interface PortfolioState {
  totalValue: number;
  holdings: PortfolioHolding[];
  setTotalValue: (value: number) => void;
  setHoldings: (holdings: PortfolioHolding[]) => void;
}

export const usePortfolioStore = create<PortfolioState>((set) => ({
  totalValue: 0,
  holdings: [],
  setTotalValue: (value) => set({ totalValue: value }),
  setHoldings: (holdings) => set({ holdings }),
}));
