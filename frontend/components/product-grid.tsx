"use client"

import { useMemo } from "react"
import { Carousel } from "./carousel"
import { product } from "@/utils/types"

interface Props {
  products: product[]
}

export const ProductGrid = ({ products }: Props) => {
  
  const columns = useMemo(() => {
    const cols: product[][] = [[], [], []];
    
    products.forEach((product, index) => {
      const columnIndex = index % 3; 
      cols[columnIndex].push(product);
    });

    return cols;
  }, [products]);

  return (
    <div className="w-full max-w-7xl mx-auto px-4">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        
        {columns.map((columnProducts, index) => (
           <div key={index} className="w-full">
              {columnProducts.length > 0 ? (
                <Carousel products={columnProducts} />
              ) : (
                <div className="h-64 flex items-center justify-center bg-gray-100 rounded-lg">
                  <span className="text-gray-400">Sem produtos</span>
                </div>
              )}
           </div>
        ))}

      </div>
    </div>
  )
}