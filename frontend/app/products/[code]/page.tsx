import { ProductDetail } from "@/components/product-details";

export default async function ProductPage({ params }: { params: Promise<{ code: number }> }) {
  const { code } = await params;

  const product = await fetch(`${process.env.NGROK_URL}/products/${code}`)
    .then(response => response.json())
    .catch(error => console.log('Error'));

  const comments = await fetch(`${process.env.NGROK_URL}/comentarios`)
    .then(response => response.json())
    .catch(error => console.log('Error'));

  const profiles = await fetch(`${process.env.NGROK_URL}/usuarios`)
    .then(response => response.json())
    .catch(error => console.log('Error'));

  const tents = await fetch(`${process.env.NGROK_URL}/tents`)
    .then(response => response.json())
    .catch(error => console.log('Error'));

  const tentName = tents.find((tent: any) => tent.code === product.tentCode)?.name || "";

  return (
    <div>
      <ProductDetail product={product} comments={comments} profiles={profiles} tentName={tentName}/>
    </div>
  )
}