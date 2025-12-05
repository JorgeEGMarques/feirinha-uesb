import { ProductList } from "@/components/product-list"
import { product } from "@/utils/types";

export default async function ProductsPage() {
  // const products = await fetch('http://localhost:3000/products')
  //   .then(response => response.json())
  //   .catch(error => console.error('Error', error));

  const products = await fetch(`${process.env.NEXT_PUBLIC_NGROK_URL}/products`)
    .then(response => response.json())
    .catch(error => console.error('Error', error));

  return (
    <div>
      <ProductList products={products} />
    </div>
  )
}