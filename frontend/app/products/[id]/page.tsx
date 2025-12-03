import { ProductDetail } from "@/components/product-details";

export default async function ProductPage({ params }: { params: Promise<{ id: number }> }) {
  const { id } = await params;
  const product = await fetch(`http://localhost:3000/products/${id}`)
    .then(response => response.json())
    .catch(error => console.error('Error', error));

  return (
    <div>
      <ProductDetail product={product} />
    </div>
  )
}