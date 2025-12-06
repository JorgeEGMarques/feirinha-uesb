"use client"

import { useState } from "react";
import { useRouter } from "next/navigation";
import Image from "next/image"
import { comment, product, profile } from "@/utils/types"
import { Button } from "./ui/button";
import { useCartStore } from "@/store/cart-store";
import { imageConverter } from "@/utils/image-converter";
import { ShoppingCart, Store } from "lucide-react";

interface ProductDetailsProps {
  product: product;
  comments: comment[];
  profiles: profile[];
  tentName: string;
}

export const ProductDetail = ({ product, comments, profiles, tentName }: ProductDetailsProps) => {
  const router = useRouter();
  const { items, addItem, removeItem } = useCartStore();
  
  const [newCommentText, setNewCommentText] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

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

  const handleBuyNow = () => {
    if (quantity === 0) {
      onAddItem();
    }
    router.push("/carrinho");
  };

  const handleSendComment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newCommentText.trim()) return;

    setIsSubmitting(true);

    try {
      const payload = {
        texto: newCommentText,  
        codProd: product.code,
        tentCode: product.tentCode || 1,
        cpfUsuario: "12345678901",
      };

      const baseUrl = process.env.NEXT_PUBLIC_NGROK_URL || process.env.NGROK_URL || "https://anja-superethical-appeasedly.ngrok-free.dev/crud/api";

      const response = await fetch(`${baseUrl}/comentarios`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload)
      });

      if (!response.ok) {
        throw new Error(`Erro: ${response.status}`);
      }

      setNewCommentText("");
      router.refresh();

    } catch (error) {
      console.error("Falha ao comentar:", error);
      alert("Não foi possível enviar o comentário.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div>
      <div className="container mx-auto px-4 py-8 flex flex-col md:flex-row gap-8 items-start"> {/* items-center -> items-start para melhor alinhamento */}
        
        <div className="relative h-96 w-full md:w-1/2 rounded-lg overflow-hidden border border-gray-200 shadow-sm">
          <Image
            src={imageConverter(product.imagem)}
            alt={product.name}
            fill={true}
            style={{ objectFit: 'cover' }}
            className="transition duration-300 hover:scale-105"
            loading="eager"
          />
        </div>

        <div className="md:w-1/2 flex flex-col justify-between h-full space-y-6">
          <div>
            <div className="flex items-center gap-2 text-black-600 mb-2">
              <Store size={18} />
              <span className="text-sm font-semibold uppercase tracking-wider">
                Vendido por {tentName}
              </span>
            </div>

            <h1 className="text-4xl font-bold mb-4 text-gray-900 leading-tight"> {product.name} </h1>

            {product.description && (
              <p className="text-gray-600 mb-6 text-lg leading-relaxed">
                {product.description}
              </p>
            )}

            {product.price && (
              <div className="bg-gray-50 p-4 rounded-lg inline-block mb-6">
                <p className="text-3xl font-bold text-gray-900">
                  R$ {(product.price).toFixed(2).replace('.', ',')}
                </p>
              </div>
            )}
          </div>

          <div className="space-y-4">
            <div className="flex items-center justify-between bg-white border border-gray-200 rounded-lg p-2 w-full md:w-64">
                <span className="text-gray-500 text-sm ml-2">Quantidade:</span>
                <div className="flex items-center gap-4">
                    <Button 
                        variant="ghost" 
                        size="sm"
                        className="hover:bg-red-50 hover:text-red-600 h-8 w-8 rounded-full" 
                        onClick={() => removeItem(product.code)}
                    > 
                        - 
                    </Button>
                    <span className="text-lg font-bold min-w-[20px] text-center"> {quantity}</span>
                    <Button 
                        variant="ghost" 
                        size="sm"
                        className="hover:bg-green-50 hover:text-green-600 h-8 w-8 rounded-full" 
                        onClick={onAddItem}
                    >
                        +
                    </Button>
                </div>
            </div>

            <Button 
                className="w-full md:w-auto px-8 py-6 text-lg bg-green-600 hover:bg-green-700 text-white shadow-lg transition-all hover:-translate-y-1 flex items-center justify-center gap-2"
                onClick={handleBuyNow}
            >
                <ShoppingCart size={24} />
                Comprar Agora
            </Button>
          </div>
        </div>
      </div>

      <div className="space-y-6 mt-12 container mx-auto px-4 max-w-4xl">
        <h3 className="text-2xl font-bold text-gray-900 border-b pb-4">
          Avaliações dos Clientes
        </h3>
        
        <div className="bg-white p-6 rounded-xl border border-gray-200 shadow-sm mb-8">
          <h4 className="text-md font-semibold text-gray-800 mb-2">Deixe sua avaliação</h4>
          <form onSubmit={handleSendComment}>
            <textarea
              className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:outline-none resize-none"
              rows={3}
              placeholder="O que você achou deste produto?"
              value={newCommentText}
              onChange={(e) => setNewCommentText(e.target.value)}
              disabled={isSubmitting}
            />
            <div className="mt-3 flex justify-end">
              <Button 
                type="submit" 
                className="hover:cursor-pointer bg-indigo-600 hover:bg-indigo-700"
                disabled={isSubmitting || !newCommentText.trim()}
              >
                {isSubmitting ? "Enviando..." : "Enviar Comentário"}
              </Button>
            </div>
          </form>
        </div>

        {comments.length === 0 ? (
          <p className="text-gray-500 italic text-center py-8">Nenhum comentário ainda. Seja o primeiro a avaliar!</p>
        ) : (
          comments.filter((com) => com.codProd === product.code).map((c: comment) => {
            const user = profiles.find((u: profile) => u.cpf === c.cpfUsuario);
            const userName = user?.nome ?? "Cliente Anônimo";
            const userAvatar = user?.fotoPerfil;

            return (
              <div key={c.id} className="flex gap-4 p-4 bg-gray-50 rounded-xl border border-gray-100 transition-colors hover:bg-white hover:shadow-sm">
                
                <div className="flex-shrink-0">
                  {userAvatar ? (
                    <img
                      src={userAvatar}
                      alt={userName}
                      className="w-12 h-12 rounded-full object-cover border-2 border-white shadow-sm"
                    />
                  ) : (
                    <div className="w-12 h-12 rounded-full bg-indigo-100 flex items-center justify-center text-indigo-600 font-bold text-lg">
                      {userName.charAt(0).toUpperCase()}
                    </div>
                  )}
                </div>

                <div className="flex-1">
                  <div className="flex items-center justify-between mb-1">
                    <h4 className="font-semibold text-gray-900 text-sm md:text-base">
                      {userName}
                    </h4>
                    <span className="text-xs text-gray-400">
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