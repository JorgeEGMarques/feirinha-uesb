import Link from "next/link";
import { Card, CardContent, CardHeader, CardTitle } from "./ui/card";
import Image from "next/image";

interface Props {
  product: { src: string, price: number, name: string }
}

export const ProductCard = ({ product }: Props) => {
  return (
    <Link href={ "/products/1" }>
      <Card className="group hover:shadow-2xl transition duration-300 py-0 h-full flex flex-col border-gray-300 gap-0">
        { product.src && (
          <div className="relative h-80 w-full">
            <Image
              src={ product.src }
              alt={ product.name }
              layout="fill"
              objectFit="cover"
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
        </CardContent>
      </Card>
    </Link>);
}