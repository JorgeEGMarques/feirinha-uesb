"use client"

import Image from "next/image"
import { comment, product, profile } from "@/utils/types"
import { Button } from "./ui/button";
import { useCartStore } from "@/store/cart-store";
import { imageConverter } from "@/utils/image-converter";

interface ProductDetailsProps {
  product: product;
  comments: comment[];
  profiles: profile[];
}

export const ProductDetail = ({ product, comments, profiles }: ProductDetailsProps) => {
  const { items, addItem, removeItem } = useCartStore();
  const cartItem = items.find((item) => item.code == product.code);
  const quantity = cartItem ? cartItem.quantity : 0;

  const onAddItem = () => {
    addItem({
      code: product.code,
      name: product.name,
      price: product.price as number,
      imageUrl: product.imagem ? product.imagem : null,
      quantity: 1,
    })
  }

  return (
    <div>
      <div className="container mx-auto px-4 py-8 flex flex-col md:flex-row gap-8 items-center">
        <div className="relative h-96 w-full md:w-1/2 rounded-lg overflow-hidden">
          <Image
            src={ imageConverter(product.imagem) }
            alt={ product.name }
            fill={true}
            style={{ objectFit: 'cover' }}
            className="transition duration-300"
            loading="eager"
          />
        </div>

        <div className="md:w-1/2">
          <h1 className="text-3xl font-bold mb-4"> {product.name} </h1>

          { product.description && (
            <p className="text-gray-700 mb-4">
              {product.description}
            </p>
          ) }

          { product.price && (
              <p className="text-lg font-semibold text-gray-900">
                R${(product.price).toFixed(2)}
              </p>
          )}

          <div className="flex items-center space-x-4">
            <Button variant="outline" className="hover:cursor-pointer" onClick={() => removeItem(product.code)}> -</Button>
            <span className="text-lg font-semibold"> {quantity}</span>
            <Button className="hover:cursor-pointer" onClick={onAddItem}>+</Button>
          </div>
        </div>
      </div>
      <div className="space-y-6 mt-8">
        <h3 className="text-xl font-bold text-gray-900 border-b pb-4">
          Avaliações dos Clientes
        </h3>

        {comments.length === 0 ? (
          <p className="text-gray-500 italic">Nenhum comentário ainda. Seja o primeiro a avaliar!</p>
        ) : (
          comments.map((c: comment) => {
            const user = profiles.find((u: profile) => u.cpf === c.cpfUsuario);
            const userName = user?.nome ?? "Cliente Anônimo";
            const userAvatar = user?.fotoPerfil;

            return (
              <div key={c.id} className="flex gap-4 p-4 bg-gray-50 rounded-xl border border-gray-100 transition-colors hover:bg-white hover:shadow-sm">
                
                {/* Coluna do Avatar */}
                <div className="flex-shrink-0">
                  {userAvatar ? (
                    <img
                      src={userAvatar}
                      alt={userName}
                      className="w-12 h-12 rounded-full object-cover border-2 border-white shadow-sm"
                    />
                  ) : (
                    // Fallback se não tiver foto (um círculo cinza com a inicial)
                    <div className="w-12 h-12 rounded-full bg-indigo-100 flex items-center justify-center text-indigo-600 font-bold text-lg">
                      {userName.charAt(0).toUpperCase()}
                    </div>
                  )}
                </div>

                {/* Coluna do Conteúdo */}
                <div className="flex-1">
                  <div className="flex items-center justify-between mb-1">
                    <h4 className="font-semibold text-gray-900 text-sm md:text-base">
                      {userName}
                    </h4>
                    <span className="text-xs text-gray-400">
                      {/* Exemplo de data fictícia ou vinda do backend */}
                      Compra verificada
                    </span>
                  </div>

                  <p className="text-gray-700 text-sm leading-relaxed">
                    {c.texto}
                  </p>
                </div>
              </div>
            );
          })
        )}
      </div>
    </div>
  )
};