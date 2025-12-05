import { ProductDetail } from "@/components/product-details";

export default async function ProductPage({ params }: { params: Promise<{ code: number }> }) {
  const { code } = await params;

  const product = await fetch(`${process.env.NEXT_PUBLIC_NGROK_URL}/products/${code}`)
    .then(response => response.json())

  const comments = await fetch(`${process.env.NEXT_PUBLIC_NGROK_URL}/comentarios`)
    .then(response => response.json())

  const profiles = await fetch(`${process.env.NEXT_PUBLIC_NGROK_URL}/usuarios`)
    .then(response => response.json())

  return (
    <div>
      <ProductDetail product={product} comments={comments} profiles={profiles} />
    </div>
  )
}