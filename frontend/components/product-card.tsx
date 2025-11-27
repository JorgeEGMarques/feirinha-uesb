import Link from "next/link";
import { Card, CardContent, CardHeader, CardTitle } from "./ui/card";
import Image from "next/image";
import { Button } from "./ui/button";
import { product } from "@/utils/types"

interface ProductCardProps {
  product: product
}

export const ProductCard = ({ product }: ProductCardProps) => {
  return (
    <Link href={ `/products/${product.id}` }>
      <Card className="group hover:shadow-2xl transition duration-300 py-0 h-full flex flex-col border-gray-300 gap-0">
        { product.src && (
          <div className="relative h-80 w-full">
            <Image
              src={ product.src }
              alt={ product.name }
              fill={true}
              style={{ objectFit: 'cover' }}
              sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
              className="group-hover:opacity-90 transition-opacity duration-300 rounded-t-lg"
            />
          </div>
        )}

        <CardHeader className="p-4">
          <CardTitle className="text-xl font-bold text-gray-800">
            { product.name }            
          </CardTitle>
        </CardHeader>
        <CardContent className="p-4 flex-grow flex flex-col justify-between">
          { product.price && (
            <p className="text-lg font-semibold text-gray-900">
              R${(product.price)}
            </p>
          ) }

          <Button>Ver detalhes</Button>
        </CardContent>
      </Card>
    </Link>);
}