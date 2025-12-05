import Image from "next/image";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { ProductGrid } from "@/components/product-grid";
import { imageConverter } from "@/utils/image-converter";

export default async function Home() {
  const products = await fetch(`${process.env.NGROK_URL}/products`)
    .then(response => response.json())
    .catch(error => console.error('Error', error));

  return (
    <div>
      <section className="rounded bg-neutral-100 py-8 sm:py-12">
        <div className="mx-auto grid grid-cols-1 items-center justify-items-center gap-8 px-8 sm:px-16 md:grid-cols-2">
          <div className="max-w-md space-y-4">
            <h2 className="text-3xl font-bold tracking-tight md:text-4xl">
              Bem-vinde à Feirinha UESB!
            </h2>
            <p className="text-neutral-600">
              Descubra os melhores itens pelos melhores preços aqui
            </p>
            <Button
              asChild
              variant="default"
              className="inline-flex items-center justify-center rounded-full px-6 py-3 bg-black text-white"
            >
              <Link
                href="/products"
                className="inline-flex items-center justify-center rounded-full px-6 py-3"
              >
                Dar uma olhada
              </Link>
            </Button>
          </div>
          <Image
            alt="Hero Image"
            src={imageConverter(products[0].imagem)}
            className="rounded"
            width={450}
            height={450}
            loading="eager"
          />
        </div>
      </section>
      <section className="py-8">
        <ProductGrid products={products}/>
      </section>
    </div>
  );
}
