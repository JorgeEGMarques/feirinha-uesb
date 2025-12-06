"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardTitle } from "./ui/card"
import Image from "next/image"
import { product } from "@/utils/types"
import { imageConverter } from "@/utils/image-converter"
import Link from "next/link"

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
    <Link href={`/products/${currentProduct.code}`} passHref>
      <Card className="relative overflow-hidden rounded-lg shadow-md border-gray-300 group">

        <div className="relative h-64 w-full flex items-center justify-center bg-gray-100">
          
          {currentProduct.imagem ? (
            <Image
              alt={currentProduct.name}
              src={imageConverter(currentProduct.imagem)}
              fill={true}
              style={{ objectFit: "cover" }}
              className="transition-transform duration-500 ease-in-out group-hover:scale-105"
            />
          ) : (
            /* Snippet de Fallback (Sem Foto) */
            <div className="flex-shrink-0 z-0">
              <div className="h-32 w-32 rounded-full bg-gray-200 flex items-center justify-center shadow-inner">
                <span className="text-gray-500 font-medium">Sem Foto</span>
              </div>
            </div>
          )}

          {/* Conteúdo sobreposto (Título e Preço) */}
          <CardContent className="absolute inset-0 flex flex-col items-center justify-center bg-black bg-opacity-40 hover:bg-opacity-50 transition-colors duration-300 z-10">
            <CardTitle className="text-2xl md:text-3xl drop-shadow-lg font-bold text-center text-white mb-2 px-2">
              {currentProduct.name}
            </CardTitle>
            {price && (
              <p className="text-lg drop-shadow-lg font-semibold text-white">
                R$ {price.toFixed(2).replace(".", ",")}
              </p>
            )}
          </CardContent>
        </div>
      </Card>
    </Link>
  )
}