import { ProductList } from "@/components/product-list"
import { product } from "@/utils/types";

export default async function ProductsPage() {
  const products = await fetch(`${process.env.NGROK_URL}/products`)
    .then(response => response.json())
    .catch(error => console.error('Error', error));

  const tents = await fetch(`${process.env.NGROK_URL}/tents`)
    .then(response => response.json())
    .catch(error => console.error('Error', error));
    
  console.log('Products:', products);
  // console.log('Tents:', tents);
  return (
    <div>
      <ProductList products={products} />
    </div>
  )
}