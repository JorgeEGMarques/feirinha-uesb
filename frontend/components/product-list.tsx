"use client"

import { useState } from "react"
import { ProductCard } from "./product-card"
import { product } from "@/utils/types"

interface ProductListProps {
  products: product[]
}

export const ProductList = ({products}: ProductListProps) => {
  const [searchTerm, setSearchTerm] = useState<string>("");

  console.log(products)

  const filteredProducts = products.filter((product) => {
    const term = searchTerm.toLowerCase();
    const nameMatch = product.name.toLowerCase().includes(term);

    return nameMatch;
  })
  
  return (
    <div>
      <div className="mb-6 flex justify-center">
        <input
          type="text"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          placeholder="Procurar produtos..."
          className="w-full max-w-md rounded border border-gray-300 px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>
      <ul className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        { filteredProducts.map((product, key) => {
          return (
            <li key={key}>
              {" "}
              <ProductCard product={ product } />
            </li>
          )
        }) }
      </ul>
    </div>
  )
}