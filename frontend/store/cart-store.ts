import { create } from "zustand";
import { persist } from "zustand/middleware"

export interface CartItem {
  code: number;
  name: string;
  price: number;
  imageUrl: string | null;
  quantity: number;
}

interface CartStore {
  items: CartItem[];
  addItem: (item: CartItem) => void;
  removeItem: (code: number) => void;
  clearCart: () => void;
}

export const useCartStore = create<CartStore>()(
  persist((set) => ({
    items: [],
    addItem: (item) => set((state) => {
      const existing = state.items.find((i) => i.code === item.code);

      if (existing) {
        return {
          items: state.items.map((i) =>
            i.code === item.code
              ? { ...i, quantity: i.quantity + item.quantity }
              : i
          ),
        };
      }

      return { items: [...state.items, item] };
    }),
    removeItem: (code) => set((state) => {
      return {
        items: state.items
          .map((item) =>
            item.code === code ? { ...item, quantity: item.quantity - 1 } : item
          )
          .filter((item) => item.quantity > 0),
      };
    }),
    clearCart: () => set(() => {
      return { items: [] }
    }),
  }), { name: "cart"})
);