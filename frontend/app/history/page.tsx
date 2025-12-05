"use client"

import { useEffect, useState } from "react"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { ArrowLeft, ShoppingBag } from "lucide-react"

export default function HistoryPage() {
  const [sales, setSales] = useState<any[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchHistory = async () => {
      const storedUserData = localStorage.getItem("userData")
      let userId = null
      
      if (storedUserData) {
          userId = JSON.parse(storedUserData).user?.cpf
      }
      
      if (!userId) userId = localStorage.getItem("userId")

      if (!userId) {
        setLoading(false)
        return
      }

      try {
        
        const allSalesJson = localStorage.getItem("mock_db_sales")
        const allSales = allSalesJson ? JSON.parse(allSalesJson) : []
        const mySales = allSales.filter((s: any) => s.userCode === userId)

        
        if (Array.isArray(mySales)) {
            mySales.sort((a: any, b: any) => {
                const dateA = new Date(a.saleDate[0], a.saleDate[1]-1, a.saleDate[2])
                const dateB = new Date(b.saleDate[0], b.saleDate[1]-1, b.saleDate[2])
                return dateB.getTime() - dateA.getTime()
            })
            setSales(mySales)
        }
      } catch (error) {
        console.error("Erro ao carregar histórico local:", error)
      } finally {
        setLoading(false)
      }
    }

    fetchHistory()
  }, [])

  const calculateTotal = (items: any[]) => {
    if (!items) return 0
    return items.reduce((acc, item) => {
        return acc + (item.salePrice * item.saleQuantity)
    }, 0)
  }

  if (loading) {
    return (
        <div className="min-h-screen flex items-center justify-center">
            <p className="text-lg text-gray-500">Carregando histórico...</p>
        </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50 p-4 sm:p-8 flex justify-center">
      <div className="w-full max-w-4xl space-y-6">
        
        <div className="flex items-center gap-4 mb-8">
            <Link href="/profile">
                <Button variant="outline" size="icon">
                    <ArrowLeft className="h-4 w-4" />
                </Button>
            </Link>
            <h1 className="text-3xl font-bold text-gray-900">Histórico de Compras</h1>
        </div>

        {sales.length === 0 ? (
          <div className="text-center py-12 bg-white rounded-xl shadow border border-gray-100">
            <ShoppingBag className="mx-auto h-12 w-12 text-gray-300 mb-3" />
            <h3 className="text-lg font-semibold text-gray-900">Nenhuma compra encontrada</h3>
            <p className="text-gray-500">Parece que você ainda não fez feira conosco.</p>
            <Link href="/products" className="mt-4 inline-block text-indigo-600 hover:underline">
                Ir para produtos
            </Link>
          </div>
        ) : (
          <div className="space-y-4">
            {sales.map((sale) => (
              <div key={sale.id} className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition duration-200">
                
                <div className="flex justify-between items-start mb-4 border-b pb-4 border-gray-100">
                  <div>
                    <p className="text-xs font-bold text-indigo-600 uppercase tracking-wide mb-1">
                        Pedido #{sale.id}
                    </p>
                    <p className="font-medium text-gray-900 text-lg">
                      {new Date(sale.saleDate[0], sale.saleDate[1]-1, sale.saleDate[2]).toLocaleDateString()}
                    </p>
                  </div>
                  <div className="text-right">
                    <span className="block text-xs text-gray-500 uppercase tracking-wide mb-1">Total</span>
                    <span className="font-bold text-green-600 text-xl">
                      R$ {calculateTotal(sale.items).toFixed(2)}
                    </span>
                  </div>
                </div>
                
                <div>
                  <p className="text-sm font-medium text-gray-700 mb-3">Itens comprados:</p>
                  <ul className="space-y-2 bg-gray-50 p-4 rounded-lg">
                    {sale.items?.map((item: any, idx: number) => (
                       <li key={idx} className="flex justify-between text-sm text-gray-700">
                         <span className="flex items-center gap-2">
                            <span className="w-2 h-2 rounded-full bg-indigo-400"></span>
                            {item.Name || item.name || `Produto Cód. ${item.productCode}`}
                            <span className="text-gray-400">x{item.saleQuantity}</span>
                         </span>
                         <span className="font-medium">
                            R$ {(item.salePrice * item.saleQuantity).toFixed(2)}
                         </span>
                       </li>
                    ))}
                  </ul>
                </div>

              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}