"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardTitle } from "./ui/card"
import Image from "next/image"
import { product } from "@/utils/types"
import { imageConverter } from "@/utils/image-converter"

interface Props {
  products: product[]
}

export const Carousel = ({ products }: Props) => {
  const [current, setCurrent] = useState<number>(0)

  useEffect(() => {
    if (!products || products.length <= 1) return; 

    const interval = setInterval(() => {
      setCurrent((prev) => (prev + 1) % products.length)
    }, 3000)

    return () => clearInterval(interval);
  }, [products.length])

  if (!products || products.length === 0) return null;

  const currentProduct = products[current];
  const price = currentProduct.price
  
  return(
    <Card className="relative overflow-hidden rounded-lg shadow-md border-gray-300">
      { currentProduct.imagem && (
        <div className="relative h-64 w-full"> 
          <Image
            alt={ currentProduct.name }
            src={ imageConverter(currentProduct.imagem) }
            fill={true}
            style={{ objectFit: 'cover' }}
            className="transition-opacity duration-500 ease-in-out hover:scale-105"
          />
        </div>
      )}
      <CardContent className="absolute inset-0 flex flex-col items-center justify-center bg-black bg-opacity-50 hover:scale-105 transition-colors">
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