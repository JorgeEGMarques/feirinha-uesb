"use client"

import { useMemo } from "react"
import { Carousel } from "./carousel" // Importe seu componente original aqui

interface Product {
  src: string
  price: number
  name: string
}

interface Props {
  products: Product[]
}

export const ProductGrid = ({ products }: Props) => {
  
  // Lógica para distribuir os produtos em 3 colunas (Round Robin)
  const columns = useMemo(() => {
    const cols: Product[][] = [[], [], []];
    
    products.forEach((product, index) => {
      // index % 3 resultará em 0, 1 ou 2, decidindo a coluna
      const columnIndex = index % 3; 
      cols[columnIndex].push(product);
    });

    return cols;
  }, [products]);

  return (
    <div className="w-full max-w-7xl mx-auto px-4">
      {/* Grid responsivo: 1 coluna no celular, 3 no desktop */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        
        {columns.map((columnProducts, index) => (
           <div key={index} className="w-full">
              {/* Verificação de segurança: só renderiza se houver produtos nessa coluna */}
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