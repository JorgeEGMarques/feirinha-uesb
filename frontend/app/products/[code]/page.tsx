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

      return (
    <div>
      <ProductDetail product={product} comments={comments} profiles={profiles} />
    </div>

    //  comments={comments} profiles={profiles}
  )
}

      // await fetch(`${process.env.NGROK_URL}/comentarios`, {
      //   method: 'POST',
      //   headers: {
      //     'Content-Type': 'application/json',
      //   },
      //   body: JSON.stringify(dataToSend)
      // })
      // .then(response => {
      //   if (!response.ok) {
      //     throw new Error(`HTTP error! status: ${response.status}`);
      //   }
      //   return response.json(); // Parse the JSON response from the server
      // })
      // .then(data => {
      //   console.log("success", data);
      // })
      // .catch(error => console.log('Error', error));