"use client"

import Image from "next/image"
import { product } from "@/utils/types"
import { Button } from "./ui/button";
import { useCartStore } from "@/store/cart-store";

interface ProductDetailsProps {
  product: product
}

export const ProductDetail = ({ product }: ProductDetailsProps) => {
  const { items, addItem, removeItem } = useCartStore();
  const cartItem = items.find((item) => item.id === product.id);
  const quantity = cartItem ? cartItem.quantity : 0;
  
  const onAddItem = () => {
    addItem({
      id: product.id,
      name: product.name,
      price: product.price as number,
      imageUrl: product.src ? product.src : null,
      quantity: 1,
    })
  }

  return (
    <div className="container mx-auto px-4 py-8 flex flex-col md:flex-row gap-8 items-center">
      <div className="relative h-96 w-full md:w-1/2 rounded-lg overflow-hidden">
        <Image
          src={ product.src }
          alt={ product.name }
          fill={true}
          style={{ objectFit: 'cover' }}
          className="transition duration-300 hover:opacity-90"
        />
      </div>

      <div className="md:w-1/2">
        <h1 className="text-3xl font-bold mb-4"> {product.name} </h1>

        { product.price && (
            <p className="text-lg font-semibold text-gray-900">
              R${(product.price)}
            </p>
        )}

        <div className="flex items-center space-x-4">
          <Button variant="outline" onClick={() => removeItem(product.id)}> -</Button>
          <span className="text-lg font-semibold"> {quantity}</span>
          <Button onClick={onAddItem}>+</Button>
        </div>
      </div>
    </div>
  )
};