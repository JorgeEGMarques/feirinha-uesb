"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { useCartStore } from "@/store/cart-store"
import { useState } from "react"
import { useRouter } from "next/navigation"

export default function CheckoutsPage() {
  const { items, removeItem, addItem, clearCart } = useCartStore()
  const [loading, setLoading] = useState(false)
  const router = useRouter()

  const total = items.reduce((acc, item) => acc + item.price * item.quantity, 0)

  const handleCheckout = async () => {
    const userData = localStorage.getItem("userData")
    let userId = null

    if (userData) {
      const parsed = JSON.parse(userData)
      userId = parsed.user?.cpf
    }

    if (!userId) {
      const legacyId = localStorage.getItem("userId")
      if (legacyId) userId = legacyId
    }

    if (!userId) {
      router.push("/login")
      return
    }

    setLoading(true)

    try {
      const hoje = new Date()
      const saleDate = [
        hoje.getFullYear(),
        hoje.getMonth() + 1,
        hoje.getDate()
      ]

      const saleData = {
        userCode: userId,
        tentCode: 1,
        items: items.map(item => ({
          productCode: item.code,
          saleQuantity: item.quantity,
          salePrice: item.price
        }))
      }

      const baseUrl = process.env.NEXT_PUBLIC_NGROK_URL || process.env.NGROK_URL || "https://anja-superethical-appeasedly.ngrok-free.dev/crud/api";

      const response = await fetch(`${baseUrl}/sales`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(saleData),
      })

      if (!response.ok) {
        throw new Error('Falha ao registrar venda')
      }

      clearCart()
      router.push("/success")

    } catch (error) {
      console.error(error)
    } finally {
      setLoading(false)
    }
  }

  if (total === 0 || items.length === 0) {
    return (
      <div className="container mx-auto px-4 py-8 text-center">
        <h1 className="text-3xl font-bold mb-4">Adicione itens ao carrinho!!</h1>
      </div>
    )
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-8 text-center">Carrinho</h1>
      <Card className="max-w-md mx-auto mb-8">
        <CardHeader>
          <CardTitle className="text-xl font-bold">Resumo do Pedido</CardTitle>
        </CardHeader>
        <CardContent>
          <ul className="space-y-4">
            {items.map((item) => (
              <li key={item.code} className="flex flex-col gap-2 border-b pb-2">
                <div className="flex justify-between">
                  <span className="font-medium">{item.name}</span>
                  <span className="font-semibold">R${(item.price * item.quantity).toFixed(2)}</span>
                </div>
                <div className="flex items-center gap-2">
                  <Button variant="outline" size="sm" onClick={() => removeItem(item.code)}>–</Button>
                  <span className="text-lg font-semibold">{item.quantity}</span>
                  <Button variant="outline" size="sm" onClick={() => addItem({ ...item, quantity: 1 })}>+</Button>
                </div>
              </li>
            ))}
          </ul>

          <div className="mt-4 border-t pt-2 text-lg font-semibold">
            Total: R${(total).toFixed(2)}
          </div>
        </CardContent>
      </Card>
      
      <div className="max-w-md mx-auto">
        <Button 
            onClick={handleCheckout} 
            variant="default" 
            className="w-full"
            disabled={loading}
        >
          {loading ? "Processando..." : "Finalizar Compra e Pagar"}
        </Button>
      </div>
    </div>
  )
}