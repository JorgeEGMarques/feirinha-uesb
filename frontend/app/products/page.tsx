import { ProductList } from "@/components/product-list"

export default function ProductsPage() {
  const products = [
    { src:"/dorivaldo_balao.webp", price: 69.50, name: "Balão Ruliço" },
    { src:"/queeeeente.webp", price: 23.98, name: "Labubs" },
    { src:"/esqueletos.webp", price: 1.00, name: "Esqueletos" },
  ]

  return (
    <div>
      <ProductList products={products} />
    </div>
  )
}