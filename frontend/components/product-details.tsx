"use client"

import Image from "next/image"
import { comment, product, profile } from "@/utils/types"
import { Button } from "./ui/button"
import { useCartStore } from "@/store/cart-store"
import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { imageConverter } from "@/utils/image-converter"

interface ProductDetailsProps {
  product: product,
  // comments: comment[],
  // profiles: profile
}

export const ProductDetail = ({ product }: ProductDetailsProps) => {
  const { items, addItem, removeItem } = useCartStore()
  const router = useRouter()

  const [comments, setComments] = useState<any[]>([])
  const [users, setUsers] = useState<any[]>([])
  
  const [newCommentText, setNewCommentText] = useState("")
  const [isSubmitting, setIsSubmitting] = useState(false)
  const cartItem = items.find((item) => item.code === product.code) || items.find((item) => item.code === product.code)
  const quantity = cartItem ? cartItem.quantity : 0
  const productSrc = "https://placehold.co/600x400?text=Produto+Sem+Foto"

  const API_URL = process.env.NGROK_URL || "http://localhost:3000"

  useEffect(() => {
    const fetchData = async () => {
        try {
          const headers = { 'ngrok-skip-browser-warning': 'true' }

          const [commentsRes, profilesRes] = await Promise.all([
            fetch(`${API_URL}/comments`, { headers }).then(res => res.json()),
            fetch(`${API_URL}/profile`, { headers }).then(res => res.json())
          ]) 
          let productComments = commentsRes.filter((a: any) => Number(a.postId) === Number(product.code))

          setComments(productComments)
          setUsers(profilesRes)
        } catch (error) {
          console.error(error)
        }
    }
    fetchData()
  }, [product.code, API_URL])

  
  const onAddItem = () => {
    addItem({
      code: product.code, 
      name: product.name,
      price: product.price as number,
      imageUrl: productSrc,
      quantity: 1,
    })
  }

  const handlePostComment = async () => {
    const userData = localStorage.getItem("userData")
    const userIdLegacy = localStorage.getItem("userId")
    
    let userId = null
    if (userData) userId = JSON.parse(userData).user?.cpf 
    if (!userId && userIdLegacy) userId = userIdLegacy

    if (!userId) {
        alert("Você precisa estar logado para comentar!")
        router.push("/login")
        return
    }

    if (!newCommentText.trim()) return

    setIsSubmitting(true)

    try {
        const newCommentObj = {
            id: Math.floor(Math.random() * 100000),
            postId: product.code, 
            userId: userId, 
            text: newCommentText
        }

        const res = await fetch(`${API_URL}/comments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'ngrok-skip-browser-warning': 'true'
            },
            body: JSON.stringify(newCommentObj)
        })

        if (res.ok) {
            setComments([...comments, newCommentObj])
            setNewCommentText("")
        } else {
            alert("Erro ao enviar comentário.")
        }

    } catch (error) {
        console.error(error)
    } finally {
        setIsSubmitting(false)
    }
  }

  return (
    <div>
      <div className="container mx-auto px-4 py-8 flex flex-col md:flex-row gap-8 items-center">
        <div className="relative h-96 w-full md:w-1/2 rounded-lg overflow-hidden border border-gray-200">
          <Image
            src={imageConverter(product.imagem)}
            alt={product.name}
            fill={true}
            style={{ objectFit: 'cover' }}
            className="transition duration-300"
            loading="eager"
          />
        </div>

        <div className="md:w-1/2">
          <h1 className="text-3xl font-bold mb-4"> {product.name} </h1>

          {product.description && (
            <p className="text-gray-700 mb-4">
              {product.description}
            </p>
          )}

          {product.price && (
            <p className="text-lg font-semibold text-gray-900">
              R${(product.price).toFixed(2)}
            </p>
          )}

          <div className="flex items-center space-x-4">
            <Button variant="outline" className="hover:cursor-pointer w-10 h-10 p-0" onClick={() => removeItem(product.code)}> - </Button>
            <span className="text-lg font-semibold min-w-[20px] text-center"> {quantity} </span>
            <Button className="hover:cursor-pointer w-10 h-10 p-0" onClick={onAddItem}> + </Button>
            
             <Button className="ml-4 hover:cursor-pointer bg-green-600 hover:bg-green-700" onClick={onAddItem}>
                Adicionar
            </Button>
          </div>
        </div>
      </div>

      <div className="space-y-6 mt-12 max-w-4xl mx-auto px-4">
        <h3 className="text-2xl font-bold text-gray-900 border-b pb-4">
          Avaliações dos Clientes
        </h3>

        <div className="bg-white p-6 rounded-xl border border-gray-200 shadow-sm mb-8">
            <label className="block text-sm font-medium text-gray-700 mb-2">
                Deixe sua opinião sobre este produto:
            </label>
            <textarea
                className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none transition resize-none"
                rows={3}
                placeholder="O que você achou do produto?"
                value={newCommentText}
                onChange={(e) => setNewCommentText(e.target.value)}
            />
            <div className="flex justify-end mt-3">
                <Button 
                    onClick={handlePostComment} 
                    disabled={isSubmitting || newCommentText.trim() === ""}
                    className="bg-indigo-600 hover:bg-indigo-700"
                >
                    {isSubmitting ? "Enviando..." : "Publicar Comentário"}
                </Button>
            </div>
        </div>

        {comments.length === 0 ? (
          <div className="text-center py-8 bg-gray-50 rounded-lg">
             <p className="text-gray-500 italic">Nenhum comentário ainda. Seja o primeiro a avaliar!</p>
          </div>
        ) : (
          <div className="space-y-4">
            {comments.map((c: any) => {
                const user = users.find((u: any) => String(u.id) === String(c.userId)) || users.find((u: any) => String(u.cpf) === String(c.userId))
                
                const userName = user?.name ?? "Usuário"
                const userAvatar = user?.src

                return (
                <div key={c.id} className="flex gap-4 p-4 bg-gray-50 rounded-xl border border-gray-100 transition-colors hover:bg-white hover:shadow-md">
                    
                  <div className="flex-shrink-0">
                  {userAvatar ? (
                      <img
                      src={userAvatar}
                      alt={userName}
                      className="w-12 h-12 rounded-full object-cover border-2 border-white shadow-sm"
                      />
                  ) : (
                      <div className="w-12 h-12 rounded-full bg-indigo-100 flex items-center justify-center text-indigo-600 font-bold text-lg border-2 border-white shadow-sm">
                      {userName.charAt(0).toUpperCase()}
                      </div>
                  )}
                  </div>

                   <div className="flex-1">
                      <div className="flex items-center justify-between mb-1">
                        <h4 className="font-semibold text-gray-900 text-sm md:text-base">
                        {userName}
                        </h4>
                        <div className="flex text-yellow-400 text-xs">
                            {"★".repeat(5)}
                       </div>
                    </div>

                    <p className="text-gray-700 text-sm leading-relaxed mt-1">
                        {c.text}
                    </p>
                  </div>
                </div>
                )
            })}
          </div>
        )}
      </div>
    </div>
  )
}