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
    const interval = setInterval(() => {
      setCurrent((prev) => (prev + 1) % products.length)
    }, 3000)

    return () => clearInterval(interval);
  }, [products.length])

  const currentProduct = products[current];

  const price = currentProduct.price
  
  return(
    <Card className="relative overflow-hidden rounded-lg shadow-md border-gray-300">
      { currentProduct.src && (
        <div className="relative h-80 w-full">
          <Image
            alt={ currentProduct.name }
            src={ currentProduct.src }
            layout="fill"
            objectFit="cover"
            className="transition-opacity duration-500 ease-in-out" />
        </div>
      )}
      <CardContent className="absolute inset-0 flex flex-col items-center justify-center bg-black bg-opacity-50">
        <CardTitle className="text-4xl drop-shadow-[0_2px_2px_rgb(0,0,0)] font-bold text-white mb-2">{ currentProduct.name }</CardTitle>
        { price && (
          <p className="text-xl text-white">
            R${price}
          </p>
        ) }
      </CardContent>

    </Card>
  )
}