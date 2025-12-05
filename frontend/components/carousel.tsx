"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardTitle } from "./ui/card"
import Image from "next/image"

interface Props {
  products: { src: string, price: number, name: string }[]
}

export const Carousel = ({ products }: Props) => {
  const [current, setCurrent] = useState<number>(0)

  useEffect(() => {
    // Se não houver produtos ou só houver 1, não precisa girar
    if (!products || products.length <= 1) return; 

    const interval = setInterval(() => {
      setCurrent((prev) => (prev + 1) % products.length)
    }, 3000)

    return () => clearInterval(interval);
  }, [products.length]) // Dependência correta

  // Se o array vier vazio por algum erro, não renderize nada para evitar crash
  if (!products || products.length === 0) return null;

  const currentProduct = products[current];
  const price = currentProduct.price
  
  return(
    <Card className="relative overflow-hidden rounded-lg shadow-md border-gray-300">
      { currentProduct.src && (
        // Mudei h-80 para h-64 ou aspect-ratio para ficar melhor em colunas
        <div className="relative h-64 w-full"> 
          <Image
            alt={ currentProduct.name }
            src={ currentProduct.src }
            fill={true}
            style={{ objectFit: 'cover' }}
            className="transition-opacity duration-500 ease-in-out hover:scale-105" // Efeito de zoom suave
          />
        </div>
      )}
      <CardContent className="absolute inset-0 flex flex-col items-center justify-center bg-black bg-opacity-50 hover:scale-105 transition-colors">
        {/* Reduzi text-4xl para text-2xl para caber nas colunas */}
        <CardTitle className="text-2xl md:text-3xl drop-shadow-md font-bold text-center text-white mb-2">
            { currentProduct.name }
        </CardTitle>
        { price && (
          <p className="text-lg font-semibold text-white">
            R$ {price.toFixed(2).replace('.', ',')}
          </p>
        ) }
      </CardContent>
    </Card>
  )
}