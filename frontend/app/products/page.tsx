import { ProductList } from "@/components/product-list"
import { product } from "@/utils/types";

export default async function ProductsPage() {
  const products = await fetch(`${process.env.NGROK_URL}/products`)
    .then(response => response.json())
    .catch(error => console.error('Error', error));

  const tents = await fetch(`${process.env.NGROK_URL}/tents`)
    .then(response => response.json())
    .catch(error => console.error('Error', error));
  
  const usuario = await fetch(`${process.env.NGROK_URL}/usuarios/23456789012`)
    .then(response => response.json())
    .catch(error => console.error('Error', error));
    
  return (
    <div>
      <ProductList products={products} />
    </div>
  )
}